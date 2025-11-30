package dev.jerkic.redis_logs.model.dto;

import dev.jerkic.redis_logs.model.entity.LogLine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record LogsViewData(
    List<LogLine> logs, Long beforeId, Long afterId, boolean addWS, String appName, Long filterId) {

  public Map<String, Object> toModelAttributes() {
    var attributes = new HashMap<String, Object>();
    attributes.put("logs", logs);
    if (beforeId != null) {
      attributes.put("beforeId", beforeId);
    }
    if (afterId != null) {
      attributes.put("afterId", afterId);
    }
    if (addWS) {
      attributes.put("addWS", true);
    }
    attributes.put("appName", appName);
    if (filterId != null) {
      attributes.put("filterId", filterId);
    }
    return attributes;
  }
}
