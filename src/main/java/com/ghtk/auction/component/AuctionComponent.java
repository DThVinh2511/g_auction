package com.ghtk.auction.component;

import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.repository.AuctionRepository;
import com.ghtk.auction.repository.UserAuctionRepository;
import com.ghtk.auction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("auctionComponent")
@RequiredArgsConstructor
public class AuctionComponent {
    private final AuthenticationComponent authenticationComponent;  

    private final AuctionRepository auctionRepository;
    private final UserAuctionRepository userAuctionRepository;
    private final UserRepository userRepository;
    
    public boolean isAuctionOwner(Long auctionId, Jwt principal) {
        Long userId = (Long)principal.getClaims().get("id");
//		return productRepository.findById(productId)
//				.map(product -> product.getOwnerId().equals(userId))
//				.orElse(false);
        
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
              () -> new NotFoundException("Auction not found")
        );
        return  userId.equals(auction.getProduct().getOwnerId());
    }
    
    public boolean isAuctionOpening(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
              () -> new NotFoundException("Auction not found")
        );
        return (auction.getStatus().equals(AuctionStatus.OPENING));
    }
    
    public boolean isRegisteredAuction(Long auctionId, Jwt principal) {
        if (!authenticationComponent.isUserValid(principal)) {
            return false;
        }
        Long userId = (Long)principal.getClaims().get("id");
        User user = userRepository.findById(userId).orElseThrow(
              () -> new NotFoundException("User not found")
        );
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
              () -> new NotFoundException("Auction not found")
        );
        return userAuctionRepository.existsByUserAndAuction(user,auction);
    }

    public boolean canParticipateAuction(Long auctionId, Jwt principal) {
        if (!authenticationComponent.isUserValid(principal)) {
            return false;
        }
        Long userId = (Long)principal.getClaims().get("id");
        User user = userRepository.findById(userId).orElseThrow(
              () -> new NotFoundException("User not found")
        );
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
              () -> new NotFoundException("Auction not found")
        );
        return auction.getStatus() == AuctionStatus.IN_PROGRESS 
            && userAuctionRepository.existsByUserAndAuction(user, auction);
    }
}


