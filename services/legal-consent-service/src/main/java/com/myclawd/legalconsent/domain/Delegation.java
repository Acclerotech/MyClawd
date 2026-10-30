package com.myclawd.legalconsent.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "delegation")
public class Delegation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clientEntityId;

    private Long providerEntityId;

    private Long delegateUserId;

    private Long grantedByUserId;

    private String status;

    private Instant revokedAt;

    private Instant createdAt;

    protected Delegation() {
        // for JPA
    }

    public Delegation(Long clientEntityId, Long providerEntityId, Long delegateUserId, Long grantedByUserId) {
        this.clientEntityId = clientEntityId;
        this.providerEntityId = providerEntityId;
        this.delegateUserId = delegateUserId;
        this.grantedByUserId = grantedByUserId;
        this.status = "Active";
        this.revokedAt = null;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getClientEntityId() {
        return clientEntityId;
    }

    public Long getProviderEntityId() {
        return providerEntityId;
    }

    public Long getDelegateUserId() {
        return delegateUserId;
    }

    public Long getGrantedByUserId() {
        return grantedByUserId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static final String ACTIVE = "Active";
    public static final String REVOKED = "Revoked";

    public boolean isActive() {
        return ACTIVE.equals(status);
    }

    /** Revoking does not affect work orders already created under this delegation. */
    public void revoke() {
        if (!isActive()) {
            throw new IllegalStateException("Delegation " + id + " is already " + status);
        }
        status = REVOKED;
        revokedAt = Instant.now();
    }
}
