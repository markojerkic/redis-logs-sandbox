package dev.jerkic.redis_logs.service;

import dev.jerkic.redis_logs.model.dto.LogLineFilter;
import dev.jerkic.redis_logs.model.dto.LogsViewData;
import dev.jerkic.redis_logs.repository.LogRepository;
import dev.jerkic.redis_logs.specification.LogLineSpecification;
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

  public LogsViewData getLogsViewData(String appName, LogLineFilter logLineFilter) {
    var logs =
        this.logRepository.findAll(
            new LogLineSpecification(appName, logLineFilter),
            PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id")));

    var hasAfter =
        logLineFilter.logId() == null
            ? logs.hasNext()
            : this.logRepository.exists(
                new LogLineSpecification(appName, logLineFilter.idFilterToAfterFilter()));

    var reversedLogs = logs.getContent().reversed();

    var beforeId =
        logs.hasNext() ? logs.getContent().get(logs.getContent().size() - 1).getId() : null;
    var afterId = logs.hasPrevious() ? logs.getContent().get(0).getId() : null;
    var addWS = hasAfter;

    var filterId = logLineFilter.logId();

    return new LogsViewData(reversedLogs, beforeId, afterId, addWS, appName, filterId);
  }
}
