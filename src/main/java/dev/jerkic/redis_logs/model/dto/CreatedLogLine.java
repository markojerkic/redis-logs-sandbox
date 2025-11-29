package dev.jerkic.redis_logs.model.dto;

import dev.jerkic.redis_logs.model.entity.LogLine;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatedLogLine {
  private LogLine log;
  private String lastLogId;
}
