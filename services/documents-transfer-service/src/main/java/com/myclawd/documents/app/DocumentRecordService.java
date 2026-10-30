package com.myclawd.documents.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.documents.api.DocumentRecordRequest;
import com.myclawd.documents.domain.DocumentRecord;
import com.myclawd.documents.domain.DocumentRecordRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentRecordService {

    private final DocumentRecordRepository repository;
    private final AuditRecorder audit;

    public DocumentRecordService(DocumentRecordRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<DocumentRecord> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public DocumentRecord get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Document", id));
    }

    @Transactional
    public DocumentRecord create(DocumentRecordRequest request, String actor) {
        DocumentRecord saved = repository.save(new DocumentRecord(request.workOrderId(), request.fileName(), request.storageUri(), request.sha256(), request.status()));
        audit.record(actor, "Document created", "Document " + saved.getId() + " created.");
        return saved;
    }
}
