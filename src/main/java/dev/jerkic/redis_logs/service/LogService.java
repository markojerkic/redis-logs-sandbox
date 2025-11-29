package dev.jerkic.redis_logs.service;

import dev.jerkic.redis_logs.model.dto.CreatedLogLine;
import dev.jerkic.redis_logs.model.entity.LogLine;
import dev.jerkic.redis_logs.repository.LogRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
  private final LogRepository logRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private static final String LOG_SORTED_SET_KEY = "logs:sorted";

  public List<LogLine> getAllLogs() {
    return this.logRepository.findAll();
  }

  public CreatedLogLine createLog(String message, String level) {
    // Get the last log ID before creating new one
    String previousLogId = getLastLogId();

    LogLine log = new LogLine();
    log.setMessage(message);
    log.setLevel(level);
    LocalDateTime timestamp = LocalDateTime.now();
    log.setTimestamp(timestamp);

    var newLog = this.logRepository.save(log);

    // Add to sorted set with timestamp as score for ordering
    double score = timestamp.toEpochSecond(ZoneOffset.UTC);
    redisTemplate.opsForZSet().add(LOG_SORTED_SET_KEY, newLog.getId().toString(), score);

    return new CreatedLogLine(newLog, previousLogId);
  }

  private String getLastLogId() {
    ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

    // Get the most recent log (highest score)
    Set<String> lastLog = zSetOps.reverseRange(LOG_SORTED_SET_KEY, 0, 0);

    if (lastLog != null && !lastLog.isEmpty()) {
      return lastLog.iterator().next();
    }

    return null;
  }

  public void deleteLog(Long id) {
    this.logRepository.deleteById(id);
    redisTemplate.opsForZSet().remove(LOG_SORTED_SET_KEY, id.toString());
  }
}
