package com.myclawd.signature.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.signature.api.SignatureRecordRequest;
import com.myclawd.signature.domain.SignatureRecord;
import com.myclawd.signature.domain.SignatureRecordRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignatureRecordService {

    private final SignatureRecordRepository repository;
    private final AuditRecorder audit;

    public SignatureRecordService(SignatureRecordRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<SignatureRecord> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public SignatureRecord get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Signature", id));
    }

    @Transactional
    public SignatureRecord create(SignatureRecordRequest request, String actor) {
        SignatureRecord saved = repository.save(new SignatureRecord(request.userId(), request.documentRef(), request.method()));
        audit.record(actor, "Signature created", "Signature " + saved.getId() + " created.");
        return saved;
    }
}
