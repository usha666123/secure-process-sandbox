package com.usha.secureprocesssandbox.controller;

import com.usha.secureprocesssandbox.model.SandboxRequest;
import com.usha.secureprocesssandbox.model.SandboxResponse;
import com.usha.secureprocesssandbox.service.SandboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
@RestController
@RequestMapping("/api/sandbox")
@CrossOrigin(origins = "*") // Allows your local React application to talk to this endpoint cleanly

public class SandboxController {

    @Autowired
    private SandboxService sandboxService;

    @PostMapping("/execute")
    public CompletableFuture<ResponseEntity<SandboxResponse>> runUntrustedPayload(@Valid @RequestBody SandboxRequest request) {
        return sandboxService.executeAsync(request)
                .thenApply(ResponseEntity::ok);
    }

    // Step 6: Execution Metrics Retrieval Route API
    @GetMapping("/executions/{executionId}")
    public ResponseEntity<SandboxResponse> fetchExecutionTelemetryMetrics(@PathVariable String executionId) {
        SandboxResponse records = sandboxService.getHistoricalReport(executionId);
        if (records == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(records);
    }

    @Autowired
    private com.github.dockerjava.api.DockerClient dockerClient;

    @GetMapping("/health")
    public ResponseEntity<java.util.Map<String, Object>> getSystemHealthState() {
        java.util.Map<String, Object> health = new java.util.HashMap<>();
        health.put("status", "UP");

        // Dynamic Docker Connection Lifecycle Verification Check
        try {
            dockerClient.pingCmd().exec();
            health.put("docker", "CONNECTED");
        } catch (Exception e) {
            health.put("docker", "DISCONNECTED");
            health.put("status", "DOWN");
        }

        health.put("activeExecutions", sandboxService.getActiveCount());
        return ResponseEntity.ok(health);
    }

}
