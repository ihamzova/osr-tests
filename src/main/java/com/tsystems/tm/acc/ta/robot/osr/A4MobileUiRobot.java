package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebDriverRunner;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileInbetriebnahmePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileMonitoringPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.stringSplit;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class A4MobileUiRobot {

    //ne-search-page
    @Step("Open UI, log in, and goTo Ne-mobile-search-page")
    public void openNetworkElementMobileSearchPage() {
        A4MobileNeSearchPage
                .login();
    }

    public void searchForNetworkElement(A4NetworkElement neData) {
        openNetworkElementMobileSearchPage();
        enterVpsz(neData.getVpsz());
        enterFsz(neData.getFsz());
        enterCategory(neData.getCategory());
        clickSearchButton();
    }

    public void doInbetriebnahme(String ztpIdent) {
        checkRadioButton("1");
        clickInbetriebnahmeButton();
        enterZtpIdent(ztpIdent);
        clickFinishButton();
    }

    public void checkSearchResultPageAfterInbetriebnahme(A4NetworkElement ne, String ztpIdent) {
        checkInstalling();
        assertEquals(readVpsz(), ne.getVpsz());
        assertEquals(readAkz(), stringSplit(ne.getVpsz(), "/").get(0));
        assertEquals(readOnkz(), stringSplit(ne.getVpsz(), "/").get(1));
        assertEquals(readVkz(), stringSplit(ne.getVpsz(), "/").get(2));
        assertEquals(readFsz(), ne.getFsz());
        assertEquals(readCategory(), ne.getCategory());
        assertEquals(readZtpIdent(), ztpIdent);
    }

    public void removeNetworkElementFromMonitoringList(Map<String, A4NetworkElement> a4NeFilteredMap, String identifier, A4NetworkElement neData) {
        a4NeFilteredMap.put(identifier, neData);
        List<String> toBeRemoved = new ArrayList<>();

        // remove all entries
        a4NeFilteredMap.forEach((k, a4NetworkElement) -> {
            clickRemoveButton();
            try {

                WebDriver driver = WebDriverRunner.getWebDriver();// new ChromeDriver(capabilities);
                WebDriverWait wait = new WebDriverWait(driver, 5000);
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                driver.switchTo().alert();
                alert.accept();
            } catch (NoAlertPresentException e) {
                System.out.println("EXCEPTION " + e.getCause());
            }
            toBeRemoved.add(k);

        });

        toBeRemoved.forEach(a4NeFilteredMap::remove);
    }

    public String[] getSplittedVpszValues(String vpszUnsplitted) {
        return vpszUnsplitted.split("/");
    }

    @Step("Enter vpsz")
    public void enterVpsz(String value) {
        $(A4MobileNeSearchPage.getAKZ_INPUT_FIELD_LOCATOR()).val(getSplittedVpszValues(value)[0]);
        $(A4MobileNeSearchPage.getONKZ_INPUT_FIELD_LOCATOR()).val(getSplittedVpszValues(value)[1]);
        $(A4MobileNeSearchPage.getVKZ_INPUT_FIELD_LOCATOR()).val(getSplittedVpszValues(value)[2]);
    }

    @Step("Read vpsz")
    public String readVpsz() {
        return $(A4MobileNeSearchPage.getVPSZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Read akz")
    public String readAkz() {
        return $(A4MobileNeSearchPage.getAKZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Read onkz")
    public String readOnkz() {
        return $(A4MobileNeSearchPage.getONKZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Read vkz")
    public String readVkz() {
        return $(A4MobileNeSearchPage.getVKZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Click search button")
    public void clickSearchButton() {
        $(A4MobileNeSearchPage.getSEARCH_BUTTON_LOCATOR()).click();
    }

    @Step("Enter fsz")
    public void enterFsz(String value) {
        $(A4MobileNeSearchPage.getFSZ_INPUT_FIELD_LOCATOR()).val(value);
    }

    @Step("Read fsz")
    public String readFsz() {
        return $(A4MobileNeSearchPage.getFSZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Enter Category")
    public void enterCategory(String category) {
        $(A4MobileNeSearchPage.getCATEGORY_INPUT_FIELD_LOCATOR()).selectOption(category);
    }

    @Step("Read Category")
    public String readCategory() {
        return $(A4MobileNeSearchPage.getCATEGORY_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Read ZTP Ident")
    public String readZtpIdent() {
        return $(A4MobileNeSearchPage.getZTPI_INPUT_FIELD_LOCATOR()).getText();
    }

    @Step("Check planning")
    public void checkPlanning() {
        $(A4MobileNeSearchPage.getPLANNING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Check if planning is checked")
    public boolean checkIsPlanningChecked() {
        return $(A4MobileNeSearchPage.getPLANNING_CHECKBOX_LOCATOR()).isSelected();
    }

    @Step("Check operating")
    public void checkOperating() {
        $(A4MobileNeSearchPage.getOPERATING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Check installing")
    public void checkInstalling() {
        $(A4MobileNeSearchPage.getINSTALLING_CHECKBOX_LOCATOR()).click();
    }

    @Step("Check if operating is checked")
    public boolean checkIsOperatingChecked() {
        return $(A4MobileNeSearchPage.getOPERATING_CHECKBOX_LOCATOR()).isSelected();
    }

    @Step("Check radioButton")
    public void checkRadioButton(String index) {
        $(A4MobileNeSearchPage.getRADIO_BUTTON_LOCATOR()).append("[" + index + "]").click();
    }

    @Step("Click inbetriebnahme button")
    public void clickInbetriebnahmeButton() {
        $(A4MobileNeSearchPage.getINBETRIEBNAHME_NE_BUTTON_LOCATOR()).click();
    }

    @Step("Click Monitoring Button")
    public void clickMonitoringButton() {
        $(A4MobileNeSearchPage.getMONITORING_BUTTON_LOCATOR()).click();
    }


    //inbetriebnahme-page
    @Step("Enter ztpIdent")
    public void enterZtpIdent(String value) {
        $(A4MobileInbetriebnahmePage.getZTPIDENT_FIELD_LOCATOR()).val(value);
    }

    @Step("Back navigation")
    public void clickFinishButton() {
        $(A4MobileInbetriebnahmePage.getFERTIG_BUTTON_LOCATOR()).click();
    }

    @Step("Remove Monitoring Item")
    public void clickRemoveButton() {
        $(A4MobileMonitoringPage.getDELETE_BUTTON_LOCATOR()).click();
    }


    //monitoring-page
    @Step("check empty Monitoring")
    public void checkEmptyMonitoringList(Map<String, A4NetworkElement> a4NeFilteredList) {
        $(A4MobileMonitoringPage.getEMPTY_LIST_MESSAGE_LOCATOR()).shouldBe(visible);
        //assertEquals($(A4MobileMonitoringPage.getEMPTY_LIST_MESSAGE_LOCATOR()).text(), "Ihre Monitoring-Liste ist leer.");
        assertEquals($(A4MobileMonitoringPage.getEMPTY_LIST_MESSAGE_LOCATOR()).text(), "Keine NetworkElements gefunden");
        assertEquals(a4NeFilteredList.size(), 0);
    }

    //monitoring-page
    @Step("check Monitoring")
    public void checkMonitoring(Map<String, A4NetworkElement> a4NeFilteredList, EquipmentData equipmentData) {
        //check if rows of tables are there, before proceeding
        waitForTableToFullyLoad(a4NeFilteredList.size());

        ElementsCollection elementsCollection = $(A4MobileMonitoringPage.getSEARCH_RESULT_TABLE_LOCATOR())
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

    //helper methods
    public void waitForTableToFullyLoad(int numberOfElements) {

        //add 1 to number of elements because of table header
       // numberOfElements++;

        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);

    }
}
