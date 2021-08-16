package com.comtudy.main;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.comtudy.account.AccountRepository;
import com.comtudy.account.AccountService;
import com.comtudy.account.SignUpForm;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
	
	@Autowired MockMvc mockMvc;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
	
	
	@BeforeEach
	void beforeEach() {
		SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("pulpury");
        signUpForm.setEmail("pulpury@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
	}
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}
	
	@DisplayName("이메일로 로그인 성공")
	@Test
	void login_with_email() throws Exception {
		mockMvc.perform(post("/login")
				// SpringSecurity에서 "username"과 "password"는 정해진 값.
				// 아래를 통해서 허나 변경할 수는 있다.
				/*http.formLogin()
        			.usernameParameter(null)
        			.passwordParameter(null)*/
				.param("username", "pulpury@email.com")
				.param("password", "12345678")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated().withUsername("pulpury"));	
	}
	
	@DisplayName("닉네임으로 로그인 성공")
	@Test
	void login_with_nick() throws Exception {
		mockMvc.perform(post("/login")
				// SpringSecurity에서 "username"과 "password"는 정해진 값.
				// 아래를 통해서 허나 변경할 수는 있다.
				/*http.formLogin()
        			.usernameParameter(null)
        			.passwordParameter(null)*/
				.param("username", "pulpury")
				.param("password", "12345678")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated().withUsername("pulpury"));
	}
	
	@DisplayName("로그인 실패")
	@Test
	void login_fail() throws Exception {
		mockMvc.perform(post("/login")
				.param("username", "111111111")
				.param("password", "000000000000")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(unauthenticated());
	}
	
	// WithMockUser 애노테이션은
	// user 'user' 값, password에 'password' 값을 
	// 자동으로 넣어줌
	@WithMockUser
	@DisplayName("로그아웃")
	@Test
	void logout() throws Exception {
		mockMvc.perform(post("/logout")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(unauthenticated());
	}

}
