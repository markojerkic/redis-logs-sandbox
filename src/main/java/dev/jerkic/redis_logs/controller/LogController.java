package dev.jerkic.redis_logs.controller;

import dev.jerkic.redis_logs.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
  public String createLog(@RequestParam String message, @RequestParam String level) {
    this.logService.createLog(message, level);
    return "redirect:/";
  }
}
