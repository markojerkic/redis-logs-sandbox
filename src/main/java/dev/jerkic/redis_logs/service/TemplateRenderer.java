package dev.jerkic.redis_logs.service;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class TemplateRenderer {

  private final SpringTemplateEngine templateEngine;
  private static final String OOB_TEMPLATE = "<div hx-swap-oob=\"%s\">%s</div>";

  public TemplateRenderer(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  public String swapOobRender(
      String templateName, String targetOOB, Map<String, Object> variables) {
    return String.format(OOB_TEMPLATE, targetOOB, this.render(templateName, variables));
  }

  public String render(String templateName, Map<String, Object> variables) {
    Context context = new Context();
    context.setVariables(variables);

    var parts = templateName.split("::");
    if (parts.length == 2) {
      return templateEngine.process(parts[0], Set.of(parts[1]), context);
    } else {
      return templateEngine.process(templateName, context);
    }
  }
}
