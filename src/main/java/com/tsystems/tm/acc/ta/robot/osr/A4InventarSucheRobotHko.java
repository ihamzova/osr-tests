package com.tsystems.tm.acc.ta.robot.osr;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.*;
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
public class A4InventarSucheRobotHko {

    A4InventarSuchePage a4InventarSuchePage = new A4InventarSuchePage();
    A4InventarSuchePageHko a4InventarSuchePageHko = new A4InventarSuchePageHko();



    // Änderungen Heiko
    @Step("Checkbox WORKING")
    public void checkboxWorking() { $(a4InventarSuchePageHko.getWORKING_CHECKBOX_LOCATOR()).click();}











    // was bisher geschah:   +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //Inventar-Suche-page
    @Step("Open UI, log in, and goTo Inventar-Suche-page")
    public void openInventarSuchePage(){
        A4InventarSuchePage
                .login();
    }
    @Step("Choose search by NetworkElementGroupName")
    public void clickNetworkElementGroup() {
        $(a4InventarSuchePage.getNEG_CHOOSE_BUTTON_LOCATOR()).click();
    }
    @Step("Enter neg name")
    public void enterNegName(String value) { $(a4InventarSuchePage.getNEG_NAME_INPUT_FIELD_LOCATOR()).val(value);
    }
    @Step("Read neg name")
    public String readNegName() {
        return $(a4InventarSuchePage.getNEG_NAME_INPUT_FIELD_LOCATOR()).val();
    }
    @Step("Click search button")
    public void clickSearchButton() {
        $(a4InventarSuchePage.getSEARCH_BUTTON_LOCATOR()).click();
    }

}
