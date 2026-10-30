package com.myclawd.scanning.api;

import com.myclawd.scanning.domain.Box;
import java.time.Instant;

public record BoxResponse(
        Long id,
        Long workOrderId,
        String boxNumber,
        String status,
        Instant createdAt) {

    public static BoxResponse from(Box e) {
        return new BoxResponse(e.getId(), e.getWorkOrderId(), e.getBoxNumber(), e.getStatus(), e.getCreatedAt());
    }
}
