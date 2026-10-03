package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--start-maximized");

        return new ChromeDriver(options);
    }

    public static void quitDriver(WebDriver driver) {

        if (driver != null) {
            driver.quit();
        }
    }
}