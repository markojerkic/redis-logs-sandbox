package dev.jerkic.redis_logs.service.message;

import dev.jerkic.redis_logs.model.entity.LogLine;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class RedisMessageListener implements MessageListener {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onMessage(Message msg, byte @Nullable [] pattern) {
    var logLine = this.objectMapper.readValue(msg.getBody(), LogLine.class);
    log.info("Received log line: {}", logLine);
  }
}
