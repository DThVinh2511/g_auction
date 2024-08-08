package com.ghtk.auction.dto.request.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

	@NotEmpty
	@Email(message = "Email is not valid")
	String email;

	@NotEmpty
	@Size(min = 8, message = "Password is not valid")
	String password;

	String fullName;

	//	@JsonFormat(pattern = "dd//MM/yyyy HH:mm:ss")
	LocalDate dateOfBirth;

	String phone;
}

