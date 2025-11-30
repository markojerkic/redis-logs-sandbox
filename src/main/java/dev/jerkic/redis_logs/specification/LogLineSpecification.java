package dev.jerkic.redis_logs.specification;

import dev.jerkic.redis_logs.model.dto.LogLineFilter;
import dev.jerkic.redis_logs.model.entity.LogLine;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class LogLineSpecification implements Specification<LogLine> {
  private final String appName;
  private final LogLineFilter filter;

  @Override
  public @Nullable Predicate toPredicate(
      Root<LogLine> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    var predicates = new ArrayList<Predicate>(3);

    predicates.add(criteriaBuilder.equal(root.get("app").get("appName"), this.appName));

    if (this.filter.before() != null) {
      predicates.add(criteriaBuilder.lessThan(root.get("id"), this.filter.before()));
    }
    if (this.filter.after() != null) {
      predicates.add(criteriaBuilder.greaterThan(root.get("id"), this.filter.after()));
    }
    if (this.filter.logId() != null) {
      predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("id"), this.filter.logId()));
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }
}
