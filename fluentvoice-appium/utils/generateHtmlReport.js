const fs = require("fs");
const path = require("path");

function generateHtmlReport(results, outputPath) {
  const total = results.length;
  const pass = results.filter(r => r.status === "PASS").length;
  const fail = results.filter(r => r.status === "FAIL").length;
  const passRate = total ? ((pass / total) * 100).toFixed(2) : "0.00";

  const rows = results.map(r => `
    <tr>
      <td>${r.id}</td>
      <td>${r.category}</td>
      <td>${r.name}</td>
      <td>${r.durationMs}ms</td>
      <td><span style="color: ${r.status === 'PASS' ? '#10B981' : '#EF4444'}">${r.status}</span></td>
    </tr>
  `).join("\n");

  const html = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Appium Execution Report</title>
  <style>
    body { font-family: sans-serif; background: #0B0F19; color: #F3F4F6; padding: 20px; }
    h1 { color: #818CF8; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #2D3748; padding: 8px; text-align: left; }
    th { background: #151B26; }
    tr:nth-child(even) { background: #1E2633; }
  </style>
</head>
<body>
  <h1>Appium Mobile Test Run Report</h1>
  <div>
    <p>Total Tests: ${total}</p>
    <p>Passed: ${pass}</p>
    <p>Failed: ${fail}</p>
    <p>Pass Rate: ${passRate}%</p>
  </div>
  <table>
    <thead>
      <tr>
        <th>ID</th>
        <th>Category</th>
        <th>Name</th>
        <th>Duration</th>
        <th>Status</th>
      </tr>
    </thead>
    <tbody>
      ${rows}
    </tbody>
  </table>
</body>
</html>
  `;
  fs.writeFileSync(outputPath, html, "utf-8");
}

module.exports = { generateHtmlReport };
