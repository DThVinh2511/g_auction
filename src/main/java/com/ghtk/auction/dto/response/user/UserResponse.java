package com.ghtk.auction.dto.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghtk.auction.enums.UserGender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
	
	String email;
	String password;
	Boolean isVerified;
	String fullName;

	LocalDate dateOfBirth;

	UserGender gender;

	String address;

	String avatar;

	String phone;
	
}
