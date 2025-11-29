package dev.jerkic.redis_logs.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class TemplateRenderer {

  private final SpringTemplateEngine templateEngine;

  public TemplateRenderer(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  public String render(String templateName, Map<String, Object> variables) {
    Context context = new Context();
    context.setVariables(variables);
    return templateEngine.process(templateName, context);
  }
}
