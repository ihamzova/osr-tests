package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebDriverRunner;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileInbetriebnahmeNELPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileMonitoringPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage.*;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.*;
import static org.testng.Assert.*;

@Slf4j
public class A4MobileUiRobot {

    //ne-search-page
    @Step("Open UI, log in, and goTo Ne-mobile-search-page")
    public void openNetworkElementMobileSearchPage() {
        login();
    }

    public void searchForNetworkElement(A4NetworkElement neData) {
        openNetworkElementMobileSearchPage();
        enterVpsz(neData.getVpsz());
        enterFsz(neData.getFsz());
        enterCategory(neData.getCategory());
        clickSearchButton();
    }

    public void checkSearchResultPageAfterNeInbetriebnahme(A4NetworkElement ne, String ztpIdent) {
        checkInstalling();
        assertEquals(readVpsz(), ne.getVpsz());
        assertEquals(readAkz(), stringSplit(ne.getVpsz(), "/").get(0));
        assertEquals(readOnkz(), stringSplit(ne.getVpsz(), "/").get(1));
        assertEquals(readVkz(), stringSplit(ne.getVpsz(), "/").get(2));
        assertEquals(readFsz(), ne.getFsz());
        assertEquals(readCategory(), ne.getCategory());
        assertEquals(readZtpIdent(), ztpIdent);
    }

    public String[] getSplittedVpszValues(String vpszUnsplitted) {
        return vpszUnsplitted.split("/");
    }

    @Step("Enter vpsz")
    public void enterVpsz(String value) {
        $(AKZ_INPUT_FIELD_LOCATOR).val(getSplittedVpszValues(value)[0]);
        $(ONKZ_INPUT_FIELD_LOCATOR).val(getSplittedVpszValues(value)[1]);
        $(VKZ_INPUT_FIELD_LOCATOR).val(getSplittedVpszValues(value)[2]);
    }

    @Step("Read vpsz")
    public String readVpsz() {
        return $(VPSZ_INPUT_FIELD_LOCATOR).val();
    }

    @Step("Read akz")
    public String readAkz() {
        return $(AKZ_INPUT_FIELD_LOCATOR).val();
    }

    @Step("Read onkz")
    public String readOnkz() {
        return $(ONKZ_INPUT_FIELD_LOCATOR).val();
    }

    @Step("Read vkz")
    public String readVkz() {
        return $(VKZ_INPUT_FIELD_LOCATOR).val();
    }

    @Step("Click search button")
    public void clickSearchButton() {
        $(SEARCH_BUTTON_LOCATOR).click();
    }

    @Step("Enter fsz")
    public void enterFsz(String value) {
        $(FSZ_INPUT_FIELD_LOCATOR).val(value);
    }

    @Step("Read fsz")
    public String readFsz() {
        return $(FSZ_INPUT_FIELD_LOCATOR).val();
    }

    @Step("Enter Category")
    public void enterCategory(String category) {
        $(CATEGORY_INPUT_FIELD_LOCATOR).selectOption(category);
    }

    @Step("Read Category")
    public String readCategory() {
        return $(CATEGORY_INPUT_FIELD_LOCATOR).val();
    }

    @Step("Read ZTP Ident")
    public String readZtpIdent() {
        return $(ZTPI_INPUT_FIELD_LOCATOR).getText();
    }

    @Step("Check planning")
    public void checkPlanning() {
        $(PLANNING_CHECKBOX_LOCATOR).click();
    }

    @Step("Check operating")
    public void checkOperating() {
        $(OPERATING_CHECKBOX_LOCATOR).click();
    }

    @Step("Check installing")
    public void checkInstalling() {
        $(INSTALLING_CHECKBOX_LOCATOR).click();
    }

    @Step("Check radioButton")
    public void checkRadioButton(String index) {
        $(RADIO_BUTTON_LOCATOR).append("[" + index + "]").click();
    }

    @Step("Click NE inbetriebnahme button")
    public void clickInbetriebnahmeButton() {
        $(INBETRIEBNAHME_NE_BUTTON_LOCATOR).click();
    }

    @Step("Click Zeige Nel zu Ne button")
    public void clickZeigeNelZuNeButton() {
        $(ZEIGE_NEL_ZU_NE_BUTTON_LOCATOR).click();
    }

    @Step("Click NEL installation button")
    public void clickNelInstallationButton() {
        $(INBETRIEBNAHME_NEL_BUTTON_LOCATOR).click();
    }

    @Step("Click Monitoring Button")
    public void clickMonitoringButton() {
        $(MONITORING_BUTTON_LOCATOR).click();
    }


    //inbetriebnahme-page
    @Step("Enter ztpIdent")
    public void enterZtpIdent(String value) {
        $(ZTPIDENT_FIELD_LOCATOR).val(value);
    }

