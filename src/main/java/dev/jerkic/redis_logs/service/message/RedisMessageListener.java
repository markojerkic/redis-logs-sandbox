package dev.jerkic.redis_logs.service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jerkic.redis_logs.model.entity.LogLine;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {
  private final ObjectMapper objectMapper;

  @Override
  @SneakyThrows
  public void onMessage(Message msg, byte @Nullable [] pattern) {
    var json = new String(msg.getBody());
    var logLine = this.objectMapper.readValue(json, LogLine.class);
    log.info("Received log line: {}", logLine);
  }
}
