package com.ghtk.auction.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.exception.ExpiredTokenException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
//	@Override
//	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//		response.addHeader(
//				HttpHeaders.WWW_AUTHENTICATE,
//				"Bearer error=\"Invalid access token\""
//		);
//
//		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//
//	}
	@Override
	public void commence(
			HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ApiResponse<?> apiResponse;
		OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException) authException;
		OAuth2Error error = oauth2Exception.getError();

		// Kiểm tra thông tin lỗi từ OAuth2Error
		if ("token_expired".equals(error.getErrorCode())) {
			apiResponse = ApiResponse.builder()
					.success(false)
					.message("ExpiredToken")
					.build();
		} else {
			apiResponse = ApiResponse.builder()
					.success(false)
					.message("Unauthorized")
					.build();
		}

		ObjectMapper objectMapper = new ObjectMapper();
		
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
		response.flushBuffer();
	}
	
}