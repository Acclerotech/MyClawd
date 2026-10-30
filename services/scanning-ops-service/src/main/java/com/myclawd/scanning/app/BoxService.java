package com.myclawd.scanning.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.scanning.api.BoxRequest;
import com.myclawd.scanning.domain.Box;
import com.myclawd.scanning.domain.BoxRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoxService {

    private final BoxRepository repository;
    private final AuditRecorder audit;

    public BoxService(BoxRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Box> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Box get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Box", id));
    }

    @Transactional
    public Box create(BoxRequest request, String actor) {
        Box saved = repository.save(new Box(request.workOrderId(), request.boxNumber(), request.status()));
        audit.record(actor, "Box created", "Box " + saved.getId() + " created.");
        return saved;
    }
}
