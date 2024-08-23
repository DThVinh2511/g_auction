package com.ghtk.auction.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ghtk.auction.enums.UserGender;
import com.ghtk.auction.enums.UserStatus;
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
	Boolean isVerified;
	String fullName;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate dateOfBirth;

	UserGender gender;

	String address;

	String avatar;

	String phone;
	
	UserStatus statusAccount;
	
	Long id;
	
	
}
