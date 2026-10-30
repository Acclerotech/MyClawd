package com.myclawd.workorder.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.workorder.api.WorkOrderRequest;
import com.myclawd.workorder.domain.WorkOrder;
import com.myclawd.workorder.domain.WorkOrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderService {

    private final WorkOrderRepository repository;
    private final AuditRecorder audit;

    public WorkOrderService(WorkOrderRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public WorkOrder get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Work order", id));
    }

    @Transactional
    public WorkOrder create(WorkOrderRequest request, String actor) {
        WorkOrder saved = repository.save(new WorkOrder(request.reference(), request.clientEntityId(), request.providerEntityId(), request.clientContactUserId(), request.assignedManagerId()));
        audit.record(actor, "Work order created", "Work order " + saved.getId() + " created.");
        return saved;
    }

    @Transactional
    public WorkOrder advance(Long id, String actor) {
        WorkOrder wo = get(id);
        int completed = wo.advanceStep();
        audit.record(actor, "Step " + completed + " completed",
                wo.getReference() + " advanced to Step " + wo.getCurrentStep() + ".");
        return wo;
    }
}
