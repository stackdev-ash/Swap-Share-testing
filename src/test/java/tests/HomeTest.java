package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import utils.ExcelReporter;
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
}