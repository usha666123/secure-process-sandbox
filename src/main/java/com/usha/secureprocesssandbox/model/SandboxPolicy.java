package com.usha.secureprocesssandbox.model;

public class SandboxPolicy {
    private double cpuLimit;
    private int memoryLimitMb;
    private int pidLimit;
    private int timeoutSeconds;
    private boolean networkEnabled;
    private boolean readOnlyFilesystem;
    private String seccompProfile;

    // Default Constructor for Jackson JSON parsing
    public SandboxPolicy() {}

    public SandboxPolicy(double cpuLimit, int memoryLimitMb, int pidLimit, int timeoutSeconds,
                         boolean networkEnabled, boolean readOnlyFilesystem, String seccompProfile) {
        this.cpuLimit = cpuLimit;
        this.memoryLimitMb = memoryLimitMb;
        this.pidLimit = pidLimit;
        this.timeoutSeconds = timeoutSeconds;
        this.networkEnabled = networkEnabled;
        this.readOnlyFilesystem = readOnlyFilesystem;
        this.seccompProfile = seccompProfile;
    }

    // Getters and Setters
    public double getCpuLimit() { return cpuLimit; }
    public void setCpuLimit(double cpuLimit) { this.cpuLimit = cpuLimit; }

    public int getMemoryLimitMb() { return memoryLimitMb; }
    public void setMemoryLimitMb(int memoryLimitMb) { this.memoryLimitMb = memoryLimitMb; }

    public int getPidLimit() { return pidLimit; }
    public void setPidLimit(int pidLimit) { this.pidLimit = pidLimit; }

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    public boolean isNetworkEnabled() { return networkEnabled; }
    public void setNetworkEnabled(boolean networkEnabled) { this.networkEnabled = networkEnabled; }

    public boolean isReadOnlyFilesystem() { return readOnlyFilesystem; }
    public void setReadOnlyFilesystem(boolean readOnlyFilesystem) { this.readOnlyFilesystem = readOnlyFilesystem; }

    public String getSeccompProfile() { return seccompProfile; }
    public void setSeccompProfile(String seccompProfile) { this.seccompProfile = seccompProfile; }
}
