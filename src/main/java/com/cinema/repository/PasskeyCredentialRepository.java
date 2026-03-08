package com.cinema.repository;

import com.cinema.entity.PasskeyCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasskeyCredentialRepository extends JpaRepository<PasskeyCredential, Long> {
    List<PasskeyCredential> findByUserIdAndEnabledTrue(Long userId);
    Optional<PasskeyCredential> findByCredentialId(String credentialId);
    boolean existsByCredentialId(String credentialId);
}
