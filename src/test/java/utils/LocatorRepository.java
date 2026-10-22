package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.By;

public final class LocatorRepository {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = LocatorRepository.class
                .getClassLoader()
                .getResourceAsStream("locators.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "locators.properties was not found.");
            }

            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private LocatorRepository() {
    }

    public static By get(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing locator key: " + key);
        }

        int separator = value.indexOf(':');

        if (separator <= 0) {
            throw new IllegalArgumentException(
                    "Invalid locator format for key: " + key);
        }

        String type = value.substring(0, separator).trim();
        String selector = value.substring(separator + 1).trim();

        return switch (type.toLowerCase()) {
            case "id" -> By.id(selector);
            case "name" -> By.name(selector);
            case "xpath" -> By.xpath(selector);
            case "css" -> By.cssSelector(selector);
            case "class" -> By.className(selector);
            case "tag" -> By.tagName(selector);
            case "linktext" -> By.linkText(selector);
            case "partiallinktext" ->
                By.partialLinkText(selector);
            default -> throw new IllegalArgumentException(
                    "Unsupported locator type: " + type);
        };
    }
}
