package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.A4ResourceInventoryNepDetailsPage;

public class A4ResourceInventoryNepDetailsRobot {

    public void checkLandedOnCorrectNepDetailsPage(A4NetworkElementPort nepData) {
        final A4ResourceInventoryNepDetailsPage a4ResourceInventoryNepDetailsPage = new A4ResourceInventoryNepDetailsPage();
        a4ResourceInventoryNepDetailsPage.validate(nepData.getUuid());
    }

}
