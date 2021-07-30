package com.comtudy.account;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.comtudy.ConsoleMailSender;
import com.comtudy.domain.Account;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {
	
	private final SignUpFormValidator signUpFormValidator;
	private final AccountService accountService;
	private final AccountRepository accountRepository;
	
//	private final ConsoleMailSender consoleMailSender;
	
	// 'signUpForm'이라는 데이터를 받을 때 binder를 설정할 수 있다.
	@InitBinder("signUpForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(signUpFormValidator);
	}
	
	
	@GetMapping("/sign-up")
	public String signUpForm(Model model) {
		// camelCase인 경우 아래처럼 생략이 가능하다..!
//		model.addAttribute("signUpForm", new SignUpForm());
		model.addAttribute(new SignUpForm());
		return "account/sign-up";
	}
	
	@PostMapping("/sign-up")
	public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
		if (errors.hasErrors()) {
			return "account/sign-up";
		}
		
		// 아래 코드를 @InitBinder를 사용하여 변경할 수 있다.
		/* signUpFormValidator.validate(signUpForm, errors);
		if (errors.hasErrors()) {
			return "account/sign-up";
		} */
		
		accountService.processNewAccount(signUpForm);
		
		// TODO 회원 가입 처리
		return "redirect:/";
	}
	
	@GetMapping("/check-email-token")
	public String checkEmailToken(String token, String email, Model model) {
		Account account = accountRepository.findByEmail(email);
		String view = "account/checked-email";
		if ( account == null ) {
			model.addAttribute("error", "wrong.email");
			return view;
		}
		
		if ( !account.getEmailCheckToken().equals(token) ) {
			model.addAttribute("error", "wrong.token");
			return view;
		}
		
		account.completeSignUp();
		model.addAttribute("numberOfUser", accountRepository.count());
		model.addAttribute("nickname", account.getNickname());
		
		return view;
	}

}
