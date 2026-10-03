package utils;

public class TestConfig {

    public static final String APP_URL = "http://localhost:5173";

    public static final String API_URL = "http://localhost:5000";

    public static final String LOGIN_URL = APP_URL + "/login";

    public static final String TEST_EMAIL = "selenium.test@gmail.com";

    public static final String TEST_PASSWORD = "SeleniumTest@123";

    public static final String EXCEL_FILE = "test-results/selenium_test_results.xlsx";

    private TestConfig() {
        // Prevent object creation
    }
}