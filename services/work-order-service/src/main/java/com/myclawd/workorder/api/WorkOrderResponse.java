package com.myclawd.workorder.api;

import com.myclawd.workorder.domain.WorkOrder;
import java.time.Instant;

public record WorkOrderResponse(
        Long id,
        String reference,
        Long clientEntityId,
        Long providerEntityId,
        Long clientContactUserId,
        Long assignedManagerId,
        Integer currentStep,
        Instant createdAt) {

    public static WorkOrderResponse from(WorkOrder e) {
        return new WorkOrderResponse(e.getId(), e.getReference(), e.getClientEntityId(), e.getProviderEntityId(), e.getClientContactUserId(), e.getAssignedManagerId(), e.getCurrentStep(), e.getCreatedAt());
    }
}
