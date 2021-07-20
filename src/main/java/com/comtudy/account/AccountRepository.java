package com.comtudy.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.comtudy.domain.Account;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

}