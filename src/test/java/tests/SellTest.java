package tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
            Assert.assertTrue(driver.getCurrentUrl().endsWith("/my-listings"),
                    "Successful listing did not navigate to My Listings.");
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
}
