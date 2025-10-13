package com.mealquest.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

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
        return status;
    }

    // Scheduled task that calls ping() every 5 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void scheduledPing() {
        String response = ping(); // call the ping() method directly
        System.out.println("Scheduled ping response: " + response + " at " + Instant.now());
    }
}
