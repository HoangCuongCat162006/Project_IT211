package com.example.project_it211.service;


import com.example.project_it211.entity.TokenBlacklist;
import com.example.project_it211.repository.TokenBlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class TokenBlackListService {

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;


    @Transactional
    public void blacklistToken(String token, long expirationMs) {
        if (token == null || token.isBlank()) {
            return;
        }
        if (tokenBlacklistRepository.existsByToken(token)) {
            return; // đã thu hồi trước đó
        }
        TokenBlacklist entry = new TokenBlacklist();
        entry.setToken(token);
        entry.setRevokedAt(LocalDateTime.now());
        if (expirationMs > 0) {
            entry.setExpiresAt(LocalDateTime.now().plusNanos(expirationMs * 1_000_000));
        }
        tokenBlacklistRepository.save(entry);
    }


    public boolean isBlacklisted(String token) {
        if (token == null) {
            return false;
        }
        return tokenBlacklistRepository.existsByToken(token);
    }
}
