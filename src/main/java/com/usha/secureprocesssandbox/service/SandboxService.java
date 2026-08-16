package com.usha.secureprocesssandbox.service;


import com.usha.secureprocesssandbox.model.SandboxPolicy;
import com.usha.secureprocesssandbox.model.SandboxRequest;
import com.usha.secureprocesssandbox.model.SandboxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SandboxService {

    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);

    // Step 6: Direct In-Memory Telemetry Database Engine Storage
    private final Map<String, SandboxResponse> executionHistory = new ConcurrentHashMap<>();

    @Autowired
    private DockerService dockerService;

    @Autowired
    private PolicyService policyService;

    private final java.util.concurrent.atomic.AtomicInteger activeExecutions = new java.util.concurrent.atomic.AtomicInteger(0);

    public int getActiveCount() {
        return activeExecutions.get();
    }
    // Step 4: Execute asynchronously inside our managed thread pool
    public CompletableFuture<SandboxResponse> executeAsync(SandboxRequest request) {
        long startTime = System.currentTimeMillis();
        SandboxResponse response = new SandboxResponse("INTERNAL_ERROR", request.getLanguage(), -1, 0, 0, "", "");
        String executionId = response.getExecutionId();

        // Step 5: Inject structural Execution ID context tracker natively into thread logs
        MDC.put("executionId", "[" + executionId + "]");
        log.info("Sandbox task initiated for engine: {}", request.getLanguage());

        SandboxPolicy activePolicy = policyService.loadPolicy(request.getPolicy());
        String containerId = null;

        try {
            containerId = dockerService.createSecureContainer(request.getLanguage(), request.getCode(), activePolicy);
            dockerService.startContainer(containerId);

            boolean finishedInTime = dockerService.waitForContainerOrTimeout(containerId, activePolicy.getTimeoutSeconds());
            long totalDuration = System.currentTimeMillis() - startTime;

            if (!finishedInTime) {
                log.warn("POLICING ACTION - Container runtime threshold breached!");
                dockerService.killContainer(containerId);
                response = new SandboxResponse("TIMEOUT", request.getLanguage(), -1, totalDuration, 0, "", "Execution terminated: Time Limit Exceeded.");
            } else {
                int exitCode = dockerService.getExitCode(containerId);
                String[] logs = dockerService.getContainerLogs(containerId);
                String status = (exitCode == 0) ? "SUCCESS" : "RUNTIME_ERROR";

                if (exitCode == 137) status = "MEMORY_LIMIT";

                response = new SandboxResponse(status, request.getLanguage(), exitCode, totalDuration, 0, logs[0], logs[1]);
                response.setExecutionId(executionId); // Retain original identifier assignment
            }

        } catch (Exception e) {
            log.error("Structural processing framework interruption: {}", e.getMessage());
            response.setStderr(e.getMessage());
        } finally {
            if (containerId != null) {
                dockerService.removeContainer(containerId);
            }
            log.info("Sandbox loop terminated safely. Final Status: {}", response.getStatus());

            // Save state record to history database and clear context trace tracking
            executionHistory.put(executionId, response);
            MDC.remove("executionId");
        }

        return CompletableFuture.completedFuture(response);
    }

    public SandboxResponse getHistoricalReport(String executionId) {
        return executionHistory.get(executionId);
    }


}
