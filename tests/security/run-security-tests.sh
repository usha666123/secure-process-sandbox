#!/bin/bash
# ==============================================================================
# SECURE PROCESS SANDBOX - ENGINE SECURITY VALIDATION SUITE
# ==============================================================================
echo "=== Starting Sandbox Engine Verification ==="

# 1. Test Normal Execution
echo -n "Test 1: Normal command execution... "
OUTPUT=$(docker run --rm --security-opt seccomp=security/seccomp/sandbox-profile.json --cap-drop=ALL secure-sandbox:dev whoami 2>&1)
if [ "$OUTPUT" == "sandbox" ]; then
    echo "✅ PASSED"
else
    echo "❌ FAILED (Got: $OUTPUT)"
fi

# 2. Test Root Block Access
echo -n "Test 2: Verifying non-root execution restriction... "
IS_ROOT=$(docker run --rm --security-opt seccomp=security/seccomp/sandbox-profile.json --cap-drop=ALL secure-sandbox:dev id -u 2>&1)
if [ "$IS_ROOT" != "0" ]; then
    echo "✅ PASSED (Non-root user confirmed)"
else
    echo "❌ FAILED (Running as dangerous root!)"
fi

# 3. Test Memory Limits
echo -n "Test 3: Verifying cgroup memory constraint... "
docker run --rm --memory=64m secure-sandbox:dev python3 -c "a = [0]*10000000" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "✅ PASSED (Out-Of-Memory process successfully terminated)"
else
    echo "❌ FAILED (Memory allocation bypassed limits)"
fi

echo "=== Validation Suite Complete ==="
