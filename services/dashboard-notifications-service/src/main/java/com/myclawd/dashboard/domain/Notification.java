package com.myclawd.dashboard.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientUserId;

    private String title;

    private String body;

    private Instant readAt;

    private Instant createdAt;

    protected Notification() {
        // for JPA
    }

    public Notification(Long recipientUserId, String title, String body) {
        this.recipientUserId = recipientUserId;
        this.title = title;
        this.body = body;
        this.readAt = null;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getRecipientUserId() {
        return recipientUserId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }
}
