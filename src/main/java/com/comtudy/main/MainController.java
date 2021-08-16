package com.comtudy.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.comtudy.account.CurrentUser;
import com.comtudy.domain.Account;

@Controller
public class MainController {
	
	@GetMapping
	public String home(@CurrentUser Account account, Model model) {
		if (account != null) {
			model.addAttribute(account);
		}
		
		return "index";
	}
	
	@GetMapping("/login")
    public String login() {
        return "login";
    }

}
