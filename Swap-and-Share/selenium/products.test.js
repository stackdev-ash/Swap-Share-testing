const {
    By,
    until,
    APP_URL,
    runCase,
    createDriver,
} = require("./helpers/common");

const TESTS = {

    inventoryLoads: {
        id: "TC-PRODUCT-001",
        scenario: "Product inventory loads",
        expected: "Products should be displayed on the Shop/Inventory page.",
    },

    productInformation: {
        id: "TC-PRODUCT-002",
        scenario: "Product information is displayed",
        expected: "Each displayed product should contain product information such as name and price.",
    },

};

(async function productTests() {

    const driver = await createDriver();
    let allPassed = true;

    try {

        console.log("\n=== PRODUCT TEST SUITE ===");

        // ====================================================
        // TC-PRODUCT-001
        // Product Inventory Loads
        // ====================================================

        allPassed = (await runCase(
            TESTS.inventoryLoads,
            async () => {

                await driver.get(`${APP_URL}/shop`);

                const products = await driver.wait(
                    until.elementsLocated(
                        By.css("[data-testid='product-card']")
                    ),
                    10000,
                    "No product cards were displayed on the Shop page."
                );

                if (products.length === 0) {
                    throw new Error(
                        "No products were displayed on the Shop page."
                    );
                }

                return `${products.length} product(s) displayed successfully on the Shop page.`;
            }
        )) && allPassed;


        // ====================================================
        // TC-PRODUCT-002
        // Product Information
        // ====================================================

        allPassed = (await runCase(
            TESTS.productInformation,
            async () => {

                await driver.get(`${APP_URL}/shop`);

                const products = await driver.wait(
                    until.elementsLocated(
                        By.css("[data-testid='product-card']")
                    ),
                    10000,
                    "No product cards were displayed."
                );

                if (products.length === 0) {
                    throw new Error(
                        "No products were available to verify."
                    );
                }

                for (let i = 0; i < products.length; i++) {

                    const product = products[i];

                    const name = await product.findElement(
                        By.css("[data-testid='product-name']")
                    );

                    const price = await product.findElement(
                        By.css("[data-testid='product-price']")
                    );

                    const nameText = (await name.getText()).trim();
                    const priceText = (await price.getText()).trim();

                    if (!nameText) {
                        throw new Error(
                            `Product ${i + 1} does not have a name.`
                        );
                    }

                    if (!priceText || !priceText.startsWith("₹")) {
                        throw new Error(
                            `Product ${i + 1} does not have a valid price.`
                        );
                    }

                    console.log(
                        `  Product ${i + 1}: ${nameText} | ${priceText}`
                    );
                }

                return `All ${products.length} product(s) contain valid names and prices.`;
            }
        )) && allPassed;

    } catch (error) {

        allPassed = false;

        console.error(
            "\n❌ PRODUCT SUITE ERROR:",
            error.message
        );

    } finally {

        console.log(
            `\n${allPassed ? "✅ PRODUCT SUITE PASSED" : "❌ PRODUCT SUITE FAILED"}`
        );

        console.log("Opera left open.");

        process.exit(allPassed ? 0 : 1);
    }

})();