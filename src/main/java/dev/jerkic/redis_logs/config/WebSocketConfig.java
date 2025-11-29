package dev.jerkic.redis_logs.config;

import dev.jerkic.redis_logs.service.message.RedisMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
  private final RedisMessageListener redisMessageListener;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(this.redisMessageListener, "/ws/logs/{appName}").setAllowedOrigins("*");
  }
}
