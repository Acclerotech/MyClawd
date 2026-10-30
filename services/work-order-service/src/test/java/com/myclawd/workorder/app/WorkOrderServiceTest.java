package com.myclawd.workorder.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.workorder.domain.WorkOrder;
import com.myclawd.workorder.domain.WorkOrderRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WorkOrderServiceTest {

    private final WorkOrderRepository repository = mock(WorkOrderRepository.class);
    private final AuditRecorder audit = mock(AuditRecorder.class);
    private final WorkOrderService service = new WorkOrderService(repository, audit);

    @Test
    void advancingWritesAnAuditEvent() {
        WorkOrder wo = new WorkOrder("WO-001", 1L, 2L, null, null);
        when(repository.findById(5L)).thenReturn(Optional.of(wo));

        WorkOrder result = service.advance(5L, "john");

        assertThat(result.getCurrentStep()).isEqualTo(2);
        verify(audit).record("john", "Step 1 completed", "WO-001 advanced to Step 2.");
    }
}
