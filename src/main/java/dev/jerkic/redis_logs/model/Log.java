package dev.jerkic.redis_logs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("logs")
public class Log {
    
    @Id
    private String id;
    private String message;
    private String level;
    private LocalDateTime timestamp;
}
