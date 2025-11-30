package dev.jerkic.redis_logs.controller;

import dev.jerkic.redis_logs.model.dto.LogLineFilter;
import dev.jerkic.redis_logs.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
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
      LogLineFilter logLineFilter,
      Model model,
      HttpServletRequest request) {
    var viewData = this.logService.getLogsViewData(appName, logLineFilter);
    model.addAllAttributes(viewData.toModelAttributes());

    boolean isHtmxRequest = "true".equals(request.getHeader("HX-Request"));
    boolean isBoosted = "true".equals(request.getHeader("HX-Boosted"));

    if (isHtmxRequest && !isBoosted) {
      return "logs::loglist";
    }

    return "logs";
  }
}
