package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4InventarSuchePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileInbetriebnahmePage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileMonitoringPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4MobileNeSearchPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Slf4j
public class A4InventarSucheRobot {

    A4InventarSuchePage a4InventarSuchePage = new A4InventarSuchePage();


    //ne-search-page
    @Step("Open UI, log in, and goTo Ne-mobile-search-page")
    public void openNetworkElementMobileSearchPage(){
        A4MobileNeSearchPage
                .login();
    }

    public String[] getSplittedVpszValues(String vpszUnsplitted){
        return vpszUnsplitted.split("/");
    }


    @Step("Click search button")
    public void clickSearchButton() {
        $(a4InventarSuchePage.getSEARCH_BUTTON_LOCATOR()).click();
    }

    @Step("Enter neg name")
    public void enterNegName(String value) { $(a4InventarSuchePage.getNEG_NAME_INPUT_FIELD_LOCATOR()).val(value); }

    @Step("Read neg name")
    public String readNegName() {
        return $(a4InventarSuchePage.getNEG_NAME_INPUT_FIELD_LOCATOR()).val();
    }

        
    //helper methods
    public void waitForTableToFullyLoad(int numberOfElements){

        //add 1 to number of elements because of table header
        numberOfElements++;

        $(By.xpath("//tr[" + numberOfElements + "]")).shouldBe(Condition.visible);

    }
}
