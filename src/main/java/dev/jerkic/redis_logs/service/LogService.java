package dev.jerkic.redis_logs.service;

import dev.jerkic.redis_logs.model.dto.CreatedLogLine;
import dev.jerkic.redis_logs.model.entity.Log;
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

  public CreatedLogLine createLog(String message, String level) {
    Log log = new Log();
    log.setId(UUID.randomUUID().toString());
    log.setMessage(message);
    log.setLevel(level);
    log.setTimestamp(LocalDateTime.now());
    var newLog = this.logRepository.save(log);
    var previousLogId =
        this.logRepository.findFirstByIdIsLessThan(newLog.getId()).map(Log::getId).orElse(null);

    return new CreatedLogLine(newLog, previousLogId);
  }

  public void deleteLog(String id) {
    this.logRepository.deleteById(id);
  }
}
