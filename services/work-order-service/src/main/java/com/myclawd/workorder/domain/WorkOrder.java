package com.myclawd.workorder.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "work_order")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference;

    private Long clientEntityId;

    private Long providerEntityId;

    private Long clientContactUserId;

    private Long assignedManagerId;

    private Integer currentStep;

    private Instant createdAt;

    protected WorkOrder() {
        // for JPA
    }

    public WorkOrder(String reference, Long clientEntityId, Long providerEntityId, Long clientContactUserId, Long assignedManagerId) {
        this.reference = reference;
        this.clientEntityId = clientEntityId;
        this.providerEntityId = providerEntityId;
        this.clientContactUserId = clientContactUserId;
        this.assignedManagerId = assignedManagerId;
        this.currentStep = 1;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public Long getClientEntityId() {
        return clientEntityId;
    }

    public Long getProviderEntityId() {
        return providerEntityId;
    }

    public Long getClientContactUserId() {
        return clientContactUserId;
    }

    public Long getAssignedManagerId() {
        return assignedManagerId;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /** The prototype's 7-step tracker: Document Set (1) through Destruction / closure (7). */
    public static final int FINAL_STEP = 7;

    /**
     * Completes the current step and moves to the next one.
     *
     * @return the step that was just completed
     */
    public int advanceStep() {
        if (currentStep >= FINAL_STEP) {
            throw new IllegalStateException("Work order " + reference + " is already at step " + FINAL_STEP);
        }
        int completed = currentStep;
        currentStep = currentStep + 1;
        return completed;
    }

    public boolean isComplete() {
        return currentStep == FINAL_STEP;
    }
}
