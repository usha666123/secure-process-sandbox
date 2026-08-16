package com.usha.secureprocesssandbox.model;

import java.util.UUID;

public class SandboxResponse {
    private String executionId;
    private String status;
    private String language;
    private int exitCode;
    private long executionTimeMs;
    private long memoryUsedMb;
    private String stdout;
    private String stderr;

    public SandboxResponse() {}

    public SandboxResponse(String status, String language, int exitCode, long executionTimeMs,
                           long memoryUsedMb, String stdout, String stderr) {
        this.executionId = UUID.randomUUID().toString().substring(0, 8);
        this.status = status;
        this.language = language;
        this.exitCode = exitCode;
        this.executionTimeMs = executionTimeMs;
        this.memoryUsedMb = memoryUsedMb;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    // Getters and Setters
    public String getExecutionId() { return executionId; }
    public void setExecutionId(String executionId) { this.executionId = executionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public long getMemoryUsedMb() { return memoryUsedMb; }
    public void setMemoryUsedMb(long memoryUsedMb) { this.memoryUsedMb = memoryUsedMb; }
    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }
    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }
}
