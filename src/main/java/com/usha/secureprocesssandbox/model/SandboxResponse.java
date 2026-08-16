package com.usha.secureprocesssandbox.model;

public class SandboxResponse {
    private String status;
    private int exitCode;
    private String stdout;
    private String stderr;
    private long executionTimeMs;

    public SandboxResponse() {}

    public SandboxResponse(String status, int exitCode, String stdout, String stderr, long executionTimeMs) {
        this.status = status;
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
        this.executionTimeMs = executionTimeMs;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }

    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }

    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
}
