package com.cb.dicegame.config;

import static com.cb.dicegame.IDiceGameConstants.SOCKET_MESSAGE_PREFIX;
import static com.cb.dicegame.IDiceGameConstants.SOCKET_URL_ENDPOINT;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configures the web socket endpoint and message broker
 * see https://docs.spring.io/spring/docs/5.2.1.RELEASE/spring-framework-reference/web.html#websocket-stomp-enable
 */
@Configuration
@EnableWebSocketMessageBroker
public class DiceGameSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(SOCKET_URL_ENDPOINT).withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// this routes messages to @MessageMapping methods in @Controller classes
		config.setApplicationDestinationPrefixes(SOCKET_MESSAGE_PREFIX);

		// this is the built in message broker
		// topic is for subscriptions (pub-sub), queue is for point to point
		config.enableSimpleBroker("/topic", "/queue");
	}

}
