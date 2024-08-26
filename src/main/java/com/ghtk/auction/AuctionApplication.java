package com.ghtk.auction;

import com.ghtk.auction.utils.ProductDataGenerator;
import com.ghtk.auction.utils.UserDataGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionApplication.class, args);
//		ProductDataGenerator newUser = new ProductDataGenerator();
//		newUser.fakeData();
	}

}
