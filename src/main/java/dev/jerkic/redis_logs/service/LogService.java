package dev.jerkic.redis_logs.service;

import dev.jerkic.redis_logs.model.Log;
import dev.jerkic.redis_logs.repository.LogRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
  private final LogRepository logRepository;

  public List<Log> getAllLogs() {
    return (List<Log>) this.logRepository.findAll();
  }

  public Log createLog(String message, String level) {
    Log log = new Log();
    log.setId(UUID.randomUUID().toString());
    log.setMessage(message);
    log.setLevel(level);
    log.setTimestamp(LocalDateTime.now());
    return this.logRepository.save(log);
  }

  public void deleteLog(String id) {
    this.logRepository.deleteById(id);
  }
}
