const { By, until, Builder } = require("selenium-webdriver");
const chrome = require("selenium-webdriver/chrome");
const XLSX = require("xlsx");
const fs = require("fs");
const path = require("path");

const excelFile = path.join(__dirname, "selenium_test_results.xlsx");


function saveResult(testCaseId, scenario, expectedResult, actualResult, status) {

    let workbook;
    let worksheet;

    // Open existing Excel file
    if (fs.existsSync(excelFile)) {

        workbook = XLSX.readFile(excelFile);

        if (workbook.Sheets["Test Results"]) {
            worksheet = workbook.Sheets["Test Results"];
        } else {

            worksheet = XLSX.utils.aoa_to_sheet([
                [
                    "Test Case ID",
                    "Test Scenario",
                    "Expected Result",
                    "Actual Result",
                    "Status",
                    "Execution Date"
                ]
            ]);

            XLSX.utils.book_append_sheet(
                workbook,
                worksheet,
                "Test Results"
            );
        }

    } else {

        workbook = XLSX.utils.book_new();

        worksheet = XLSX.utils.aoa_to_sheet([
            [
                "Test Case ID",
                "Test Scenario",
                "Expected Result",
                "Actual Result",
                "Status",
                "Execution Date"
            ]
        ]);

        XLSX.utils.book_append_sheet(
            workbook,
            worksheet,
            "Test Results"
        );
    }


    // Append result
    XLSX.utils.sheet_add_aoa(
        worksheet,
        [[
            testCaseId,
            scenario,
            expectedResult,
            actualResult,
            status,
            new Date().toLocaleString()
        ]],
        {
            origin: -1
        }
    );


    XLSX.writeFile(workbook, excelFile);

    console.log(`Excel report saved to: ${excelFile}`);
}


// ============================================================
// CONNECT TO EXISTING OPERA
// ============================================================

async function createOperaDriver() {

    const options = new chrome.Options();

    // IMPORTANT:
    // Change this path if your Opera executable is different.
    options.setChromeBinaryPath("/usr/bin/opera");

    // Connect to manually opened Opera
    options.debuggerAddress("127.0.0.1:9222");

    return await new Builder()
        .forBrowser("chrome")
        .setChromeOptions(options)
        .build();
}


// ============================================================
// NAVIGATION TEST SUITE
// ============================================================

