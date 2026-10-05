const { Builder, By, until } = require("selenium-webdriver");
const chrome = require("selenium-webdriver/chrome");
const XLSX = require("xlsx");
const fs = require("fs");
const path = require("path");

// ---------------- Config ----------------
const APP_URL = "http://localhost:5173";
const API_URL = "http://localhost:5000";
const EXCEL_FILE = path.join(__dirname, "..", "selenium_test_results.xlsx");
const SHEET = "Test Results";
const HEADERS = ["Test Case ID", "Test Scenario", "Expected Result", "Actual Result", "Status", "Execution Date"];

// ---------------- Excel report ----------------
// Upserts one row per test case ID, so re-runs overwrite instead of duplicating.
// Rows written by other test files (navigation, etc.) are preserved.
function saveResult({ id, scenario, expected, actual, status }) {
    const wb = fs.existsSync(EXCEL_FILE) ? XLSX.readFile(EXCEL_FILE) : XLSX.utils.book_new();
    const rows = wb.Sheets[SHEET]
        ? XLSX.utils.sheet_to_json(wb.Sheets[SHEET], { header: 1, blankrows: false })
        : [HEADERS];

    const row = [id, scenario, expected, actual, status, new Date().toLocaleString()];
    const idx = rows.findIndex((r, i) => i > 0 && r[0] === id);
    if (idx > 0) rows[idx] = row;
    else rows.push(row);

    const ws = XLSX.utils.aoa_to_sheet(rows);
    ws["!cols"] = [{ wch: 16 }, { wch: 38 }, { wch: 60 }, { wch: 70 }, { wch: 9 }, { wch: 22 }];

    if (wb.SheetNames.includes(SHEET)) wb.Sheets[SHEET] = ws;
    else XLSX.utils.book_append_sheet(wb, ws, SHEET);

    XLSX.writeFile(wb, EXCEL_FILE);
}

// Runs one test case, logs it and saves PASS/FAIL to Excel. Returns true on pass.
async function runCase(tc, fn) {
    console.log(`\n${tc.id}: ${tc.scenario}`);
    try {
        const actual = await fn();
        saveResult({ ...tc, actual, status: "PASS" });
        console.log(`  ✅ PASSED - ${actual}`);
        return true;
    } catch (error) {
        saveResult({ ...tc, actual: `Test failed: ${error.message}`, status: "FAIL" });
        console.error(`  ❌ FAILED - ${error.message}`);
        return false;
    }
}

// Records a case that could not run because an earlier case it depends on failed.
function skipCase(tc, reason) {
    saveResult({ ...tc, actual: `Skipped: ${reason}`, status: "SKIPPED" });
    console.log(`\n${tc.id}: ${tc.scenario}\n  ⏭️  SKIPPED - ${reason}`);
}

// ---------------- Browser helpers ----------------

// Attach to an already-running Opera started with: opera --remote-debugging-port=9222
// (chromedriver must match Opera's Chromium version)
function createDriver() {
    const options = new chrome.Options().debuggerAddress("127.0.0.1:9222");
    return new Builder().forBrowser("chrome").setChromeOptions(options).build();
}

// Switches to the first window whose URL matches predicate; waits until one does.
async function switchToWindow(driver, predicate, timeout, label) {
    await driver.wait(async () => {
        for (const handle of await driver.getAllWindowHandles()) {
            try {
                await driver.switchTo().window(handle);
                if (predicate(await driver.getCurrentUrl())) return true;
            } catch {
                // window closed mid-check; keep looking
            }
        }
        return false;
    }, timeout, `Timed out waiting for ${label}`);
}

// Calls the backend from the app's origin so the session cookie is sent (like the React app does).
// Returns { status, body }.
async function apiRequest(driver, method, apiPath) {
    if (!(await driver.getCurrentUrl()).startsWith(APP_URL)) await driver.get(APP_URL);
    return driver.executeAsyncScript(
        function (url, method, done) {
            fetch(url, { method, credentials: "include" })
                .then(async (res) => {
                    const text = await res.text();
                    let body;
                    try { body = JSON.parse(text); } catch { body = text; }
                    done({ status: res.status, body });
                })
                .catch((err) => done({ status: 0, body: String(err) }));
        },
        `${API_URL}${apiPath}`,
        method
    );
}

// Precondition for login tests: make sure no session is active.
async function ensureLoggedOut(driver) {
    await driver.get(APP_URL);
    const out = await apiRequest(driver, "POST", "/auth/logout");
    const check = await apiRequest(driver, "GET", "/auth/user");
    if (check.status !== 401) {
        throw new Error(
            `Precondition failed: could not log out before the test. POST /auth/logout -> ${out.status} ${JSON.stringify(out.body)}; GET /auth/user -> ${check.status}`
        );
    }
    await driver.get(APP_URL);
}

// ---------------- Navbar locators (from Navbar.jsx) ----------------
const LOGOUT_BUTTON = By.xpath("//button[normalize-space()='Logout']");
const LOGIN_LINK = By.xpath("//a[normalize-space()='Login']");

module.exports = {
    By, until,
    APP_URL, API_URL,
    LOGOUT_BUTTON, LOGIN_LINK,
    saveResult, runCase, skipCase,
    createDriver, switchToWindow, apiRequest, ensureLoggedOut,
};