package com.comtudy.settings;

import static com.comtudy.settings.SettingsController.ACCOUNT;
import static com.comtudy.settings.SettingsController.PASSWORD;
import static com.comtudy.settings.SettingsController.PROFILE;
import static com.comtudy.settings.SettingsController.ROOT;
import static com.comtudy.settings.SettingsController.SETTINGS;
import static com.comtudy.settings.SettingsController.TAGS;
import static com.comtudy.settings.SettingsController.ZONES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.comtudy.WithAccount;
import com.comtudy.account.AccountRepository;
import com.comtudy.account.AccountService;
import com.comtudy.domain.Account;
import com.comtudy.domain.Tag;
import com.comtudy.domain.Zone;
import com.comtudy.tag.TagForm;
import com.comtudy.tag.TagRepository;
import com.comtudy.zone.ZoneForm;
import com.comtudy.zone.ZoneRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    TagRepository tagRepository;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    ZoneRepository zoneRepository;

    private Zone testZone = Zone.builder().city("test").localNameOfCity("????????????").province("????????????").build();

	// '@WithAccount'??? ????????? ??? '@withAccount'????????? ????????? ?????????
	// '@BeforEach'??? ????????? ????????? ?????????
	// '@WithUserDetails'??? 'setupBefore'??? ?????? ????????? ??? ?????? ??????!
	/*
	 * @BeforeEach void beforeEach() { SignUpForm signUpForm = new SignUpForm();
	 * signUpForm.setNickname("pulpury"); signUpForm.setEmail("pulpury@email.com");
	 * signUpForm.setPassword("12345678");
	 * accountService.processNewAccount(signUpForm); }
	 */
	
	// '@WithUserDetails'??? ????????? ??? ????????? ????????? ?????????
		// ????????? ?????? '@WithAccount'??? ???????????? ????????? ???
		// ????????? ??? ?????????????????????.
		
	@AfterEach void afterEach() {
		accountRepository.deleteAll();zoneRepository.deleteAll();
    }

    @WithAccount("pulpury")
    @DisplayName("????????? ?????? ?????? ?????? ???")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("pulpury")
    @DisplayName("????????? ?????? ?????? ??????")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Account pulpury = accountRepository.findByNickname("pulpury");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(pulpury.getZones().contains(zone));
    }

    @WithAccount("pulpury")
    @DisplayName("????????? ?????? ?????? ??????")
    @Test
    void removeZone() throws Exception {
        Account pulpury = accountRepository.findByNickname("pulpury");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        accountService.addZone(pulpury, zone);

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(pulpury.getZones().contains(zone));
    }
	
	@WithAccount("pulpury")
    @DisplayName("????????? ?????? ?????? ???")
    @Test
    void updateTagsForm() throws Exception {
		mockMvc.perform(get(ROOT + SETTINGS + TAGS))
        .andExpect(view().name(SETTINGS + TAGS))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("pulpury")
    @DisplayName("????????? ?????? ??????")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account pulpury = accountRepository.findByNickname("pulpury");
        assertTrue(pulpury.getTags().contains(newTag));
    }

    @WithAccount("pulpury")
    @DisplayName("????????? ?????? ??????")
    @Test
    void removeTag() throws Exception {
        Account pulpury = accountRepository.findByNickname("pulpury");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(pulpury, newTag);

        assertTrue(pulpury.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(pulpury.getTags().contains(newTag));
    }
		 

	
		// '@WithUserDetails'??? ????????? ?????? ??? ?????? '@WithAccount'??? ???????????????
		// '@WithUserDetails'??? ????????? ????????? ?????????????????? ?????????!
//		@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@WithAccount("pulpury")
	@DisplayName("????????? ?????? ???")
	@Test
	void updateProfilefForm() throws Exception {
		mockMvc.perform(get(ROOT + SETTINGS + PROFILE))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("profile"));
	}

	// '@WithUserDetails'??? ????????? ??? ????????? ????????? ?????????
	// ????????? ?????? '@WithAccount'??? ???????????? ????????? ???
	// ????????? ??? ?????????????????????.
	/*
	 * @AfterEach void afterEach() { accountRepository.deleteAll(); }
	 */

	@WithAccount("pulpury")
	// '@WithUserDetails'??? ????????? ?????? ??? ?????? '@WithAccount'??? ???????????????
	// '@WithUserDetails'??? ????????? ????????? ?????????????????? ?????????!
//	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@DisplayName("????????? ???????????? - ?????? ??? ??????")
	@Test
	void updateProfile() throws Exception {
		String bio = "?????? ????????? ???????????? ??????.";
		mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
				.param("bio", bio)
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
				.andExpect(flash().attributeExists("message"));

		Account pulpury = accountRepository.findByNickname("pulpury");
		assertEquals(bio, pulpury.getBio());
	}
	
	@WithAccount("pulpury")
	// '@WithUserDetails'??? ????????? ?????? ??? ?????? '@WithAccount'??? ???????????????
	// '@WithUserDetails'??? ????????? ????????? ?????????????????? ?????????!
//	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@DisplayName("????????? ???????????? - ?????? ??? ??????")
	@Test
	void updateProfile_error() throws Exception {
		String bio = "???????????? ?????? ????????? ???????????? ??????. ???????????? ?????? ????????? ???????????? ??????. ???????????? ?????? ????????? ???????????? ??????. ???????????? ?????? ????????? ???????????? ??????.";
		mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
				.param("bio", bio)
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name(SETTINGS + PROFILE))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("profile"))
				.andExpect(model().hasErrors());

		Account pulpury = accountRepository.findByNickname("pulpury");
		assertNull(pulpury.getBio());
	}
	
//	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@WithAccount("pulpury")
    @DisplayName("???????????? ?????? ???")
    @Test
    void updatePassword_form() throws Exception {
		mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

//	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@WithAccount("pulpury")
    @DisplayName("???????????? ?????? - ????????? ??????")
    @Test
    void updatePassword_success() throws Exception {
		mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
                .andExpect(flash().attributeExists("message"));

        Account pulpury = accountRepository.findByNickname("pulpury");
        assertTrue(passwordEncoder.matches("12345678", pulpury.getPassword()));
    }

//	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@WithAccount("pulpury")
    @DisplayName("???????????? ?????? - ????????? ?????? - ???????????? ?????????")
    @Test
    void updatePassword_fail() throws Exception {
		mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "11111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }
	
//	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@WithAccount("pulpury")
    @DisplayName("????????? ?????? ???")
    @Test
    void updateAccountForm() throws Exception {
		mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

//	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@WithAccount("pulpury")
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateAccount_success() throws Exception {
        String newNickname = "whiteship";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + ACCOUNT))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname("whiteship"));
    }

//	@WithUserDetails(value = "pulpury", setupBefore = TestExecutionEvent.TEST_EXECUTION)
	@WithAccount("pulpury")
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "??\\_(???)_/??";
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ACCOUNT))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

}
