package com.ghtk.auction.service.impl;

import com.ghtk.auction.dto.request.user.UserChangePasswordRequest;
import com.ghtk.auction.dto.request.user.UserCreationRequest;
import com.ghtk.auction.dto.request.user.UserForgetPasswordRequest;
import com.ghtk.auction.dto.request.user.UserUpdateRequest;
import com.ghtk.auction.dto.response.user.PageResponse;
import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.enums.UserGender;
import com.ghtk.auction.enums.UserRole;
import com.ghtk.auction.enums.UserStatus;
import com.ghtk.auction.exception.AlreadyExistsException;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.mapper.UserMapper;
import com.ghtk.auction.repository.UserRepository;
import com.ghtk.auction.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	final UserRepository userRepository;
	
	final PasswordEncoder passwordEncoder;
	
	final EmailServiceImpl emailService;
	
	final RedisTemplate<String, String> redisTemplate;
	
	final UserMapper userMapper;
	
	final static String DEFAULT_PASSWORD = "jack97deptrai" ;
	
	@Override
	public UserResponse createUser(UserCreationRequest request) {
		
		//check username
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new AlreadyExistsException("Email has been used!");
		}
		
		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setDateOfBirth(request.getDateOfBirth());
		user.setFullName(request.getFullName());
		user.setCreatedAt(LocalDateTime.now());
		user.setIsVerified(false);
		user.setPhone(request.getPhone());
		user.setStatusAccount(UserStatus.ACTIVE);
		user.setRole(UserRole.USER);
		userRepository.save(user);
		
		sendOtp(user.getEmail());
		
		return UserResponse.builder()
				.email(request.getEmail())
				.password(request.getPassword())
				.isVerified(false)
				.build();
	}
	
	@Override
	public void reSendOTP(String email) {
		sendOtp(email);
	}
	
	
	@Override
	public boolean verifyOTP(String email, String otp) {
//		User user = userRepository.findByEmail(email);
//		if(user == null) {
//			throw new UsernameNotFoundException("User not found!");
//		} else if (user.getIsVerified()) {
//				throw new RuntimeException("Account is verified!");
//		}
		
		// Retrieve OTP from Redis
		String redisOtp = (String) redisTemplate.opsForValue().get(email);
		if (otp.equals(redisOtp)) {
		// Verify the user account if OTP is correct

			User user = userRepository.findByEmail(email);
			user.setIsVerified(true);

		//update on db
			userRepository.save(user);

		//remove OTP
			redisTemplate.delete(email);

			return true;
		}
		return false;
	}

	@Override
	public boolean forgetPassword(UserForgetPasswordRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			User user = userRepository.findByEmail(request.getEmail());
			String newPassword = generateRandomPassword(8);
			user.setPassword(passwordEncoder.encode(newPassword));
			//update on db
			userRepository.save(user);
			
			//send email
			emailService.sendDefaultPassword(user.getEmail(), newPassword);
			return true;
		}
		else{
			throw new AlreadyExistsException("user with " + request.getEmail() + " not exists!");
		}
	
	}
	
	
	@Override
	public boolean updatePassword(UserChangePasswordRequest request) {
		var context = SecurityContextHolder.getContext();
		String email = context.getAuthentication().getName();
		
		User user = userRepository.findByEmail(email);
		if(passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
			userRepository.save(user);
			return true;
		}
		else {
			return false;
		}
		
	}
	
	@Override
	public UserResponse getMyInfo() {
		Authentication context = SecurityContextHolder.getContext().getAuthentication();
		String email = context.getName();
		
//		Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String subject = jwt.getSubject();
//		log.info(subject);
//		Map<String, Object> claims = jwt.getClaims();
//		log.info("claims: {}", claims);

		User user = userRepository.findByEmail(email);
		return userMapper.toUserResponse(user);
	}
	
	
	@Override
	public UserResponse updateMyInfo(UserUpdateRequest request) {
		var context = SecurityContextHolder.getContext();
		String email = context.getAuthentication().getName();
		
		User user = userRepository.findByEmail(email);
		if(request.getFullName() != null){
			user.setFullName(request.getFullName());
		}
		if (request.getDateOfBirth() != null){
			user.setDateOfBirth(request.getDateOfBirth());
		}
		if (request.getGender() != null) {
			if (request.getGender() == UserGender.MALE){
				user.setGender(UserGender.MALE);
			} else if (request.getGender() == UserGender.FEMALE){
				user.setGender(UserGender.FEMALE);
			}
		}
		if (request.getAddress() != null) {
			user.setAddress(request.getAddress());
		}
		if (request.getAvatar() != null) {
			user.setAvatar(request.getAvatar());
		}
		if (request.getPhone() != null) {
			user.setPhone(request.getPhone());
		}

		userRepository.save(user);

		return userMapper.toUserResponse(user);
	}
	
	@Override
	public String updateStatus(UserStatus statusAccount,Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User not found!"));
		user.setStatusAccount(statusAccount);
		return "update status account successfully!";
	}
	
	@Override
	public PageResponse<UserResponse> getAllInfo(int pageNo, int pageSize, String sortBy, String sortDir) {
		Sort sort =sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

		Pageable pageable= PageRequest.of(pageNo,pageSize,sort);

		Page<User> users =userRepository.findAll(pageable);

		List<User> listOfUser =users.getContent();

		List<UserResponse> content =listOfUser.stream().map(userMapper::toUserResponse).toList();

		PageResponse<UserResponse> pageUserResponse = new PageResponse<>();
		pageUserResponse.setPageNo(pageNo);
		pageUserResponse.setPageSize(pageSize);
		pageUserResponse.setTotalPages(users.getTotalPages());
		pageUserResponse.setTotalElements(users.getTotalElements());
		pageUserResponse.setLast(users.isLast());
		pageUserResponse.setContent(content);
		return pageUserResponse;
	}
	
	@Override
	public Objects getByPhoneorEmail() {
		return null;
	}


	@Override
	public UserResponse getAnotherInfo(Long id) {
		User user =userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User not found!"));

		return UserResponse.builder()
				.fullName(user.getFullName())
				.dateOfBirth(user.getDateOfBirth())
				.gender(user.getGender())
				.avatar(user.getAvatar())
				.build();
	}
	
	private String generateOTP() {
		//return String.valueOf((int) (Math.random() * 900000) + 100000);
		return  String.format("%06d", new Random().nextInt(999999));
	}
	private String generateRandomPassword(int num) {
		return RandomStringUtils.randomAlphanumeric(num);
	}
	
	private void sendOtp(String email) {
		// gen OTP and save to Redis
		String otpSent = generateOTP();
		redisTemplate.opsForValue().set(email,otpSent , Duration.ofMinutes(5));
		
		//send OTP
		emailService.sendOtpEmail(email, otpSent);
	}
	
}
