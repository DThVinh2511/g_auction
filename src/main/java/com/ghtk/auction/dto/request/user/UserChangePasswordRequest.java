package com.ghtk.auction.dto.request.user;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserChangePasswordRequest {
	
//	String email;
	
	@NotEmpty(message = "Old password is required")
	String oldPassword;
	
	@NotEmpty(message = "New password is required")
	@Size(min = 8, message = "New password must be at least 8 characters long")
	String newPassword;
}
