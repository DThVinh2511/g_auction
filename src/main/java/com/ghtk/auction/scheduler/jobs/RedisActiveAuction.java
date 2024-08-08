package com.ghtk.auction.scheduler.jobs;


import com.ghtk.auction.service.AuctionRealtimeService;
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
public class RedisActiveAuction implements Job {
	
	private final AuctionRealtimeService auctionRealtimeService;
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		Long auctionId = jobDataMap.getLong("auctionId");
		auctionRealtimeService.startAuction(auctionId);
	}

	
}
