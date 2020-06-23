package com.tsystems.tm.acc.ta.domain.a4functionality;

import com.tsystems.tm.acc.data.models.stable.Credentials;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementport.A4NetworkElementPortCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.equipmentdata.EquipmentDataCase;
import com.tsystems.tm.acc.data.osr.models.uewegdata.UewegDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4FrontEndInventoryImporterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.WiremockRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class ZtpIdentUiTest  extends BaseTest {
    private static final int WAIT_TIME = 5_000;

    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final A4ResourceInventoryUiRobot a4ResourceInventoryUiRobot = new A4ResourceInventoryUiRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final WiremockRobot wiremockRobot = new WiremockRobot();
    private final A4FrontEndInventoryImporterRobot a4FrontEndInventoryImporterRobot = new A4FrontEndInventoryImporterRobot();

    private A4NetworkElementGroup a4NetworkElementGroup;
    private A4NetworkElement a4NetworkElementA;
    private A4NetworkElement a4NetworkElementB;
    private A4NetworkElementPort a4NetworkElementPortA;
    private A4NetworkElementPort a4NetworkElementPortB;
    private UewegData uewegData;
    private EquipmentData equipmentDataA;
    private EquipmentData equipmentDataB;

    @BeforeClass
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider()
                .get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        a4NetworkElementA = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        a4NetworkElementB = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementB);
        a4NetworkElementPortA = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort_logicalLabel_10G_001);
        a4NetworkElementPortB = osrTestContext.getData().getA4NetworkElementPortDataProvider()
                .get(A4NetworkElementPortCase.defaultNetworkElementPort_logicalLabel_10G_002);
        uewegData = osrTestContext.getData().getUewegDataDataProvider().get(UewegDataCase.defaultUeweg);
        equipmentDataA = osrTestContext.getData().getEquipmentDataDataProvider().get(EquipmentDataCase.equipment_MatNr_42999901);
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);
        a4ResourceInventoryRobot.createNetworkElement(a4NetworkElementA, a4NetworkElementGroup);
        a4ResourceInventoryRobot.createNetworkElement(a4NetworkElementB, a4NetworkElementGroup);

        a4ResourceInventoryRobot.wipeA4NetworkElementPortsIncludingChildren(a4NetworkElementPortA, a4NetworkElementA);
        a4ResourceInventoryRobot.wipeA4NetworkElementPortsIncludingChildren(a4NetworkElementPortB, a4NetworkElementB);

        a4ResourceInventoryRobot.createNetworkElementPort(a4NetworkElementPortA, a4NetworkElementA);
        a4ResourceInventoryRobot.createNetworkElementPort(a4NetworkElementPortB, a4NetworkElementB);
        wiremockRobot.setUpRebellWiremock(uewegData, a4NetworkElementA, a4NetworkElementB);
        wiremockRobot.setUpPslWiremock(equipmentDataA, a4NetworkElementA);
    }

    @AfterMethod
    public void cleanUp() {
        a4ResourceInventoryRobot.deleteNetworkElementPort(a4NetworkElementPortA.getUuid());
        a4ResourceInventoryRobot.deleteNetworkElementPort(a4NetworkElementPortB.getUuid());
        a4ResourceInventoryRobot.deleteNetworkElement(a4NetworkElementA.getUuid());
        a4ResourceInventoryRobot.deleteNetworkElement(a4NetworkElementB.getUuid());
        a4ResourceInventoryRobot.deleteNetworkElementGroup(a4NetworkElementGroup.getUuid());
        wiremockRobot.tearDownWiremock(uewegData.getRebellWiremockUuid());
        wiremockRobot.tearDownWiremock(equipmentDataA.getPslWiremockUuid());
    }

    @Test(description = "DIGIHUB-xxxxx Installation user enters ZTP Ident for Network Element in UI")
    void ztpTest() throws InterruptedException {
        // GIVEN / Arrange
        String ztpIdent = "ZTP Ident UI Test " + UUID.randomUUID().toString().substring(1, 4);

        // WHEN / Action
        a4ResourceInventoryUiRobot.openNetworkElement(a4NetworkElementA);
        a4ResourceInventoryUiRobot.enterZtpIdent(ztpIdent);

        // THEN
        a4ResourceInventoryUiRobot.checkMonitoringPage(a4NetworkElementA, ztpIdent);
        Thread.sleep(WAIT_TIME);
        // TODO Add step for check nemo update
//        a4ResourceInventoryRobot.checkNetworkElementLinkExists(uewegData, a4NetworkElementPortA.getUuid(),
//                a4NetworkElementPortB.getUuid());
        a4FrontEndInventoryImporterRobot.checkUpdateNetworkElementPsl(a4NetworkElementA.getUuid(), equipmentDataA);
        a4FrontEndInventoryImporterRobot.checkNetworkElementLinkExists(uewegData, a4NetworkElementPortA.getUuid(), a4NetworkElementPortB.getUuid());

        // AFTER / Clean-up
        a4FrontEndInventoryImporterRobot.cleanUpNetworkElementLinks(a4NetworkElementPortA.getUuid());
    }
}
