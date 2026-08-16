package com.usha.secureprocesssandbox.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SandboxRequest {

    @NotBlank(message = "Language field is mandatory.")
    @Pattern(regexp = "^(?i)(python|node)$", message = "Supported languages are strictly limited to 'python' or 'node'.")
    private String language;

    @NotBlank(message = "Code segment payload cannot be empty.")
    private String code;

    @NotBlank(message = "Policy allocation parameter is mandatory.")
    @Pattern(regexp = "^(?i)(restricted|standard|development)$", message = "Target policy configuration metadata must match 'restricted', 'standard', or 'development'.")
    private String policy;

    public SandboxRequest() {}

    public SandboxRequest(String language, String code, String policy) {
        this.language = language;
        this.code = code;
        this.policy = policy;
    }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getPolicy() { return policy; }
    public void setPolicy(String policy) { this.policy = policy; }
}
