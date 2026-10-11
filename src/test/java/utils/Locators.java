package utils;

import org.openqa.selenium.By;

public class Locators {
    // Navbar
    public static final By HOME_LINK = By.xpath("//a[normalize-space()='Home']");
    public static final By INVENTORY_LINK = By.xpath("//a[normalize-space()='Inventory']");
    public static final By SELL_LINK = By.xpath("//a[normalize-space()='Sell/Donate']");
    public static final By MY_LISTINGS_LINK = By.xpath("//a[normalize-space()='My Listings']");
    public static final By CART_LINK = By.xpath("//a[@href='/cart']");
    public static final By LOGIN_LINK = By.xpath("//a[normalize-space()='Login']");
    public static final By LOGOUT_BUTTON = By.xpath("//button[normalize-space()='Logout']");

    // Home
    public static final By SHOP_NOW_BUTTON = By.xpath("//a[normalize-space()='Shop Now']");

    // Login Page
    public static final By GOOGLE_LOGIN_BUTTON = By.xpath("//button[contains(., 'Sign in with Google')]");
    public static final By GOOGLE_ACCOUNT = By.cssSelector("[data-identifier], [data-email]");
    public static final By GOOGLE_CONTINUE_BUTTON = By.xpath("//button[normalize-space()='Continue' or .//span[normalize-space()='Continue']]");
    public static final By EMAIL_INPUT = By.id("email-address");
    public static final By PASSWORD_INPUT = By.id("password");
    public static final By REMEMBER_ME = By.id("remember-me");
    public static final By SIGN_IN_BUTTON = By.xpath("//button[normalize-space()='Sign in']");

    // Shop
    public static final By SHOP_HEADING = By.xpath("//h1[normalize-space()='Shop']");
    public static final By PRODUCT_CARDS = By.xpath("//div[.//button[normalize-space()='Add to Cart'] and .//h3]");
    public static final By ADD_TO_CART_BUTTONS = By.xpath("//button[normalize-space()='Add to Cart']");
    public static final By VIEW_CART_BUTTON = By.xpath("//button[normalize-space()='View Cart']");

    // Cart
    public static final By CART_HEADING = By.xpath("//h1[normalize-space()='Shopping Cart']");
    public static final By CART_ITEM_NAMES = By.xpath("//div[.//button[normalize-space()='Remove']]//h3");

    private Locators() {}
}