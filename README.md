# 🛡️ Policy-Driven Secure Process Sandbox Platform

A high-performance, asynchronous, zero-trust code execution platform engineered with **Spring Boot 3**, the native **Docker Engine API**, and **React (Vite 5)**. This system is designed to ingest untrusted, user-submitted code strings (Python, Node.js) and safely evaluate them inside multi-layered isolation boundaries enforced at the host Linux kernel level. 

---

## 🗺️ System Architecture Workflow

Rather than using high-overhead shell scripts or unsafe command line text concatenations, this project communicates directly with the Docker daemon using object-oriented, native Windows Named Pipes / Unix Sockets.

```text
  [ React Control Panel ]
             │
             ▼ (POST /api/sandbox/execute)
  [ Spring Boot REST Controller ] ──► (Validates Input Schema & Payload Constraints)
             │
             ▼ (Dispatches Asynchronous Thread)
  [ ThreadPoolTaskExecutor Cluster ]
             │
             ├──► [ PolicyService ] ──► (Loads Tiered Security Blueprint JSON Matrix)
             ▼
  [ DockerService API Wrapper ] ────► (Assembles HostConfig Engine Parameters)
             │
             ▼ (Direct Named Pipe Sockets Connection)
  [ Docker Daemon (Host Kernel Boundary) ]
             │
             ▼
  [ Hardened OCI Micro-Container ] ──► (Evaluates script via memory data injection)
             │
             ▼ (Java Watchdog Loop monitors 5s Threshold Countdown)
  [ Telemetry Report Map Records ] ──► (Pushes MDC Metrics back to Dashboard Dashboard)
```

---

## 🛡️ Multi-Layer Security Hardening Matrix

This project implements a **Defense-in-Depth** model to enforce the **Principle of Least Privilege** directly at the operating system virtualization boundary:

| Security Isolation Boundary | Underlying Implementation Mechanism | Purpose Countermeasure Defended |
| :--- | :--- | :--- |
| **🔐 Non-Root Privileges** | Explicit non-root image compilation (`USER sandbox`) | Neutralizes standard container escape primitives by removing administrative root hooks. |
| **🚫 Dropped Capabilities** | `HostConfig.withCapDrop(Capability.ALL)` | Strips raw kernel execution hooks (e.g., packet sniffs, physical clocks manipulation). |
| **🧠 Memory Ceilings** | `HostConfig.withMemory(64MB - 256MB)` | Instantly triggers an OOM killer shutdown if a script attempts a RAM depletion attack. |
| **⚡ CPU Throttling** | `HostConfig.withCpuQuota(25% - 100%)` | Restricts core processing shares to stop infinite execution loops from locking the host CPU. |
| **🧱 Thread Thread Caps** | `HostConfig.withPidsLimit(20 - 100)` | Completely deadlocks Fork-Bombs by restricting maximum concurrent internal threads. |
| **🌐 Egress Blackout** | `HostConfig.withNetworkMode("none")` | Enforces a strict network blackout boundary to stop telemetry data exfiltration and reverse shells. |
| **📁 Writable Lockdown** | `withReadonlyRootfs(true)` & `withTmpFs()` | Blocks writes to root binaries while safely mounting an ephemeral, 16MB isolated memory loop at `/tmp`. |
| **⏱️ Active Policing Watchdog** | Java Thread `CompletableFuture` Timeout Tracker | Forcefully kills un-terminated or sleeping background scripts exceeding their assigned deadline. |

---

## 📂 Project Structural Scaffolding

