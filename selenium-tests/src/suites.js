const fs = require("node:fs");
const path = require("node:path");
const {
  assert,
  By,
  Key,
  until,
  SkipTest,
  navigate,
  visible,
  clickText,
  login,
  apiRequest,
  navigationMetrics,
  assertNoHorizontalOverflow,
} = require("./core/helpers");

function registerSuites(runner) {
  const test = (definition) => runner.test(definition);
  const smoke = ["full", "smoke"];
  const compatibility = ["full", "compatibility"];

  test({
    id: "FUN-001",
    category: "Functional Testing",
    name: "Landing page core content",
    description: "Verifies that the public landing page loads with its primary content and call to action.",
    steps: ["Open /", "Verify the main heading", "Verify a sign-in link exists"],
    expected: "Landing page displays FluentVoice content and a login CTA.",
    suites: smoke,
    async run({ driver, config }) {
      await navigate(driver, config, "/");
      const heading = await visible(driver, By.css("h1"), config.timeoutMs);
      assert.match(await heading.getText(), /speech|fluency|voice/i);
      assert.ok((await driver.findElements(By.css("a[href*='/login']"))).length > 0);
      return "Landing page and primary CTA rendered.";
    },
  });

  test({
    id: "FUN-002",
    category: "Functional Testing",
    name: "Login form validation",
    description: "Checks required-field validation without submitting credentials.",
    steps: ["Open /login", "Click Sign in with blank fields", "Read validation message"],
    expected: "A clear email and password validation message is shown.",
    suites: smoke,
    async run({ driver, config }) {
      await navigate(driver, config, "/login");
      const submit = await visible(driver, By.css("button.w-full"), config.timeoutMs);
      await submit.click();
      const source = await driver.getPageSource();
      assert.match(source, /Email and password are required/i);
      return "Required-field validation displayed.";
    },
  });

  test({
    id: "UI-001",
    category: "UI/UX Testing",
    name: "Desktop layout has no horizontal overflow",
    description: "Checks the landing and login layouts at the standard desktop viewport.",
    steps: ["Open /", "Measure document width", "Open /login", "Measure document width"],
    expected: "Neither page creates horizontal scrolling.",
    suites: smoke,
    async run({ driver, config }) {
      await navigate(driver, config, "/");
      await assertNoHorizontalOverflow(driver);
      await navigate(driver, config, "/login");
      await assertNoHorizontalOverflow(driver);
      return "Landing and login pages fit the desktop viewport.";
    },
  });

  test({
    id: "UI-002",
    category: "UI/UX Testing",
    name: "Interactive controls have visible focus",
    description: "Checks keyboard focus styling on the login form.",
    steps: ["Open /login", "Focus the email field", "Inspect focus outline or box shadow"],
    expected: "The focused email field has a visible focus indicator.",
    async run({ driver, config }) {
      await navigate(driver, config, "/login");
      const email = await visible(driver, By.css("input[type='email']"), config.timeoutMs);
      await email.click();
      const styles = await driver.executeScript(`
        const element = document.activeElement;
        const style = getComputedStyle(element);
        return { outline: style.outline, boxShadow: style.boxShadow, borderColor: style.borderColor };
      `);
      assert.ok(
        styles.outline !== "none" || styles.boxShadow !== "none" || styles.borderColor !== "rgb(221, 227, 240)",
        `No visible focus style detected: ${JSON.stringify(styles)}`
      );
      return "Focused input has a visible style change.";
    },
  });

  test({
    id: "COMP-001",
    category: "Compatibility Testing",
    name: "Cross-browser public route smoke test",
    description: "Loads public routes in every configured desktop browser.",
    steps: ["Open /", "Open /login", "Verify each page has an H1"],
    expected: "Landing and login routes render in the selected browser.",
    suites: compatibility,
    async run({ driver, config }) {
      for (const route of ["/", "/login"]) {
        await navigate(driver, config, route);
        await visible(driver, By.css("h1"), config.timeoutMs);
      }
      return "Public routes rendered successfully.";
    },
  });

  test({
    id: "PERF-001",
    category: "Performance Testing",
    name: "Landing page navigation timing",
    description: "Captures W3C Navigation Timing metrics and enforces a configurable load threshold.",
    steps: ["Open /", "Read Navigation Timing", "Compare duration with threshold"],
    expected: "Landing page completes within PAGE_LOAD_THRESHOLD_MS.",
    suites: smoke,
    async run({ driver, config, recordPerformance }) {
      await navigate(driver, config, "/");
      const metrics = await navigationMetrics(driver);
      assert.ok(metrics, "Navigation Timing API returned no metrics.");
      recordPerformance({ route: "/", ...metrics });
      assert.ok(
        metrics.duration <= config.pageLoadThresholdMs,
        `Load duration ${metrics.duration}ms exceeded ${config.pageLoadThresholdMs}ms.`
      );
      return `Landing page loaded in ${metrics.duration}ms.`;
    },
  });

  test({
    id: "SEC-001",
    category: "Security Testing",
    name: "Protected routes reject anonymous users",
    description: "Checks middleware protection for patient, therapist, and settings routes.",
    steps: ["Open each protected route without cookies", "Verify redirect to /login"],
    expected: "Every protected route redirects anonymous users to login.",
    suites: smoke,
    async run({ driver, config }) {
      for (const route of ["/patient", "/therapist", "/settings"]) {
        await driver.manage().deleteAllCookies();
        await navigate(driver, config, route);
        assert.match(await driver.getCurrentUrl(), /\/login\/?\?from=(%2F|\/)/);
      }
      return "Anonymous access was redirected for all protected route groups.";
    },
  });

  test({
    id: "SEC-002",
    category: "Security Testing",
    name: "Security response headers",
    description: "Checks important browser security headers on the landing response.",
    steps: ["Request /", "Inspect response headers", "Evaluate required headers"],
    expected: "Content-type protection, framing protection, and referrer policy are configured.",
    async run({ driver, config }) {
      await navigate(driver, config, "/");
      const response = await apiRequest(driver, "/");
      const headers = response.headers || {};
      const checks = {
        "x-content-type-options": headers["x-content-type-options"],
        "frame protection": headers["content-security-policy"] || headers["x-frame-options"],
        "referrer-policy": headers["referrer-policy"],
      };
      const missing = Object.entries(checks).filter(([, value]) => !value).map(([name]) => name);
      if (missing.length && config.securityHeadersStrict) {
        assert.fail(`Missing security headers: ${missing.join(", ")}`);
      }
      return missing.length
        ? `Advisory: missing ${missing.join(", ")}. Enable SECURITY_HEADERS_STRICT=true to fail.`
        : "Core security headers are present.";
    },
  });

  test({
    id: "API-001",
    category: "API Testing",
    name: "Authentication API validation",
    description: "Validates error handling for malformed and unauthenticated API requests.",
    steps: ["POST an empty login payload", "GET /api/auth/me without a session", "GET /api/sessions without a session"],
    expected: "Login returns 400 and protected APIs return 401.",
    suites: smoke,
    async run({ driver, config }) {
      await navigate(driver, config, "/login");
      const emptyLogin = await apiRequest(driver, "/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({}),
      });
      assert.equal(emptyLogin.status, 400);
      assert.equal((await apiRequest(driver, "/api/auth/me")).status, 401);
      assert.equal((await apiRequest(driver, "/api/sessions")).status, 401);
      return "Authentication and protected API status codes are correct.";
    },
  });

  test({
    id: "DB-001",
    category: "Database Testing",
    name: "Authenticated persistence read",
    description: "Verifies that authenticated profile and session APIs can read persisted MongoDB-backed data.",
    steps: ["Sign in as patient", "GET /api/profile", "GET /api/sessions", "Validate response shape"],
    expected: "Profile and session queries return valid persisted-data structures.",
    async run({ driver, config }) {
      await login(driver, config, "patient");
      const profile = await apiRequest(driver, "/api/profile");
      const sessions = await apiRequest(driver, "/api/sessions");
      assert.equal(profile.status, 200);
      assert.equal(sessions.status, 200);
      assert.equal(typeof profile.body.profile.id, "string");
      assert.ok(Array.isArray(sessions.body.sessions));
      return `Database-backed APIs returned a profile and ${sessions.body.sessions.length} session(s).`;
    },
  });

  test({
    id: "DB-002",
    category: "Database Testing",
    name: "Profile update persists and is restored",
    description: "Updates a harmless profile field, confirms persistence, then restores the original value.",
    steps: ["Sign in", "Read profile", "Update phone", "Read it back", "Restore original phone"],
    expected: "The updated value persists through the API and cleanup restores original data.",
    async run({ driver, config }) {
      if (!config.allowDataMutation) {
        throw new SkipTest("Set ALLOW_DATA_MUTATION=true to run persistence write/restore testing.");
      }
      await login(driver, config, "patient");
      const original = await apiRequest(driver, "/api/profile");
      assert.equal(original.status, 200);
      const profile = original.body.profile;
      const marker = `E2E-${Date.now()}`;
      try {
        const update = await apiRequest(driver, "/api/profile", {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ phone: marker }),
        });
        assert.equal(update.status, 200);
        const reread = await apiRequest(driver, "/api/profile");
        assert.equal(reread.body.profile.phone, marker);
      } finally {
        await apiRequest(driver, "/api/profile", {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ phone: profile.phone || "" }),
        });
      }
      return "Profile update persisted and original value was restored.";
    },
  });

  test({
    id: "A11Y-001",
    category: "Accessibility Testing",
    name: "Automated WCAG scan",
    description: "Runs axe-core against the landing and login pages.",
    steps: ["Open each public page", "Inject axe-core", "Run WCAG 2 A/AA rules", "Record violations"],
    expected: "No serious or critical accessibility violations are detected.",
    suites: smoke,
    async run({ driver, config, recordAccessibility }) {
      const axeSource = fs.readFileSync(require.resolve("axe-core/axe.min.js"), "utf8");
      const blocking = [];
      for (const route of ["/", "/login"]) {
        await navigate(driver, config, route);
        await driver.executeScript(axeSource);
        const result = await driver.executeAsyncScript(`
          const done = arguments[arguments.length - 1];
          axe.run(document, { runOnly: { type: "tag", values: ["wcag2a", "wcag2aa"] } })
            .then(done)
            .catch((error) => done({ violations: [{ id: "axe-error", impact: "critical", description: error.message, nodes: [] }] }));
        `);
        for (const violation of result.violations) {
          recordAccessibility({
            page: route,
            id: violation.id,
            impact: violation.impact || "unknown",
            description: violation.description,
            nodes: violation.nodes.length,
            helpUrl: violation.helpUrl || "",
          });
          if (["serious", "critical"].includes(violation.impact)) blocking.push(`${route}: ${violation.id}`);
        }
      }
      if (blocking.length && config.failOnSeriousA11y) {
        assert.fail(`Serious accessibility violations: ${blocking.join(", ")}`);
      }
      return blocking.length ? `${blocking.length} serious findings recorded as advisory.` : "No serious or critical axe violations.";
    },
  });

  test({
    id: "MOB-001",
    category: "Mobile-Specific Testing",
    name: "Mobile responsive public journey",
    description: "Uses Pixel 7 emulation to verify responsive rendering and touch-sized primary controls.",
    steps: ["Emulate Pixel 7", "Open /", "Check overflow", "Open /login", "Measure primary button"],
    expected: "Pages fit the mobile viewport and the primary action is at least 44px high.",
    browsers: ["chrome"],
    mobile: true,
    suites: smoke,
    async run({ driver, config }) {
      await navigate(driver, config, "/");
      await assertNoHorizontalOverflow(driver);
      await navigate(driver, config, "/login");
      await assertNoHorizontalOverflow(driver);
      const button = await visible(driver, By.css("button.w-full"), config.timeoutMs);
      const rect = await button.getRect();
      assert.ok(rect.height >= 44, `Primary button is ${rect.height}px high; expected at least 44px.`);
      return `Mobile layout fits and primary action is ${Math.round(rect.height)}px high.`;
    },
  });

  test({
    id: "REG-001",
    category: "Regression Testing",
    name: "Public route regression smoke",
    description: "Checks stable public routes for server errors and expected page structure.",
    steps: ["Open each public route", "Verify no 5xx error page", "Verify body content"],
    expected: "All public routes load usable content.",
    suites: smoke,
    async run({ driver, config }) {
      for (const route of ["/", "/login/", "/forgot-password/", "/offline/"]) {
        await navigate(driver, config, route);
        const body = await visible(driver, By.css("body"), config.timeoutMs);
        await driver.wait(
          async () => (await body.getText()).trim().length > 20,
          config.timeoutMs,
          `Timed out waiting for content on ${route}`
        );
        const text = await body.getText();
        assert.doesNotMatch(text, /Internal Server Error|Application error/i);
      }
      return "All public routes passed regression smoke checks.";
    },
  });

  test({
    id: "E2E-001",
    category: "End-to-End (E2E) Testing",
    name: "Patient authenticated journey",
    description: "Signs in as a patient and traverses the complete patient navigation surface.",
    steps: ["Sign in as patient", "Visit dashboard, record, sessions, appointments, treatment, profile, settings", "Verify each route"],
    expected: "The patient can access every patient route without errors.",
    async run({ driver, config }) {
      await login(driver, config, "patient");
      const routes = [
        "/patient",
        "/patient/record",
        "/patient/sessions",
        "/patient/appointments",
        "/patient/treatment",
        "/patient/profile",
        "/settings",
      ];
      for (const route of routes) {
        await navigate(driver, config, route);
        assert.match(await driver.getCurrentUrl(), new RegExp(route.replace(/\//g, "\\/")));
        await visible(driver, By.css("h1"), config.timeoutMs);
      }
      return `Patient completed ${routes.length} authenticated route checks.`;
    },
  });

  test({
    id: "E2E-002",
    category: "End-to-End (E2E) Testing",
    name: "Therapist authenticated journey",
    description: "Signs in as a therapist and traverses the therapist dashboard and management routes.",
    steps: ["Sign in as therapist", "Visit dashboard, patients, appointments, profile, settings", "Verify each route"],
    expected: "The therapist can access every therapist route without errors.",
    async run({ driver, config }) {
      await login(driver, config, "therapist");
      const routes = [
        "/therapist",
        "/therapist/patients",
        "/therapist/appointments",
        "/therapist/profile",
        "/settings",
      ];
      for (const route of routes) {
        await navigate(driver, config, route);
        assert.match(await driver.getCurrentUrl(), new RegExp(route.replace(/\//g, "\\/")));
        await visible(driver, By.css("h1"), config.timeoutMs);
      }
      return `Therapist completed ${routes.length} authenticated route checks.`;
    },
  });

  test({
    id: "E2E-003",
    category: "End-to-End (E2E) Testing",
    name: "Role-based access control",
    description: "Verifies that authenticated users cannot open the other role's dashboard.",
    steps: ["Sign in as patient", "Open /therapist", "Verify redirect to /patient"],
    expected: "The middleware redirects the patient to the patient dashboard.",
    async run({ driver, config }) {
      await login(driver, config, "patient");
      await navigate(driver, config, "/therapist");
      await driver.wait(until.urlMatches(/\/patient\/?$/), config.timeoutMs);
      return "Patient was prevented from entering therapist routes.";
    },
  });
}

module.exports = { registerSuites };
