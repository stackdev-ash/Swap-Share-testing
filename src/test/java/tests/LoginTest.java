package tests;

import java.util.Map;
import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.ExcelReporter;
import utils.Locators;
import utils.TestBase;
import utils.TestConfig;

public class LoginTest extends TestBase {
    @Test
    public void seleniumTestLogin() {
        String testCaseId = "TC-LOGIN-001";
        String scenario = "Selenium test account login";
        String expected =
                "Test account successfully creates an authenticated session.";

        try {
            System.out.println(
                    "\nTC-LOGIN-001: Selenium test account login");
            helper.open(TestConfig.LOGIN_URL);

            helper.type(
                    Locators.EMAIL_INPUT,
                    TestConfig.TEST_EMAIL);

            helper.type(
                    Locators.PASSWORD_INPUT,
                    TestConfig.TEST_PASSWORD);

            helper.click(Locators.SIGN_IN_BUTTON);

            WebElement logoutButton =
                    helper.waitForElement(
                            Locators.LOGOUT_BUTTON, 10);

            Assert.assertTrue(
                    logoutButton.isDisplayed(),
                    "Logout button was not displayed after login.");

            System.out.println("Selenium login successful.");
            System.out.println("User is authenticated.");

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

    @Test(dependsOnMethods = "seleniumTestLogin")
    public void sessionPersistsAfterReload() {

        String testCaseId = "TC-LOGIN-002";
        String scenario =
                "Authenticated session persists after reload";
        String expected =
                "User remains authenticated after page reload.";

        try {
            System.out.println(
                    "\nTC-LOGIN-002: Session persists after reload");

            // Same WebDriver instance and same session.
            helper.refresh();

            WebElement logoutButton =
                    helper.waitForElement(
                            Locators.LOGOUT_BUTTON, 10);

            Assert.assertTrue(
                    logoutButton.isDisplayed(),
                    "Logout button disappeared after reload.");

            System.out.println(
                    "User remained logged in after reload.");

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

    @Test(dependsOnMethods = "sessionPersistsAfterReload")
    public void authenticatedUserEndpointWorks() {

        String testCaseId = "TC-LOGIN-003";
        String scenario =
                "Verify authenticated user endpoint";
        String expected =
                "/auth/user returns HTTP 200.";

        try {
            System.out.println(
                    "\nTC-LOGIN-003: Verify authenticated user endpoint");

            JavascriptExecutor js =
                    (JavascriptExecutor) driver;

            String script =
                    "const callback = arguments[arguments.length - 1];"
                    + "fetch(arguments[0], {"
                    + "method: 'GET',"
                    + "credentials: 'include'"
                    + "})"
                    + ".then(response => callback({status: response.status}))"
                    + ".catch(error => callback({status: 0}));";

            Object result =
                    js.executeAsyncScript(
                            script,
                            TestConfig.API_URL + "/auth/user");

            @SuppressWarnings("unchecked")
            Map<String, Object> response =
                    (Map<String, Object>) result;

            int status =
                    ((Number) response.get("status")).intValue();

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

    @Test
    public void loginPageElementsAreDisplayed() {

    String testCaseId = "TC-LOGIN-004";
    String scenario = "Verify login page elements";
    String expected = "Email, password, remember-me and Sign in controls are displayed.";

    try {
        helper.open(TestConfig.LOGIN_URL);

        Assert.assertTrue(
                helper.waitForElement(Locators.EMAIL_INPUT, 10).isDisplayed(),
                "Email input is not displayed."
        );

        Assert.assertTrue(
                helper.waitForElement(Locators.PASSWORD_INPUT, 10).isDisplayed(),
                "Password input is not displayed."
        );

        Assert.assertTrue(
                helper.waitForElement(Locators.REMEMBER_ME, 10).isDisplayed(),
                "Remember Me checkbox is not displayed."
        );

        Assert.assertTrue(
                helper.waitForElement(Locators.SIGN_IN_BUTTON, 10).isDisplayed(),
                "Sign in button is not displayed."
        );

        ExcelReporter.saveResult(
                testCaseId,
                scenario,
                expected,
                "All login page controls are displayed.",
                "PASS"
        );

    } catch (AssertionError | RuntimeException e) {

        ExcelReporter.saveResult(
                testCaseId,
                scenario,
                expected,
                e.getMessage(),
                "FAIL"
        );

        throw e;
    }
  }

  @Test
public void invalidLoginDoesNotAuthenticate() {

    String testCaseId = "TC-LOGIN-005";
    String scenario = "Login with invalid credentials";
    String expected = "Invalid credentials should not create an authenticated session.";

    try {
        helper.open(TestConfig.LOGIN_URL);

        helper.type(
                Locators.EMAIL_INPUT,
                "invalid@example.com"
        );

        helper.type(
                Locators.PASSWORD_INPUT,
                "wrongpassword"
        );

        helper.click(Locators.SIGN_IN_BUTTON);

        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(d -> !helper.isDisplayed(Locators.LOGOUT_BUTTON));

        boolean logoutDisplayed =
                helper.isDisplayed(Locators.LOGOUT_BUTTON);

        Assert.assertFalse(
                logoutDisplayed,
                "User became authenticated with invalid credentials."
        );

        ExcelReporter.saveResult(
                testCaseId,
                scenario,
                expected,
                "Invalid credentials did not authenticate the user.",
                "PASS"
        );

    } catch (AssertionError | RuntimeException e) {

        ExcelReporter.saveResult(
                testCaseId,
                scenario,
                expected,
                e.getMessage(),
                "FAIL"
        );

        throw e;
    }
  }

  @Test
  public void rememberMeCheckboxWorks() {

    String testCaseId = "TC-LOGIN-006";
    String scenario = "Verify Remember Me checkbox";
    String expected = "Remember Me checkbox can be selected and deselected.";

    try {
        helper.open(TestConfig.LOGIN_URL);

        WebElement rememberMe =
                helper.waitForElement(
                        Locators.REMEMBER_ME,
                        10
                );

        Assert.assertFalse(
                rememberMe.isSelected(),
                "Remember Me is selected by default."
        );

        rememberMe.click();

        Assert.assertTrue(
                rememberMe.isSelected(),
                "Remember Me checkbox could not be selected."
        );

        rememberMe.click();

        Assert.assertFalse(
                rememberMe.isSelected(),
                "Remember Me checkbox could not be deselected."
        );

        ExcelReporter.saveResult(
                testCaseId,
                scenario,
                expected,
                "Remember Me checkbox works correctly.",
                "PASS"
        );

    } catch (AssertionError | RuntimeException e) {

        ExcelReporter.saveResult(
                testCaseId,
                scenario,
                expected,
                e.getMessage(),
                "FAIL"
        );

        throw e;
    }
  }
}