    @Step("Back navigation")
    public void clickFinishButton() {
        $(FERTIG_BUTTON_LOCATOR).click();
    }

    @Step("Remove NE Monitoring Item")
    public void clickRemoveFromNEListButton() {
        $(A4MobileMonitoringPage.getSEARCH_NE_RESULT_TABLE_LOCATOR()).toWebElement().findElement(A4MobileMonitoringPage.getDELETE_BUTTON_LOCATOR()).click();
    }

    @Step("Remove NEL Monitoring Item")
    public void clickRemoveFromNELListButton() {
        $(A4MobileMonitoringPage.getSEARCH_NEL_RESULT_TABLE_LOCATOR()).toWebElement().findElement(A4MobileMonitoringPage.getDELETE_BUTTON_LOCATOR()).click();
    }

    public void doNeInbetriebnahme(String ztpIdent) {
        checkRadioButton("1");
        clickInbetriebnahmeButton();
        enterZtpIdent(ztpIdent);
        clickFinishButton();
    }

    public void doNelInstallation() {
        checkCheckbox();
        clickButtonAndConfirm();
    }

    public void startNelInstallation() {
        checkRadioButton("1");
        clickNelInstallationButton();
    }

    public void doNelInbetriebnahme() {
        startNelInstallation();
        checkPlanningFilter();
        doNelInstallation();
        sleepForSeconds(5); // Give logic some time to do requests to PSL, REBELL and A4 resource inventory
    }

    @Step("Check radioButton")
    public void checkCheckbox() {
        $(A4MobileInbetriebnahmeNELPage.getCHECKBOX_LOCATOR()).click();
    }

    @Step("Click NEL Install Button")
    public void clickButtonAndConfirm() {
        $(A4MobileInbetriebnahmeNELPage.getSTART_INSTALL_BTN()).click();
        checkAlert();
    }

    private void checkAlert() {
        try {
            WebDriver driver = WebDriverRunner.getWebDriver();
            WebDriverWait wait = new WebDriverWait(driver, 5000);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException e) {
            System.out.println("EXCEPTION " + e.getCause());
        }
    }

    @Step("Click NE Reset to planning button")
    public void clickNeResetToPlanningButtonAndConfirm() {
        System.out.println("+++ Button Enabled: " + $(NE_RESET_TO_PLANNING_BUTTON_LOCATOR).isEnabled());
        $(NE_RESET_TO_PLANNING_BUTTON_LOCATOR).click();
        checkAlert();
    }

    public ElementsCollection getNeElementsCollection() throws InterruptedException {
        Thread.sleep(2000);
        return $(SEARCH_RESULT_TABLE_LOCATOR).findAll(By.xpath("tr/td"));
    }


    @Step("Check NE Reset to planning button is disabled")
    public void checkNeResetToPlanningButtonDisabled() {
        assertFalse($(NE_RESET_TO_PLANNING_BUTTON_LOCATOR).isEnabled());
    }

    @Step("Check error message not found")
    public String notFoundMsg() {
        return $(A4MobileInbetriebnahmeNELPage.getERROR_LOCATOR()).getText();
    }

    @Step("Conten not found msg")
    public void checkNotFound() {
        Assert.assertTrue(notFoundMsg().contains("Keine NetworkElementLinks zu diesem NetworkElement gefunden!"));
    }

    @Step("click planning filter")
    public void checkPlanningFilter() {
        $(A4MobileInbetriebnahmeNELPage.getPLANNING_FILTER_LOCATOR()).click();
    }


    //monitoring-page
    @Step("check empty NE Monitoring")
    public void checkEmptyNeMonitoringList(Map<String, A4NetworkElement> a4NeFilteredList) {
        $(A4MobileMonitoringPage.getEMPTY_NE_LIST_MESSAGE_LOCATOR()).shouldBe(visible);
        assertEquals($(A4MobileMonitoringPage.getEMPTY_NE_LIST_MESSAGE_LOCATOR()).text(), "Ihre Monitoring-Liste für NetworkElements ist leer.");
        assertEquals(a4NeFilteredList.size(), 0);
    }


    //monitoring-page
    @Step("check empty NEL Monitoring")
    public void checkEmptyNelMonitoringList(Map<String, A4NetworkElementLink> a4NelFilteredList) {
        $(A4MobileMonitoringPage.getEMPTY_NEL_LIST_MESSAGE_LOCATOR()).shouldBe(visible);
        assertEquals($(A4MobileMonitoringPage.getEMPTY_NEL_LIST_MESSAGE_LOCATOR()).text(), "Ihre Monitoring-Liste für NetworkElementLinks ist leer.");
        assertEquals(a4NelFilteredList.size(), 0);
    }

