package com.myclawd.entitytrust.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "org_entity")
public class OrgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String entityType;

    private String country;

    private Integer trustLevel;

    private Instant createdAt;

    protected OrgEntity() {
        // for JPA
    }

    public OrgEntity(String name, String entityType, String country) {
        this.name = name;
        this.entityType = entityType;
        this.country = country;
        this.trustLevel = 0;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getCountry() {
        return country;
    }

    public Integer getTrustLevel() {
        return trustLevel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setTrustLevel(Integer trustLevel) {
        this.trustLevel = trustLevel;
    }
}
