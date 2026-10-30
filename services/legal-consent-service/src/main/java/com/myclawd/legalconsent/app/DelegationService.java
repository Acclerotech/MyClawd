package com.myclawd.legalconsent.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.legalconsent.api.DelegationRequest;
import com.myclawd.legalconsent.domain.Delegation;
import com.myclawd.legalconsent.domain.DelegationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DelegationService {

    private final DelegationRepository repository;
    private final AuditRecorder audit;

    public DelegationService(DelegationRepository repository, AuditRecorder audit) {
        this.repository = repository;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Delegation> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Delegation get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Delegation", id));
    }

    /**
     * Grants a Delegation of Authority. Only one delegation per client/provider pair is
     * active at a time, so granting a new one revokes the previous one.
     */
    @Transactional
    public Delegation create(DelegationRequest request, String actor) {
        for (Delegation previous : repository.findByClientEntityIdAndProviderEntityIdAndStatus(
                request.clientEntityId(), request.providerEntityId(), Delegation.ACTIVE)) {
            previous.revoke();
            audit.record(actor, "Delegation of authority revoked",
                    "Delegation " + previous.getId() + " replaced by a new grant.");
        }
        Delegation saved = repository.save(new Delegation(request.clientEntityId(), request.providerEntityId(),
                request.delegateUserId(), request.grantedByUserId()));
        audit.record(actor, "Delegation of authority granted",
                "Entity " + saved.getClientEntityId() + " delegated authority to user " + saved.getDelegateUserId()
                        + " at provider " + saved.getProviderEntityId() + ".");
        return saved;
    }

    @Transactional
    public Delegation revoke(Long id, String actor) {
        Delegation delegation = get(id);
        delegation.revoke();
        audit.record(actor, "Delegation of authority revoked", "Delegation " + id + " revoked.");
        return delegation;
    }

    /** Providers this client may create work orders with (the work-order gate). */
    @Transactional(readOnly = true)
    public List<Delegation> activeFor(Long clientEntityId) {
        return repository.findByClientEntityIdAndStatus(clientEntityId, Delegation.ACTIVE);
    }
}
