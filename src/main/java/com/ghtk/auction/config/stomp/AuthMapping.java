package com.ghtk.auction.config.stomp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.messaging.simp.stomp.StompCommand;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuthMapping {
  StompCommand command() default StompCommand.STOMP; // means all commands
  @AliasFor("path")
  String value();
  @AliasFor("value")
  String path() default "";
}