package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileInbetriebnahmePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileMonitoringPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class A4MobileUiRobot {

    A4MobileNeSearchPage a4MobileNeSearchPage = new A4MobileNeSearchPage();
    A4MobileInbetriebnahmePage a4MobileInbetriebnahmePage = new A4MobileInbetriebnahmePage();
    A4MobileMonitoringPage a4MobileMonitoringPage = new A4MobileMonitoringPage();

    @Step("Open UI, log in, and goTo Ne-mobile-search-page")
    public void openNetworkElementMobileSearchPage(){
        A4MobileNeSearchPage
                .login();
    }

    @Step("Enter vpsz")
    public void enterVpsz(String value) {
        $(a4MobileNeSearchPage.getVPSZ_INPUT_FIELD_LOCATOR()).val(value);
    }

    @Step("Read vpsz")
    public String readVpsz() {
        return $(a4MobileNeSearchPage.getVPSZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Click search button")
    public void clickSearchButton() {
        $(a4MobileNeSearchPage.getSEARCH_BUTTON_LOCATOR()).click();
    }

    @Step("Enter fsz")
    public void enterFsz(String value) { $(a4MobileNeSearchPage.getFSZ_INPUT_FIELD_LOCATOR()).val(value); }

    @Step("Read fsz")
    public String readFsz() {
        return $(a4MobileNeSearchPage.getFSZ_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Enter Category")
    public void enterCategory(String category) {$(a4MobileNeSearchPage.getCATEGORY_INPUT_FIELD_LOCATOR()).selectOption(category);}

    @Step("Read Category")
    public String readCategory() {
        return $(a4MobileNeSearchPage.getCATEGORY_INPUT_FIELD_LOCATOR()).val();
    }

    @Step("Check planning")
    public void checkPlanning() { $(a4MobileNeSearchPage.getPLANNING_CHECKBOX_LOCATOR()).click();}

    @Step("Check if planning is checked")
    public boolean checkIsPlanningChecked() { return $(a4MobileNeSearchPage.getPLANNING_CHECKBOX_LOCATOR()).isSelected();}

    @Step("Check operating")
    public void checkOperating() { $(a4MobileNeSearchPage.getOPERATING_CHECKBOX_LOCATOR()).click();}

    @Step("Check if operating is checked")
    public boolean checkIsOperatingChecked() { return $(a4MobileNeSearchPage.getOPERATING_CHECKBOX_LOCATOR()).isSelected();}

    @Step("Check radioButton")
    public void checkRadioButton(String index) { $(a4MobileNeSearchPage.getRADIO_BUTTON_LOCATOR()).append("[" + index + "]").click();}

    @Step("Click inbetriebnahme button")
    public void clickInbetriebnahmeButton() { $(a4MobileNeSearchPage.getINBETRIEBNAHME_BUTTON_LOCATOR()).click();}

    @Step("Back navigation")
    public void clickFinishButton() {$(a4MobileInbetriebnahmePage.getFERTIG_BUTTON_LOCATOR()).click();}

    @Step("Click Monitoring Button")
    public void clickMonitoringButton() {$(a4MobileNeSearchPage.getMONITORING_BUTTON_LOCATOR()).click();}

    @Step("check Monitoring")
    public void checkMonitoring(Map<String, A4NetworkElement> a4NeFilteredList) {
        //check if rows of tables are there, before proceeding
        waitForTableToFullyLoad(a4NeFilteredList.size());

        ElementsCollection elementsCollection = $(a4MobileMonitoringPage.getSEARCH_RESULT_TABLE_LOCATOR())
                .findAll(By.xpath("tr/td"));

        List<String> concat = new ArrayList<>();

        elementsCollection.forEach(k -> concat.add(k.getText()));

        //VPSZ	FSZ	Type	Planning Device Name	ZTP Ident	Planned MatNumber	Lifecycle State	Operational State

        a4NeFilteredList.forEach((k, a4NetworkElement) -> {
            assertTrue(concat.contains(a4NetworkElement.getVpsz()),a4NetworkElement.getVpsz());
            assertTrue(concat.contains(a4NetworkElement.getFsz()),a4NetworkElement.getFsz());
            assertTrue(concat.contains(a4NetworkElement.getType()),a4NetworkElement.getType());
            assertTrue(concat.contains(a4NetworkElement.getPlanningDeviceName()),a4NetworkElement.getPlanningDeviceName());
            assertTrue(concat.contains(a4NetworkElement.getPlannedMatNr()),a4NetworkElement.getPlannedMatNr());
            //assertTrue(concat.contains(a4NetworkElement.getLifecycleState()),a4NetworkElement.getLifecycleState());
            assertTrue(concat.contains(a4NetworkElement.getOperationalState()),a4NetworkElement.getOperationalState());
        });

        log.info("+++" + concat.toString());

        a4NeFilteredList.forEach((k,v) -> log.info("+++" + v.getCategory()));

        //check if table has only as many rows as expected by test data set
        //table has 6 columns and a4NeFilteredList contains cells, so we need to calculate a little bit
        assertEquals(concat.size()/6, a4NeFilteredList.size());
    }

    //helper methods
    public void waitForTableToFullyLoad(int numberOfElements){

        //add 1 to number of elements because of table header
        numberOfElements++;

        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);

    }
}
