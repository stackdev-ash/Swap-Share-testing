package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static WebDriver createDriver() {

        ChromeOptions options = new ChromeOptions();

        // Connect to the Chrome instance started manually
        options.setExperimentalOption(
                "debuggerAddress",
                "127.0.0.1:9222"
        );

        return new ChromeDriver(options);
    }
}