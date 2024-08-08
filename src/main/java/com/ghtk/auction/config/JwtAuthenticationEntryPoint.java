package com.ghtk.auction.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghtk.auction.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
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
		
		ApiResponse<?> apiResponse = ApiResponse.builder()
				.success(false)
				.message("Unauthorized")
				.build();
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
		response.flushBuffer();
	}
	
}