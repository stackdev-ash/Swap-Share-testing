const {
    By, until, APP_URL,
    LOGOUT_BUTTON, LOGIN_LINK,
    runCase, skipCase,
    createDriver, switchToWindow, apiRequest, ensureLoggedOut,
} = require("./helpers/common");

const TESTS = {
    oauth: {
        id: "TC-LOGIN-001",
        scenario: "Google OAuth Login",
        expected: "User successfully logs in using Google and is redirected to Swap-and-Share",
    },
    navbar: {
        id: "TC-LOGIN-002",
        scenario: "Navbar reflects logged-in state",
        expected: "After login the navbar shows the Logout button and hides the Login link.",
    },
    persist: {
        id: "TC-LOGIN-003",
        scenario: "Session persists after page reload",
        expected: "After reloading the page the user is still logged in.",
    },
};

async function selectFirstGoogleAccount(driver) {
    try {
        const account = await driver.wait(until.elementLocated(By.css("[data-identifier], [data-email]")), 10000);
        await driver.executeScript("arguments[0].scrollIntoView({block:'center'});", account);
        await driver.wait(until.elementIsVisible(account), 5000);
        await account.click();
        console.log("  First Google account selected.");
    } catch {
        // Google sometimes skips the chooser (single session / auto-consent)
        console.log("  No account chooser shown, continuing...");
    }
}

async function clickGoogleContinueIfShown(driver) {
    try {
        const btn = await driver.wait(
            until.elementLocated(By.xpath("//button[normalize-space()='Continue' or .//span[normalize-space()='Continue']]")),
            4000
        );
        await btn.click();
        console.log("  Google consent 'Continue' clicked.");
    } catch {
        // no consent screen, or page already redirected
    }
}

(async function loginTests() {
    const driver = await createDriver();
    let allPassed = true;

    try {
        console.log("\n=== LOGIN TEST SUITE ===");
        await ensureLoggedOut(driver);

        // TC-LOGIN-001
        const oauthOk = await runCase(TESTS.oauth, async () => {
            await driver.get(`${APP_URL}/login`);

            const googleButton = await driver.wait(
                until.elementLocated(By.xpath("//button[contains(., 'Sign in with Google')]")),
                10000
            );
            await driver.wait(until.elementIsVisible(googleButton), 5000);
            await googleButton.click();
            console.log("  Google login button clicked.");

            // Google may show the chooser, or auto-approve and bounce straight back
            // (already signed in + app already authorised). Accept either outcome.
            const isGoogle = (url) => url.includes("accounts.google.com");
            const isApp = (url) => {
                try {
                    const u = new URL(url);
                    return u.origin === APP_URL && u.pathname !== "/login";
                } catch {
                    return false;
                }
            };

            let outcome;
            await driver.wait(async () => {
                for (const handle of await driver.getAllWindowHandles()) {
                    try {
                        await driver.switchTo().window(handle);
                        const url = await driver.getCurrentUrl();
                        if (isGoogle(url)) { outcome = "google"; return true; }
                        if (isApp(url)) { outcome = "app"; return true; }
                    } catch {
                        // window closed mid-check
                    }
                }
                return false;
            }, 30000, "Timed out: neither the Google page nor a redirect back to the app appeared after clicking sign-in");

            if (outcome === "google") {
                console.log("  Google OAuth page detected. Complete any password/2FA prompt manually if shown.");
                await selectFirstGoogleAccount(driver);
                await clickGoogleContinueIfShown(driver);
                await switchToWindow(driver, isApp, 120000, "redirect to Swap-and-Share");
            } else {
                console.log("  Google auto-approved (already signed in); redirected straight back to the app.");
            }

            const cookies = await driver.manage().getCookies();

            console.log("\n=== Browser Cookies After OAuth ===");

            cookies.forEach(cookie => {
                console.log(
                    `  ${cookie.name} | ${cookie.domain} | ${cookie.path}`
                );
            });

            const sessionCookie = cookies.find(cookie => cookie.name === "connect.sid");

            if (sessionCookie) {
                console.log("  ✅ connect.sid session cookie found.");
            } else {
                console.log("  ❌ connect.sid session cookie NOT found.");
            }

            const { status, body } = await apiRequest(driver, "GET", "/auth/user");
            if (status !== 200 || typeof body !== "object" || !Object.keys(body).length) {
                throw new Error(`Google OAuth completed but /auth/user returned ${status} ${JSON.stringify(body)}.`);
            }
            return "Google authentication successful. First Google account was selected, user was redirected to Swap-and-Share, and /auth/user returned authenticated user data.";
        });
        allPassed = allPassed && oauthOk;

        if (!oauthOk) {
            skipCase(TESTS.navbar, "TC-LOGIN-001 failed");
            skipCase(TESTS.persist, "TC-LOGIN-001 failed");
            return;
        }

        // TC-LOGIN-002
        allPassed = (await runCase(TESTS.navbar, async () => {
            await driver.get(APP_URL);
            await driver.wait(until.elementLocated(LOGOUT_BUTTON), 10000, "Logout button not shown after login");
            if ((await driver.findElements(LOGIN_LINK)).length) throw new Error("Login link is still visible after login.");
            return "Logout button is displayed and the Login link is hidden after login.";
        })) && allPassed;

        // TC-LOGIN-003
        allPassed = (await runCase(TESTS.persist, async () => {
            await driver.navigate().refresh();
            await driver.wait(until.elementLocated(LOGOUT_BUTTON), 10000, "Logout button not shown after reload");
            const { status } = await apiRequest(driver, "GET", "/auth/user");
            if (status !== 200) throw new Error(`/auth/user returned ${status} after reload.`);
            return "User remained logged in after reload; Logout button shown and /auth/user returned 200.";
        })) && allPassed;
    } catch (error) {
        allPassed = false;
        console.error("\n❌ LOGIN SUITE ERROR:", error.message);
    } finally {
        // Don't quit: the driver is attached to your existing Opera session.
        console.log(`\n${allPassed ? "✅ LOGIN SUITE PASSED" : "❌ LOGIN SUITE FAILED"}\nOpera left open.`);
        process.exit(allPassed ? 0 : 1);
    }
})();