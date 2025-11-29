package dev.jerkic.redis_logs.controller;

import dev.jerkic.redis_logs.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
  @ResponseBody
  public void createLog(@RequestParam String message, @RequestParam String level) {
    var result = this.logService.createLog(message, level);
    var createdLog = result.getLog();
    var lastLogId = result.getLastLogId();
    log.info("Last log ID: {}, new log id: {}", lastLogId, createdLog.getId());
  }
}
