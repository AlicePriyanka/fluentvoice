const path = require("path");
const fs = require("fs");
const { startRun, recordTest, generateReport } = require("./utils/xlsxReporter");

exports.config = {
  runner: 'local',
  port: 4723,
  path: '/',
  specs: [
    process.env.WDIO_CI_SPEC || './tests/12_e2e/**/*.js'
  ],
  maxInstances: 1,
  capabilities: [{
    platformName: 'Android',
    'appium:deviceName': 'Android Emulator',
    'appium:platformVersion': '9.0',
    'appium:automationName': 'UiAutomator2',
    'appium:app': process.env.APK_PATH || path.join(__dirname, '../fluentvoice/app/build/outputs/apk/debug/app-debug.apk'),
    'appium:autoGrantPermissions': true,
    'appium:newCommandTimeout': 240,
    'appium:headless': true
  }],
  logLevel: 'info',
  bail: 0,
  waitforTimeout: 10000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,
  services: [],
  framework: 'mocha',
  reporters: ['spec'],
  mochaOpts: {
    ui: 'bdd',
    timeout: 600000
  },

  onPrepare: function (config, capabilities) {
    const resultsFile = path.join(__dirname, ".wdio-results.jsonl");
    if (fs.existsSync(resultsFile)) {
      fs.unlinkSync(resultsFile);
    }
    fs.mkdirSync(path.join(__dirname, "artifacts/reports"), { recursive: true });
  },

  afterTest: function (test, context, { error, duration, passed, retries }) {
    const resultsFile = path.join(__dirname, ".wdio-results.jsonl");
    const status = passed ? "PASS" : "FAIL";
    const record = {
      id: test.title.match(/TC_\d+/)?.[0] || "TC_000",
      category: test.parent || "General",
      name: test.title,
      durationMs: duration || 0,
      status: status,
      error: error ? error.stack : ""
    };
    fs.appendFileSync(resultsFile, JSON.stringify(record) + "\n");
  },

  onComplete: async function (exitCode, config, capabilities, results) {
    const resultsFile = path.join(__dirname, ".wdio-results.jsonl");
    const allTests = [];
    if (fs.existsSync(resultsFile)) {
      const lines = fs.readFileSync(resultsFile, "utf-8").trim().split("\n");
      for (const line of lines) {
        if (line) {
          allTests.push(JSON.parse(line));
        }
      }
    }

    const outputPath = path.join(__dirname, "artifacts/reports/selenium-report.xlsx");
    startRun();
    for (const t of allTests) {
      recordTest(t.id, t.category, t.name, t.durationMs, t.status, t.error);
    }
    await generateReport(outputPath);

    const htmlPath = path.join(__dirname, "artifacts/reports/execution-report.html");
    const { generateHtmlReport } = require("./utils/generateHtmlReport");
    try {
      generateHtmlReport(allTests, htmlPath);
    } catch (e) {
      console.error("HTML Report failed:", e);
    }
  }
};
