package com.myclawd.ra.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.ra.api.RaCaseRequest;
import com.myclawd.ra.domain.RaCase;
import com.myclawd.ra.domain.RaCaseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RaCaseService {

    private final RaCaseRepository repository;
    private final AuditRecorder audit;

    public RaCaseService(RaCaseRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<RaCase> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public RaCase get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("RA case", id));
    }

    @Transactional
    public RaCase create(RaCaseRequest request, String actor) {
        RaCase saved = repository.save(new RaCase(request.subjectType(), request.subjectId(), request.certRef()));
        audit.record(actor, "RA case created", "RA case " + saved.getId() + " created.");
        return saved;
    }
}
