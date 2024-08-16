// package com.ghtk.auction.aspect;

// import org.aspectj.lang.annotation.AfterReturning;
// import org.aspectj.lang.annotation.Aspect;
// import org.springframework.stereotype.Component;

// import com.ghtk.auction.dto.stomp.BidMessage;
// import com.ghtk.auction.dto.stomp.CommentMessage;
// import com.ghtk.auction.dto.stomp.NotifyMessage;
// import com.ghtk.auction.service.AuctionRealtimeService;
// import com.ghtk.auction.service.StompService;

// import lombok.RequiredArgsConstructor;

// @Aspect
// @Component
// @RequiredArgsConstructor
// public class StompAspect {
//   private final StompService stompService;
//   @SuppressWarnings("unused")
//   private final AuctionRealtimeService auctionRealtimeService; // redundant, just to specify the dependency

//   @AfterReturning(pointcut = "execution("
//       + "com.ghtk.auction.dto.stomp.BidMessage"
//       + " com.ghtk.auction.service.AuctionRealtimeService.bid(Long, Long, Long))"
//       + " && args(userId, auctionId, bid)",
//       returning = "result")
//   public void afterBid(Long userId, Long auctionId, Long bid, BidMessage result) {
//     System.out.println("after bid");
//     stompService.broadcastBid(auctionId, result);
//   }

//   @AfterReturning(pointcut = "execution("
//       + "com.ghtk.auction.dto.stomp.CommentMessage"
//       + " com.ghtk.auction.service.AuctionRealtimeService.comment(Long, Long, String))"
//       + " && args(userId, auctionId, comment)",
//       returning = "result")
//   public void afterComment(Long userId, Long auctionId, String comment, CommentMessage result) {
//     System.out.println("after comment");
//     stompService.broadcastComment(auctionId, result);
//   }

//   @AfterReturning(pointcut = "execution("
//       + "com.ghtk.auction.dto.stomp.NotifyMessage"
//       + " com.ghtk.auction.service.AuctionRealtimeService.makeNotification(Long, Long, String))"
//       + " && args(userId, auctionId, message)",
//       returning = "result")
//   public void afterNotify(Long userId, Long auctionId, String message, NotifyMessage result) {
//     System.out.println("after notify");
//     stompService.broadcastNotification(auctionId, result);
//   }

//   @AfterReturning(pointcut = "execution("
//       + "void com.ghtk.auction.service.AuctionRealtimeService.startAuction(Long))"
//       + " && args(auctionId)")
//   public void afterStartAuction(Long auctionId) {
//     System.out.println("after start auction");
//     stompService.broadcastStartAuction(auctionId);
//   }

//   @AfterReturning(pointcut = "execution("
//       + "void com.ghtk.auction.service.AuctionRealtimeService.endAuction(Long))"
//       + " && args(auctionId)")
//   public void afterEndAuction(Long auctionId) {
//     System.out.println("after start auction");
//     stompService.broadcastEndAuction(auctionId);
//   }
// }
