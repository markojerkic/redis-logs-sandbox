package dev.jerkic.redis_logs.config;

import dev.jerkic.redis_logs.model.entity.LogLine;
import dev.jerkic.redis_logs.service.message.RedisMessageListener;
import dev.jerkic.redis_logs.service.message.RedisMessagePublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisWebsocketConfig {

  @Bean
  public RedisTemplate<String, LogLine> redisTemplate(RedisConnectionFactory connectionFactory) {
    var template = new RedisTemplate<String, LogLine>();
    template.setConnectionFactory(connectionFactory);
    return template;
  }

  @Bean
  public RedisMessageListenerContainer redisContainer(
      RedisConnectionFactory connectionFactory,
      RedisMessagePublisher broadcaster,
      RedisMessageListener listener) {

    var container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(listener, new PatternTopic("ws.*"));
    return container;
  }
}
