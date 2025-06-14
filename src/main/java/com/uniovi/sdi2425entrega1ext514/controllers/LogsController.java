package com.uniovi.sdi2425entrega1ext514.controllers;

import com.uniovi.sdi2425entrega1ext514.entities.LogEntry;
import com.uniovi.sdi2425entrega1ext514.services.LogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
public class LogsController {

    private final LogService logService;

    public LogsController(final LogService logService) {
        this.logService = logService;
    }

    @RequestMapping("/logs")
    public String logs(@RequestParam(value = "type", required = false) String type, Model model) {
        List<LogEntry> logs;

        if (type != null && !type.isEmpty()) {
            logs = logService.getLogsByType(type);
            model.addAttribute("selectedType", type);
        } else {
            logs = logService.getAllLogs();
            model.addAttribute("selectedType", null);
        }

        logs.sort(Comparator.comparing(LogEntry::getTimestamp).reversed());
        model.addAttribute("logsList", logs);
        model.addAttribute("logTypes", List.of("PET", "LOGIN-EX", "LOGIN-ERR", "ALTA", "LOGOUT"));

        logService.saveLog(getCurrentUsername(), "PET", "/user/logs");

        return "logs/list";
    }

    @PostMapping("/logs/delete")
    public String deleteLogs(@RequestParam(value = "type", required = false) String type) {
        if (type != null && !type.isEmpty()) {
            logService.deleteLogsByType(type);
        } else {
            logService.deleteLogs();
        }
        return "redirect:/logs" + (type != null ? "?type=" + type : "");
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
