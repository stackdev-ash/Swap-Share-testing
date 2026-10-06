package tests;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.ExcelReporter;
import utils.Locators;
import utils.TestBase;
import utils.TestConfig;

import java.time.Duration;

public class ProductTest extends TestBase {

    @Test
    public void productInventoryLoads() {
        String id = "TC-PROD-001";
        String scenario = "Load available products in Inventory";
        String expected = "Inventory should load successfully and display products when products exist.";

        try {
            loginUsingTestAccount();
            helper.open(TestConfig.APP_URL + "/shop");
            helper.waitForElement(Locators.SHOP_HEADING, 10);

            List<WebElement> products = driver.findElements(Locators.PRODUCT_CARDS);

            if (products.isEmpty()) {
                WebElement emptyState = driver.findElement(
                        By.xpath("//*[normalize-space()='No products available at the moment.']"));

                Assert.assertTrue(
                        emptyState.isDisplayed(),
                        "Inventory is empty but the expected empty-state message was not displayed.");

                ExcelReporter.saveResult(
                        id, scenario, expected,
                        "Inventory loaded and displayed the expected empty-state message.",
                        "PASS");
                return;
            }

            Assert.assertTrue(
                    products.size() > 0,
                    "Inventory page opened but no product cards were found.");

            ExcelReporter.saveResult(
                    id, scenario, expected,
                    "Inventory loaded with " + products.size() + " product card(s).",
                    "PASS");

        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test(dependsOnMethods = "productInventoryLoads")
    public void productCardDisplaysRequiredInformation() {
        String id = "TC-PROD-002";
        String scenario = "Verify product card displays essential product information";
        String expected = "A product card should display name, price, product details, and Add to Cart.";

        try {
            helper.open(TestConfig.APP_URL + "/shop");
            helper.waitForElement(Locators.SHOP_HEADING, 10);

            List<WebElement> products = driver.findElements(Locators.PRODUCT_CARDS);

            Assert.assertFalse(
                    products.isEmpty(),
                    "No product is available for product-card validation.");

            WebElement product = products.get(0);

            String name = product.findElement(By.tagName("h3")).getText().trim();

            List<WebElement> paragraphs = product.findElements(By.tagName("p"));
            String details = "";
            for (WebElement paragraph : paragraphs) {
                details += " " + paragraph.getText().trim();
            }

            WebElement addButton = product.findElement(
                    By.xpath(".//button[normalize-space()='Add to Cart']"));

            Assert.assertFalse(name.isBlank(), "Product name is missing.");
            Assert.assertTrue(details.contains("₹"), "Product price is missing.");
            Assert.assertFalse(details.trim().isBlank(), "Product details are missing.");
            Assert.assertTrue(addButton.isDisplayed(), "Add to Cart button is not displayed.");

            ExcelReporter.saveResult(
                    id, scenario, expected,
                    "Product '" + name + "' displayed name, price/details, and Add to Cart.",
                    "PASS");

        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test(dependsOnMethods = "productCardDisplaysRequiredInformation")
    public void addProductToCart() {
        String id = "TC-PROD-003";
        String scenario = "Add an available product to the shopping cart";
        String expected = "Authenticated user should be able to add a product and see it in the cart.";

        try {
            loginUsingTestAccount();

            helper.open(TestConfig.APP_URL + "/shop");
            helper.waitForElement(Locators.SHOP_HEADING, 10);

            List<WebElement> products = driver.findElements(Locators.PRODUCT_CARDS);

            Assert.assertFalse(
                    products.isEmpty(),
                    "No product is available for the cart test.");

            WebElement product = products.get(0);

            String productName = product.findElement(By.tagName("h3")).getText().trim();

            product.findElement(
                    By.xpath(".//button[normalize-space()='Add to Cart']")).click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            String alertText = alert.getText();

            Assert.assertEquals(
                    alertText,
                    "Added to cart successfully!");

            alert.accept();

            helper.open(TestConfig.APP_URL + "/cart");
            helper.waitForElement(Locators.CART_HEADING, 10);

            List<WebElement> cartItems = driver.findElements(Locators.CART_ITEM_NAMES);

            boolean found = false;

            for (WebElement item : cartItems) {
                if (item.getText().trim().equals(productName)) {
                    found = true;
                    break;
                }
            }

            Assert.assertTrue(
                    found,
                    "Added product '" + productName +
                            "' was not found in the shopping cart.");

            ExcelReporter.saveResult(
                    id, scenario, expected,
                    "Product '" + productName +
                            "' was added and verified in the Shopping Cart.",
                    "PASS");

        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }
@Test
public void removeProductFromCart() {
    String id = "TC-PROD-004";
    String scenario = "Remove an added product from the shopping cart";
    String expected = "Authenticated user should be able to remove a product from the cart.";

    try {
        loginUsingTestAccount();

        helper.open(TestConfig.APP_URL + "/shop");
        helper.waitForElement(Locators.SHOP_HEADING, 10);

        List<WebElement> products = driver.findElements(Locators.PRODUCT_CARDS);

        Assert.assertFalse(
                products.isEmpty(),
                "No product is available for the remove-from-cart test."
        );

        WebElement product = products.get(0);

        String productName =
                product.findElement(By.tagName("h3")).getText().trim();

        product.findElement(
                By.xpath(".//button[normalize-space()='Add to Cart']")
        ).click();

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(5));

        Alert alert =
                wait.until(ExpectedConditions.alertIsPresent());

        String alertText = alert.getText();

        Assert.assertEquals(
                alertText,
                "Added to cart successfully!"
        );

        alert.accept();

        helper.open(TestConfig.APP_URL + "/cart");
        helper.waitForElement(Locators.CART_HEADING, 10);

        By removeButton = By.xpath(
                "//div[.//h3[normalize-space()='" + productName + "']]"
                        + "//button[normalize-space()='Remove']"
        );

        WebElement remove =
                driver.findElement(removeButton);

        Assert.assertTrue(
                remove.isDisplayed(),
                "Remove button is not displayed for the added product."
        );

        remove.click();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(removeButton)
        );

        List<WebElement> remainingItems =
                driver.findElements(Locators.CART_ITEM_NAMES);

        boolean found = false;

        for (WebElement item : remainingItems) {
            if (item.getText().trim().equals(productName)) {
                found = true;
                break;
            }
        }

        Assert.assertFalse(
                found,
                "Product '" + productName
                        + "' was still found in the shopping cart after removal."
        );

        ExcelReporter.saveResult(
                id,
                scenario,
                expected,
                "Product '" + productName
                        + "' was removed successfully from the Shopping Cart.",
                "PASS"
        );

    } catch (AssertionError | RuntimeException e) {
        ExcelReporter.saveResult(
                id,
                scenario,
                expected,
                e.getMessage(),
                "FAIL"
        );
        throw e;
    }
}
    private void loginUsingTestAccount() {
        helper.open(TestConfig.APP_URL);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        String script = """
                const callback = arguments[arguments.length - 1];

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

        Object result = js.executeAsyncScript(
                script,
                TestConfig.API_URL + "/auth/test-login",
                TestConfig.TEST_EMAIL,
                TestConfig.TEST_PASSWORD);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) result;

        int status = ((Number) response.get("status")).intValue();

        Assert.assertEquals(
                status,
                200,
                "Temporary Selenium login failed: " + response.get("body"));

        helper.open(TestConfig.APP_URL);
        helper.waitForElement(Locators.LOGOUT_BUTTON, 10);
    }
}
