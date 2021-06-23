package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNelDetailsPage;

public class A4ResourceInventoryNelDetailsRobot {

    public void checkLandedOnCorrectNelDetailsPage(A4NetworkElementLink nelData) {
        final A4ResourceInventoryNelDetailsPage a4ResourceInventoryNelDetailsPage = new A4ResourceInventoryNelDetailsPage();
        a4ResourceInventoryNelDetailsPage.validate(nelData.getUuid());
    }

}
