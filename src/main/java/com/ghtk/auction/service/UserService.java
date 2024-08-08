package com.ghtk.auction.service;

import com.ghtk.auction.dto.request.user.UserChangePasswordRequest;
import com.ghtk.auction.dto.request.user.UserCreationRequest;
import com.ghtk.auction.dto.request.user.UserForgetPasswordRequest;
import com.ghtk.auction.dto.request.user.UserUpdateRequest;
import com.ghtk.auction.dto.response.user.PageResponse;
import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.enums.UserStatus;

import java.util.Objects;

public interface UserService {
	
	UserResponse createUser(UserCreationRequest request);
	
	void reSendOTP(String email);
	
	boolean verifyOTP(String email, String otp);
	
	boolean forgetPassword(UserForgetPasswordRequest request);
	
	boolean updatePassword( UserChangePasswordRequest request);

	UserResponse getMyInfo() ;

	UserResponse updateMyInfo(UserUpdateRequest request);
	
	Objects getByPhoneorEmail();

	String updateStatus(UserStatus statusAccount, Long id);
	
	PageResponse<UserResponse> getAllInfo(int pageNo, int pageSize, String sortBy, String sortDir);

	UserResponse getAnotherInfo(Long id);
}