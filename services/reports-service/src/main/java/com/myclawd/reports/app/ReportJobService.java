package com.myclawd.reports.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.reports.api.ReportJobRequest;
import com.myclawd.reports.domain.ReportJob;
import com.myclawd.reports.domain.ReportJobRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportJobService {

    private final ReportJobRepository repository;
    private final AuditRecorder audit;

    public ReportJobService(ReportJobRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<ReportJob> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ReportJob get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Report job", id));
    }

    @Transactional
    public ReportJob create(ReportJobRequest request, String actor) {
        ReportJob saved = repository.save(new ReportJob(request.reportType(), request.workOrderId(), request.requestedBy(), request.blobUri()));
        audit.record(actor, "Report job created", "Report job " + saved.getId() + " created.");
        return saved;
    }
}
