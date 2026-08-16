package com.usha.secureprocesssandbox.service;


import com.usha.secureprocesssandbox.model.SandboxRequest;
import com.usha.secureprocesssandbox.model.SandboxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SandboxService {

    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);

    @Autowired
    private DockerService dockerService;

    public SandboxResponse executeSandboxRequest(SandboxRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Sandbox execution process initiated.");

        if (request.getCommand() == null || request.getCommand().trim().isEmpty()) {
            return new SandboxResponse("ERROR", -1, "", "Command payload cannot be empty", 0);
        }

        String rawCommand = request.getCommand().trim();
        String containerId = null;

        try {
            String[] commandArgs = rawCommand.split("\\s+");

            // 1. Create and configure safety hooks
            containerId = dockerService.createSecureContainer(commandArgs);

            // 2. Launch
            dockerService.startContainer(containerId);

            // 3. Monitor for explicit 5-second timeout thresholds
            boolean finishedInTime = dockerService.waitForContainerOrTimeout(containerId, 5);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Execution tracker recorded runtime: {}ms", duration);

            if (!finishedInTime) {
                log.warn("POLICING RULE: Process exceeded maximum threshold allocation!");
                dockerService.killContainer(containerId);
                return new SandboxResponse("TIMEOUT", -1, "", "Execution terminated: Time Limit Exceeded (5s)", duration);
            }

            // 4. Evaluate container status logs if completed in time
            int exitCode = dockerService.getExitCode(containerId);
            String[] executionLogs = dockerService.getContainerLogs(containerId);
            String stdout = executionLogs[0];
            String stderr = executionLogs[1];

            String status = "SUCCESS";

            // Smart Parsing of Linux Container Termination signals
            if (exitCode != 0) {
                status = "FAILED";
                // 137 exit code indicates the container process was killed by the system OOM killer
                if (exitCode == 137 || stderr.contains("Out of memory")) {
                    status = "RESOURCE_LIMIT";
                    stderr = "Execution terminated: Memory Limit Exceeded (128MB)";
                }
                // Operation not permitted / Seccomp interception crashes
                else if (exitCode == 1 || stderr.contains("Operation not permitted")) {
                    status = "SECURITY_VIOLATION";
                    stderr = "Execution terminated: Security boundary violation detected via Kernel Filter.";
                }
            }

            log.info("Sandbox request closed out with Status: {}", status);
            return new SandboxResponse(status, exitCode, stdout, stderr, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Internal processing fault: ", e);
            return new SandboxResponse("SYSTEM_ERROR", -1, "", e.getMessage(), duration);
        } finally {
            // 5. Clean structural filesystems out immediately
            if (containerId != null) {
                try {
                    dockerService.removeContainer(containerId);
                } catch (Exception e) {
                    log.error("Cleanup pipeline error: {}", e.getMessage());
                }
            }
        }
    }
}

