package com.comtudy.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.comtudy.WithAccount;
import com.comtudy.account.AccountRepository;
import com.comtudy.account.AccountService;
import com.comtudy.account.SignUpForm;
import com.comtudy.domain.Account;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	// '@WithAccount'를 사용할 땐 '@withAccount'쪽에서 처리가 되므로
	// '@BeforEach'를 사용할 필요가 없지만
	// '@WithUserDetails'를 'setupBefore'와 함께 사용할 땐 필요 없다!
	@BeforeEach
	void beforeEach() {
		SignUpForm signUpForm = new SignUpForm();
		signUpForm.setNickname("pulpury");
		signUpForm.setEmail("pulpury@email.com");
		signUpForm.setPassword("12345678");
		accountService.processNewAccount(signUpForm);
	}
	
	// '@WithUserDetails'를 사용할 땐 지워줄 필요가 없지만
		// 에러가 있어 '@WithAccount'로 대체해서 사용할 땐
		// 테스트 후 지워줬어야했다.
		/*
		 * @AfterEach void afterEach() { accountRepository.deleteAll(); }
		 */

		// @WithAccount("pulpury")
		// '@WithUserDetails'가 에러가 있을 땐 위의 '@WithAccount'를 사용했지만
		// '@WithUserDetails'의 에러가 현재는 해결됐으므로 괜찮다!
		@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
		@DisplayName("프로필 수정하기 폼")
		@Test
		void updateProfilefForm() throws Exception {
			String bio = "짧은 소개를 수정하는 경우.";
			mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
					.andExpect(status().isOk())
					.andExpect(model().attributeExists("account"))
					.andExpect(model().attributeExists("profile"));
		}

	// '@WithUserDetails'를 사용할 땐 지워줄 필요가 없지만
	// 에러가 있어 '@WithAccount'로 대체해서 사용할 땐
	// 테스트 후 지워줬어야했다.
	/*
	 * @AfterEach void afterEach() { accountRepository.deleteAll(); }
	 */

	// @WithAccount("pulpury")
	// '@WithUserDetails'가 에러가 있을 땐 위의 '@WithAccount'를 사용했지만
	// '@WithUserDetails'의 에러가 현재는 해결됐으므로 괜찮다!
	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@DisplayName("프로필 수정하기 - 입력 값 정상")
	@Test
	void updateProfile() throws Exception {
		String bio = "짧은 소개를 수정하는 경우.";
		mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
				.param("bio", bio)
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
				.andExpect(flash().attributeExists("message"));

		Account pulpury = accountRepository.findByNickname("pulpury");
		assertEquals(bio, pulpury.getBio());
	}
	
	// @WithAccount("pulpury")
	// '@WithUserDetails'가 에러가 있을 땐 위의 '@WithAccount'를 사용했지만
	// '@WithUserDetails'의 에러가 현재는 해결됐으므로 괜찮다!
	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@DisplayName("프로필 수정하기 - 입력 값 에러")
	@Test
	void updateProfile_error() throws Exception {
		String bio = "너무나도 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우.";
		mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
				.param("bio", bio)
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("profile"))
				.andExpect(model().hasErrors());

		Account pulpury = accountRepository.findByNickname("pulpury");
		assertNull(pulpury.getBio());
	}
	
	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword_form() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account pulpury = accountRepository.findByNickname("pulpury");
        assertTrue(passwordEncoder.matches("12345678", pulpury.getPassword()));
    }

	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

}
