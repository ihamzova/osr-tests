package com.tsystems.tm.acc.ta.pages.osr.mobiledpu;

import com.tsystems.tm.acc.ta.pages.osr.ztcommissioning.OltInstallationPage;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@Slf4j

public class MobileDpuPage {

    private static final Integer WAIT_TIME_FOR_BUTTON_ENABLED = 2_000;
    private static final Integer WAIT_TIME_FOR_PROCESS = 90_000;
    public static final String APP = "portal-proxy";
    public static final String ENDPOINT = "/auftragnehmerportal-mui/dpu-installation/";

    public static final By DPU_DEMAND_RADIO_BUTTON = By.id("mat-radio-2");
    public static final By CONFIRM_BUTTON = By.xpath("//button[text()=' Auswahl bestätigen ']");
    public static final By NEXT_BUTTON = By.cssSelector(".btn-next");
    public static final By DPU_SERIAL_NUMBER_INPUT = By.xpath("//input[@id='demand_serialNumber']");
    public static final By SET_SERIAL_NUMBER = By.xpath("//button[text()=' Seriennummer setzen ']");
    public static final By START_COMMISSIONING = By.xpath("//button[text()=' Inbetriebnahme starten ']");
    public static final By DPU_IN_SYNC = By.cssSelector(".form-btn");
    public static final By FINISH_WO = By.xpath("//button[text()=' Arbeitsauftrag beenden ']");
    public static final By FIRST_DEMAND_RADIO_BUTTON = By.id("mat-radio-4");
    public static final By SECOND_DEMAND_RADIO_BUTTON = By.id("mat-radio-5");
    public static final By ALERT = By.xpath("//h2[@role='alert']");

    @Step("Open MobileDpuPage")
    public static MobileDpuPage openPage() {
        URL url = new GigabitUrlBuilder(APP).withoutSuffix().withEndpoint(ENDPOINT).withParameter("a-cid", "47100").buildExternal();
        String targetUrl = url.toString();
        targetUrl += "#/651799/edit";
        log.info("DPU Mobile UI Opening url " + targetUrl);
        return open(targetUrl, MobileDpuPage.class);
    }

    @Step("Go to Next page")
    public MobileDpuPage goToNextPage() {
        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }

        $(NEXT_BUTTON).click();

        return this;
    }

    @Step("Select DPU Demand")
    public MobileDpuPage selectDpuDemand() {

        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }

        $(DPU_DEMAND_RADIO_BUTTON).click();

        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }

        $(CONFIRM_BUTTON).click();

        return this;
    }

    @Step("Select Multiple DPU Demands")
    public MobileDpuPage selectMultipleDpuDemands() {

        $(FIRST_DEMAND_RADIO_BUTTON).shouldBe(visible).click();
        $(CONFIRM_BUTTON).shouldBe(enabled);
        $(SECOND_DEMAND_RADIO_BUTTON).shouldBe(visible).click();
        $(CONFIRM_BUTTON).shouldBe(enabled);

        return this;
    }

    @Step("Select Multiple DPU Demands")
    public MobileDpuPage selectMultipleDpuDemandsDisabled() {

        $(CONFIRM_BUTTON).shouldNotBe(enabled);

        return this;
    }

    @Step("Error Notification Displayed")
    public MobileDpuPage errorNotificationDisplayed() {

        $(ALERT).shouldBe(visible);

        return this;
    }

    @Step("Input Serial number")
    public MobileDpuPage inputSerialNumber() {

        $(DPU_SERIAL_NUMBER_INPUT).shouldBe(visible).click();
        $(DPU_SERIAL_NUMBER_INPUT).setValue("9988765");

        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }
        $(SET_SERIAL_NUMBER).click();

        return this;
    }

    @Step("Start Commissioning")
    public MobileDpuPage startCommissioning() {

        $(START_COMMISSIONING).shouldBe(enabled).click();

        try {
            Thread.sleep(WAIT_TIME_FOR_PROCESS);
        } catch (Exception e) {
            log.error("Interrupted");
        }

        return this;
    }

    @Step("Finish Commissioning")
    public MobileDpuPage finishCommissioning() {

        $(DPU_IN_SYNC).shouldBe(enabled).click();

        try {
            Thread.sleep(WAIT_TIME_FOR_BUTTON_ENABLED);
        } catch (Exception e) {
            log.error("Interrupted");
        }

        $(FINISH_WO).shouldBe(enabled).click();

        return this;
    }
}
