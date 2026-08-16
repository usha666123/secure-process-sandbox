package com.usha.secureprocesssandbox.model;


public class SandboxRequest {
    private String command;
    private String policyName; // e.g., "restricted", "standard", "development"

    public SandboxRequest() {}

    public SandboxRequest(String command, String policyName) {
        this.command = command;
        this.policyName = policyName;
    }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
}
