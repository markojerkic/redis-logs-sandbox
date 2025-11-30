package dev.jerkic.redis_logs.model.dto;

public record LogLineFilter(Long before, Long after, Long logId) {
  public LogLineFilter idFilterToAfterFilter() {
    if (this.logId() == null) {
      throw new IllegalStateException("Cannot convert ID filter to after filter");
    }
    return new LogLineFilter(null, this.logId(), null);
  }
}
