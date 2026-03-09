package com.cinema.service;

import com.cinema.entity.PasskeyCredential;
import com.cinema.entity.User;
import com.cinema.repository.PasskeyCredentialRepository;
import com.cinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasskeyService {
    
    private final PasskeyCredentialRepository passkeyCredentialRepository;
    private final UserRepository userRepository;
    
    @Value("${APP_DOMAIN}")
    private String appDomain;
    
    // Store challenges temporarily (in production, use Redis)
    private final Map<String, ChallengeData> challengeStore = new HashMap<>();
    
    public Map<String, Object> startRegistration(User user, String deviceName) {
        // Generate challenge
        String challenge = generateChallenge();
        String userId = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(user.getId().toString().getBytes());
        
        // Store challenge
        challengeStore.put(challenge, new ChallengeData(user.getId(), System.currentTimeMillis()));
        
        Map<String, Object> response = new HashMap<>();
        response.put("challenge", challenge);
        response.put("rp", Map.of(
            "name", "Cinema Booking",
            "id", appDomain
        ));
        response.put("user", Map.of(
            "id", userId,
            "name", user.getEmail(),
            "displayName", user.getFullName()
        ));
        response.put("pubKeyCredParams", List.of(
            Map.of("type", "public-key", "alg", -7),  // ES256
            Map.of("type", "public-key", "alg", -257) // RS256
        ));
        response.put("timeout", 60000);
        response.put("attestation", "none");
        response.put("authenticatorSelection", Map.of(
            "authenticatorAttachment", "platform",
            "requireResidentKey", false,
            "userVerification", "preferred"
        ));
        
        return response;
    }
    
    @Transactional
    public PasskeyCredential finishRegistration(
        User user,
        String credentialId,
        String publicKey,
        String deviceName,
        String challenge
    ) {
        // Verify challenge
        ChallengeData challengeData = challengeStore.get(challenge);
        if (challengeData == null || !challengeData.userId.equals(user.getId())) {
            throw new RuntimeException("Invalid challenge");
        }
        
        // Remove used challenge
        challengeStore.remove(challenge);
        
        // Check if credential already exists
        if (passkeyCredentialRepository.existsByCredentialId(credentialId)) {
            throw new RuntimeException("Credential already registered");
        }
        
        // Save credential
        PasskeyCredential credential = PasskeyCredential.builder()
            .user(user)
            .credentialId(credentialId)
            .publicKey(publicKey)
            .signatureCount(0L)
            .aaguid("00000000-0000-0000-0000-000000000000")
            .deviceName(deviceName != null ? deviceName : "Unknown Device")
            .enabled(true)
            .build();
        
        return passkeyCredentialRepository.save(credential);
    }
    
    public Map<String, Object> startAuthentication(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<PasskeyCredential> credentials = passkeyCredentialRepository
            .findByUserIdAndEnabledTrue(user.getId());
        
        if (credentials.isEmpty()) {
            throw new RuntimeException("No passkey registered");
        }
        
        String challenge = generateChallenge();
        challengeStore.put(challenge, new ChallengeData(user.getId(), System.currentTimeMillis()));
        
        List<Map<String, String>> allowCredentials = credentials.stream()
            .map(cred -> Map.of(
                "type", "public-key",
                "id", cred.getCredentialId()
            ))
            .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("challenge", challenge);
        response.put("timeout", 60000);
        response.put("rpId", appDomain);
        response.put("allowCredentials", allowCredentials);
        response.put("userVerification", "preferred");
        
        return response;
    }
    
    @Transactional
    public User finishAuthentication(
        String credentialId,
        String challenge,
        String signature,
        String authenticatorData
    ) {
        // Verify challenge
        ChallengeData challengeData = challengeStore.get(challenge);
        if (challengeData == null) {
            throw new RuntimeException("Invalid challenge");
        }
        
        challengeStore.remove(challenge);
        
        // Find credential
        PasskeyCredential credential = passkeyCredentialRepository
            .findByCredentialId(credentialId)
            .orElseThrow(() -> new RuntimeException("Credential not found"));
        
        // Update last used
        credential.setLastUsedAt(LocalDateTime.now());
        credential.setSignatureCount(credential.getSignatureCount() + 1);
        passkeyCredentialRepository.save(credential);
        
        return credential.getUser();
    }
    
    public List<PasskeyCredential> getUserPasskeys(Long userId) {
        return passkeyCredentialRepository.findByUserIdAndEnabledTrue(userId);
    }
    
    @Transactional
    public void deletePasskey(Long passkeyId, Long userId) {
        PasskeyCredential credential = passkeyCredentialRepository.findById(passkeyId)
            .orElseThrow(() -> new RuntimeException("Passkey not found"));
        
        if (!credential.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        passkeyCredentialRepository.delete(credential);
    }
    
    private String generateChallenge() {
        byte[] bytes = new byte[32];
        new Random().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    // Clean up old challenges (call this periodically)
    public void cleanupOldChallenges() {
        long now = System.currentTimeMillis();
        challengeStore.entrySet().removeIf(entry -> 
            now - entry.getValue().timestamp > 300000 // 5 minutes
        );
    }
    
    private record ChallengeData(Long userId, long timestamp) {}
}
