package com.ghtk.auction.scheduler.jobs;


import com.ghtk.auction.dto.request.auction.AuctionUpdateStatusRequest;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.repository.AuctionRepository;
import com.ghtk.auction.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateAuctionStatus implements Job {
	
	private final AuctionService auctionService;
	private final AuctionRepository auctionRepository;
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info("Updating status of auction");

		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

		AuctionUpdateStatusRequest request = new AuctionUpdateStatusRequest();

		request.setAuctionId(jobDataMap.getLong("auctionId")) ;
		request.setAuctionStatus(AuctionStatus.valueOf(jobDataMap.getString("auctionStatus")));
		
//		Auction auction = auctionRepository.findById(request.getAuctionId()).orElseThrow(
//				() -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
//		);
		auctionService.updateStatus(request);
	}

	
}
