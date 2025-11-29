package dev.jerkic.redis_logs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LogLine implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String message;

  @Column(nullable = false)
  private String level;

  @Column(columnDefinition = "TEXT", nullable = false)
  private LocalDateTime timestamp;

  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "app_name", referencedColumnName = "appName", nullable = false),
    @JoinColumn(name = "instance_id", referencedColumnName = "instanceId", nullable = false)
  })
  private LogProducer app;
}
