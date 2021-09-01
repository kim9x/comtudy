package com.comtudy.settings;

import org.hibernate.validator.constraints.Length;

import com.comtudy.domain.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Profile {
	
	@Length(max = 35)
	private String bio;

	@Length(max = 50)
	private String url;

	@Length(max = 50)
	private String occupation;

	@Length(max = 50)
	private String location;
	
	private String profileImage;
}
