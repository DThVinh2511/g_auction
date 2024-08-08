package com.ghtk.auction.dto.request.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ghtk.auction.enums.UserGender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserUpdateRequest {

	String fullName;
	
	LocalDate dateOfBirth;
	
	UserGender gender;
	
	String address;
	
	String avatar;
	
	String phone;
	
}
