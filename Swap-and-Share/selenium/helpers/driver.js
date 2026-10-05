const { Builder, Browser } = require("selenium-webdriver");
const chrome = require("selenium-webdriver/chrome");

async function createDriver() {

    const options = new chrome.Options();

    options.addArguments(
        `--user-data-dir=${process.env.HOME}/.config/swap-share-selenium`
    );

    return await new Builder()
        .forBrowser(Browser.CHROME)
        .setChromeOptions(options)
        .build();
}

module.exports = createDriver;