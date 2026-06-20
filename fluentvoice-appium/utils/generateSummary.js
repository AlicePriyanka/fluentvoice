const fs = require("fs");
const path = require("path");

const resultsFile = fs.existsSync(path.join(__dirname, "../.wdio-results.jsonl"))
  ? path.join(__dirname, "../.wdio-results.jsonl")
  : path.join(__dirname, "../artifacts/reports/results.jsonl");

if (fs.existsSync(resultsFile)) {
  const content = fs.readFileSync(resultsFile, "utf-8").trim();
  if (!content) {
    console.log("Empty results file.");
    process.exit(0);
  }
  const lines = content.split("\n");
  const results = lines.map(line => JSON.parse(line));
  const total = results.length;
  const pass = results.filter(r => r.status === "PASS").length;
  const fail = results.filter(r => r.status === "FAIL").length;

  function getBaseTestingType(category) {
    if (category === "UI_UX") return "UI/UX";
    if (category === "End_to_End") return "End-to-End";
    if (category === "Mobile_Specific") return "Mobile";
    return category;
  }

  const typeSummary = {};
  const baseCategoriesList = [
    "Functional", "UI/UX", "Compatibility", "Performance", "Security",
    "API", "Database", "Accessibility", "Mobile", "Regression", "End-to-End"
  ];
  baseCategoriesList.forEach(t => {
    typeSummary[t] = { total: 0, passed: 0, failed: 0 };
  });

  for (const r of results) {
    const tType = getBaseTestingType(r.category);
    if (!typeSummary[tType]) {
      typeSummary[tType] = { total: 0, passed: 0, failed: 0 };
    }
    typeSummary[tType].total++;
    if (r.status === "PASS") {
      typeSummary[tType].passed++;
    } else {
      typeSummary[tType].failed++;
    }
  }

  let typeRows = "";
  Object.entries(typeSummary).forEach(([type, metrics]) => {
    if (metrics.total > 0 || baseCategoriesList.includes(type)) {
      typeRows += `| ${type} | ${metrics.total} | ${metrics.passed} | ${metrics.failed} |\n`;
    }
  });

  let testRows = "";
  for (const r of results) {
    const statusIcon = r.status === "PASS" ? "🟢 PASS PASSED" : "🔴 FAIL FAILED";
    const suite = r.category || "-";
    const type = getBaseTestingType(r.category || "-");
    const name = r.name || "-";
    const dur = `${r.durationMs || 0}ms`;
    const err = r.error ? `\`${r.error.replace(/\n/g, " ")}\`` : "";
    testRows += `| ${statusIcon} | ${suite} | ${type} | ${name} | ${dur} | ${err} |\n`;
  }

  const markdown = `
### E2E Appium Mobile Test Results
Total Tests: ${total} | Failed: ${fail}

#### Testing Types Performed

| Testing Type | Total | Passed | Failed |
| --- | --- | --- | --- |
${typeRows}

#### Test Results

| Status | Suite | Testing Type | Test Name | Duration | Error |
| --- | --- | --- | --- | --- | --- |
${testRows}
`;

  if (process.env.GITHUB_STEP_SUMMARY) {
    fs.appendFileSync(process.env.GITHUB_STEP_SUMMARY, markdown);
  } else {
    console.log(markdown);
  }
} else {
  console.log("No results file found at " + resultsFile);
}
