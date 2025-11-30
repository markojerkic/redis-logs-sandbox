package dev.jerkic.redis_logs.repository;

import dev.jerkic.redis_logs.model.entity.LogLine;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository
    extends JpaRepository<LogLine, Long>, JpaSpecificationExecutor<LogLine> {
  Page<LogLine> findAllByApp_appName(String appName, Pageable pageable);

  @Query("SELECT DISTINCT p.appName FROM LogProducer p")
  List<String> findAllApps();
}
