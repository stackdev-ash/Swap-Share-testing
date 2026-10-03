package tests;

import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import org.testng.Assert;
import org.testng.annotations.Test;

import utils.ExcelReporter;
import utils.Locators;
import utils.TestBase;
import utils.TestConfig;

public class LoginTest extends TestBase {

        // ============================================================
        // TC-LOGIN-001
        // TEMPORARY TEST LOGIN
        // ============================================================

        @Test
        public void seleniumTestLogin() {

                String testCaseId = "TC-LOGIN-001";

                String scenario = "Selenium test account login";

                String expected = "Test account successfully creates an authenticated session.";

                try {

                        System.out.println(
                                        "\nTC-LOGIN-001: Selenium test account login");

                        // Open application
                        helper.open(
                                        TestConfig.APP_URL);

                        JavascriptExecutor js = (JavascriptExecutor) driver;

                        String script = """

                                        const callback =
                                            arguments[arguments.length - 1];

                                        fetch(arguments[0], {
                                            method: "POST",
                                            credentials: "include",
                                            headers: {
                                                "Content-Type": "application/json"
                                            },
                                            body: JSON.stringify({
                                                email: arguments[1],
                                                password: arguments[2]
                                            })
                                        })
                                        .then(async response => {

                                            const text =
                                                await response.text();

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

                        Object result = js.executeAsyncScript(
                                        script,

                                        TestConfig.API_URL
                                                        + "/auth/test-login",

                                        TestConfig.TEST_EMAIL,

                                        TestConfig.TEST_PASSWORD);

                        @SuppressWarnings("unchecked")
                        Map<String, Object> response = (Map<String, Object>) result;

                        int status = ((Number) response.get("status"))
                                        .intValue();

                        Assert.assertEquals(
                                        status,
                                        200,
                                        "Test login failed: "
                                                        + response.get("body"));

                        System.out.println(
                                        "Temporary Selenium login successful.");

                        // Verify authenticated state
                        helper.open(
                                        TestConfig.APP_URL);

                        WebElement logoutButton = helper.waitForElement(
                                        Locators.LOGOUT_BUTTON,
                                        10);

                        Assert.assertTrue(
                                        logoutButton.isDisplayed(),
                                        "Logout button was not displayed after login.");

                        System.out.println(
                                        "User is authenticated.");

                        ExcelReporter.saveResult(
                                        testCaseId,
                                        scenario,
                                        expected,
                                        "Test login successful and Logout button displayed.",
                                        "PASS");

                } catch (AssertionError | RuntimeException e) {

                        ExcelReporter.saveResult(
                                        testCaseId,
                                        scenario,
                                        expected,
                                        e.getMessage(),
                                        "FAIL");

                        throw e;
                }
        }

        // ============================================================
        // TC-LOGIN-002
        // SESSION PERSISTS AFTER RELOAD
        // ============================================================

        @Test(dependsOnMethods = "seleniumTestLogin")
        public void sessionPersistsAfterReload() {

                String testCaseId = "TC-LOGIN-002";

                String scenario = "Authenticated session persists after reload";

                String expected = "User remains authenticated after page reload.";

                try {

                        System.out.println(
                                        "\nTC-LOGIN-002: Session persists after reload");

                        helper.refresh();

                        WebElement logoutButton = helper.waitForElement(
                                        Locators.LOGOUT_BUTTON,
                                        10);

                        Assert.assertTrue(
                                        logoutButton.isDisplayed(),
                                        "Logout button disappeared after reload.");

                        ExcelReporter.saveResult(
                                        testCaseId,
                                        scenario,
                                        expected,
                                        "Session persisted after page reload.",
                                        "PASS");

                } catch (AssertionError | RuntimeException e) {

                        ExcelReporter.saveResult(
                                        testCaseId,
                                        scenario,
                                        expected,
                                        e.getMessage(),
                                        "FAIL");

                        throw e;
                }
        }

        // ============================================================
        // TC-LOGIN-003
        // VERIFY /auth/user
        // ============================================================

        @Test(dependsOnMethods = "sessionPersistsAfterReload")
        public void authenticatedUserEndpointWorks() {

                String testCaseId = "TC-LOGIN-003";

                String scenario = "Verify authenticated user endpoint";

                String expected = "/auth/user returns HTTP 200.";

                try {

                        System.out.println(
                                        "\nTC-LOGIN-003: Verify authenticated user endpoint");

                        JavascriptExecutor js = (JavascriptExecutor) driver;

                        String script = """

                                        const callback =
                                            arguments[arguments.length - 1];

                                        fetch(arguments[0], {
                                            method: "GET",
                                            credentials: "include"
                                        })
                                        .then(response => {

                                            callback({
                                                status: response.status
                                            });

                                        })
                                        .catch(error => {

                                            callback({
                                                status: 0
                                            });

                                        });

                                        """;

                        Object result = js.executeAsyncScript(
                                        script,
                                        TestConfig.API_URL
                                                        + "/auth/user");

                        @SuppressWarnings("unchecked")
                        Map<String, Object> response = (Map<String, Object>) result;

                        int status = ((Number) response.get("status"))
                                        .intValue();

                        Assert.assertEquals(
                                        status,
                                        200,
                                        "/auth/user did not return 200.");

                        ExcelReporter.saveResult(
                                        testCaseId,
                                        scenario,
                                        expected,
                                        "/auth/user returned 200.",
                                        "PASS");

                } catch (AssertionError | RuntimeException e) {

                        ExcelReporter.saveResult(
                                        testCaseId,
                                        scenario,
                                        expected,
                                        e.getMessage(),
                                        "FAIL");

                        throw e;
                }
        }
}