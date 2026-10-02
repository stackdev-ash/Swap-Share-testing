import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FirstSeleniumTest {

    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
    }

    @Test
    public void openApplication() {

        driver.get("http://localhost:5173");

        String title = driver.getTitle();

        System.out.println("Page Title: " + title);

        Assert.assertNotNull(title);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}