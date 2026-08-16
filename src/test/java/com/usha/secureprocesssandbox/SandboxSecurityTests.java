package com.usha.secureprocesssandbox;

import com.usha.secureprocesssandbox.model.SandboxRequest;
import com.usha.secureprocesssandbox.model.SandboxResponse;
import com.usha.secureprocesssandbox.service.SandboxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SandboxSecurityTests {

    @Autowired
    private SandboxService sandboxService;

    @Test
    public void testNormalExecution_ShouldReturnSuccess() throws Exception {
        SandboxRequest request = new SandboxRequest("python", "print('Hello')", "restricted");
        SandboxResponse response = sandboxService.executeAsync(request).get();
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("Hello", response.getStdout().trim());
    }

    @Test
    public void testInfiniteLoop_ShouldReturnTimeout() throws Exception {
        SandboxRequest request = new SandboxRequest("python", "while True: pass", "restricted");
        SandboxResponse response = sandboxService.executeAsync(request).get();
        assertEquals("TIMEOUT", response.getStatus());
    }

    @Test
    public void testMemoryExhaustion_ShouldReturnResourceLimit() throws Exception {
        SandboxRequest request = new SandboxRequest("python", "x = [0] * 50000000", "restricted");
        SandboxResponse response = sandboxService.executeAsync(request).get();

        // ACCEPTANCE CRITERIA: The resource attack is successfully defended if the engine
        // terminates it via an OOM signal (MEMORY_LIMIT), an allocation failure (RUNTIME_ERROR),
        // or a hard watchdog intervention (TIMEOUT).
        String status = response.getStatus();
        assertTrue("MEMORY_LIMIT".equals(status) || "RUNTIME_ERROR".equals(status) || "TIMEOUT".equals(status),
                "Expected memory limit termination, allocation error, or watchdog timeout, but got: " + status);
    }


    @Test
    public void testNetworkAccess_ShouldBeBlocked() throws Exception {
        SandboxRequest request = new SandboxRequest("python", "import urllib.request\nurllib.request.urlopen('https://google.com', timeout=1)", "restricted");
        SandboxResponse response = sandboxService.executeAsync(request).get();

        // ACCEPTANCE CRITERIA: A blocked connection is safe if it throws an immediate
        // RUNTIME_ERROR (no route) OR if it hangs and is safely killed by our TIMEOUT watchdog.
        String status = response.getStatus();
        assertTrue("RUNTIME_ERROR".equals(status) || "TIMEOUT".equals(status),
                "Expected network failure or hard timeout block, but got: " + status);
    }

    @Test
    public void testFilesystemModification_ShouldBeBlocked() throws Exception {
        SandboxRequest request = new SandboxRequest("python", "open('/bin/attack.txt', 'w')", "restricted");
        SandboxResponse response = sandboxService.executeAsync(request).get();
        assertEquals("RUNTIME_ERROR", response.getStatus());
        assertTrue(response.getStderr().contains("Read-only file system"));
    }
}



