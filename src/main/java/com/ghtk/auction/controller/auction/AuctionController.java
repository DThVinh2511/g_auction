package com.ghtk.auction.controller.auction;


import com.ghtk.auction.dto.request.auction.AuctionCreationRequest;
import com.ghtk.auction.dto.request.comment.CommentFilter;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.response.auction.AuctionCreationResponse;
import com.ghtk.auction.dto.response.auction.AuctionJoinResponse;
import com.ghtk.auction.dto.response.auction.AuctionListResponse;
import com.ghtk.auction.dto.response.auction.AuctionResponse;
import com.ghtk.auction.dto.response.user.PageResponse;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.UserAuction;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.scheduler.jobs.UpdateAuctionStatus;
import com.ghtk.auction.service.AuctionService;
import com.ghtk.auction.service.JobSchedulerService;
import com.ghtk.auction.service.AuctionRealtimeService;
import com.ghtk.auction.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/auctions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuctionController {
	
	final AuctionService auctionService;
	final JobSchedulerService jobSchedulerService;
	final UpdateAuctionStatus updateAuctionStatus;
  final AuctionRealtimeService auctionRealtimeService;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse<AuctionCreationResponse>> createAuction(
			@AuthenticationPrincipal Jwt jwt,
			@RequestBody @Valid AuctionCreationRequest auctionCreationRequest) {
		return ResponseEntity.ok(ApiResponse.success(auctionService.addAuction(jwt, auctionCreationRequest)));
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/get-my-created")
	public ResponseEntity<ApiResponse<List<AuctionResponse>>> getMyCreatedAuctions(@AuthenticationPrincipal Jwt jwt) {
		return ResponseEntity.ok(ApiResponse.success(auctionService.getMyCreatedAuction(jwt)));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Auction>> getAuctionById(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(auctionService.getAuctionById(id)));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Auction>> deleteAuction(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(auctionService.deleteAuction(jwt,id)));
	}
	
	@GetMapping("/get-my-registered")
	public ResponseEntity<ApiResponse<List<Auction>>> getMyRegisteredAuction(@AuthenticationPrincipal Jwt jwt) {
		return ResponseEntity.ok(ApiResponse.success(auctionService.getMyRegisteredAuction(jwt)));
	}
	
	@GetMapping("/get-my-joined")
	public ResponseEntity<ApiResponse<List<Auction>>> getMyJoined(@AuthenticationPrincipal Jwt jwt) {
		return ResponseEntity.ok(ApiResponse.success(auctionService.getMyJoinedAuction(jwt)));
	}
	
	@PostMapping("/{id}/regis-join")
	public ResponseEntity<ApiResponse<String>> regisJoinAuction(
			@AuthenticationPrincipal Jwt jwt,
			@PathVariable Long id
	) {
		return ResponseEntity.ok(ApiResponse.success(auctionService.registerJoinAuction(jwt, id)));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/confirm/{id}")
	public ResponseEntity<ApiResponse<Auction>> confirmAuction(@PathVariable Long id) throws SchedulerException {
		return ResponseEntity.ok(ApiResponse.success(auctionService.confirmAuction(id)));
	}
	
//	@PreAuthorize("hasRole('ADMIN')")
//	@PostMapping("/update-status")
//	public ResponseEntity<ApiResponse<Auction>> updateAuction(@RequestBody AuctionUpdateStatusRequest request) throws SchedulerException {
//		jobSchedulerService.updateAuctionStatus(request);
//		return ResponseEntity.ok(ApiResponse.ok("Update trang thai thanh cong"));
//	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/reject/{id}")
	public ResponseEntity<ApiResponse<?>> rejectAuction(@PathVariable Long id) {
		auctionService.rejectAuction(id);
		return  ResponseEntity.ok(ApiResponse.ok("Tu choi phien dau gia nay"));
	}

  @GetMapping("/active")
  @PreAuthorize("isAuthenticated()")
  public ApiResponse<List<AuctionResponse>> getRegisActiveAuctions(
    @AuthenticationPrincipal Jwt jwt
  ) {
    return ApiResponse.success(auctionService.getRegisActiveAuctions(jwt));
  }

  @GetMapping("/joinable")
  @PreAuthorize("isAuthenticated()")
  public ApiResponse<List<Auction>> getJoinableAuctions(
      @AuthenticationPrincipal Jwt jwt) {
    Long userId = (Long) jwt.getClaims().get("id");
    return ApiResponse.success(auctionRealtimeService.getJoinableNotis(userId));
  }

  @PostMapping("/{auctionId}/join")
  public ApiResponse<AuctionJoinResponse> joinAuction(
      @PathVariable Long auctionId,
      @AuthenticationPrincipal Jwt jwt
  ) {
    Long userId = (Long) jwt.getClaims().get("id");
    var response = auctionRealtimeService.joinAuction(userId, auctionId);
    return ApiResponse.success(response);
  }

  @PostMapping("/{auctionId}/leave")
  public ApiResponse<Void> leaveAuction(
      @PathVariable Long auctionId,
      @AuthenticationPrincipal Jwt jwt
  ) {
    Long userId = (Long) jwt.getClaims().get("id");
    auctionRealtimeService.leaveAuction(userId, auctionId);
    return ApiResponse.success(null);
  }

  // @GetMapping("/{id}/current-price")
  // public ApiResponse<Long> getCurrentPrice(
  //     @PathVariable Long auctionId,
  //     @AuthenticationPrincipal Jwt jwt
  // ) {
  //   return ApiResponse.success(auctionService.getCurrentPrice(jwt, auctionId));
  // }

  // @GetMapping("/{id}/bids")
  // @PreAuthorize("@auctionComponent.canParticipateAuction(#auctionId, principal)")
  // public ApiResponse<List<BidResponse>> getBids(
  //     @PathVariable Long auctionId,
  //     BidFilter filter,
  //     @AuthenticationPrincipal Jwt jwt
  // ) {
  //   return ApiResponse.success(auctionService.getBids(jwt, auctionId, filter));
  // }
  
  @GetMapping("/{auctionId}/comments")
  @PreAuthorize("@auctionComponent.canParticipateAuction(#auctionId, principal)")
  public ApiResponse<List<CommentMessage>> getComments(
        @PathVariable("auctionId") Long auctionId, 
        CommentFilter filter,
        @AuthenticationPrincipal Jwt principal) {
    Long userId = (Long) principal.getClaim("id");
    return ApiResponse.success(auctionRealtimeService.getComments(userId, auctionId, filter));
  }
	@GetMapping("/get-all-auction")
	public ResponseEntity<ApiResponse<PageResponse<AuctionListResponse>>> getAllAuction(
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize
	){
		return ResponseEntity.ok(ApiResponse.success(auctionService.getAllList(pageNo, pageSize)));
	}
	
	@GetMapping("/get-all-auction-by-status")
	public ResponseEntity<ApiResponse<PageResponse<AuctionListResponse>>> getAllAuctionByStatus(
			@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
			@RequestParam(value ="statusAuction") AuctionStatus status
	){
		return ResponseEntity.ok(ApiResponse.success(auctionService.getAllAuctionByStatus(status,pageNo, pageSize)));
	}
}
