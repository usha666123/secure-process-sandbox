package com.usha.secureprocesssandbox.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.usha.secureprocesssandbox.model.SandboxPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;

;

@Service
public class PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // HARD IMMUTABLE ENGINE CEILINGS (Guardrail Layer)
    private static final double MAX_CPU = 2.0;
    private static final int MAX_MEMORY_MB = 512;
    private static final int MAX_PIDS = 100;
    private static final int MAX_TIMEOUT_SEC = 30;

    public SandboxPolicy loadPolicy(String policyName) {
        String sanitizedName = policyName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        File policyFile = Paths.get("security", "policies", sanitizedName + ".json").toFile();

        if (!policyFile.exists()) {
            log.warn("Target profile missing. Falling back to standard.json");
            policyFile = Paths.get("security", "policies", "standard.json").toFile();
        }

        try {
            SandboxPolicy policy = objectMapper.readValue(policyFile, SandboxPolicy.class);
            applyGlobalGuardrails(policy);
            return policy;
        } catch (Exception e) {
            log.error("Failed to parse policy definition config. Applying ultra-safe fallback.");
            return new SandboxPolicy(0.25, 64, 20, 3, false, true, "unconfined");
        }
    }

    private void applyGlobalGuardrails(SandboxPolicy policy) {
        // Enforce hard global boundaries to mitigate privilege escalation
        if (policy.getCpuLimit() > MAX_CPU) policy.setCpuLimit(MAX_CPU);
        if (policy.getMemoryLimitMb() > MAX_MEMORY_MB) policy.setMemoryLimitMb(MAX_MEMORY_MB);
        if (policy.getPidLimit() > MAX_PIDS) policy.setPidLimit(MAX_PIDS);
        if (policy.getTimeoutSeconds() > MAX_TIMEOUT_SEC) policy.setTimeoutSeconds(MAX_TIMEOUT_SEC);

        // Zero-Trust Overrides
        policy.setNetworkEnabled(false);      // Absolute networking blackout
        policy.setReadOnlyFilesystem(true);  // Lock down core container writes
    }
}
