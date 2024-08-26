package com.ghtk.auction.component;

import com.ghtk.auction.dto.redis.AuctionRoom;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.entity.UserAuction;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.repository.AuctionRepository;
import com.ghtk.auction.repository.UserAuctionRepository;
import com.ghtk.auction.repository.UserRepository;
import com.ghtk.auction.repository.Custom.AuctionSessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component("auctionComponent")
@RequiredArgsConstructor
public class AuctionComponent {
    private final AuthenticationComponent authenticationComponent;  

    private final AuctionRepository auctionRepository;
    private final UserAuctionRepository userAuctionRepository;
    private final UserRepository userRepository;
    private final AuctionSessionRepository auctionSessionRepository;
    
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
        boolean result = userAuctionRepository.existsByUserAndAuction(user,auction);
        return result;
    }

    public boolean canParticipateAuction(Long auctionId, Jwt principal) {
        if (!authenticationComponent.isUserValid(principal)) {
            return false;
        }
        Long userId = (Long)principal.getClaims().get("id");
        Optional<AuctionRoom> room = auctionSessionRepository.getAuctionRoom(auctionId);
        if (room.isPresent()) {
            return auctionSessionRepository.existsJoinable(auctionId, userId);
        } else {
          try {
            UserAuction ua = userAuctionRepository.findByUserIdAndAuctionId(userId, auctionId);
            return ua != null;
          } catch (Exception e) {
            return false;
          } 
        }
    }
}


