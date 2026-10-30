package com.myclawd.audit.app;

import com.myclawd.platform.web.NotFoundException;
import com.myclawd.audit.api.AuditEventRequest;
import com.myclawd.audit.domain.AuditEvent;
import com.myclawd.audit.domain.AuditEventRepository;
import com.myclawd.audit.domain.HashChain;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditEventService {

    private final AuditEventRepository repository;

    public AuditEventService(AuditEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> list() {
        return repository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public AuditEvent get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Audit event", id));
    }

    /**
     * Appends one event to the chain. The {@code actor} header is the calling service; the
     * person who performed the action is {@code request.actor()}.
     */
    @Transactional
    public AuditEvent create(AuditEventRequest request, String actor) {
        AuditEvent event = new AuditEvent(request.source(), request.actor(), request.event(), request.evidence(),
                request.correlationId(), request.occurredAt());
        String tip = repository.findTopByOrderByIdDesc().map(AuditEvent::getHash).orElse(HashChain.GENESIS);
        HashChain.link(tip, event);
        return repository.save(event);
    }

    /** @return id of the first tampered event, or null if the whole chain verifies */
    @Transactional(readOnly = true)
    public Long firstBroken() {
        return HashChain.firstBroken(repository.findAllByOrderByIdAsc());
    }
}
