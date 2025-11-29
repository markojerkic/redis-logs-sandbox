package dev.jerkic.redis_logs.service.message;

import dev.jerkic.redis_logs.model.entity.LogLine;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  @SneakyThrows
  public void publishLog(LogLine logLine) {
    var json = objectMapper.writeValueAsString(logLine);
    this.redisTemplate.convertAndSend("logs", json);
  }
}
