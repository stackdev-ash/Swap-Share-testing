package tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.ExcelReporter;
import utils.TestAccount;
import utils.TestBase;
import utils.TestConfig;

public class SellTest extends TestBase {
    private WebDriverWait uiWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void requiredFieldsPreventEmptyListingSubmission() {
        String id = "TC-SELL-001";
        String scenario = "Validate required fields on Sell form";
        String expected = "Form cannot be submitted when required fields are empty.";
        try {
            TestAccount.login(driver);
            driver.get(TestConfig.APP_URL + "/sell");
            uiWait().until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h2[normalize-space()='List Your Item for Sale']")));
            WebElement name = driver.findElement(By.name("name"));
            name.clear();
            boolean formValid = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('form').checkValidity();");
            Assert.assertFalse(formValid, "Empty required fields should make the form invalid.");
            Assert.assertTrue(name.getAttribute("required") != null,
                    "Item Name should be marked as required.");
            ExcelReporter.saveResult(id, scenario, expected,
                    "Browser form validation rejected submission with required fields empty.", "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void validListingCanBeCreatedAndFoundInMyListings() {
        String id = "TC-SELL-002";
        String scenario = "Create a listing and verify it in My Listings";
        String expected = "Valid listing is saved and displayed in the authenticated user's My Listings.";
        try {
            TestAccount.login(driver);
            String itemName = TestAccount.createListing(driver);
           Assert.assertTrue(uiWait().until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//h3[normalize-space()=" + TestAccount.xpathLiteral(itemName) + "]"))).isDisplayed(),
        "Created listing was not displayed in My Listings.");
            Assert.assertTrue(driver.findElement(By.xpath(
                    "//h3[normalize-space()=" + TestAccount.xpathLiteral(itemName) + "]")).isDisplayed(),
                    "Created listing was not displayed in My Listings.");
            TestAccount.deleteListing(driver, itemName);
            ExcelReporter.saveResult(id, scenario, expected,
                    "Created listing '" + itemName + "' was found in My Listings; test data cleaned up.", "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void negativeSellingPriceIsRejected() {

    String id = "TC-SELL-003";
    String scenario = "Reject negative selling price";
    String expected = "Negative selling price fails browser validation.";

    try {
        TestAccount.login(driver);
        driver.get(TestConfig.APP_URL + "/sell");

        uiWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.name("price")));

        WebElement price = driver.findElement(By.name("price"));

        Assert.assertEquals(
                price.getAttribute("min"),
                "0",
                "Minimum price should be zero."
        );

        Boolean valid = (Boolean) ((JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].value = '-1';" +
                        "return arguments[0].checkValidity();",
                        price
                );

        Assert.assertFalse(
                valid,
                "Negative selling price should be invalid."
        );

        ExcelReporter.saveResult(
                id, scenario, expected,
                "Browser validation rejected the negative price.", "PASS"
        );

    } catch (AssertionError | RuntimeException e) {
        ExcelReporter.saveResult(
                id, scenario, expected, e.getMessage(), "FAIL"
        );
        throw e;
    }
  }

  @Test
   public void dropdownOptionsAndDefaultsAreCorrect() {

    String id = "TC-SELL-004";
    String scenario = "Verify listing dropdowns";
    String expected = "Dropdowns have the correct defaults and options.";

    try {
        TestAccount.login(driver);
        driver.get(TestConfig.APP_URL + "/sell");

        uiWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.name("size")));

        Select size = new Select(driver.findElement(By.name("size")));
        Select condition = new Select(driver.findElement(By.name("condition")));
        Select category = new Select(driver.findElement(By.name("category")));
        Select gender = new Select(driver.findElement(By.name("gender")));
        Select ageGroup = new Select(driver.findElement(By.name("age_group")));

        Assert.assertEquals(
                size.getFirstSelectedOption().getText().trim(),
                "Select Size"
        );

        Assert.assertEquals(
                condition.getFirstSelectedOption().getText(),
                "New"
        );

        Assert.assertEquals(
                category.getFirstSelectedOption().getText(),
                "Clothing"
        );

        Assert.assertEquals(
                gender.getFirstSelectedOption().getText(),
                "Unisex"
        );

        Assert.assertEquals(
                ageGroup.getFirstSelectedOption().getText(),
                "Adult"
        );

        Assert.assertTrue(
                size.getOptions().stream()
                        .anyMatch(o -> o.getText().trim().equals("M")),
                "Medium size option is missing."
        );

        Assert.assertTrue(
                condition.getOptions().stream()
                        .anyMatch(o -> o.getText().equals("Poor")),
                "Poor condition option is missing."
        );

        Assert.assertTrue(
                category.getOptions().stream()
                        .anyMatch(o -> o.getText().equals("Shoes")),
                "Shoes category is missing."
        );

        ExcelReporter.saveResult(
                id, scenario, expected,
                "Dropdown defaults and options are correct.", "PASS"
        );

    } catch (AssertionError | RuntimeException e) {
        ExcelReporter.saveResult(
                id, scenario, expected, e.getMessage(), "FAIL"
        );
        throw e;
    }
  }
}
