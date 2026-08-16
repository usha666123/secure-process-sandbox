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

@RestController
@RequestMapping("/api/sandbox")
public class SandboxController {

    @Autowired
    private SandboxService sandboxService;

    @PostMapping("/execute")
    public ResponseEntity<SandboxResponse> executeCode(@RequestBody SandboxRequest request) {
        SandboxResponse response = sandboxService.executeSandboxRequest(request);
        return ResponseEntity.ok(response);
    }
}