```text
secure-process-sandbox/
├── docs/                      # Technical design documents and visual data flows
├── docker/                    # Dockerfile container configuration definitions
│   └── sandbox/               # Unified base micro-image blueprints
├── sandbox-images/            # Multi-language custom isolated runtimes
│   ├── python/                # Clean Python 3 Alpine security layer
│   └── node/                  # Clean Node.js Alpine security layer
├── security/                  # Policy definition metadata schemas
│   ├── seccomp/               # Linux low-level system call whitelist rules
│   └── policies/              # Tiered policy definitions (restricted, standard, development)
├── src/                       # Java orchestration backend environment layout
│   ├── main/java/com/sandbox/
│   │   ├── config/            # Async Thread Pools and Docker Engine API configurations
│   │   ├── controller/        # Validated API endpoints and health indicators
│   │   ├── model/             # Structured Request/Response analytics data entities
│   │   └── service/           # Low-level container lifecycle and policy services
│   └── test/java/com/sandbox/ # Automated security verification and latency benchmarks
└── sandbox-dashboard/         # Clean, unbundled React web control management application
```

---

## 📊 Performance Benchmarks & Operational Telemetry

This data was captured using our built-in benchmarking suite running 50 consecutive code evaluations under the `restricted` profile layer on our local host core:

* **Total Successful Sandbox Completions**: 50 Cycle Routines  
* **System Isolation Success Rate**: 100%  
* **Average Execution Latency Overhead**: 162.40 ms  
* **P50 Latency (Median Baseline Processing)**: 155 ms  
* **P95 Latency (Burst Queue Boundary)**: 210 ms  
* **P99 Latency (Maximum Lifecycle Ceiling)**: 245 ms  

---

## 🧪 Validated Security Test Matrix

Our automated integration test suite transforms traditional raw Linux termination codes into high-fidelity, business-layer API results:

| Attack Scenario Testing Payload | Underlying Kernel Vector Triggered | Extracted API Engine Status Result |
| :--- | :--- | :--- |
| `print("Hello World")` | Standard clean output execution trace | `✅ SUCCESS` |
| `while True: pass` | Watchdog thread intercepts execution hang | `❌ TIMEOUT` |
| `x = [1] * 50000000` | Host control group forces termination | `❌ MEMORY_LIMIT` |
| `open('/bin/attack.txt', 'w')` | Kernel rejects file handle allocation | `❌ RUNTIME_ERROR (Read-Only FS)` |
| `urllib.request.urlopen(...)` | Engine drops localized virtual route link | `❌ RUNTIME_ERROR (No Network)` |

---

## 🚀 Local Installation & Quickstart

### Prerequisites
* Java 17+ / Maven 3.x Installed
* Node.js v20+ 
* Docker Desktop active on your machine.

### 1. Build the Multi-Language Micro-Images
From your project terminal root folder, execute the baseline asset compilation:
```bash
docker build -t sandbox-python:latest ./sandbox-images/python
docker build -t sandbox-node:latest ./sandbox-images/node
```

### 2. Boot the Spring Boot Orchestration API Server
```bash
mvn clean test             # Executes all automated security matrices first
mvn spring-boot:run        # Mounts API Gateway router engine on port 8080
```

### 3. Expose the Observability User UI Control Panel
Navigate to the frontend folder package layout inside a separate terminal window and launch the clean server application:
```bash
cd sandbox-dashboard
npm install
npm run dev
```
Open **`http://localhost:5173`** inside your web browser to test your sandbox defenses in real-time.

---

## 🛑 Operational Limitations & Production Considerations

Understanding the boundaries of your security tools is critical for designing robust enterprise architectures:

1. **Shared OS Kernel Weakness**: Standard containers share the underlying host machine's Linux kernel. If a malicious script utilizes a kernel-level privilege escalation zero-day exploit, it could break through the container regardless of our configured limits or resource caps.
2. **Virtualization Translation Layers**: Running Docker via WSL2 on Windows handles system calls differently than native Linux. For a true production deployment, this platform should be hosted on a bare-metal Linux engine where **Seccomp** filters can directly monitor raw host system calls.
3. **Enterprise Production Improvements**: To upgrade this architecture to support public multi-tenant workloads (like a real-world Cloud IDE), the standard container runtime (`runc`) should be replaced with a user-space kernel isolation layer like **gVisor (`runsc`)** or **Kata Containers (Micro-VMs)**. This removes direct visibility of the host operating system kernel entirely.
