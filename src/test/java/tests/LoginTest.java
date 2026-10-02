package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import utils.DriverFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class LoginTest {

    private WebDriver driver;

    private final String APP_URL = "http://localhost:5173";
    private final String API_URL = "http://localhost:5000";

    private final By GOOGLE_BUTTON =
            By.xpath("//button[contains(., 'Sign in with Google')]");

    private final By LOGOUT_BUTTON =
            By.xpath("//button[normalize-space()='Logout']");

    private final By LOGIN_LINK =
            By.xpath("//a[normalize-space()='Login']");

    @BeforeClass
    public void setUp() {

        driver = DriverFactory.createDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        System.out.println("\n=== LOGIN TEST SUITE ===");

        ensureLoggedOut();
    }

    // Google OAuth Login
    @Test
    public void googleOAuthLogin() {

        System.out.println("\nTC-LOGIN-001: Google OAuth Login");

        driver.get(APP_URL + "/login");

        WebElement googleButton = waitForElement(GOOGLE_BUTTON, 10000);

        Assert.assertTrue(
                googleButton.isDisplayed(),
                "Google login button is not displayed."
        );

        googleButton.click();

        System.out.println("Google login button clicked.");

        String outcome = waitForGoogleOrApp();

        if (outcome.equals("google")) {

            System.out.println(
                    "Google OAuth page detected."
            );

            System.out.println(
                    "Complete Google login manually if required."
            );

            selectFirstGoogleAccount();

            clickGoogleContinueIfShown();

            waitForAppRedirect();

        } else {

            System.out.println(
                    "Google automatically approved login and redirected to the app."
            );
        }


        // Check session cookie

        Set<Cookie> cookies = driver.manage().getCookies();

        System.out.println("\n=== Browser Cookies After OAuth ===");

        boolean sessionCookieFound = false;

        for (Cookie cookie : cookies) {

            System.out.println(
                    cookie.getName()
                            + " | "
                            + cookie.getDomain()
                            + " | "
                            + cookie.getPath()
            );

            if (cookie.getName().equals("connect.sid")) {
                sessionCookieFound = true;
            }
        }

        Assert.assertTrue(
                sessionCookieFound,
                "connect.sid session cookie was not found."
        );

        System.out.println("connect.sid session cookie found.");


        // Check authenticated user through backend

        ApiResponse response =
                apiRequest("GET", "/auth/user");

        Assert.assertEquals(
                response.status,
                200,
                "/auth/user did not return 200."
        );

        Assert.assertNotNull(
                response.body,
                "/auth/user returned empty response."
        );

        System.out.println(
                "Authenticated user returned successfully."
        );
    }

    // Navbar reflects logged-in state
    @Test(
            dependsOnMethods = "googleOAuthLogin"
    )
    public void navbarReflectsLoggedInState() {

        System.out.println(
                "\nTC-LOGIN-002: Navbar reflects logged-in state"
        );

        driver.get(APP_URL);

        WebElement logoutButton =
                waitForElement(LOGOUT_BUTTON, 10000);

        Assert.assertTrue(
                logoutButton.isDisplayed(),
                "Logout button is not displayed after login."
        );

        List<WebElement> loginLinks =
                driver.findElements(LOGIN_LINK);

        Assert.assertTrue(
                loginLinks.isEmpty(),
                "Login link is still visible after login."
        );

        System.out.println(
                "Logout button displayed."
        );

        System.out.println(
                "Login link hidden."
        );
    }

    // Session persists after page reload

    @Test(
            dependsOnMethods = "navbarReflectsLoggedInState"
    )
    public void sessionPersistsAfterReload() {

        System.out.println(
                "\nTC-LOGIN-003: Session persists after page reload"
        );

        driver.navigate().refresh();

        WebElement logoutButton =
                waitForElement(LOGOUT_BUTTON, 10000);

        Assert.assertTrue(
                logoutButton.isDisplayed(),
                "Logout button is not displayed after reload."
        );

        ApiResponse response =
                apiRequest("GET", "/auth/user");

        Assert.assertEquals(
                response.status,
                200,
                "/auth/user did not return 200 after reload."
        );

        System.out.println(
                "User remained logged in after reload."
        );

        System.out.println(
                "/auth/user returned 200."
        );
    }


    // ============================================================
    // GOOGLE ACCOUNT SELECTION
    // ============================================================

    private void selectFirstGoogleAccount() {

        try {

            By accountLocator =
                    By.cssSelector("[data-identifier], [data-email]");

            WebElement account =
                    waitForElement(accountLocator, 10000);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    account
            );

            account.click();

            System.out.println(
                    "First Google account selected."
            );

        } catch (Exception e) {

            System.out.println(
                    "No Google account chooser shown. Continuing..."
            );
        }
    }


    // ============================================================
    // GOOGLE CONTINUE BUTTON
    // ============================================================

    private void clickGoogleContinueIfShown() {

        try {

            By continueButton =
                    By.xpath(
                            "//button[normalize-space()='Continue' " +
                            "or .//span[normalize-space()='Continue']]"
                    );

            WebElement button =
                    waitForElement(continueButton, 4000);

            button.click();

            System.out.println(
                    "Google consent Continue clicked."
            );

        } catch (Exception e) {

            System.out.println(
                    "Google Continue screen not shown."
            );
        }
    }


    // ============================================================
    // WAIT FOR GOOGLE OR APPLICATION
    // ============================================================

    private String waitForGoogleOrApp() {

        long endTime =
                System.currentTimeMillis() + 30000;

        while (System.currentTimeMillis() < endTime) {

            Set<String> handles =
                    driver.getWindowHandles();

            for (String handle : handles) {

                try {

                    driver.switchTo().window(handle);

                    String url =
                            driver.getCurrentUrl();

                    if (url.contains("accounts.google.com")) {

                        return "google";
                    }

                    if (isAppAfterLogin(url)) {

                        return "app";
                    }

                } catch (Exception ignored) {
                }
            }

            sleep(500);
        }

        Assert.fail(
                "Timed out waiting for Google OAuth or application redirect."
        );

        return null;
    }


    // ============================================================
    // WAIT FOR APPLICATION REDIRECT
    // ============================================================

    private void waitForAppRedirect() {

        long endTime =
                System.currentTimeMillis() + 120000;

        while (System.currentTimeMillis() < endTime) {

            Set<String> handles =
                    driver.getWindowHandles();

            for (String handle : handles) {

                try {

                    driver.switchTo().window(handle);

                    String url =
                            driver.getCurrentUrl();

                    if (isAppAfterLogin(url)) {

                        System.out.println(
                                "Redirected back to Swap-and-Share."
                        );

                        return;
                    }

                } catch (Exception ignored) {
                }
            }

            sleep(500);
        }

        Assert.fail(
                "Timed out waiting for redirect back to Swap-and-Share."
        );
    }


    // ============================================================
    // CHECK APPLICATION URL
    // ============================================================

    private boolean isAppAfterLogin(String url) {

        try {

            return url.startsWith(APP_URL)
                    && !url.equals(APP_URL + "/login");

        } catch (Exception e) {

            return false;
        }
    }


    // ============================================================
    // API REQUEST
    // ============================================================

    private ApiResponse apiRequest(
            String method,
            String apiPath
    ) {

        if (!driver.getCurrentUrl().startsWith(APP_URL)) {

            driver.get(APP_URL);
        }

        JavascriptExecutor js =
                (JavascriptExecutor) driver;

        String script = """

                const url = arguments[0];
                const method = arguments[1];
                const callback = arguments[arguments.length - 1];

                fetch(url, {
                    method: method,
                    credentials: "include"
                })
                .then(async response => {

                    const text = await response.text();

                    let body;

                    try {
                        body = JSON.parse(text);
                    } catch {
                        body = text;
                    }

                    callback({
                        status: response.status,
                        body: body
                    });

                })
                .catch(error => {

                    callback({
                        status: 0,
                        body: String(error)
                    });

                });

                """;

        Object result =
                js.executeAsyncScript(
                        script,
                        API_URL + apiPath,
                        method
                );

        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data =
                (java.util.Map<String, Object>) result;

        int status =
                ((Number) data.get("status")).intValue();

        Object body =
                data.get("body");

        return new ApiResponse(status, body);
    }


    // ============================================================
    // ENSURE LOGGED OUT
    // ============================================================

    private void ensureLoggedOut() {

        driver.get(APP_URL);

        ApiResponse logout =
                apiRequest("POST", "/auth/logout");

        ApiResponse check =
                apiRequest("GET", "/auth/user");

        if (check.status != 401) {

            throw new RuntimeException(
                    "Could not log out before test. "
                            + "POST /auth/logout = "
                            + logout.status
                            + ", GET /auth/user = "
                            + check.status
            );
        }

        driver.get(APP_URL);

        System.out.println(
                "Login precondition verified: user is logged out."
        );
    }


    // ============================================================
    // ELEMENT WAIT
    // ============================================================

    private WebElement waitForElement(
            By locator,
            int timeout
    ) {

        long endTime =
                System.currentTimeMillis() + timeout;

        while (System.currentTimeMillis() < endTime) {

            List<WebElement> elements =
                    driver.findElements(locator);

            if (!elements.isEmpty()) {

                WebElement element =
                        elements.get(0);

                if (element.isDisplayed()) {

                    return element;
                }
            }

            sleep(250);
        }

        throw new RuntimeException(
                "Element not found: " + locator
        );
    }


    // ============================================================
    // SLEEP
    // ============================================================

    private void sleep(long milliseconds) {

        try {

            Thread.sleep(milliseconds);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }


    // ============================================================
    // API RESPONSE CLASS
    // ============================================================

    private static class ApiResponse {

        int status;
        Object body;

        ApiResponse(int status, Object body) {

            this.status = status;
            this.body = body;
        }
    }


    // ============================================================
    // TEARDOWN
    // ============================================================

    @AfterClass
    public void tearDown() {

        if (driver != null) {

            driver.quit();
        }
    }
}