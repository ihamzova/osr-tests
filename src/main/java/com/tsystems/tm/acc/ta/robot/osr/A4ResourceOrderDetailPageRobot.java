package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceOrderDetailPage;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class A4ResourceOrderDetailPageRobot {


    @Step("Read vuep")
    public String readVuep() { return $(A4ResourceOrderDetailPage.getRO_VUEP_FIELD_LOCATOR()).val();

    }





}
