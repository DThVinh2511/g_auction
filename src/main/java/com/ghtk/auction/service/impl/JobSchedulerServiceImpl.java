package com.ghtk.auction.service.impl;

import com.ghtk.auction.dto.request.auction.AuctionUpdateStatusRequest;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.repository.AuctionRepository;
import com.ghtk.auction.scheduler.jobs.RedisActiveAuction;
import com.ghtk.auction.scheduler.jobs.RedisOpenAuction;
import com.ghtk.auction.scheduler.jobs.UpdateAuctionStatus;
import com.ghtk.auction.service.JobSchedulerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobSchedulerServiceImpl implements JobSchedulerService {
	
	final Scheduler scheduler;
	final AuctionRepository auctionRepository;
	
	@Override
	public void scheduleStatusUpdates(Auction auction) throws SchedulerException {
		// Lên lịch chuyển sang trạng thái CLOSED
		scheduleStatusUpdate(auction, AuctionStatus.CLOSED, auction.getEndRegistration());
		
		// Lên lịch chuyển sang trạng thái IN_PROGRESS
		scheduleStatusUpdate(auction, AuctionStatus.IN_PROGRESS, auction.getStartTime());
		
		// Lên lịch chuyển sang trạng thái FINISHED
		scheduleStatusUpdate(auction, AuctionStatus.FINISHED, auction.getEndTime());
	}
	@Override
	public void scheduleRedisAuction(Auction auction) throws SchedulerException {
		LocalDateTime time = auction.getStartTime().minusMinutes(10);
		
		// Lên lịch thêm vào redis OpenAuction : preparing
		scheduleRedisAuction(auction,time);
		
		// Lên lịch thêm vào redis thông tin Auction khi bắt đầu start
		scheduleRedisActiveAuction(auction,auction.getStartTime());
	}
	private void scheduleStatusUpdate(Auction auction, AuctionStatus newStatus, LocalDateTime scheduledTime) throws SchedulerException {
		AuctionUpdateStatusRequest request = new AuctionUpdateStatusRequest();
		request.setAuctionId(auction.getId());
		request.setAuctionStatus(newStatus);
		
		JobDetail job = buildAuctionJobDetail(UpdateAuctionStatus.class, request);
		Trigger trigger = buildAuctionJobTrigger(UpdateAuctionStatus.class, request, scheduledTime);
		
		scheduler.scheduleJob(job, trigger);
	}
	
	private  void scheduleRedisAuction(Auction auction, LocalDateTime scheduledTime) throws SchedulerException {
		Long auctionId = auction.getId();
		JobDetail job = buildRedisAuctionJobDetail(RedisOpenAuction.class,auctionId);
		Trigger trigger = buildRedisAuctionJobTrigger(RedisOpenAuction.class,auctionId,scheduledTime);
		
		scheduler.scheduleJob(job, trigger);
	}
	
	private void scheduleRedisActiveAuction(Auction auction, LocalDateTime scheduledTime) throws SchedulerException {
		Long auctionId = auction.getId();
		JobDetail job = buildRedisAuctionJobDetail(RedisActiveAuction.class,auctionId);
		Trigger trigger = buildRedisAuctionJobTrigger(RedisActiveAuction.class,auctionId,scheduledTime);
		
		scheduler.scheduleJob(job, trigger);
	}
	
	private JobDetail buildAuctionJobDetail(Class className, AuctionUpdateStatusRequest request) {
		return JobBuilder.newJob(className)
				.withIdentity(className.getName() + "-" + request.getAuctionId()+ "-" + request.getAuctionStatus(), "auction-status-jobs")
				.usingJobData("auctionId", request.getAuctionId())
				.usingJobData("auctionStatus", String.valueOf(request.getAuctionStatus()))
				.storeDurably(true)
				.build();
	}
	
	private JobDetail buildRedisAuctionJobDetail(Class className,Long auctionId) {
		return JobBuilder.newJob(className)
				.withIdentity(className.getName() + "-" + auctionId, "auction-redis-jobs")
				.usingJobData("auctionId", String.valueOf(auctionId))
				.storeDurably(true)
				.build();
	}
	
	private Trigger buildAuctionJobTrigger(Class className, AuctionUpdateStatusRequest request, LocalDateTime scheduledTime) {
		ZoneId zoneId = ZoneId.systemDefault();
		Date initTime = Date.from(scheduledTime.atZone(zoneId).toInstant());
		return TriggerBuilder.newTrigger()
				.withIdentity(className.getName() + "-" + request.getAuctionId() + "-" + request.getAuctionStatus(), "auction-trigger-jobs")
				.startAt(initTime)
				.build();
	}
	
	private Trigger buildRedisAuctionJobTrigger(Class className, Long auctionId, LocalDateTime scheduledTime) {
		ZoneId zoneId = ZoneId.systemDefault();
		Date initTime = Date.from(scheduledTime.atZone(zoneId).toInstant());
		return TriggerBuilder.newTrigger()
				.withIdentity(className.getName() + "-" + auctionId, "auction-trigger-redis-jobs")
				.startAt(initTime)
				.build();
	}
	
	

//	private JobDetail buildAuctionJobDetail(Class className, AuctionUpdateStatusRequest request) {
//		return newJob(className)
//				.withIdentity(className.getName(),"auction-jobs")
//				.usingJobData("auctionId", request.getAuctionId())
//				.usingJobData("auctionStatus", String.valueOf(request.getAuctionStatus()))
//				.storeDurably(true)
//				.build();
//	}
//
//	private Trigger buildAuctionJobTrigger(Class className, AuctionUpdateStatusRequest request) {
//		Auction auction = auctionRepository.findById(request.getAuctionId()).orElseThrow(
//				() -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
//		);
//
//		if (auction.getStatus().equals(AuctionStatus.PENDING)
//				|| auction.getStatus().equals(AuctionStatus.FINISHED)
//				|| auction.getStatus().equals(AuctionStatus.CANCELED)) {
//			throw new RuntimeException("Trang thai dau vao khong hop le");
//		}
//		LocalDateTime date = null;
//		if (auction.getStatus().equals(AuctionStatus.OPENING)) {
//			 date = auction.getEndRegistration();
//		}
//		if (auction.getStatus().equals(AuctionStatus.CLOSED)) {
//			 date = auction.getStartTime();
//		}
//		if (auction.getStatus().equals(AuctionStatus.IN_PROGRESS)) {
//			 date = auction.getEndTime();
//		}
//		if (date.isBefore(LocalDateTime.now())) {
//			throw new RuntimeException("Yeu cau khong hop le");
//		}
//
//		ZoneId zoneId = ZoneId.systemDefault();
//		Date initTime = Date.from(date.atZone(zoneId).toInstant());
//		return newTrigger()
//				.withIdentity(className.getName(),"auction-trigger-jobs")
//				.startAt(initTime)
//				.build();
//	}
}
