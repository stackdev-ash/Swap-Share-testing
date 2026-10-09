package tests;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.ExcelReporter;
import utils.Locators;
import utils.TestAccount;
import utils.TestBase;
import utils.TestConfig;

public class CartTest extends TestBase {
    private final WebDriverWait waitForUi() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private String addFirstProductAndOpenCart() {
        TestAccount.login(driver);
        driver.get(TestConfig.APP_URL + "/shop");
        waitForUi().until(ExpectedConditions.visibilityOfElementLocated(Locators.SHOP_HEADING));
        List<WebElement> buttons = driver.findElements(Locators.ADD_TO_CART_BUTTONS);
        Assert.assertFalse(buttons.isEmpty(), "No product is available to add to the cart.");
        buttons.get(0).click();
        Alert alert = waitForUi().until(ExpectedConditions.alertIsPresent());
        Assert.assertEquals(alert.getText(), "Added to cart successfully!");
        alert.accept();
        driver.get(TestConfig.APP_URL + "/cart");
        waitForUi().until(ExpectedConditions.visibilityOfElementLocated(Locators.CART_HEADING));
        return driver.findElement(By.xpath("(//div[.//button[normalize-space()='Remove']]//h3)[1]")).getText().trim();
    }

    private WebElement firstCartItem() {
        return waitForUi().until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//div[contains(@class,'bg-white') and .//button[normalize-space()='Remove']])[1]")));
    }

    private int firstItemQuantity() {
        return Integer
                .parseInt(firstCartItem().findElement(By.xpath(".//span[contains(@class,'px-4')]")).getText().trim());
    }

    private double money(String text) {
        return Double.parseDouble(text.replace("₹", "").replace(",", "").trim());
    }

    @Test
    public void quantityCanIncreaseAndDecrease() {
        String id = "TC-CART-001";
        String scenario = "Increase and decrease cart item quantity";
        String expected = "Quantity increments and decrements correctly for a cart item.";
        try {
            addFirstProductAndOpenCart();
            WebElement item = firstCartItem();
            int initialQuantity = firstItemQuantity();
            item.findElement(By.xpath(".//button[normalize-space()='+']")).click();
            waitForUi().until(d -> firstItemQuantity() == initialQuantity + 1);
            firstCartItem().findElement(By.xpath(".//button[normalize-space()='-']")).click();
            waitForUi().until(d -> firstItemQuantity() == initialQuantity);
            ExcelReporter.saveResult(id, scenario, expected,
                    "Quantity increased to " + (initialQuantity + 1) + " and returned to " + initialQuantity + ".",
                    "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void subtotalAndTotalAreCalculatedCorrectly() {
        String id = "TC-CART-002";
        String scenario = "Verify cart subtotal, shipping and total";
        String expected = "Subtotal equals item price times quantity and total equals subtotal plus ₹150 shipping.";
        try {
            addFirstProductAndOpenCart();
            WebElement item = firstCartItem();
            double unitPrice = money(item.findElement(By.xpath(".//p[contains(.,'₹')]")).getText());
            int quantity = firstItemQuantity();
            double expectedSubtotal = unitPrice * quantity;
            double actualSubtotal = money(
                    driver.findElement(By.xpath("//span[normalize-space()='Subtotal']/following-sibling::span[1]"))
                            .getText());
            double actualShipping = money(
                    driver.findElement(By.xpath("//span[normalize-space()='Shipping']/following-sibling::span[1]"))
                            .getText());
            double actualTotal = money(driver
                    .findElement(By.xpath("//span[normalize-space()='Total']/following-sibling::span[1]")).getText());
            Assert.assertEquals(actualSubtotal, expectedSubtotal, 0.01, "Subtotal is incorrect.");
            Assert.assertEquals(actualShipping, 150.00, 0.01, "Shipping should be ₹150.");
            Assert.assertEquals(actualTotal, expectedSubtotal + 150.00, 0.01, "Order total is incorrect.");
            ExcelReporter.saveResult(id, scenario, expected,
                    String.format("Subtotal ₹%.2f + shipping ₹%.2f = total ₹%.2f.", actualSubtotal, actualShipping,
                            actualTotal),
                    "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void removedItemDisappearsFromCart() {
        String id = "TC-CART-003";
        String scenario = "Remove an item from the shopping cart";
        String expected = "Removed item no longer appears in the cart.";
        try {
            String productName = addFirstProductAndOpenCart();
            firstCartItem().findElement(By.xpath(".//button[normalize-space()='Remove']")).click();
            waitForUi().until(d -> d.findElements(By.xpath(
                    "//div[.//button[normalize-space()='Remove']]//h3[normalize-space()="
                            + TestAccount.xpathLiteral(productName) + "]"))
                    .isEmpty());
            boolean itemStillPresent = driver.findElements(By.xpath(
                    "//div[.//button[normalize-space()='Remove']]//h3[normalize-space()="
                            + TestAccount.xpathLiteral(productName) + "]"))
                    .size() > 0;
            Assert.assertFalse(itemStillPresent, "Removed product is still displayed in the cart.");
            ExcelReporter.saveResult(id, scenario, expected,
                    "Removed product '" + productName + "' is no longer displayed.", "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void cartContentsPersistAfterReload() {
        String id = "TC-CART-004";
        String scenario = "Cart contents persist after page reload";
        String expected = "The same cart item and quantity remain after reload.";
        try {
            String productName = addFirstProductAndOpenCart();
            int quantityBefore = firstItemQuantity();
            helper.refresh();
            waitForUi().until(ExpectedConditions.visibilityOfElementLocated(Locators.CART_HEADING));
            waitForUi().until(d -> !d.findElements(By.xpath(
                    "//div[.//button[normalize-space()='Remove']]//h3[normalize-space()="
                            + TestAccount.xpathLiteral(productName) + "]"))
                    .isEmpty());
            int quantityAfter = firstItemQuantity();
            Assert.assertEquals(quantityAfter, quantityBefore, "Cart quantity changed after reload.");
            ExcelReporter.saveResult(id, scenario, expected,
                    "Product '" + productName + "' remained in cart with quantity " + quantityAfter + " after reload.",
                    "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }
}
