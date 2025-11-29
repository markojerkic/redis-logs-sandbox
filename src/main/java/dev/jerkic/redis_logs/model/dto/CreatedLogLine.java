package dev.jerkic.redis_logs.model.dto;

import dev.jerkic.redis_logs.model.entity.Log;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatedLogLine {
  private Log log;
  private String lastLogId;
}
