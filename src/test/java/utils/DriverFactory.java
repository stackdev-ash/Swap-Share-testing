package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DriverFactory {

    public static WebDriver createDriver() {

        ChromeOptions options = new ChromeOptions();

        // Separate Chrome profile.
        Path seleniumProfile = Paths.get(
                System.getProperty("user.home"),
                "selenium-profiles",
                "swap-share"
        );

        options.addArguments(
                "--user-data-dir=" + seleniumProfile.toString()
        );

        return new ChromeDriver(options);
    }
}