(async function navigationTest() {

    const driver = await createOperaDriver();

    try {

        console.log("");
        console.log("======================================");
        console.log("STARTING NAVIGATION TEST SUITE");
        console.log("======================================");


        // ====================================================
        // TC-NAV-001
        // Home Navigation
        // ====================================================

        console.log("");
        console.log("TC-NAV-001: Home Navigation");


        await driver.get("http://localhost:5173/");


        await driver.wait(
            until.elementLocated(
                By.tagName("body")
            ),
            10000
        );


        let url = await driver.getCurrentUrl();


        if (!url || !url.endsWith("/")) {

            throw new Error(
                `Expected Home page but current URL is ${url}`
            );

        }


        console.log("Home page loaded successfully.");
        console.log(`Current URL: ${url}`);


        saveResult(
            "TC-NAV-001",
            "Home page navigation",
            "User should be redirected to the Swap-and-Share Home page.",
            `Home page successfully loaded at ${url}.`,
            "PASS"
        );


        console.log("TC-NAV-001 PASSED");


        // ====================================================
        // TC-NAV-002
        // Inventory Navigation
        // ====================================================

        console.log("");
        console.log("TC-NAV-002: Inventory Navigation");


        const inventoryLink = await driver.wait(
            until.elementLocated(
                By.xpath("//a[contains(normalize-space(), 'Inventory')]")
            ),
            10000
        );


        await inventoryLink.click();


        await driver.wait(
            async () => {

                const currentUrl = await driver.getCurrentUrl();

                return currentUrl &&
                    currentUrl.includes("/shop");

            },
            10000
        );


        url = await driver.getCurrentUrl();


        console.log(`Inventory page loaded: ${url}`);


        saveResult(
            "TC-NAV-002",
            "Inventory navigation",
            "Clicking Inventory should navigate the user to the Shop page.",
            `Inventory page successfully opened at ${url}.`,
            "PASS"
        );


        console.log("TC-NAV-002 PASSED");


        // ====================================================
        // TC-NAV-003
        // Sell / Donate Navigation
        // ====================================================

        console.log("");
        console.log("TC-NAV-003: Sell / Donate Navigation");


        await driver.get("http://localhost:5173/");


        const sellLink = await driver.wait(
            until.elementLocated(
                By.xpath("//a[contains(normalize-space(), 'Sell')]")
            ),
            10000
        );


        await sellLink.click();


        await driver.wait(
            async () => {

                const currentUrl = await driver.getCurrentUrl();

                return currentUrl &&
                    currentUrl.includes("/sell");

            },
            10000
        );


        url = await driver.getCurrentUrl();


        console.log(`Sell page loaded: ${url}`);


        saveResult(
            "TC-NAV-003",
            "Sell/Donate navigation",
            "Clicking Sell/Donate should navigate the user to the listing page.",
            `Sell/Donate page successfully opened at ${url}.`,
            "PASS"
        );


        console.log("TC-NAV-003 PASSED");


        // ====================================================
        // TC-NAV-004
        // My Listings Navigation
        // ====================================================

        console.log("");
        console.log("TC-NAV-004: My Listings Navigation");


        await driver.get("http://localhost:5173/");


        const listingsLink = await driver.wait(
            until.elementLocated(
                By.xpath("//a[contains(normalize-space(), 'My Listings')]")
            ),
            10000
        );


        await listingsLink.click();


        await driver.wait(
            async () => {

                const currentUrl = await driver.getCurrentUrl();

                return currentUrl &&
                    currentUrl.includes("/my-listings");

            },
            10000
        );


        url = await driver.getCurrentUrl();


        console.log(`My Listings page loaded: ${url}`);


        saveResult(
            "TC-NAV-004",
            "My Listings navigation",
            "Clicking My Listings should open the user's listings page.",
            `My Listings page successfully opened at ${url}.`,
            "PASS"
        );


        console.log("TC-NAV-004 PASSED");


        // ====================================================
        // TC-NAV-005
        // Cart Navigation
        // ====================================================

        console.log("");
        console.log("TC-NAV-005: Cart Navigation");


        await driver.get("http://localhost:5173/");


        const cartLink = await driver.wait(
            until.elementLocated(
                By.xpath("//a[contains(normalize-space(), 'Cart')]")
            ),
            10000
        );


        await cartLink.click();


        await driver.wait(
            async () => {

                const currentUrl = await driver.getCurrentUrl();

                return currentUrl &&
                    currentUrl.includes("/cart");

            },
            10000
        );


        url = await driver.getCurrentUrl();


        console.log(`Cart page loaded: ${url}`);


        saveResult(
            "TC-NAV-005",
            "Cart navigation",
            "Clicking Cart should open the user's shopping cart.",
            `Cart page successfully opened at ${url}.`,
            "PASS"
        );


        console.log("TC-NAV-005 PASSED");


        // ====================================================
        // FINAL RESULT
        // ====================================================

        console.log("");
        console.log("======================================");
        console.log("NAVIGATION TEST SUITE PASSED");
        console.log("======================================");

    }


    catch (error) {

        console.error("");
        console.error("======================================");
        console.error("NAVIGATION TEST SUITE FAILED");
        console.error("======================================");

        console.error(error.message);


        saveResult(
            "TC-NAV-ERROR",
            "Navigation test execution",
            "All navigation test cases should execute successfully.",
            `Test execution failed: ${error.message}`,
            "FAIL"
        );

    }


    finally {

        // Do not close manually opened Opera.

        console.log("");
        console.log("Opera left open.");

    }

})();