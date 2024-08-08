package com.ghtk.auction.service;

import com.ghtk.auction.dto.request.auction.AuctionUpdateStatusRequest;
import com.ghtk.auction.entity.Auction;
import org.quartz.SchedulerException;

public interface JobSchedulerService {
	
//	public void updateClosedAuctionStatus(AuctionUpdateStatusRequest request) throws SchedulerException;
//
//	public void updateInProgressAuctionStatus(AuctionUpdateStatusRequest request);
//
//	public void updateFinishedAuctionStatus(AuctionUpdateStatusRequest request);
	
//	public void updateAuctionStatus(AuctionUpdateStatusRequest request) throws SchedulerException;
	
	public void scheduleStatusUpdates(Auction auction) throws SchedulerException;
	
	public void scheduleRedisAuction(Auction auction) throws SchedulerException;
}
