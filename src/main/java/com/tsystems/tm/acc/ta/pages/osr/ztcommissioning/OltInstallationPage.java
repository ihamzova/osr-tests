package com.tsystems.tm.acc.ta.pages.osr.ztcommissioning;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;
import java.time.Duration;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.tsystems.tm.acc.ta.util.AsyncAssert.assertUrlContainsWithTimeout;

@Slf4j
public class OltInstallationPage {

    public static final String APP = "portal-proxy";
    public static final String ENDPOINT = "/auftragnehmerportal-mui/olt-installation/";

    public static final By OLT_AKZ_INPUT_LOCATOR = By.id("akzInput");
    public static final By OLT_ONKZ_INPUT_LOCATOR = By.id("onkzInput");
    public static final By OLT_VKZ_INPUT_LOCATOR = By.id("vkzInput");
    public static final By OLT_FSZ_INPUT_LOCATOR = By.id("fszInput");
    public static final By SERIALNUMBER_INPUT_LOCATOR = By.id("serialNumberInput");
    public static final By PARTNUMBER_INPUT_LOCATOR = By.id("partNumberInput");

    public static final By START_BUTTON = By.cssSelector("button[type=submit]");
    //public static final By START_BUTTON = By.cssSelector("button.ui.icon.button");

    public static final By CONTINUE = By.cssSelector("p:nth-child(7)");

    @Step("Open OLT-Installation page")
    public static OltInstallationPage openInstallationPage(String acid) {
        URL url = new GigabitUrlBuilder(APP).withoutSuffix().withEndpoint(ENDPOINT).withParameter("a-cid", acid).buildExternal();
        log.info("ZTC Opening url " + url);
        return open(url, OltInstallationPage.class);
    }

    @Step("ZTC Validate Url")
    public OltInstallationPage validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout.intValue());
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout.intValue());
        return this;
    }

    @Step("Start zero touch commissioning process")
    public OltInstallationPage startZtCommisioningProcess(OltDevice oltDevice, Integer timeout) {
        inputDeviceParameters(oltDevice.getEndsz());
        $(SERIALNUMBER_INPUT_LOCATOR).click();
        $(SERIALNUMBER_INPUT_LOCATOR).val(oltDevice.getSeriennummer());
        $(PARTNUMBER_INPUT_LOCATOR).click();
        $(PARTNUMBER_INPUT_LOCATOR).val(oltDevice.getTkz());
        $(START_BUTTON).click();
        // wait for "install OLT and connect to BNG port"
        $(CONTINUE).should(exist , Duration.ofMillis(timeout));
        return this;
    }

    private void inputDeviceParameters(String endSz) {
        String[] endSzn = endSz.split("/");
        $(OLT_AKZ_INPUT_LOCATOR).click();
        $(OLT_AKZ_INPUT_LOCATOR).val(endSzn[0]);
        $(OLT_ONKZ_INPUT_LOCATOR).click();
        $(OLT_ONKZ_INPUT_LOCATOR).val(endSzn[1]);
        $(OLT_VKZ_INPUT_LOCATOR).click();
        $(OLT_VKZ_INPUT_LOCATOR).val(endSzn[2]);
        $(OLT_FSZ_INPUT_LOCATOR).click();
        $(OLT_FSZ_INPUT_LOCATOR).val(endSzn[3]);
    }
}
