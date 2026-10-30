package com.myclawd.dashboard.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.dashboard.api.NotificationRequest;
import com.myclawd.dashboard.domain.Notification;
import com.myclawd.dashboard.domain.NotificationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final AuditRecorder audit;

    public NotificationService(NotificationRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Notification> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Notification get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Notification", id));
    }

    @Transactional
    public Notification create(NotificationRequest request, String actor) {
        Notification saved = repository.save(new Notification(request.recipientUserId(), request.title(), request.body()));
        audit.record(actor, "Notification created", "Notification " + saved.getId() + " created.");
        return saved;
    }
}
