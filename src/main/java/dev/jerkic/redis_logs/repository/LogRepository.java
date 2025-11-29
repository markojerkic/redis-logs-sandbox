package dev.jerkic.redis_logs.repository;

import dev.jerkic.redis_logs.model.Log;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends CrudRepository<Log, String> {
}
