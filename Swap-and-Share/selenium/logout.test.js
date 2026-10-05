const {
    until, APP_URL,
    LOGOUT_BUTTON, LOGIN_LINK,
    runCase, skipCase,
    createDriver, apiRequest,
} = require("./helpers/common");

const TESTS = {
    logout: {
        id: "TC-LOGOUT-001",
        scenario: "Authenticated user logout",
        expected: "User should be logged out, redirected to Home, and shown the Login option.",
    },
    session: {
        id: "TC-LOGOUT-002",
        scenario: "Verify session invalidation after logout",
        expected: "Authenticated session should be destroyed and /auth/user should reject the request with 401.",
    },
    protectedApi: {
        id: "TC-LOGOUT-003",
        scenario: "Protected API rejects request after logout",
        expected: "/api/cart should return 401 'Please log in first' once the session is destroyed.",
    },
    persist: {
        id: "TC-LOGOUT-004",
        scenario: "Logged-out state persists after page reload",
        expected: "After reloading Home the Login link is shown and the Logout button is absent.",
    },
};

(async function logoutTests() {
    const driver = await createDriver();
    let allPassed = true;

    try {
        console.log("\n=== LOGOUT TEST SUITE ===");
        console.log("Precondition: an authenticated session must exist (run login.test.js first).");

        // TC-LOGOUT-001
        const logoutOk = await runCase(TESTS.logout, async () => {
            await driver.get(APP_URL);

            let logoutButton;
            try {
                logoutButton = await driver.wait(until.elementLocated(LOGOUT_BUTTON), 10000);
            } catch {
                throw new Error("Precondition failed: no authenticated user (Logout button not found). Run login.test.js first.");
            }

            // Record the response of the app's own POST /auth/logout (axios uses XHR)
            await driver.executeScript(function () {
                window.__logoutResult = null;
                const origOpen = XMLHttpRequest.prototype.open;
                XMLHttpRequest.prototype.open = function (method, url) {
                    if (String(url).includes("/auth/logout")) {
                        this.addEventListener("loadend", () => {
                            window.__logoutResult = { status: this.status, body: this.responseText };
                        });
                    }
                    return origOpen.apply(this, arguments);
                };
            });

            await logoutButton.click();
            console.log("  Logout button clicked.");

            try {
                await driver.wait(until.elementLocated(LOGIN_LINK), 10000);
            } catch {
                const req = await driver.executeScript("return window.__logoutResult;");
                const srv = await apiRequest(driver, "GET", "/auth/user");
                throw new Error(
                    `Login link not displayed after logout. POST /auth/logout -> ${req ? `${req.status} ${req.body}` : "no response (request hung, was blocked, or never sent)"}; server session GET /auth/user -> ${srv.status}.`
                );
            }
            await driver.wait(async () => (await driver.getCurrentUrl()) === `${APP_URL}/`, 10000, "Not on Home page after logout");
            if ((await driver.findElements(LOGOUT_BUTTON)).length) throw new Error("Logout button is still visible after logout.");

            return "User successfully logged out, was redirected to Home, and the Login option was displayed.";
        });
        allPassed = allPassed && logoutOk;

        if (!logoutOk) {
            skipCase(TESTS.session, "TC-LOGOUT-001 failed");
            skipCase(TESTS.protectedApi, "TC-LOGOUT-001 failed");
            skipCase(TESTS.persist, "TC-LOGOUT-001 failed");
            return;
        }

        // TC-LOGOUT-002
        allPassed = (await runCase(TESTS.session, async () => {
            const { status, body } = await apiRequest(driver, "GET", "/auth/user");
            if (status !== 401) throw new Error(`Session may still be active. /auth/user returned ${status}: ${JSON.stringify(body)}`);
            return `Logout destroyed the session. /auth/user returned 401 ${JSON.stringify(body)}.`;
        })) && allPassed;

        // TC-LOGOUT-003
        allPassed = (await runCase(TESTS.protectedApi, async () => {
            const { status, body } = await apiRequest(driver, "GET", "/api/cart");
            if (status !== 401) throw new Error(`/api/cart returned ${status}: ${JSON.stringify(body)} (expected 401).`);
            return `/api/cart rejected the request with 401 ${JSON.stringify(body)}.`;
        })) && allPassed;

        // TC-LOGOUT-004
        allPassed = (await runCase(TESTS.persist, async () => {
            await driver.get(APP_URL);
            await driver.navigate().refresh();
            await driver.wait(until.elementLocated(LOGIN_LINK), 10000, "Login link not shown after reload");
            if ((await driver.findElements(LOGOUT_BUTTON)).length) throw new Error("Logout button reappeared after reload.");
            return "Login link displayed and Logout button absent after reload; user stayed logged out.";
        })) && allPassed;
    } catch (error) {
        allPassed = false;
        console.error("\n❌ LOGOUT SUITE ERROR:", error.message);
    } finally {
        // Don't quit: the driver is attached to your existing Opera session.
        console.log(`\n${allPassed ? "✅ LOGOUT SUITE PASSED" : "❌ LOGOUT SUITE FAILED"}\nOpera left open.`);
        process.exit(allPassed ? 0 : 1);
    }
})();