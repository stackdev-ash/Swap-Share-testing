package utils;

import org.openqa.selenium.By;

public final class Locators {
    private Locators() {
    }

    // Navigation
    public static final By HOME_LINK = LocatorRepository.get("navbar.home");

    public static final By INVENTORY_LINK = LocatorRepository.get("navbar.inventory");

    public static final By SELL_LINK = LocatorRepository.get("navbar.sell");

    public static final By MY_LISTINGS_LINK = LocatorRepository.get("navbar.myListings");

    public static final By CART_LINK = LocatorRepository.get("navbar.cart");

    public static final By LOGIN_LINK = LocatorRepository.get("navbar.login");

    public static final By LOGOUT_BUTTON = LocatorRepository.get("navbar.logout");

    // Home
    public static final By SHOP_NOW_BUTTON = LocatorRepository.get("home.shopNow");

    // Login
    public static final By GOOGLE_LOGIN_BUTTON = LocatorRepository.get("login.googleButton");

    public static final By EMAIL_INPUT = LocatorRepository.get("login.email");

    public static final By PASSWORD_INPUT = LocatorRepository.get("login.password");

    public static final By REMEMBER_ME = LocatorRepository.get("login.rememberMe");

    public static final By SIGN_IN_BUTTON = LocatorRepository.get("login.signIn");

    // Shop
    public static final By SHOP_HEADING = LocatorRepository.get("shop.heading");

    public static final By PRODUCT_CARDS = LocatorRepository.get("shop.productCards");

    public static final By ADD_TO_CART_BUTTONS = LocatorRepository.get("shop.addToCart");

    public static final By VIEW_CART_BUTTON = LocatorRepository.get("shop.viewCart");

    // Cart
    public static final By CART_HEADING = LocatorRepository.get("cart.heading");

    public static final By CART_ITEM_NAMES = LocatorRepository.get("cart.itemNames");
}