package dev.jerkic.redis_logs.service.message;

import dev.jerkic.redis_logs.model.entity.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {
  private final RedisTemplate<String, Log> redisTemplate;

  public void publishLog(Log logLine) {
    this.redisTemplate.convertAndSend("logs", logLine);
  }
}
