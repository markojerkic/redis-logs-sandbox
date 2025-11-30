package dev.jerkic.redis_logs.service;

import dev.jerkic.redis_logs.model.dto.LogLineFilter;
import dev.jerkic.redis_logs.model.entity.LogLine;
import dev.jerkic.redis_logs.repository.LogRepository;
import dev.jerkic.redis_logs.specification.LogLineSpecification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

  public Page<LogLine> getAllLogs(String appName, LogLineFilter logLineFilter) {
    var result =
        this.logRepository.findAll(
            new LogLineSpecification(appName, logLineFilter),
            PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id")));

    return result;
  }
}
