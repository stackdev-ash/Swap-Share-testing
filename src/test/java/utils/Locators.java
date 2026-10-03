package utils;

import org.openqa.selenium.By;

public class Locators {
    // Navbar
    public static final By HOME_LINK = By.xpath("//a[normalize-space()='Home']");

    public static final By LOGIN_LINK = By.xpath("//a[normalize-space()='Login']");

    public static final By LOGOUT_BUTTON = By.xpath("//button[normalize-space()='Logout']");

    // Login Page

    public static final By GOOGLE_LOGIN_BUTTON = By.xpath("//button[contains(., 'Sign in with Google')]");

    public static final By GOOGLE_ACCOUNT = By.cssSelector("[data-identifier], [data-email]");

    public static final By GOOGLE_CONTINUE_BUTTON = By.xpath(
            "//button[normalize-space()='Continue' " +
                    "or .//span[normalize-space()='Continue']]");

    public static final By EMAIL_INPUT = By.id("email-address");

    public static final By PASSWORD_INPUT = By.id("password");

    public static final By REMEMBER_ME = By.id("remember-me");

    public static final By SIGN_IN_BUTTON = By.xpath("//button[normalize-space()='Sign in']");

    private Locators() {
        // Prevent object creation
    }
}