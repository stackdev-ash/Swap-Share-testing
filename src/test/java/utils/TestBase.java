package utils;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class TestBase {

    protected WebDriver driver;
    protected TestHelper helper;

    @BeforeMethod
    public void setUp() {

        driver =
                DriverFactory.createDriver();

        helper =
                new TestHelper(driver);
    }

    @AfterMethod
    public void tearDown() {

        DriverFactory.quitDriver(driver);
    }
}