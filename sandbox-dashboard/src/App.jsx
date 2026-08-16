import React, { useState } from 'react';
import './App.css';

function App() {
  const [language, setLanguage] = useState('python');
  const [policy, setPolicy] = useState('restricted');
  const [code, setCode] = useState("print('Hello Sandbox Dashboard!')");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleExecute = async () => {
    setLoading(true);
    setResult(null);

    try {
      const response = await fetch('http://localhost:8080/api/sandbox/execute', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ language, code, policy })
      });

      if (!response.ok) {
        throw new Error(`Server returned error status code: ${response.status}`);
      }

      const data = await response.json();
      setResult(data);
    } catch (error) {
      setResult({
        status: 'SYSTEM_ERROR',
        exitCode: -1,
        stdout: '',
        stderr: `Failed to connect to backend engine orchestrator: ${error.message}`,
        executionTimeMs: 0
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <header>
        <h1>🛡️ Secure Process Sandbox</h1>
        <p className="subtitle">Policy-Driven Code Execution Platform</p>
      </header>

      <div className="workspace">
        <div className="control-panel">
          <div className="form-group">
            <label>Runtime Language</label>
            <select value={language} onChange={(e) => setLanguage(e.target.value)}>
              <option value="python">Python 3 (Alpine)</option>
              <option value="node">Node.js (Alpine)</option>
            </select>
          </div>

          <div className="form-group">
            <label>Security Isolation Policy</label>
            <select value={policy} onChange={(e) => setPolicy(e.target.value)}>
              <option value="restricted">Restricted (Strict Ceilings)</option>
              <option value="standard">Standard (Baseline Constraints)</option>
              <option value="development">Development (Loose Quotas)</option>
            </select>
          </div>
        </div>

        <div className="editor-pane">
          <label>Source Code Submission</label>
          <textarea
            value={code}
            onChange={(e) => setCode(e.target.value)}
            rows={8}
            placeholder="Write your untrusted application code string here..."
          />
          <button onClick={handleExecute} disabled={loading}>
            {loading ? 'Evaluating in Cage...' : '⚡ Execute Secure Code'}
          </button>
        </div>

        {result && (
          <div className={`analytics-pane ${result.status}`}>
            <h2>Execution Reports & Telemetry</h2>
            <div className="metrics-grid">
              <div className="metric-box">
                <span className="label">Status:</span>
                <span className="value status-text">{result.status}</span>
              </div>
              <div className="metric-box">
                <span className="label">Time:</span>
                <span className="value">{result.executionTimeMs} ms</span>
              </div>
              <div className="metric-box">
                <span className="label">Exit Code:</span>
                <span className="value">{result.exitCode}</span>
              </div>
              <div className="metric-box">
                <span className="label">Execution ID:</span>
                <span className="value token">{result.executionId || 'N/A'}</span>
              </div>
            </div>

            {result.stdout && (
              <div className="stream-box stdout">
                <h3>Standard Output (stdout)</h3>
                <pre>{result.stdout}</pre>
              </div>
            )}

            {result.stderr && (
              <div className="stream-box stderr">
                <h3>Standard Error / Violations (stderr)</h3>
                <pre>{result.stderr}</pre>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