    @Step("check NE Monitoring")
    public void checkNEMonitoringList(Map<String, A4NetworkElement> a4NeFilteredList, EquipmentData equipmentData) {
        //check if rows of tables are there, before proceeding
        waitForTableToFullyLoad(a4NeFilteredList.size());

        ElementsCollection elementsCollection = $(A4MobileMonitoringPage.getSEARCH_NE_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));
        List<String> concat = new ArrayList<>();

        elementsCollection.forEach(k -> concat.add(k.getText()));
        //VPSZ,FSZ,Type,Planning Device Name,ZTP Ident,Planned MatNumber,Lifecycle State,Operational State

        a4NeFilteredList.forEach((k, a4NetworkElement) -> {
            assertTrue(concat.contains(a4NetworkElement.getVpsz()), a4NetworkElement.getVpsz());
            assertTrue(concat.contains(a4NetworkElement.getFsz()), a4NetworkElement.getFsz());
            assertTrue(concat.contains(a4NetworkElement.getType()), a4NetworkElement.getType());
            assertTrue(concat.contains(a4NetworkElement.getPlanningDeviceName()), a4NetworkElement.getPlanningDeviceName());
            // Check for planned mat number has to be done with psl wiremock equip data, because A4NetworkElement doesn't have this property
            assertTrue(concat.contains(equipmentData.getSubmt()), equipmentData.getSubmt());
            assertTrue(concat.contains(a4NetworkElement.getOperationalState()), a4NetworkElement.getOperationalState());
        });

        //check if table has only as many rows as expected by test data set
        //table has 6 columns and a4NeFilteredList contains cells, so we need to calculate a little bit
        assertEquals(concat.size() / 6, a4NeFilteredList.size());
    }


    @Step("check NEL Monitoring")
    public void checkNELMonitoringList(Map<String, A4NetworkElement> a4NeFilteredList,
                                       String endPointVpsz, String endPointFsz) {
        //check if rows of tables are there, before proceeding
        waitForTableToFullyLoad(a4NeFilteredList.size());

        ElementsCollection elementsCollection = $(A4MobileMonitoringPage.getSEARCH_NEL_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));
        List<String> monitoringZeile = new ArrayList<>();

        elementsCollection.forEach(k -> monitoringZeile.add(k.getText()));

        String monitoringZeileString = monitoringZeile.stream().map(String::toString).reduce("", String::concat);

        assertTrue(monitoringZeile.contains("4N4"), "4N4");
        //  NEL has to be in LifecycleState: INSTALLING
        assertTrue(monitoringZeile.contains("INSTALLING"));
        //  NEL has to be in OperationalState: NOT_WORKING
        assertTrue(monitoringZeile.contains("NOT_WORKING"));
        a4NeFilteredList.forEach((k, a4NetworkElement) -> {
            // lbz from NEL in Monitoring List must contain: vpsz and fsz from both NE's
            assertTrue(monitoringZeileString.contains(a4NetworkElement.getVpsz()));
            assertTrue(monitoringZeileString.contains(endPointVpsz));
            assertTrue(monitoringZeileString.contains(a4NetworkElement.getFsz()));
            assertTrue(monitoringZeileString.contains(endPointFsz));
        });

        //check if table has only as many rows as expected by test data set
        //table has 5 columns and a4NeFilteredList contains cells, so we need to calculate a little bit
        assertEquals(monitoringZeile.size() / 5, a4NeFilteredList.size());
    }


    public void removeNetworkElementFromNEMonitoringList(Map<String, A4NetworkElement> a4NeFilteredMap, String identifier, A4NetworkElement neData) {
        a4NeFilteredMap.put(identifier, neData);
        List<String> toBeRemoved = new ArrayList<>();

        // remove all entries
        a4NeFilteredMap.forEach((k, a4NetworkElement) -> {
            clickRemoveFromNEListButton();
            checkAlert();
            toBeRemoved.add(k);

        });

        toBeRemoved.forEach(a4NeFilteredMap::remove);
    }


    public void removeNetworkElementFromNELMonitoringList(Map<String, A4NetworkElementLink> a4NELFilteredMap, String identifier, A4NetworkElementLink nelData) {
        a4NELFilteredMap.put(identifier, nelData);
        List<String> toBeRemoved = new ArrayList<>();

        // remove all entries
        a4NELFilteredMap.forEach((k, a4NetworkElementLink) -> {
            clickRemoveFromNELListButton();
            checkAlert();
            toBeRemoved.add(k);

        });

        toBeRemoved.forEach(a4NELFilteredMap::remove);
    }

    //helper methods
    public void waitForTableToFullyLoad(int numberOfElements) {
        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);
    }
}
