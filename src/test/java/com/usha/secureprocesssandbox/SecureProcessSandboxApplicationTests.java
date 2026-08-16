package com.usha.secureprocesssandbox;

import com.usha.secureprocesssandbox.model.SandboxRequest;
import com.usha.secureprocesssandbox.model.SandboxResponse;
import com.usha.secureprocesssandbox.service.SandboxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class SecureProcessSandboxApplicationTests {

    @Test
    void contextLoads() {
    }

    @SpringBootTest
    public static class SandboxBenchmark {

        @Autowired
        private SandboxService sandboxService;

        @Test
        public void runPerformanceBenchmarkSuite() throws Exception {
            int totalExecutions = 50;
            List<Long> latencies = new ArrayList<>();
            int successCount = 0;

            System.out.println("Starting performance benchmark suite (" + totalExecutions + " cycles)...");

            for (int i = 0; i < totalExecutions; i++) {
                SandboxRequest request = new SandboxRequest("python", "print('Benchmarking')", "restricted");
                long start = System.currentTimeMillis();
                SandboxResponse response = sandboxService.executeAsync(request).get();
                long duration = System.currentTimeMillis() - start;

                if ("SUCCESS".equals(response.getStatus())) {
                    successCount++;
                }
                latencies.add(duration);
            }

            Collections.sort(latencies);
            double avg = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0);
            long p50 = latencies.get((int) (totalExecutions * 0.50));
            long p95 = latencies.get((int) (totalExecutions * 0.95));
            long p99 = latencies.get((int) (totalExecutions * 0.99));

            System.out.println("\n=== BENCHMARK SUITE REPORT ===");
            System.out.println("Total Executions : " + totalExecutions);
            System.out.println("Success Rate     : " + ((double) successCount / totalExecutions * 100) + "%");
            System.out.println("Average Latency  : " + String.format("%.2f", avg) + " ms");
            System.out.println("P50 Latency      : " + p50 + " ms");
            System.out.println("P95 Latency      : " + p95 + " ms");
            System.out.println("P99 Latency      : " + p99 + " ms");
            System.out.println("==============================\n");
        }
    }
}
