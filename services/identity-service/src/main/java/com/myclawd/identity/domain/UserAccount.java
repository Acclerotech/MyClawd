package com.myclawd.identity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userCode;

    private String fullName;

    private String email;

    private String role;

    private String accountType;

    private Instant createdAt;

    protected UserAccount() {
        // for JPA
    }

    public UserAccount(String fullName, String email, String role, String accountType) {
        this.userCode = null;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.accountType = accountType;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getAccountType() {
        return accountType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
