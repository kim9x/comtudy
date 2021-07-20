package com.comtudy.account;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// final이면 lombok이 생성자를 자동으로 생성해준다.
public class SignUpFormValidator implements Validator {
	
	
	private final AccountRepository accountRepository;

	@Override
	public boolean supports(Class<?> aClass) {
		// TODO Auto-generated method stub
		return aClass.isAssignableFrom(SignUpForm.class);
	}

	@Override
	public void validate(Object object, Errors errors) {
		SignUpForm signUpForm = (SignUpForm)object;
		if (accountRepository.existsByEmail(signUpForm.getEmail())) {
			errors.rejectValue("email", "invalid.email", new Object[] {signUpForm.getEmail()}, "이미 사용중인 이메일입니다.");
		}

		if (accountRepository.existsByNickname(signUpForm.getNickname())) {
			errors.rejectValue("nickname", "invalid.nickname", new Object[] {signUpForm.getNickname()}, "이미 사용중인 이메일입니다.");
		}
		
	}
	
	

}
