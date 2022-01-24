package com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

@Slf4j
public class MonitoringPage {

    public static final String ENDPOINT = "/monitoring";

    public static final By A4_INVENTORY_IMPORTER_HEADER_LOCATOR = byXpath("//h2[contains(text(),'NetworkElement Status-Monitoring')]");
    public static final By NETWORK_ELEMENT_TABLE = byId("ne");
    public static final By ZTP_FIELD = byXpath("//*[@id=\"ne\"]/div[2]/table/tbody/tr/td[5]");
    public static final By INSTALLATION_BUTTON = byId("btnInstallation");

    @Step("Validate page")
    public void validate() {
        $(A4_INVENTORY_IMPORTER_HEADER_LOCATOR).shouldBe(visible, Duration.ofMillis(3000));
        $(INSTALLATION_BUTTON).shouldBe(visible, Duration.ofMillis(3000));
        assertContains(url(), ENDPOINT);
    }

    @Step("Check NE data")
    public void checkNeData(A4NetworkElement neData, String ztpIdent) {
        assertContains($(NETWORK_ELEMENT_TABLE).getText(), neData.getVpsz());
        assertContains($(NETWORK_ELEMENT_TABLE).getText(), neData.getFsz());
        assertContains($(ZTP_FIELD).getText(), ztpIdent);
    }

}
