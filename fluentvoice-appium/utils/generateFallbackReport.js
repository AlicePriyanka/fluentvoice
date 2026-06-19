const { startRun, recordTest, generateReport } = require("./xlsxReporter");
const { generateHtmlReport } = require("./generateHtmlReport");
const path = require("path");
const fs = require("fs");

async function main() {
  const reportsDir = path.join(__dirname, "../artifacts/reports");
  fs.mkdirSync(reportsDir, { recursive: true });

  const outputPath = path.join(reportsDir, "selenium-report.xlsx");
  const htmlPath = path.join(reportsDir, "execution-report.html");
  const jsonlPath = path.join(reportsDir, "results.jsonl");
  
  startRun();
  recordTest("TC_ERR", "Appium Error", "Fatal Error - Setup failed or timeout starting emulator", 120, "FAIL", "Check workflow output for Appium logs.");
  await generateReport(outputPath);
  
  const results = [{
    id: "TC_ERR",
    category: "Appium Error",
    name: "Fatal Error - Setup failed or timeout starting emulator",
    durationMs: 120,
    status: "FAIL",
    error: "Check workflow output for Appium logs."
  }];
  generateHtmlReport(results, htmlPath);

  // Write a JSONL results file for summary generation
  fs.writeFileSync(jsonlPath, results.map(r => JSON.stringify(r)).join("\n") + "\n", "utf-8");
}

main().catch(console.error);
