package dev.jerkic.redis_logs.service.message;

import dev.jerkic.redis_logs.model.entity.LogLine;
import dev.jerkic.redis_logs.service.TemplateRenderer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.web.util.UriTemplate;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageListener extends TextWebSocketHandler implements MessageListener {
  private final ObjectMapper objectMapper;
  private final ConcurrentHashMap<WebSocketSession, String> sessionsToAppNames =
      new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Set<WebSocketSession>> appNamesToSessions =
      new ConcurrentHashMap<>();
  private final TemplateRenderer templateRenderer;
  private static final UriTemplate APP_NAME_URI_TEMPLATE = new UriTemplate("/ws/logs/{appName}");

  @Override
  @SneakyThrows
  public void onMessage(Message msg, byte @Nullable [] pattern) {
    var json = new String(msg.getBody());
    var logLine = this.objectMapper.readValue(json, LogLine.class);
    var logLineRendered =
        this.templateRenderer.swapOobRender(
            "logs::logline", "beforeend:#log-list", Map.of("logs", List.of(logLine)));
    this.appNamesToSessions
        .getOrDefault(logLine.getApp().getAppName(), Collections.emptySet())
        .forEach(
            (session) -> {
              this.sendMessage(session, logLineRendered);
            });
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    var appName = this.sessionsToAppNames.remove(session);
    this.appNamesToSessions.computeIfPresent(
        appName,
        (an, sessions) -> {
          sessions.remove(session);
          return sessions;
        });
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    var pathVars = APP_NAME_URI_TEMPLATE.match(session.getUri().toString());
    var appName = pathVars.get("appName");
    this.sessionsToAppNames.put(session, appName);
    this.appNamesToSessions.compute(
        appName,
        (an, existingSessions) -> {
          var sessions =
              existingSessions != null ? existingSessions : new HashSet<WebSocketSession>();
          sessions.add(session);
          return sessions;
        });
  }

  @SneakyThrows
  private synchronized void sendMessage(WebSocketSession session, String message) {
    if (session.isOpen()) {
      session.sendMessage(new TextMessage(message));
    }
  }
}
