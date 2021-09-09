package com.comtudy.account;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comtudy.domain.Account;
import com.comtudy.domain.Tag;
import com.comtudy.settings.form.Notifications;
import com.comtudy.settings.form.Profile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
	
	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;
//	private final AuthenticationManager authenticationManager;
	private final ModelMapper modelMapper;
	
	public Account processNewAccount(@Valid SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
		sendSignUpConfirmEmail(newAccount);
		
		return newAccount;
	}
	
	private Account saveNewAccount(SignUpForm signUpForm) {
		signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
	}


	public void sendSignUpConfirmEmail(Account newAccount) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setSubject("스터디올래, 회원 가입 인증");
		mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());		
		javaMailSender.send(mailMessage);
	}

	public void login(Account account) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				new UserAccount(account)
				, account.getPassword()
				, List.of(new SimpleGrantedAuthority("ROLE_USER")));
		
		SecurityContextHolder.getContext().setAuthentication(token);

		// 원래 정석적인 방법
//		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//				username, password);
//		Authentication authenticate = authenticationManager.authenticate(token);
//		 context.setAuthentication(authenticate);
		
		
//		 SecurityContext context = SecurityContextHolder.getContext();
//		 context.setAuthentication(null);
		
	}

	@Override
	public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
	}

	public void completeSignUp(Account account) {
		account.completeSignUp();
		login(account);
	}

    public void updateProfile(Account account, Profile profile) {
    	modelMapper.map(profile, account);
        accountRepository.save(account);
        // TODO 문제가 하나 더 남았습니다.
    }

	public void updatePassword(Account account, String newPassword) {
		account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
	}
	
	public void updateNotifications(Account account, Notifications notifications) {
        account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
        account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
        account.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());
        account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
        account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());
        account.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());
        accountRepository.save(account);
    }
	
	public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

	public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디올래, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }
	
	public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

	public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

}
