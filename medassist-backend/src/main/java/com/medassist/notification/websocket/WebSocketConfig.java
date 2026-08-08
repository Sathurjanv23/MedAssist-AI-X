package com.medassist.notification.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket + STOMP configuration for real-time notifications.
 *
 * <p>Protocol: STOMP over WebSocket (SockJS fallback for browsers without WS support)
 * <p>Topics:
 *   /topic/notifications/{userId}  â€” user-specific notifications
 *   /topic/ai-progress/{reportId} â€” AI processing progress
 *   /topic/ocr-progress/{reportId}â€” OCR progress
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // In-memory message broker for topic broadcasting
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefix for client â†’ server messages
        registry.setApplicationDestinationPrefixes("/app");
        // User-specific destinations prefix
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                    "http://localhost:3000",
                    "http://localhost:3001",
                    "https://*.medassist.ai",
                    "https://*.vercel.app"
                )
                .withSockJS(); // SockJS fallback for browsers
    }
}

