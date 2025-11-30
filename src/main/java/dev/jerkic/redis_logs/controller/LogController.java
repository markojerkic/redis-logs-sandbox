package dev.jerkic.redis_logs.controller;

import dev.jerkic.redis_logs.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LogController {

  private final LogService logService;

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("apps", this.logService.getApps());
    return "index";
  }

  @GetMapping("/{appName}")
  public String logsByApp(
      @PathVariable String appName,
      Optional<Long> before,
      Optional<Long> after,
      Model model,
      HttpServletRequest request) {
    var logs = this.logService.getAllLogs(appName, before, after);
    model.addAttribute("logs", logs.getContent().reversed());
    if (logs.hasNext()) {
      var next = logs.getContent().get(logs.getContent().size() - 1).getId();
      model.addAttribute("beforeId", next);
    }
    if (logs.hasPrevious()) {
      var prev = logs.getContent().get(0).getId();
      model.addAttribute("afterId", prev);
    } else {
      model.addAttribute("addWS", true);
    }
    model.addAttribute("appName", appName);

    if (request.getHeader("HX-Request") == "true" && request.getHeader("HX-Boosted") != "true") {
      return "logs::loglist";
    }

    return "logs";
  }
}
