package com.tsystems.tm.acc.ta.robot.osr;


import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceOrderSearchPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class A4ResourceOrderSearchPageRobot {

    @Step("Open UI, log in, and goTo RO-Search-page")
    public void openRoSearchPage(){
        A4ResourceOrderSearchPage
                .login();
    }

    @Step("Enter vuep")
    public void enterRoVuep(String value) { $(A4ResourceOrderSearchPage.getRO_VUEP_NUMBER_FIELD_LOCATOR()).val(value); }

    @Step("Select InProgress")
    public void selectInProgress() { $(A4ResourceOrderSearchPage.getRO_CHECKBOX_IN_PROGRESS()).click(); }

    @Step("Select Completed")
    public void selectCompleted() { $(A4ResourceOrderSearchPage.getRO_CHECKBOX_COMPLETED()).click(); }

    @Step("Select Rejected")
    public void selectRejected() { $(A4ResourceOrderSearchPage.getRO_CHECKBOX_REJECTED()).click(); }


    @Step("Click search button")
    public void clickRoSearchButton() {
        $(A4ResourceOrderSearchPage.getRO_SEARCH_BUTTON_LOCATOR()).click();
    }

    @Step("Click Row 1")
    public void clickSearchResultRow1() {
        $(A4ResourceOrderSearchPage.getRO_TABLE_ROW_1()).click();
        //$(A4ResourceOrderSearchPage.getRO_TABLE_ROW_1()).doubleClick();
    }


    public void clickFirstRowInSearchResultTable() {
        getRoElementsCollection().get(2).click();
    }


    public ElementsCollection getRoElementsCollection() {
        // waitForTableToFullyLoad(elementsCollection.size());
        try {
            Thread.sleep(2000);
            return $(A4ResourceOrderSearchPage.getRO_SEARCH_RESULT_TABLE_LOCATOR())
                    .findAll(By.xpath("tr/td"));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
