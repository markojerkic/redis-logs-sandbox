package dev.jerkic.redis_logs.service.message;

import dev.jerkic.redis_logs.model.entity.LogLine;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageListener extends TextWebSocketHandler implements MessageListener {
  private final ObjectMapper objectMapper;
  private final ConcurrentHashMap<WebSocketSession, Boolean> sessions = new ConcurrentHashMap<>();

  @Override
  @SneakyThrows
  public void onMessage(Message msg, byte @Nullable [] pattern) {
    var json = new String(msg.getBody());
    var logLine = this.objectMapper.readValue(json, LogLine.class);
    log.info("Received log line: {}", logLine);
    this.sessions.forEach(
        (session, isOpen) -> {
          this.sendMessage(session, json);
        });
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    this.sessions.remove(session);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    this.sessions.put(session, true);
  }

  @SneakyThrows
  private void sendMessage(WebSocketSession session, String message) {
    session.sendMessage(new TextMessage(message));
  }
}
