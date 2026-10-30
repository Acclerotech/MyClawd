package com.myclawd.identity.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    boolean existsByUserCode(String userCode);

    Optional<UserAccount> findByUserCode(String userCode);
}
