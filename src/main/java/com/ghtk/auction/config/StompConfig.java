package com.ghtk.auction.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {
    private final String stompEndpoint; 
    private final String[] allowedOrigins;


    public StompConfig(
        @Value("${websocket.endpoint}") String stompEndpoint, 
        @Value("${alloworigins}") String allowedOrigins) {
      this.stompEndpoint = stompEndpoint;
      this.allowedOrigins = new String[]{allowedOrigins};
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
      registry.addEndpoint(stompEndpoint)
              .setAllowedOrigins(allowedOrigins)
              .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
      registry.enableSimpleBroker("/topic", "/user");
      registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
      messageConverters.add(new MappingJackson2MessageConverter());
      return true;
    }

    // @Override
    // public void configureClientInboundChannel(ChannelRegistration registration) {
    //   registration.interceptors(new Interceptor());
    // }

    
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
      var authBuilder = new MessageMatcherDelegatingAuthorizationManager.Builder();
      authBuilder.nullDestMatcher().authenticated() 
                  .simpSubscribeDestMatchers("/topic/errors").permitAll() 
                  .simpSubscribeDestMatchers("/user/*/queue/control").authenticated() 
                  .simpSubscribeDestMatchers("/user/*/queue/notification").authenticated() 
                  .simpSubscribeDestMatchers("/topic/auction/*/control").authenticated()
                  .simpSubscribeDestMatchers("/topic/auction/*/notification").authenticated()
                  .simpSubscribeDestMatchers("/topic/auction/*/bid").authenticated()
                  .simpSubscribeDestMatchers("/topic/auction/*/comment").authenticated()
                  .simpDestMatchers("/app/auction/**").hasRole("USER")
                  //.simpTypeMatchers(MessageType.MESSAGE).denyAll()
                  .anyMessage().denyAll(); 

      return authBuilder.build();
    }

    // public static class MyChannelInterceptor implements ChannelInterceptor {

    //   @Override
    //   public Message<?> preSend(Message<?> message, MessageChannel channel) {
    //     StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    //     StompCommand command = accessor.getStompCommand();
    //     
    //     return message;
    //   }
    // }
    
}
