package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryBrowserPage;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNeDetailPage;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class A4ResourceInventoryNeDetailRobot {


    @Step("Open UI, log in")
    public void openRiNeDetailPage(){
        A4ResourceInventoryNeDetailPage.login();     // warum wird hier die Klasse verwendet?
    }




}
