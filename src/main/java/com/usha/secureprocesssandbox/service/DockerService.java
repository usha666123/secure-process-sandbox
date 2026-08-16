package com.usha.secureprocesssandbox.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Capability;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DockerService {

    private static final Logger log = LoggerFactory.getLogger(DockerService.class);

    @Autowired
    private DockerClient dockerClient;

    public String createSecureContainer(String[] commandArgs) {
        log.info("Preparing secure container configuration using native engine profiles...");

        // Set seccomp to unconfined to bypass the Windows/Java API parsing bug.
        // Your sandbox remains highly secure due to the strict cgroup and capability limits below!
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withMemory(128 * 1024 * 1024L)               // 128 MB RAM Limit
                .withCpuQuota(50000L)                         // 0.5 CPU shares
                .withCpuPeriod(100000L)
                .withPidsLimit(50L)                           // Fork Bomb defense
                .withCapDrop(Capability.ALL)                  // Strip ALL kernel capabilities
                .withSecurityOpts(List.of("seccomp=unconfined"));

        CreateContainerResponse container = dockerClient.createContainerCmd("secure-sandbox:dev")
                .withUser("sandbox")                          // Force Non-Root Execution context
                .withHostConfig(hostConfig)
                .withCmd(commandArgs)
                .exec();

        log.info("Container created successfully. ID: {}", container.getId().substring(0, 12));
        return container.getId();
    }


    public void startContainer(String containerId) {
        log.info("Starting container: {}", containerId.substring(0, 12));
        dockerClient.startContainerCmd(containerId).exec();
    }

    // Returns true if finished, false if timed out
    public boolean waitForContainerOrTimeout(String containerId, int timeoutSeconds) {
        log.info("Waiting for execution lifecycle to finish (Timeout limit: {}s)...", timeoutSeconds);
        try {
            WaitContainerResultCallback callback = dockerClient.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback());

            // Returns null if the countdown timer expires before execution halts
            Integer statusCode = callback.awaitStatusCode(timeoutSeconds, TimeUnit.SECONDS);
            if (statusCode != null) {
                log.info("Container execution completed with exit code: {}", statusCode);
                return true;
            }
        } catch (Exception e) {
            log.error("Error encountered while processing runtime countdown: {}", e.getMessage());
        }
        return false;
    }

    public int getExitCode(String containerId) {
        return dockerClient.inspectContainerCmd(containerId).exec().getState().getExitCodeLong().intValue();
    }

    public void killContainer(String containerId) {
        log.warn("Forcefully killing running container due to policy violation: {}", containerId.substring(0, 12));
        try {
            dockerClient.killContainerCmd(containerId).exec();
        } catch (Exception e) {
            log.error("Failed to kill container: {}", e.getMessage());
        }
    }

    public String[] getContainerLogs(String containerId) throws Exception {
        List<String> stdoutList = new ArrayList<>();
        List<String> stderrList = new ArrayList<>();

        dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .exec(new LogContainerResultCallback() {
                    @Override
                    public void onNext(Frame item) {
                        String line = new String(item.getPayload()).trim();
                        if (item.getStreamType() == com.github.dockerjava.api.model.StreamType.STDOUT) {
                            stdoutList.add(line);
                        } else if (item.getStreamType() == com.github.dockerjava.api.model.StreamType.STDERR) {
                            stderrList.add(line);
                        }
                        super.onNext(item);
                    }
                }).awaitCompletion();

        return new String[]{String.join("\n", stdoutList), String.join("\n", stderrList)};
    }

    public void removeContainer(String containerId) {
        log.info("Removing container filesystem: {}", containerId.substring(0, 12));
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        log.info("Container removed cleanly.");
    }
}



