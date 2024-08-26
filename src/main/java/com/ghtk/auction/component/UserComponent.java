package com.ghtk.auction.component;

import com.ghtk.auction.entity.Product;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("userComponent")
@RequiredArgsConstructor
public class UserComponent {
	
	private final UserRepository userRepository;
	
	
	public boolean isBanUser(String email) {
		
		User user = userRepository.findByEmail(email);
		
		String status = user.getStatusAccount().toString();
		
		return status.equals("BAN");
	}
	
	public boolean isActiveUser(Jwt principle) {
		
		User user = userRepository.findByEmail(principle.getClaims().get("sub").toString());
		
		String status = user.getStatusAccount().toString();
		
		return status.equals("ACTIVE");
	}
	
}
