package com.ghtk.auction.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"com.ghtk.auction.service", "com.ghtk.auction.aspect"})
@EnableAspectJAutoProxy
public class AopConfig {
  
}
