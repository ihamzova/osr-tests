package com.tsystems.tm.acc.ta.team.mercury.rmk_at;


import com.tsystems.tm.acc.data.osr.models.oltuplinkbusinessreferencen.OltUplinkBusinessReferencenCase;
import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferencen;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@Slf4j
@ServiceLog({ANCP_CONFIGURATION_MS, OLT_UPLINK_MANAGEMENT_MS, OLT_RESOURCE_INVENTORY_MS})
@Epic("OS&R")
@Feature("Description MPC Switch: Bulk update in olt-uplink-management")
@TmsLink("DIGIHUB-128147") // This is the Jira id of TestSet
public class MpcSwitchTest extends GigabitTest {

    private OltUplinkBusinessReferencen defaultOltUplinkBusinessReferencen;

    @BeforeMethod
    public void init() {

        defaultOltUplinkBusinessReferencen = OsrTestContext.get().getData()
                .getOltUplinkBusinessReferencenDataProvider()
                .get(OltUplinkBusinessReferencenCase.OltUplinkSuccess);

    }

    @AfterClass
    public void teardown() {

    }

    @Test(description = "DIGIHUB-127784 Check MPC Switch: happy case. BNG Port is free")
    public void changeBngPortBulkUplinkSuccess() {
        log.info("--- defaultOltUplinkBusinessReferencen = {}", defaultOltUplinkBusinessReferencen);
    }


}
