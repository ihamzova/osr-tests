package com.tsystems.tm.acc.ta.domain.provisioning;

import com.tsystems.tm.acc.data.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.DataBundle;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.ta.apitest.ApiTest;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4PreProvisioningRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryServiceRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementGroupDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.NetworkElementPortDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.LogicalResourceUpdate;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceCharacteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRef;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.service.client.model.ResourceRelationship;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NewTpFromNemoWithPreprovisioningAndNspCreation extends ApiTest {
    private static final int WAIT_TIME = 15_000;

    private A4PreProvisioningRobot a4PreProvisioningRobot;
    private A4ResourceInventoryRobot a4ResourceInventoryRobot;
    private A4ResourceInventoryServiceRobot a4ResourceInventoryServiceRobot;

    private NetworkElementGroupDto networkElementGroup;
    private NetworkElementDto networkElement;
    private NetworkElementPortDto networkElementPort;
    private LogicalResourceUpdate terminationPointLogicalResource;

    private PortProvisioning port;

    @BeforeClass
    public void init() {
        a4PreProvisioningRobot = new A4PreProvisioningRobot();
        a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
        a4ResourceInventoryServiceRobot = new A4ResourceInventoryServiceRobot();

        DataBundle dataBundle = OsrTestContext.get().getData();
        port = dataBundle.getPortProvisioningDataProvider().get(PortProvisioningCase.a4Port);
    }

    @BeforeMethod
    public void prepareData() {
        // Generate data for different a4 resource inventory entries. Has to be refactored to make use of "official" data mgmt procedure
        networkElementGroup = setUpNetworkElementGroup();
        networkElement = setUpNetworkElement();
        networkElementPort = setUpNetworkElementPort();

        // Add prepared entries into DB. Needs to be done because to-be-tested termination point needs a NEP parent
        a4ResourceInventoryRobot.createNetworkElementGroup(networkElementGroup); // NE needs a NEG parent
        a4ResourceInventoryRobot.createNetworkElement(networkElement); // NEP needs a NE parent
        a4ResourceInventoryRobot.createNetworkElementPort(networkElementPort); // TP needs a NEP parent

        // Overwrite some parameters to match data from prepared a4 resource inventory entries
        port.setEndSz(networkElement.getVpsz() + "/" + networkElement.getFsz());
        port.setPortNumber(networkElementPort.getLogicalLabel().split("_")[1]);
        port.setSlotNumber("99");
    }

    @AfterMethod
    public void clearData() {
        a4ResourceInventoryRobot.deleteNetworkElementPort(networkElementPort.getUuid());
        a4ResourceInventoryRobot.deleteNetworkElement(networkElement.getUuid());
        a4ResourceInventoryRobot.deleteNetworkElementGroup(networkElementGroup.getUuid());
    }

    @Test(description = "DIGIHUB-59383 NEMO creates new Termination Point with Preprovisioning and new network service profile creation")
    @Owner("bela.kovac@t-systems.com")
    @TmsLink("DIGIHUB-59383")
    @Description("NEMO creates new Termination Point with Preprovisioning and new network service profile creation")
    public void newTpWithPreprovisioning() throws InterruptedException {
        // GIVEN / Arrange
        String uuidTp = UUID.randomUUID().toString();
        terminationPointLogicalResource = setUpTerminationPointWithNepParentAsLogicalResource(networkElementPort);

        // WHEN / Action
        a4ResourceInventoryServiceRobot.createTerminationPoint(uuidTp, terminationPointLogicalResource); // Create TP needs to be triggered with a4-resource-inventory-service, else preprovisioning will not be started
        Thread.sleep(WAIT_TIME);

        // THEN / Assert
        a4PreProvisioningRobot.checkResults(port);
        a4ResourceInventoryRobot.checkNetworkServiceProfileConnectedToTerminationPointExists(uuidTp);

        // AFTER / Clean-up
        a4ResourceInventoryRobot.deleteNetworkServiceProfileConnectedToTerminationPoint(uuidTp);
        a4ResourceInventoryRobot.deleteTerminationPoint(uuidTp);
    }

    private NetworkElementGroupDto setUpNetworkElementGroup() {
        return new NetworkElementGroupDto()
                .uuid(UUID.randomUUID().toString())
                .description("NEG for domain test DIGIHUB-59383")
                .type("OLT")
                .specificationVersion("1")
                .operationalState("INSTALLING")
                .name("NEG_" + UUID.randomUUID().toString().substring(0, 6)) // satisfy unique constraints
                .lifeCycleState("WORKING")
                .lastUpdateTime(OffsetDateTime.now())
                .description("NEG for domain test")
                .creationTime(OffsetDateTime.now())
                .centralOfficeNetworkOperator("operator")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }

    private NetworkElementDto setUpNetworkElement() {
        return new NetworkElementDto()
                .uuid(UUID.randomUUID().toString())
                .networkElementGroupUuid(networkElementGroup.getUuid()) // NE needs NEG as parent
                .description("NE for domain test DIGIHUB-59383")
                .address("address")
                .administrativeState("ACTIVATED")
                .lifecycleState("PLANNING")
                .operationalState("INSTALLING")
                .category("OLT") // must be 'OLT', else preprovisioning will not be started
                .fsz(UUID.randomUUID().toString().substring(0, 4)) // satisfy unique constraints
                .vpsz("49/8492/0")
                .klsId("123456")
                .plannedRackId("rackid")
                .plannedRackPosition("rackpos")
                .planningDeviceName("planname")
                .roles("role")
                .type("SPINE_SWITCH")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }

    private NetworkElementPortDto setUpNetworkElementPort() {
        Random random = new Random();
        int randoms = random.ints(1, 9999).findFirst().getAsInt();

        return new NetworkElementPortDto()
                .uuid(UUID.randomUUID().toString())
                .description("NEP for domain test DIGIHUB-59383")
                .networkElementUuid(networkElement.getUuid()) // NEP needs NE as parent
                .logicalLabel("LogicalLabel_" + randoms) // Prefix 'LogicalLabel_' must be given, else preprovisioning will not be started. Also, satisfy unique constraints
                .accessNetworkOperator("NetOp")
                .administrativeState("ACTIVATED")
                .operationalState("INSTALLING")
                .role("role")
                .creationTime(OffsetDateTime.now())
                .lastUpdateTime(OffsetDateTime.now());
    }

    // Set up a termination point as LogicalResource representation. It's bad and it's ugly, but it's how TMF639 is
    private LogicalResourceUpdate setUpTerminationPointWithNepParentAsLogicalResource(NetworkElementPortDto nepParent) {
        List<ResourceCharacteristic> tpCharacteristics = new ArrayList<>();
        ResourceCharacteristic rc1 = new ResourceCharacteristic()
                .name("creationTime")
                .value(OffsetDateTime.now().toString());
        tpCharacteristics.add(rc1);
        ResourceCharacteristic rc2 = new ResourceCharacteristic()
                .name("lastUpdateTime")
                .value(OffsetDateTime.now().toString());
        tpCharacteristics.add(rc2);
        ResourceCharacteristic rc3 = new ResourceCharacteristic()
                .name("type")
                .value("type");
        tpCharacteristics.add(rc3);
        ResourceCharacteristic rc4 = new ResourceCharacteristic()
                .name("state")
                .value("state");
        tpCharacteristics.add(rc4);
        ResourceCharacteristic rc5 = new ResourceCharacteristic()
                .name("lockedForNspUsage")
                .value("true");
        tpCharacteristics.add(rc5);

        ResourceRef resourceRef = new ResourceRef() // Parent must be NEP, else preprovisioning will not be started
                .id(nepParent.getUuid())
                .type("NetworkElementPort");

        ResourceRelationship resourceRelationship = new ResourceRelationship();
        resourceRelationship.setResourceRef(resourceRef);

        List<ResourceRelationship> tpResourceRelationships = new ArrayList<>();
        tpResourceRelationships.add(resourceRelationship);

        terminationPointLogicalResource = new LogicalResourceUpdate()
                .baseType("LogicalResource")
                .type("TerminationPoint")
                .version("1")
                .description("TP for domain test DIGIHUB-59383");
        terminationPointLogicalResource.setCharacteristic(tpCharacteristics);
        terminationPointLogicalResource.setResourceRelationship(tpResourceRelationships);

        return terminationPointLogicalResource;
    }
}
