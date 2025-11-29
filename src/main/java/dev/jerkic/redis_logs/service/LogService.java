package dev.jerkic.redis_logs.service;

import dev.jerkic.redis_logs.model.dto.CreatedLogLine;
import dev.jerkic.redis_logs.model.entity.LogLine;
import dev.jerkic.redis_logs.repository.LogRepository;
import dev.jerkic.redis_logs.service.message.RedisMessagePublisher;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
  private final LogRepository logRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final RedisMessagePublisher redisMessagePublisher;

  public List<LogLine> getAllLogs() {
    return this.logRepository.findAll();
  }

  public CreatedLogLine createLog(String message, String level) {
    String previousLogId = null;

    LogLine log = new LogLine();
    log.setMessage(message);
    log.setLevel(level);
    var timestamp = LocalDateTime.now();
    log.setTimestamp(timestamp);

    var newLog = this.logRepository.save(log);
    this.redisMessagePublisher.publishLog(newLog);

    return new CreatedLogLine(newLog, previousLogId);
  }
}
