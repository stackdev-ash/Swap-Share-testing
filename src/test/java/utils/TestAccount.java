package utils;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

// helpers for tests
public final class TestAccount {
        private TestAccount() {
        }

        public static void login(WebDriver driver) {
                driver.get(TestConfig.APP_URL);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String script = """
                                const callback = arguments[arguments.length - 1];
                                fetch(arguments[0], {
                                  method: 'POST',
                                  credentials: 'include',
                                  headers: {'Content-Type': 'application/json'},
                                  body: JSON.stringify({email: arguments[1], password: arguments[2]})
                                }).then(async response => callback({status: response.status, body: await response.text()}))
                                  .catch(error => callback({status: 0, body: String(error)}));
                                """;
                Object result = js.executeAsyncScript(
                                script,
                                TestConfig.API_URL + "/auth/test-login",
                                TestConfig.TEST_EMAIL,
                                TestConfig.TEST_PASSWORD);
                Assert.assertNotNull(result, "Test login request returned no response.");
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) result;
                Assert.assertEquals(((Number) response.get("status")).intValue(), 200,
                                "Test login failed: " + response.get("body"));
                driver.get(TestConfig.APP_URL);
                new WebDriverWait(driver, Duration.ofSeconds(10))
                                .until(ExpectedConditions.visibilityOfElementLocated(Locators.LOGOUT_BUTTON));
        }

        /**
         * Creates a uniquely named listing through the real Sell form and returns its
         * name.
         */
        public static String createListing(WebDriver driver) {
                String itemName = "Selenium QA Item " + System.currentTimeMillis();
                driver.get(TestConfig.APP_URL + "/sell");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//h2[normalize-space()='List Your Item for Sale']")));

                driver.findElement(By.name("name")).sendKeys(itemName);
                driver.findElement(By.name("description"))
                                .sendKeys("Automated test listing created by Selenium.");
                driver.findElement(By.name("price")).sendKeys("1234");
                driver.findElement(By.name("original_price")).sendKeys("2000");
                driver.findElement(By.name("brand")).sendKeys("Selenium QA");
                new org.openqa.selenium.support.ui.Select(driver.findElement(By.name("size")))
                                .selectByVisibleText("M");
                driver.findElement(By.name("image_url"))
                                .sendKeys("https://example.com/selenium-test-item.jpg");
                driver.findElement(By
                                .xpath("//button[@type='submit' and normalize-space()='List Item']"))
                                .click();
                wait.until(ExpectedConditions.urlContains("/my-listings"));
                try {
                        wait.until(ExpectedConditions.visibilityOfElementLocated(
                                        By.xpath("//h3[normalize-space()="
                                                        + xpathLiteral(itemName) + "]")));
                } catch (org.openqa.selenium.TimeoutException e) {
                        System.out.println("=== LISTING DEBUG ===");
                        System.out.println("Expected item: " + itemName);
                        System.out.println("Current URL: " + driver.getCurrentUrl());
                        System.out.println("Page text: "
                                        + driver.findElement(By.tagName("body")).getText());

                        throw e;
                }

                return itemName;
        }

        /**
         * Deletes only the uniquely named listing created by a test and confirms the
         * browser prompt.
         */
        public static void deleteListing(WebDriver driver, String itemName) {
                By card = By.xpath(
                                "//div[contains(@class,'shadow-md') and .//h3[normalize-space()="
                                                + xpathLiteral(itemName) + "]]");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.visibilityOfElementLocated(card))
                                .findElement(By.xpath(".//button[normalize-space()='Delete']"))
                                .click();
                wait.until(ExpectedConditions.alertIsPresent()).accept();
                wait.until(d -> d.findElements(By.xpath(
                                "//h3[normalize-space()=" + xpathLiteral(itemName) + "]")).isEmpty());
        }

        public static String xpathLiteral(String value) {
                if (!value.contains("'"))
                        return "'" + value + "'";
                if (!value.contains("\""))
                        return "\"" + value + "\"";
                String[] parts = value.split("'", -1);
                StringBuilder expression = new StringBuilder("concat(");
                for (int i = 0; i < parts.length; i++) {
                        if (i > 0)
                                expression.append(", \"'\", ");
                        expression.append("'").append(parts[i]).append("'");
                }
                return expression.append(")").toString();
        }
}
