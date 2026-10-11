package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import utils.ExcelReporter;
import utils.Locators;
import utils.TestBase;
import utils.TestConfig;

public class HomeTest extends TestBase {
    @Test
    public void applicationOpensSuccessfully() {

        String testCaseId = "TC-HOME-001";

        String scenario = "Application opens successfully";

        String expected = "Swap&Share application loads successfully";

        try {

            helper.open(TestConfig.APP_URL);

            String title = helper.getTitle();

            Assert.assertEquals(
                    title,
                    "Swap&Share");

            ExcelReporter.saveResult(
                    testCaseId,
                    scenario,
                    expected,
                    "Application opened with title: " + title,
                    "PASS");

        } catch (AssertionError | Exception e) {

            ExcelReporter.saveResult(
                    testCaseId,
                    scenario,
                    expected,
                    "Test failed: " + e.getMessage(),
                    "FAIL");

            throw e;
        }
    }

    @Test
    public void homePageContentIsDisplayed() {

    String testCaseId = "TC-HOME-002";
    String scenario = "Verify home page content";
    String expected = "Home page displays main heading, description and Shop Now button.";

    try {
        helper.open(TestConfig.APP_URL);

        WebElement heading =
                helper.waitForElement(
                        By.xpath("//h1[contains(normalize-space(), 'Sustainable Style')]"),
                        10
                );

        WebElement description =
                helper.waitForElement(
                        By.xpath("//p[contains(normalize-space(), 'Find unique second-hand clothing')]"),
                        10
                );

        WebElement shopNow =
                helper.waitForElement(
                        Locators.SHOP_NOW_BUTTON,
                        10
                );

        Assert.assertTrue(heading.isDisplayed());
        Assert.assertTrue(description.isDisplayed());
        Assert.assertTrue(shopNow.isDisplayed());

        ExcelReporter.saveResult(
                testCaseId,
                scenario,
                expected,
                "Home page content is displayed correctly.",
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
  public void shopNowNavigatesToShop() {

    String testCaseId = "TC-HOME-003";
    String scenario = "Verify Shop Now navigation";
    String expected = "Clicking Shop Now navigates user to the Shop page.";

    try {
        helper.open(TestConfig.APP_URL);

        helper.click(Locators.SHOP_NOW_BUTTON);

        helper.sleep(1000);

        Assert.assertEquals(
                driver.getCurrentUrl(),
                TestConfig.APP_URL + "/shop",
                "Shop Now did not navigate to Shop page."
        );

        ExcelReporter.saveResult(
                testCaseId,
                scenario,
                expected,
                "Shop Now successfully navigated to /shop.",
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