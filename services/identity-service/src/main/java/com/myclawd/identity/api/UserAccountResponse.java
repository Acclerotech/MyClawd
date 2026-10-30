package com.myclawd.identity.api;

import com.myclawd.identity.domain.UserAccount;
import java.time.Instant;

public record UserAccountResponse(
        Long id,
        String userCode,
        String fullName,
        String email,
        String role,
        String accountType,
        Instant createdAt) {

    public static UserAccountResponse from(UserAccount e) {
        return new UserAccountResponse(e.getId(), e.getUserCode(), e.getFullName(), e.getEmail(), e.getRole(), e.getAccountType(), e.getCreatedAt());
    }
}
