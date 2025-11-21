package com.mealquest.controller;

import com.mealquest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/healthcheck")
    public Map<String, Object> healthcheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "ok");
        status.put("timestamp", Instant.now().toString());
        status.put("uptimeSeconds", ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        
        try {
            long userCount = userRepository.count();
            status.put("userCount", userCount);
            status.put("database", "connected");
        } catch (Exception e) {
            status.put("database", "error: " + e.getMessage());
        }
        
        return status;
    }

    @GetMapping("/api/test")
    public Map<String, Object> testApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "API is working");
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}