package dev.jerkic.redis_logs.repository;

import dev.jerkic.redis_logs.model.entity.LogLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogLine, Long> {}
