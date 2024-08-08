package com.ghtk.auction.controller.user;


import com.ghtk.auction.dto.request.user.UserChangePasswordRequest;
import com.ghtk.auction.dto.request.user.UserCreationRequest;
import com.ghtk.auction.dto.request.user.UserForgetPasswordRequest;
import com.ghtk.auction.dto.request.user.UserUpdateRequest;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.response.user.PageResponse;
import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.enums.UserStatus;
import com.ghtk.auction.exception.EmailException;
import com.ghtk.auction.service.UserService;
import com.ghtk.auction.service.impl.EmailServiceImpl;
import com.ghtk.auction.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
	
	@Autowired
	UserService userService;

	@Autowired
	private EmailServiceImpl emailService;

	@PostMapping("/test")
	public ResponseEntity<ApiResponse<Void>> test(@RequestParam String email, @RequestParam String otp) {
		emailService.sendOtpEmail(email, otp);
		return ResponseEntity.ok(ApiResponse.success("Sent!"));
		
	}
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserCreationRequest request) {
		return ResponseEntity.ok(ApiResponse.success(userService.createUser(request)));
		
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse<Void>> reSendOtp(@RequestParam String email) {
		userService.reSendOTP(email);
		return ResponseEntity.ok(ApiResponse.success("OTP sent!"));

	}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestParam String email,@RequestParam  String otp ) {
		if (!userService.verifyOTP(email,otp)){
			throw new EmailException("Invalid OTP or OTP expired.");
		}
		return ResponseEntity.ok(ApiResponse.success("Account verified successfully."));
	}

	@PutMapping("/forget-password")
	public ResponseEntity<ApiResponse<Void>> forgetPassword(@RequestBody UserForgetPasswordRequest request) {
		boolean result = userService.forgetPassword(request);
		return result ? ResponseEntity.ok(ApiResponse.success("Password reset successfully. Please check your email for the new password."))
				: ResponseEntity.badRequest().body(ApiResponse.error("Forget password failed. Email not found."));
	}
	
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/change-password")
	public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody UserChangePasswordRequest request) {
		boolean result = userService.updatePassword(request);
		return result ? ResponseEntity.ok(ApiResponse.success("Password changed successfully."))
				: ResponseEntity.badRequest().body(ApiResponse.error("Password change failed."));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/get-my-info")
	public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
		return ResponseEntity.ok(ApiResponse.success(userService.getMyInfo()));
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/update-my-info")
	public ResponseEntity<ApiResponse<UserResponse>> updateMyInfo(@RequestBody  UserUpdateRequest request) {
		return ResponseEntity.ok(ApiResponse.success(userService.updateMyInfo(request)));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/get-another-info/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> getAnother(
			@PathVariable Long id) {

		return ResponseEntity.ok(ApiResponse.success(userService.getAnotherInfo(id)));


	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/get-all-info")
	public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllInfo(
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
	){
		return ResponseEntity.ok(ApiResponse.success(userService.getAllInfo(pageNo, pageSize, sortBy, sortDir)));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/update-status/{id}")
	public ResponseEntity<ApiResponse<Void>> updateStatus(
			@RequestParam UserStatus status,
			@PathVariable Long id
	){
		return ResponseEntity.ok(ApiResponse.success(userService.updateStatus(status,id)));

	}
}
