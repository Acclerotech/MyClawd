package com.myclawd.signature.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "signature_record")
public class SignatureRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String documentRef;

    private String method;

    private Instant createdAt;

    protected SignatureRecord() {
        // for JPA
    }

    public SignatureRecord(Long userId, String documentRef, String method) {
        this.userId = userId;
        this.documentRef = documentRef;
        this.method = method;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDocumentRef() {
        return documentRef;
    }

    public String getMethod() {
        return method;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
