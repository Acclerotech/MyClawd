package com.myclawd.identity.app;

import com.myclawd.platform.audit.AuditRecorder;
import com.myclawd.platform.web.NotFoundException;
import com.myclawd.identity.api.UserAccountRequest;
import com.myclawd.identity.domain.UserAccount;
import com.myclawd.identity.domain.UserAccountRepository;
import com.myclawd.identity.domain.UserCodeGenerator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {

    private final UserAccountRepository repository;
    private final AuditRecorder audit;
    private final UserCodeGenerator codes;

    public UserAccountService(UserAccountRepository repository, AuditRecorder audit, UserCodeGenerator codes) {
        this.repository = repository;
        this.audit = audit;
        this.codes = codes;
    }

    @Transactional(readOnly = true)
    public List<UserAccount> list() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public UserAccount get(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    @Transactional
    public UserAccount create(UserAccountRequest request, String actor) {
        UserAccount user = new UserAccount(request.fullName(), request.email(), request.role(), request.accountType());
        user.setUserCode(codes.next(repository::existsByUserCode));
        UserAccount saved = repository.save(user);
        audit.record(actor, "User created", saved.getFullName() + " (" + saved.getUserCode() + ") created.");
        return saved;
    }

    @Transactional(readOnly = true)
    public UserAccount getByCode(String userCode) {
        return repository.findByUserCode(userCode).orElseThrow(() -> new NotFoundException("User", userCode));
    }
}
