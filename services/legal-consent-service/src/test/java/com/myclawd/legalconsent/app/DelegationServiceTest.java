package com.myclawd.legalconsent.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myclawd.legalconsent.api.DelegationRequest;
import com.myclawd.legalconsent.domain.Delegation;
import com.myclawd.legalconsent.domain.DelegationRepository;
import com.myclawd.platform.audit.AuditRecorder;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DelegationServiceTest {

    private final DelegationRepository repository = mock(DelegationRepository.class);
    private final AuditRecorder audit = mock(AuditRecorder.class);
    private final DelegationService service = new DelegationService(repository, audit);

    @Test
    void grantingANewDelegationRevokesThePreviousActiveOne() {
        Delegation previous = new Delegation(1L, 2L, 30L, 40L);
        when(repository.findByClientEntityIdAndProviderEntityIdAndStatus(1L, 2L, Delegation.ACTIVE))
                .thenReturn(List.of(previous));
        when(repository.save(any(Delegation.class))).thenAnswer(inv -> inv.getArgument(0));

        Delegation granted = service.create(new DelegationRequest(1L, 2L, 31L, 40L), "alice");

        assertThat(previous.isActive()).isFalse();
        assertThat(previous.getRevokedAt()).isNotNull();
        assertThat(granted.isActive()).isTrue();
        assertThat(granted.getDelegateUserId()).isEqualTo(31L);
        verify(audit).record(eq("alice"), eq("Delegation of authority granted"), anyString());
    }

    @Test
    void cannotRevokeTwice() {
        Delegation delegation = new Delegation(1L, 2L, 30L, 40L);
        when(repository.findById(9L)).thenReturn(Optional.of(delegation));

        service.revoke(9L, "alice");

        assertThatThrownBy(() -> service.revoke(9L, "alice")).isInstanceOf(IllegalStateException.class);
    }
}
