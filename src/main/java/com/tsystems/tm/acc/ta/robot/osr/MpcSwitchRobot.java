package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.AncpResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.api.osr.DeviceResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.api.osr.DeviceTestDataManagementClient;
import com.tsystems.tm.acc.ta.api.osr.UplinkResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.OltUplinkBusinessReferencenMapper;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentBusinessRef;
import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferencen;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.ancp.resource.inventory.management.v5_0_0.client.model.AncpSession;
import com.tsystems.tm.acc.tests.osr.device.resource.inventory.management.v5_6_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.ChangeBngPort;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.Uplink;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_BFF_PROXY_MS;

@Slf4j
public class MpcSwitchRobot {

    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS));
    private UplinkResourceInventoryManagementClient uplinkResourceInventoryManagementClient = new UplinkResourceInventoryManagementClient(authTokenProvider);
    private AncpResourceInventoryManagementClient ancpResourceInventoryManagementClient = new AncpResourceInventoryManagementClient(authTokenProvider);
    private DeviceResourceInventoryManagementClient deviceResourceInventoryManagementClient = new DeviceResourceInventoryManagementClient(authTokenProvider);
    private DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();


    @Step("change BNG Port success")
    public void changeBngPortSuccess(List<ChangeBngPort> changeBngPortList) {

        log.info("changeBngPortSuccess() changeBngPortList = {}", changeBngPortList);
        uplinkResourceInventoryManagementClient.getClient().uplink().changeBngPortBulkUplink()
                .body(changeBngPortList)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

    }

    @Step("change BNG Port unhappy case scenarios")
    public void changeBngPortError(List<ChangeBngPort> changeBngPortList) {

        log.info("changeBngPortError() changeBngPortList = {}", changeBngPortList);
        Response response = uplinkResourceInventoryManagementClient.getClient().uplink().changeBngPortBulkUplink()
                .body(changeBngPortList)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));

        // To check for sub string presence get the Response body as a String.
        String bodyAsString = response.getBody().asString();
        Assert.assertTrue(bodyAsString.contains("code"), "Response body does not contains \"code\"");
        Assert.assertTrue(bodyAsString.contains("reason"), "Response body does not contains \"reason\"");
        Assert.assertTrue(bodyAsString.contains("message"), "Response body does not contains \"message\"");
        Assert.assertTrue(bodyAsString.contains("status"), "Response body does not contains \"status\"");

        JsonPath jsonPathEvaluator = response.jsonPath();
        log.info("reason received from Response " + jsonPathEvaluator.get("reason"));
    }


    @Step("change BNG Port unhappy case scenarios with checks")
    public void changeBngPortError(OltUplinkBusinessReferencen oltUplinkBusinessReferencen) {

        checkEquipmentBusinessRef(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());

        changeBngPortError(OltUplinkBusinessReferencenMapper.getChangeBngPorts(oltUplinkBusinessReferencen));

        checkEquipmentBusinessRef(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef(),
                oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef());
    }

    @Step("check Equipment Business Referencen")
    public void checkEquipmentBusinessRef(EquipmentBusinessRef oltEquipmentBusinessRef, EquipmentBusinessRef bngEquipmentBusinessRef) {

        List<Uplink> uplinkList = uplinkResourceInventoryManagementClient.getClient().uplink().listUplink()
                .portsEquipmentBusinessRefEndSzQuery(oltEquipmentBusinessRef.getEndSz())
                .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(uplinkList.size(), 1L, "uplinkList.size missmatch, OLT endSz = " + oltEquipmentBusinessRef.getEndSz());
        Uplink uplink = uplinkList.get(0);
        Assert.assertEquals(uplink.getState(), "ACTIVE", "uplink not activ, OLT endSz = " + oltEquipmentBusinessRef.getEndSz());
        List<com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.EquipmentBusinessRef> equipmentBusinessRefs = uplink.getPortsEquipmentBusinessRef();
        Assert.assertEquals(equipmentBusinessRefs.size(), 2L, "checkEquipmentBusinessRef EquipmentBusinessRef size missmatch");
        EquipmentBusinessRef equipmentBusinessRef1 = OltUplinkBusinessReferencenMapper.getEquipmentBusinessRef(equipmentBusinessRefs.get(0));
        EquipmentBusinessRef equipmentBusinessRef2 = OltUplinkBusinessReferencenMapper.getEquipmentBusinessRef(equipmentBusinessRefs.get(1));

        if (equipmentBusinessRef1.getDeviceType().equals("OLT")) {
            Assert.assertEquals(equipmentBusinessRef1, oltEquipmentBusinessRef, "checkEquipmentBusinessRef1 OLT Ref missmatch, OLT endSz = " + oltEquipmentBusinessRef.getEndSz());
            Assert.assertEquals(equipmentBusinessRef2, bngEquipmentBusinessRef, "checkEquipmentBusinessRef2 BNG Ref missmatch, OLT endSz = " + oltEquipmentBusinessRef.getEndSz());
        } else {
            Assert.assertEquals(equipmentBusinessRef2, oltEquipmentBusinessRef, "checkEquipmentBusinessRef2 OLT Ref missmatch, OLT endSz = " + oltEquipmentBusinessRef.getEndSz());
            Assert.assertEquals(equipmentBusinessRef1, bngEquipmentBusinessRef, "checkEquipmentBusinessRef1 BNG Ref missmatch, OLT endSz = " + oltEquipmentBusinessRef.getEndSz());
        }

        checkAncpSession(oltEquipmentBusinessRef, bngEquipmentBusinessRef);
    }

    @Step("check ANCP Session")
    public void checkAncpSession(EquipmentBusinessRef oltEquipmentBusinessRef, EquipmentBusinessRef bngEquipmentBusinessRef) {

        List<AncpSession> ancpSessionList = ancpResourceInventoryManagementClient.getClient().ancpSession().listAncpSession()
                .accessNodeEquipmentBusinessRefEndSzQuery(oltEquipmentBusinessRef.getEndSz())
                .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        Assert.assertEquals(ancpSessionList.size(), 1L, "ancpSessionList1.size missmatch");
        Assert.assertEquals(ancpSessionList.get(0).getConfigurationStatus(), "ACTIVE", "ANCP ConfigurationStatus missmatch");

        EquipmentBusinessRef accessNodeEquipmentBusinessRef = OltUplinkBusinessReferencenMapper.getAncpEquipmentBusinessRef(ancpSessionList.get(0).getAccessNodeEquipmentBusinessRef());
        Assert.assertEquals(accessNodeEquipmentBusinessRef, oltEquipmentBusinessRef, "oltEquipmentBusinessRef mismatch");

        EquipmentBusinessRef oltUplinkPortEquipmentBusinessRef = OltUplinkBusinessReferencenMapper.getAncpEquipmentBusinessRef(ancpSessionList.get(0).getOltUplinkPortEquipmentBusinessRef());
        Assert.assertEquals(oltUplinkPortEquipmentBusinessRef, oltUplinkPortEquipmentBusinessRef, "oltUplinkPortEquipmentBusinessRef mismatch");

        EquipmentBusinessRef bngDownlinkPortEquipmentBusinessRef = OltUplinkBusinessReferencenMapper.getAncpEquipmentBusinessRef(ancpSessionList.get(0).getBngDownlinkPortEquipmentBusinessRef());
        Assert.assertEquals(bngEquipmentBusinessRef, bngDownlinkPortEquipmentBusinessRef, "bngDownlinkPortEquipmentBusinessRef mismatch");
    }

    @Step("create an OLT MA5600 Device in the inventory databases with test data")
    public void createOltDeviceInResourceInventory(EquipmentBusinessRef oltEquipmentBusinessRef, EquipmentBusinessRef bngEquipmentBusinessRef) {

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(oltEquipmentBusinessRef.getEndSz()).depthQuery(1).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 0L, "createOltDeviceInResourceInventory Device is present");

        deviceTestDataManagementClient.getClient().deviceTestDataManagement().createTestData()
                .deviceEmsNbiNameQuery(EMS_NBI_NAME_MA5600)
                .deviceEndSzQuery(oltEquipmentBusinessRef.getEndSz())
                .deviceSlotNumbersQuery("3,4,5," + oltEquipmentBusinessRef.getSlotName())
                .deviceKlsIdQuery("12377812")
                .deviceCompositePartyIdQuery(COMPOSITE_PARTY_ID_DTAG.toString())
                .uplinkEndSzQuery(bngEquipmentBusinessRef.getEndSz())
                .uplinkTargetPortQuery(bngEquipmentBusinessRef.getPortName())
                .uplinkAncpConfigurationQuery("1")
                .executeSqlQuery("1")
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("create an OLT SDX 6320-16 Device in the inventory databases with test data")
    public void createAdtranOltDeviceInResourceInventory(EquipmentBusinessRef oltEquipmentBusinessRef, EquipmentBusinessRef bngEquipmentBusinessRef) {

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(oltEquipmentBusinessRef.getEndSz()).depthQuery(1).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 0L, "createAdtranOltDeviceInResourceInventory Device is present");

        deviceTestDataManagementClient.getClient().deviceTestDataManagement().createTestData()
                .deviceEmsNbiNameQuery(EMS_NBI_NAME_SDX6320_16)
                .deviceEndSzQuery(oltEquipmentBusinessRef.getEndSz())
                .deviceKlsIdQuery("12377842")
                .deviceCompositePartyIdQuery(COMPOSITE_PARTY_ID_DTAG.toString())
                .uplinkEndSzQuery(bngEquipmentBusinessRef.getEndSz())
                .uplinkTargetPortQuery(bngEquipmentBusinessRef.getPortName())
                .uplinkAncpConfigurationQuery("1")
                .executeSqlQuery("1")
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("create a DPU SDX2221-08-TP Device in the inventory databases with test data")
    public void createDpuDeviceInResourceInventory(String endSz, EquipmentBusinessRef oltEquipmentBusinessRef) {

        List<Device> deviceList = deviceResourceInventoryManagementClient.getClient().device().listDevice()
                .endSzQuery(endSz).depthQuery(1).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(deviceList.size(), 0L, "createAdtranOltDeviceInResourceInventory Device is present");

        deviceTestDataManagementClient.getClient().deviceTestDataManagement().createTestData()
                .deviceEmsNbiNameQuery(EMS_NBI_NAME_SDX2221_08_TP)
                .deviceEndSzQuery(endSz)
                .deviceKlsIdQuery("12388842")
                .deviceCompositePartyIdQuery(COMPOSITE_PARTY_ID_DTAG.toString())
                .uplinkEndSzQuery(oltEquipmentBusinessRef.getEndSz())
                .uplinkTargetPortQuery("3/2") // OLT downlink port
                .deviceFiberOnLocationIdQuery("1000000085")
                .uplinkAncpConfigurationQuery("1")
                .executeSqlQuery("1")
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Clear device in inventory databases")
    public void clearResourceInventoryDataBase(String endSz) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

}
