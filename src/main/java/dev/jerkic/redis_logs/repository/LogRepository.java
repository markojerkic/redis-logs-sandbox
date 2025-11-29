package dev.jerkic.redis_logs.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.jerkic.redis_logs.model.entity.Log;

@Repository
public interface LogRepository extends CrudRepository<Log, String> {

  Optional<Log> findFirstByIdLessThan(String id);
}
