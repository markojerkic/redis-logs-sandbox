package dev.jerkic.redis_logs.service;

import dev.jerkic.redis_logs.model.entity.LogLine;
import dev.jerkic.redis_logs.repository.LogRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  public Page<LogLine> getAllLogs(String appName, Optional<Long> before, Optional<Long> after) {
    var result =
        this.logRepository.findAll(
            (root, criteriaQuery, criteriaBuilder) -> {
              var predicates = new ArrayList<Predicate>(3);

              predicates.add(criteriaBuilder.equal(root.get("app").get("appName"), appName));

              if (before.isPresent()) {
                predicates.add(criteriaBuilder.lessThan(root.get("id"), before.get()));
              }
              if (after.isPresent()) {
                predicates.add(criteriaBuilder.greaterThan(root.get("id"), after.get()));
              }

              return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            },
            PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id")));

    return result;
  }
}
