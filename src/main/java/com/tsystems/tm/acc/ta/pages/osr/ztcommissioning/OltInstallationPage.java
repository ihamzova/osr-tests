package com.tsystems.tm.acc.ta.pages.osr.ztcommissioning;

import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;
import java.time.Duration;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.AsyncAssert.assertUrlContainsWithTimeout;

@Slf4j
public class OltInstallationPage {

    public static final Integer MAX_LATENCY_FOR_ELEMENT_APPEARS = 1000;
    public static final Integer WAIT_FOR_MESSAGE = 15_000;

    public static final String APP = "portal-proxy";
    public static final String ENDPOINT = "/auftragnehmerportal-mui/olt-installation/";

    public static final By OLT_AKZ_INPUT_LOCATOR = By.id("akzInput");
    public static final By OLT_ONKZ_INPUT_LOCATOR = By.id("onkzInput");
    public static final By OLT_VKZ_INPUT_LOCATOR = By.id("vkzInput");
    public static final By OLT_FSZ_INPUT_LOCATOR = By.id("fszInput");
    public static final By SERIALNUMBER_INPUT_LOCATOR = By.id("serialNumberInput");
    public static final By PARTNUMBER_INPUT_LOCATOR = By.id("partNumberInput");
    public static final By START_BUTTON = By.cssSelector("button[type=submit]");

    public static final By OPEN_FORCE_PROCEED_LINK = By.id("open-force-proceed");
    public static final By FORCE_PROCEED_BUTTON = By.id("force-proceed");
    public static final By POSITIVE_MESSAGE = By.cssSelector("div.ui.positive.icon.message");
    public static final By NEGATIVE_MESSAGE = By.cssSelector("div.ui.negative.message");
    public static final By FINISHED_BACKWARD_BUTTON  = By.id("process-finished-backward");

    @Step("Open OLT-Installation page")
    public static OltInstallationPage openInstallationPage(String acid) {
        URL url = new GigabitUrlBuilder(APP).withoutSuffix().withEndpoint(ENDPOINT).withParameter("a-cid", acid).buildExternal();
        log.info("OLT Mobile UI Opening url " + url);
        return open(url, OltInstallationPage.class);
    }

    @Step("ZTC Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP);
        assertUrlContainsWithTimeout(ENDPOINT);
    }

    @Step("Start zero touch commissioning process")
    public OltInstallationPage startZtCommissioningProcess(OltDevice oltDevice) {
        inputDeviceParameters(oltDevice.getEndsz());
        $(SERIALNUMBER_INPUT_LOCATOR).click();
        $(SERIALNUMBER_INPUT_LOCATOR).val(oltDevice.getSeriennummer());
        $(PARTNUMBER_INPUT_LOCATOR).click();
        $(PARTNUMBER_INPUT_LOCATOR).val(oltDevice.getTkz());
        $(START_BUTTON).click();
        return this;
    }

    @Step("Waiting on the process until the force proceed link exist")
     public OltInstallationPage checkForceProceedLinkExist(Integer timeout) {
         $(OPEN_FORCE_PROCEED_LINK).should(exist , Duration.ofMillis(timeout));
         return this;
     }

    @Step("Check force proceed link exist")
    public OltInstallationPage checkForceProceedLinkExist() {
        sleep(MAX_LATENCY_FOR_ELEMENT_APPEARS);
        $(OPEN_FORCE_PROCEED_LINK).should(exist);
        return this;
    }

    @Step("Manually continue zero touch commissioning process and wait for a error message")
    public OltInstallationPage continueZtCommissioningProcessCallbackError(Integer timeout) {
        sleep(MAX_LATENCY_FOR_ELEMENT_APPEARS*3); // 1 second is not enough
        $(OPEN_FORCE_PROCEED_LINK).click();
        $(FORCE_PROCEED_BUTTON).should(exist , Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
        sleep(MAX_LATENCY_FOR_ELEMENT_APPEARS);
        $(OPEN_FORCE_PROCEED_LINK).should(exist , Duration.ofMillis(timeout));
        return this;
    }

    @Step("Manually continue zero touch commissioning process")
    public OltInstallationPage continueZtCommissioningProcess() {
        sleep(MAX_LATENCY_FOR_ELEMENT_APPEARS);
        $(OPEN_FORCE_PROCEED_LINK).click();
        $(FORCE_PROCEED_BUTTON).should(exist , Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
        return this;
    }

    @Step("Wait until the zero touch commissioning process is finished")
    public OltInstallationPage waitZtCommissioningProcessFinishedSuccess(Integer timeout) {
        //  check "Die Installation vor Ort ist abgeschlossen und die Betriebsstelle kann nun verlassen werden."
        $(POSITIVE_MESSAGE).should(exist , Duration.ofMillis(WAIT_FOR_MESSAGE));
        // wait for backward button
        $(FINISHED_BACKWARD_BUTTON).should(exist , Duration.ofMillis(timeout)).click();
        $(START_BUTTON).should(exist , Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS));
        return this;
    }

    @Step("Wait until an error message is displayed")
    public OltInstallationPage waitZtCommissioningProcessErrorMessage(Integer timeout) {
        $(NEGATIVE_MESSAGE).should(exist, Duration.ofMillis(timeout));
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
