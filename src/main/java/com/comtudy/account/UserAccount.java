package com.comtudy.account;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.comtudy.domain.Account;

import lombok.Getter;

@Getter
public class UserAccount extends User {
	
	@Autowired
	private Account account;

	public UserAccount(Account account) {
		super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
		// TODO Auto-generated constructor stub
		this.account = account;
	}

}
