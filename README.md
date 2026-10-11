# Swap & Share – Selenium Automation Testing

## Overview
Swap & Share is a community-focused marketplace where users can discover pre-owned items and buy, sell, or manage listings. This repository contains a **Java-based Selenium automation suite** built to verify important user-facing workflows and help catch regressions during development.

The goal of the testing project is to check that core features behave as expected—not just that pages open—including navigation, product discovery, cart actions, login, and listing-form validation. Test outcomes are also recorded in an Excel report for easier review.

## What Is Tested?
- **Home page:** application loading and key page content.
- **Authentication:** login using a configured test account.
- **Navigation:** moving between the home page and inventory/shop pages.
- **Products:** inventory display and product-related interactions.
- **Cart:** adding items, checking cart contents, updating quantities, and removing items where supported.
- **Sell form:** required-field validation and listing creation checks.
- **Test reporting:** records test case ID, scenario, expected result, actual result, and PASS/FAIL status in an Excel workbook.

## Technology Stack
- **Java 21**
- **Selenium WebDriver 4.49.0** – browser automation
- **Maven** – dependency and test management
- **Apache POI** – Excel test-result reporting
- **React, Vite, Node.js, Express, PostgreSQL/Supabase** – application components included in the repository

## Project Structure
```text
Swap&Share/
├── pom.xml
├── src/test/java/
│   ├── FirstSeleniumTest.java
│   ├── tests/
│   │   ├── HomeTest.java
│   │   ├── LoginTest.java
│   │   ├── NavigationTest.java
│   │   ├── ProductTest.java
│   │   ├── CartTest.java
│   │   └── SellTest.java
│   └── utils/
│       ├── DriverFactory.java
│       ├── TestBase.java
│       ├── TestConfig.java
│       ├── TestHelper.java
│       ├── Locators.java
│       ├── TestAccount.java
│       └── ExcelReporter.java
├── test-results/
└── Swap-and-Share/        # application source (client, server, and JS tests)
```

## Run the Tests
### Prerequisites
Install **JDK 21**, **Maven**, and **Google Chrome**. The application and a usable test account must be available before running tests that require authentication or data.

1. Start the Swap & Share frontend and backend using the setup instructions and environment variables for the application.
2. Check the values in `src/test/java/utils/TestConfig.java`. By default, the frontend URL is `http://localhost:5173` and the API URL is `http://localhost:5000`.
3. From the repository root, run:

```bash
mvn clean test
```

TestNG and Maven Surefire run the automated tests. Results are written to `test-results/selenium_test_results.xlsx` when the test reporter is able to save them.

## Configuration Notes
- Update the application URLs and test credentials in `TestConfig.java` / `TestAccount.java` to match your local test environment.
- Use a dedicated test account and test data. Avoid putting real credentials or secrets into a public repository.
- Some tests depend on existing products or an authenticated session, so their results can depend on the current application data and configuration.
- The repository also includes a separate JavaScript Selenium suite under `Swap-and-Share/selenium/`; the Maven command above runs the Java tests.

## Topics Covered
Web UI automation, Selenium WebDriver, Java testing, TestNG assertions, Maven, test case design, reusable test utilities, explicit waits, regression testing, form validation, browser interaction, and Excel-based test reporting.
