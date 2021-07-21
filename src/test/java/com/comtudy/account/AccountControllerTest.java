package com.comtudy.account;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import com.comtudy.domain.Account;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class AccountControllerTest {

	@Autowired 
	private MockMvc mockMvc;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@MockBean
	JavaMailSender javaMailSender;
	
	
	@DisplayName("회원 가입 화면 보이는지 테스트")
	@Test
	void signUpForm() throws Exception {
		mockMvc.perform(get("/sign-up"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up"))
				.andExpect(model().attributeExists("signUpForm"));
	}
	
	@DisplayName("회원 가입 처리 - 입력 값 오류")
	@Test
	void signUpSubmit_with_wrong_input() throws Exception {
		mockMvc.perform(post("/sign-up")
				.param("nickname", "taeju")
				.param("email", "email..")
				.param("password", "12345")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up"));
	}
	
	@DisplayName("회원 가입 처리 - 입력 값 정상")
	@Test
	void signUpSubmit_with_correct_input() throws Exception {
		mockMvc.perform(post("/sign-up")
				.param("nickname", "taeju")
				.param("email", "taeju@email.com")
				.param("password", "12345678")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/"));
		
		Account account = accountRepository.findByEmail("taeju@email.com");
		assertNotNull(account);
		//	assertNotNull(account); 과 같은 테스트가 되어서 주석 처리
//		assertTrue(accountRepository.existsByEmail("taeju@email.com"));
		assertNotEquals(account.getPassword(), "12345678");
		then(javaMailSender).should().send(any(SimpleMailMessage.class));
	}
	
	@DisplayName("테스트")
	@Test
	void test() {
		String vrfTaskStaDay = "today";
		String prodGrpStatCd = "006.002";
		log.info("start");
		
		if (vrfTaskStaDay != null  && !"006.001".contains(prodGrpStatCd)
				&& !"006.002".contains(prodGrpStatCd)) {
			log.info("return grp");
			
		}
		
		log.info("end");
		
	}

}
