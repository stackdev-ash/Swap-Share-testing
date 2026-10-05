package tests;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.ExcelReporter;
import utils.Locators;
import utils.TestBase;
import utils.TestConfig;

public class NavigationTest extends TestBase {

    @Test
    public void homeToInventory() {
        String id = "TC-NAV-001";
        String scenario = "Navigate from Home to Inventory as an authenticated user";
        String expected = "Authenticated user should reach Inventory and see the Shop heading.";
        try {
            loginUsingTestAccount();
            helper.open(TestConfig.APP_URL);
            helper.waitForElement(Locators.INVENTORY_LINK, 10);
            helper.click(Locators.INVENTORY_LINK);
            helper.waitForElement(Locators.SHOP_HEADING, 10);
            Assert.assertTrue(driver.getCurrentUrl().endsWith("/shop"), "Inventory did not open /shop.");
            ExcelReporter.saveResult(id, scenario, expected, "Inventory opened successfully and Shop heading was displayed.", "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void homeShopNowNavigation() {
        String id = "TC-NAV-002";
        String scenario = "Navigate to Inventory using Home page Shop Now";
        String expected = "Shop Now should open the Inventory page for an authenticated user.";
        try {
            loginUsingTestAccount();
            helper.open(TestConfig.APP_URL);
            helper.waitForElement(Locators.SHOP_NOW_BUTTON, 10);
            helper.click(Locators.SHOP_NOW_BUTTON);
            helper.waitForElement(Locators.SHOP_HEADING, 10);
            Assert.assertTrue(driver.getCurrentUrl().endsWith("/shop"), "Shop Now did not open /shop.");
            ExcelReporter.saveResult(id, scenario, expected, "Shop Now successfully opened Inventory.", "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void unauthenticatedSellAccess() {
        runProtectedNavigationCase("TC-NAV-003", "Unauthenticated access to Sell/Donate", "Unauthenticated user should be redirected to Login.", Locators.SELL_LINK, "Sell/Donate access redirected to Login.");
    }

    @Test
    public void unauthenticatedCartAccess() {
        runProtectedNavigationCase("TC-NAV-004", "Unauthenticated access to Shopping Cart", "Unauthenticated user should be redirected to Login.", Locators.CART_LINK, "Cart access redirected to Login.");
    }

    private void runProtectedNavigationCase(String id, String scenario, String expected, By link, String actual) {
        try {
            logoutUsingTestAccount();
            helper.open(TestConfig.APP_URL);
            helper.waitForElement(link, 10);
            helper.click(link);
            helper.waitForElement(Locators.LOGIN_LINK, 10);
            Assert.assertTrue(driver.getCurrentUrl().endsWith("/login"), "Protected route did not redirect to /login.");
            ExcelReporter.saveResult(id, scenario, expected, actual, "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    private void loginUsingTestAccount() {
        helper.open(TestConfig.APP_URL);
        apiAuth("/auth/test-login");
        helper.open(TestConfig.APP_URL);
        helper.waitForElement(Locators.LOGOUT_BUTTON, 10);
    }

    private void logoutUsingTestAccount() {
        helper.open(TestConfig.APP_URL);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script = """
                const callback = arguments[arguments.length - 1];
                fetch(arguments[0], { method: 'POST', credentials: 'include' })
                  .then(r => callback(r.status))
                  .catch(() => callback(0));
                """;
        Object result = js.executeAsyncScript(script, TestConfig.API_URL + "/auth/logout");
        int status = ((Number) result).intValue();
        Assert.assertTrue(status == 200 || status == 204, "Logout request failed with status " + status);
    }

    private void apiAuth(String path) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script = """
                const callback = arguments[arguments.length - 1];
                fetch(arguments[0], {
                    method: 'POST', credentials: 'include',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({email: arguments[1], password: arguments[2]})
                }).then(async r => callback({status:r.status, body:await r.text()}))
                  .catch(e => callback({status:0, body:String(e)}));
                """;
        Object result = js.executeAsyncScript(script, TestConfig.API_URL + path, TestConfig.TEST_EMAIL, TestConfig.TEST_PASSWORD);
        @SuppressWarnings("unchecked") Map<String,Object> response = (Map<String,Object>) result;
        int status = ((Number) response.get("status")).intValue();
        Assert.assertEquals(status, 200, "Temporary Selenium login failed: " + response.get("body"));
    }
}
