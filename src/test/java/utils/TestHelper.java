package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TestHelper {

    private final WebDriver driver;


    public TestHelper(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String url) {

        driver.get(url);
    }


    public String getTitle() {

        return driver.getTitle();
    }


    public String getCurrentUrl() {

        return driver.getCurrentUrl();
    }


    public WebElement find(By locator) {

        return driver.findElement(locator);
    }


    public boolean isDisplayed(By locator) {

        List<WebElement> elements =
                driver.findElements(locator);

        return !elements.isEmpty()
                && elements.get(0).isDisplayed();
    }


    public void click(By locator) {
    WebElement element = find(locator);

    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
        element
    );

    element.click();
}


    public void type(
            By locator,
            String text
    ) {

        WebElement element =
                find(locator);

        element.clear();

        element.sendKeys(text);
    }


    public boolean isSelected(By locator) {

        return find(locator).isSelected();
    }


    public void refresh() {

        driver.navigate().refresh();
    }

    public WebElement waitForElement(
            By locator,
            int seconds
    ) {

        long endTime =
                System.currentTimeMillis()
                        + (seconds * 1000L);

        while (
                System.currentTimeMillis()
                        < endTime
        ) {

            if (isDisplayed(locator)) {
                return find(locator);
            }

            sleep(250);
        }

        throw new RuntimeException(
                "Element not found: " + locator
        );
    }


    public void sleep(long milliseconds) {

        try {

            Thread.sleep(milliseconds);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}