package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
public class A4StartPage {
    public static final String APP = "a4-resource-inventory-ui";
    public static final String ENDPOINT = "a4-resource-inventory-ui/portal";

    public static final By A4_INVENTORY_IMPORTER_HEADER_LOCATOR = byXpath("//h2[contains(text(),'A4 Resource Inventory Portal')]");
    public static final By TO_INSTALLATION_BUTTON = By.id("btnInstallation");

    @Step("Validate page")
    public A4StartPage validate() {
        $(A4_INVENTORY_IMPORTER_HEADER_LOCATOR).waitUntil(visible, 3000);
        $(TO_INSTALLATION_BUTTON).waitUntil(visible, 3000);
        assertContains(url(), ENDPOINT);
        return this;
    }

    @Step("Login")
    public static A4StartPage login() {
        URL url = new OCUrlBuilder(APP).build();
        //this part is needed for external users which have problem with the proxy setup...
        /*URL url = null;
        try {
            url = new URL("https://a4-resource-inventory-ui-proxy-berlinium-01.support.magic.telekom.de/");
            log.info("Opening url " + url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("broken");
        }*/
        return open(url, A4StartPage.class);
    }

    @Step("Go to Installation")
    public InstallationPage goToInstallation() {
        $(TO_INSTALLATION_BUTTON).click();
        return new InstallationPage();
    }
}
