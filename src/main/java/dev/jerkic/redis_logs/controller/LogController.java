package dev.jerkic.redis_logs.controller;

import dev.jerkic.redis_logs.service.LogService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LogController {

  private final LogService logService;

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("logs", this.logService.getAllLogs());
    return "index";
  }

  @PostMapping("/logs")
  public String createLog(
      Model model,
      @RequestParam String message,
      @RequestParam String level,
      HttpServletResponse response) {
    var result = this.logService.createLog(message, level);
    var createdLog = result.getLog();
    var lastLogId = result.getLastLogId();
    log.info("Last log ID: {}, new log id: {}", lastLogId, createdLog.getId());

    if (lastLogId != null) {
      response.setHeader("HX-Reswap", "afterend scroll:bottom");
      response.setHeader("HX-Retarget", "#log-" + lastLogId);
    }

    model.addAttribute("logs", List.of(createdLog));
    return "index::logline";
  }
}
