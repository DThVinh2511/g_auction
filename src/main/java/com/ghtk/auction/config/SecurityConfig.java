package com.ghtk.auction.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	
	@Autowired
	private CustomJwtDecoder customJwtDecoder;

	private final String[] PUBLIC_POST_ENDPOINTS =
			{"v1/users/test","v1/users/","v1/users/verify-otp"
					, "v1/users/resend-otp"
					,"v1/auths/authenticate","v1/auths/introspect","v1/auths/logout","v1/auths/refresh"};
	private  final String[] PUBLIC_PUT_ENDPOINTS =
			{"v1/users/forget-password"};
	private static final String[] PUBLIC_GET_ENDPOINTS = {"v1/products/**"};
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.authorizeHttpRequests(req ->
						req.requestMatchers(HttpMethod.POST , PUBLIC_POST_ENDPOINTS).permitAll()
								.requestMatchers(HttpMethod.PUT , PUBLIC_PUT_ENDPOINTS).permitAll()
								.requestMatchers(HttpMethod.GET , PUBLIC_GET_ENDPOINTS).permitAll()
								.anyRequest().authenticated()
				);
		httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
						.decoder(customJwtDecoder)
						.jwtAuthenticationConverter(jwtAuthenticationConverter()))
				.authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
		
		httpSecurity.csrf(AbstractHttpConfigurer::disable);
		
		return httpSecurity.build();
	}
	
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedHeader("*");
		
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}
	
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
		
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		
		return jwtAuthenticationConverter;
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
}
