package dev.jerkic.redis_logs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
public class LogProducer implements Serializable {
  @Id
  @Column(nullable = false)
  private String appName;

  @Id
  @Column(nullable = false)
  private String instanceId;
}
