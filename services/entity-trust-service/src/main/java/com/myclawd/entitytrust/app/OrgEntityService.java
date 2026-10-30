package com.myclawd.entitytrust.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.entitytrust.api.OrgEntityRequest;
import com.myclawd.entitytrust.domain.OrgEntity;
import com.myclawd.entitytrust.domain.OrgEntityRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrgEntityService {

    private final OrgEntityRepository repository;
    private final AuditRecorder audit;

    public OrgEntityService(OrgEntityRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<OrgEntity> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public OrgEntity get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Entity", id));
    }

    @Transactional
    public OrgEntity create(OrgEntityRequest request, String actor) {
        OrgEntity saved = repository.save(new OrgEntity(request.name(), request.entityType(), request.country()));
        audit.record(actor, "Entity created", "Entity " + saved.getId() + " created.");
        return saved;
    }
}
