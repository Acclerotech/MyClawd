package com.myclawd.ra.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ra_case")
public class RaCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectType;

    private Long subjectId;

    private Integer raStep;

    private String status;

    private String certRef;

    private Instant createdAt;

    protected RaCase() {
        // for JPA
    }

    public RaCase(String subjectType, Long subjectId, String certRef) {
        this.subjectType = subjectType;
        this.subjectId = subjectId;
        this.raStep = 0;
        this.status = "Open";
        this.certRef = certRef;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public Integer getRaStep() {
        return raStep;
    }

    public String getStatus() {
        return status;
    }

    public String getCertRef() {
        return certRef;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setRaStep(Integer raStep) {
        this.raStep = raStep;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
