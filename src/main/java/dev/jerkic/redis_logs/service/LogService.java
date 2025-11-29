package dev.jerkic.redis_logs.service;

import dev.jerkic.redis_logs.model.entity.LogLine;
import dev.jerkic.redis_logs.repository.LogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
  private final LogRepository logRepository;

  public List<String> getApps() {
    return this.logRepository.findAllApps();
  }

  public List<LogLine> getAllLogs(String appName) {
    return this.logRepository
        .findAllByApp_appName(appName, PageRequest.of(0, 1000, Sort.by("id").descending()))
        .getContent();
  }
}
