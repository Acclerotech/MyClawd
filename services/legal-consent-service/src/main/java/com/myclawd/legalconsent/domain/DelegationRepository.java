package com.myclawd.legalconsent.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelegationRepository extends JpaRepository<Delegation, Long> {

    List<Delegation> findByClientEntityIdAndProviderEntityIdAndStatus(Long clientEntityId, Long providerEntityId,
                                                                      String status);

    List<Delegation> findByClientEntityIdAndStatus(Long clientEntityId, String status);
}
