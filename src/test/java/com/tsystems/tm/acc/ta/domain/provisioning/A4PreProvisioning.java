package com.tsystems.tm.acc.ta.domain.provisioning;

import com.tsystems.tm.acc.data.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.DataBundle;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4PreProvisioningRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.AdditionalAttributeDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.TerminationPointDto;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.internal.client.model.TpRefDto;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

public class A4PreProvisioning extends ApiTest {
    private static final int WAIT_TIME = 10_000;

    private PortProvisioning port;
    private TpRefDto tpRef;
    private A4PreProvisioningRobot a4PreProvisioningRobot;

    @BeforeClass
    public void init() {
        DataBundle dataBundle = OsrTestContext.get().getData();
        port = dataBundle.getPortProvisioningDataProvider().get(PortProvisioningCase.a4Port);

        a4PreProvisioningRobot = new A4PreProvisioningRobot();
    }

    @BeforeMethod
    public void prepareData() {
        NetworkElementGroupDto networkElementGroup = new NetworkElementGroupDto()
                .uuid(UUID.randomUUID().toString())
                .type("OLT")
                .specificationVersion("1")
                .operationalState("INSTALLING")
                .name(UUID.randomUUID().toString().substring(0, 6))
                .lifeCycleState("WORKING")
                .lastUpdateTime(OffsetDateTime.now())
                .description("test DIGIHUB-47437")
                .creationTime(OffsetDateTime.now())
                .centralOfficeNetworkOperator("operator");

        TerminationPointDto terminationPoint = new TerminationPointDto()
                .uuid(UUID.randomUUID().toString())
                .href("href")
                .parentUuid(networkElementGroup.getUuid())
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now())
                .type("type")
                .description("test DIGIHUB-47437")
                .state("state")
                .lockedForNspUsage(true)
                .additionalAttribute(Collections.singletonList(new AdditionalAttributeDto().key("key").value("value")));

        tpRef = new TpRefDto()
                .endSz("49/8492/0/76A4")
                .slotNumber("2")
                .portNumber("5")
                .klsId("123456")
                .partyId(10001L)
                .tpRef(terminationPoint.getUuid());

        a4PreProvisioningRobot.prepareData(networkElementGroup, terminationPoint);
    }

    @AfterMethod
    public void clearData() {
//        a4PreProvisioningRobot.clearData(networkElementGroup.getUuid(), terminationPoint.getUuid());
    }

    @Test(description = "A4 preprovisioning case")
    @TmsLink("DIGIHUB-47437")
    @Description("A4 preprovisioning case")
    public void a4ProvisioningTest() throws InterruptedException {
        a4PreProvisioningRobot.startA4PreProvisioning(tpRef);
        Thread.sleep(WAIT_TIME);
        a4PreProvisioningRobot.checkResults(port);
    }
}
