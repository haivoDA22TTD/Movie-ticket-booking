package com.cinema.controller;

import com.cinema.entity.PasskeyCredential;
import com.cinema.entity.User;
import com.cinema.service.PasskeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/passkey")
@RequiredArgsConstructor
public class PasskeyController {
    
    private final PasskeyService passkeyService;
    
    @GetMapping("/settings")
    public String passkeySettings(@AuthenticationPrincipal User user, Model model) {
        List<PasskeyCredential> passkeys = passkeyService.getUserPasskeys(user.getId());
        model.addAttribute("passkeys", passkeys);
        return "passkey-settings";
    }
    
    @PostMapping("/register/start")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> startRegistration(
        @AuthenticationPrincipal User user,
        @RequestParam(required = false) String deviceName
    ) {
        Map<String, Object> options = passkeyService.startRegistration(user, deviceName);
        return ResponseEntity.ok(options);
    }
    
    @PostMapping("/register/finish")
    @ResponseBody
    public ResponseEntity<Map<String, String>> finishRegistration(
        @AuthenticationPrincipal User user,
        @RequestBody Map<String, String> request
    ) {
        try {
            passkeyService.finishRegistration(
                user,
                request.get("credentialId"),
                request.get("publicKey"),
                request.get("deviceName"),
                request.get("challenge")
            );
            return ResponseEntity.ok(Map.of("status", "success", "message", "Passkey đã được đăng ký"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    
    @PostMapping("/authenticate/start")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> startAuthentication(@RequestParam String email) {
        try {
            Map<String, Object> options = passkeyService.startAuthentication(email);
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/authenticate/finish")
    @ResponseBody
    public ResponseEntity<Map<String, String>> finishAuthentication(
        @RequestBody Map<String, String> request
    ) {
        try {
            User user = passkeyService.finishAuthentication(
                request.get("credentialId"),
                request.get("challenge"),
                request.get("signature"),
                request.get("authenticatorData")
            );
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Đăng nhập thành công",
                "redirect", "/"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{passkeyId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deletePasskey(
        @PathVariable Long passkeyId,
        @AuthenticationPrincipal User user
    ) {
        try {
            passkeyService.deletePasskey(passkeyId, user.getId());
            return ResponseEntity.ok(Map.of("status", "success", "message", "Đã xóa passkey"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
