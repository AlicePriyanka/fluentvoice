const { startRun, recordTest, generateReport } = require("./xlsxReporter");
const { generateHtmlReport } = require("./generateHtmlReport");
const path = require("path");
const fs = require("fs");

async function main() {
  const outputPath = path.join(__dirname, "../artifacts/reports/selenium-report.xlsx");
  const htmlPath = path.join(__dirname, "../artifacts/reports/execution-report.html");
  
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
}

main().catch(console.error);
