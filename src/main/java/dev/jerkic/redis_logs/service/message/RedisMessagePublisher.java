package dev.jerkic.redis_logs.service.message;

import dev.jerkic.redis_logs.model.entity.LogLine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {
  private final RedisTemplate<String, LogLine> redisTemplate;

  public void publishLog(LogLine logLine) {
    this.redisTemplate.convertAndSend("logs", logLine);
  }
}
