package dev.jerkic.redis_logs.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "logs")
public class LogLine {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String message;
  private String level;
  private LocalDateTime timestamp;
}
