package tests;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.ExcelReporter;
import utils.TestAccount;
import utils.TestBase;
import utils.TestConfig;

public class MyListingsTest extends TestBase {
    private WebDriverWait uiWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private WebElement listingCard(String itemName) {
        String headingXpath = "//h3[normalize-space()="
                + TestAccount.xpathLiteral(itemName) + "]";

        WebElement heading = uiWait().until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(headingXpath)));

        return heading.findElement(
                By.xpath("./ancestor::*[.//select][1]"));
    }

    @Test
    public void listingStatusCanBeUpdated() {
    String id = "TC-LIST-001";
    String scenario = "Update a listing status";
    String expected = "Changing listing status updates the displayed status.";

    try {
        TestAccount.login(driver);

        String itemName = TestAccount.createListing(driver);

        By headingLocator = By.xpath(
                "//h3[normalize-space()="
                        + TestAccount.xpathLiteral(itemName) + "]"
        );

        WebElement card = listingCard(itemName);

        
        new Select(card.findElement(By.tagName("select")))
                .selectByValue("sold");

        
        uiWait().until(d -> {

            
            List<WebElement> errors =
            d.findElements(By.cssSelector("div.text-red-600"));

            if (!errors.isEmpty() && errors.get(0).isDisplayed()) {
                throw new AssertionError(
                    "Status update API failed: " + errors.get(0).getText()
                );
            }

            List<WebElement> headings =
                    d.findElements(headingLocator);

            if (headings.isEmpty()) {
                return false;
            }

            WebElement updatedCard = headings.get(0).findElement(
                    By.xpath(
                            "./ancestor::div[contains(@class,'shadow-md')][1]"
                    )
            );

            return updatedCard.findElements(
                    By.xpath(".//span[normalize-space()='sold']")
            ).stream().anyMatch(WebElement::isDisplayed);
        });

        Assert.assertTrue(
                listingCard(itemName).findElements(
                        By.xpath(".//span[normalize-space()='sold']")
                ).stream().anyMatch(WebElement::isDisplayed),
                "Listing status did not change to sold."
        );

        TestAccount.deleteListing(driver, itemName);

        ExcelReporter.saveResult(
                id,
                scenario,
                expected,
                "Listing status changed to sold; test data cleaned up.",
                "PASS"
        );

    } catch (AssertionError | RuntimeException e) {
        ExcelReporter.saveResult(
                id, scenario, expected, e.getMessage(), "FAIL"
        );
        throw e;
    }
   }

    @Test
    public void deleteListingAfterConfirmation() {
        String id = "TC-LIST-002";
        String scenario = "Delete a listing after confirmation";
        String expected = "Confirming deletion removes the listing from My Listings.";
        try {
            TestAccount.login(driver);
            String itemName = TestAccount.createListing(driver);
            WebElement card = listingCard(itemName);
            card.findElement(By.xpath(".//button[normalize-space()='Delete']")).click();
            Alert confirmation = uiWait().until(ExpectedConditions.alertIsPresent());
            Assert.assertTrue(confirmation.getText().contains("delete this listing"),
                    "Unexpected delete confirmation message.");
            confirmation.accept();
            uiWait().until(d -> d.findElements(By.xpath(
                    "//h3[normalize-space()=" + TestAccount.xpathLiteral(itemName) + "]")).isEmpty());
            Assert.assertTrue(driver.findElements(By.xpath(
                    "//h3[normalize-space()=" + TestAccount.xpathLiteral(itemName) + "]")).isEmpty(),
                    "Listing remained visible after confirming deletion.");
            ExcelReporter.saveResult(id, scenario, expected,
                    "Confirmed deletion removed listing '" + itemName + "'.", "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void cancelDeleteKeepsListing() {
        String id = "TC-LIST-003";
        String scenario = "Cancel listing deletion";
        String expected = "Cancelling deletion keeps the listing visible.";
        try {
            TestAccount.login(driver);
            String itemName = TestAccount.createListing(driver);
            listingCard(itemName).findElement(By.xpath(".//button[normalize-space()='Delete']")).click();
            Alert confirmation = uiWait().until(ExpectedConditions.alertIsPresent());
            confirmation.dismiss();
            Assert.assertTrue(listingCard(itemName).isDisplayed(),
                    "Listing disappeared after cancelling deletion.");
            ExcelReporter.saveResult(id, scenario, expected,
                    "Cancelled deletion and listing '" + itemName + "' remained visible.", "PASS");
            
            TestAccount.deleteListing(driver, itemName);
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }

    @Test
    public void listingBelongsToAuthenticatedUser() {
        String id = "TC-LIST-004";
        String scenario = "Verify listing ownership for authenticated user";
        String expected = "Created listing seller_id matches the authenticated user's user_id.";
        try {
            TestAccount.login(driver);
            String itemName = TestAccount.createListing(driver);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String script = """
                    const callback = arguments[arguments.length - 1];
                    Promise.all([
                      fetch(arguments[0] + '/auth/user', {credentials: 'include'}).then(r => r.json()),
                      fetch(arguments[0] + '/api/products/my-listings', {credentials: 'include'}).then(r => r.json())
                    ]).then(([user, listings]) => {
                      const item = listings.find(p => p.name === arguments[1]);
                      callback({userId: user.user_id, sellerId: item ? item.seller_id : null, found: !!item});
                    }).catch(e => callback({error: String(e)}));
                    """;
            Object result = js.executeAsyncScript(script, TestConfig.API_URL, itemName);
            Assert.assertNotNull(result, "Ownership check returned no response.");
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) result;
            Assert.assertEquals(response.get("found"), Boolean.TRUE,
                    "Created listing was not returned by the authenticated user's listings endpoint.");
            Assert.assertNotNull(response.get("userId"), "Authenticated user ID was missing.");
            Assert.assertEquals(String.valueOf(response.get("sellerId")),
                    String.valueOf(response.get("userId")),
                    "Listing seller_id does not match the authenticated user.");
            TestAccount.deleteListing(driver, itemName);
            ExcelReporter.saveResult(id, scenario, expected,
                    "Listing '" + itemName + "' belongs to the authenticated test user; test data cleaned up.", "PASS");
        } catch (AssertionError | RuntimeException e) {
            ExcelReporter.saveResult(id, scenario, expected, e.getMessage(), "FAIL");
            throw e;
        }
    }
}
