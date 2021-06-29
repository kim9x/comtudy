package com.comtudy.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
	
	@GetMapping("/sign-up")
	public String signUpForm(Model model) {
		// camelCase인 경우 아래처럼 생략이 가능하다..!
//		model.addAttribute("signUpForm", new SignUpForm());
		model.addAttribute(new SignUpForm());
		return "account/sign-up";
	}

}
