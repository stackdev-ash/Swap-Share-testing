package utils;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class TestBase {

    protected WebDriver driver;
    protected TestHelper helper;

    @BeforeClass
    public void setUp() {
        driver = DriverFactory.createDriver();
        helper = new TestHelper(driver);
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver(driver);
    }
}