package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.DeviceTestDataManagementClient;
import com.tsystems.tm.acc.ta.api.osr.UplinkResourceInventoryManagementClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.OltUplinkBusinessReferencenMapper;
import com.tsystems.tm.acc.ta.data.osr.models.OltUplinkBusinessReferencen;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.uplink.resource.inventory.management.v5_2_1_client.model.Uplink;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.COMPOSITE_PARTY_ID_DTAG;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_MA5600;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.OLT_BFF_PROXY_MS;

@Slf4j
public class MpcSwitchRobot {

    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider(OLT_BFF_PROXY_MS, RhssoHelper.getSecretOfGigabitHub(OLT_BFF_PROXY_MS));
    private UplinkResourceInventoryManagementClient uplinkResourceInventoryManagementClient = new UplinkResourceInventoryManagementClient(authTokenProvider);
    private DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();

    @Step("fill olt-resource-inventory database with test data")
    public void fillDeviceInResourceInventory(OltUplinkBusinessReferencen oltUplinkBusinessReferencen) {

        deviceTestDataManagementClient.getClient().deviceTestDataManagement().createTestData()
                .deviceEmsNbiNameQuery(EMS_NBI_NAME_MA5600)
                .deviceEndSzQuery(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz())
                .deviceSlotNumbersQuery("3,4,5,19")
                .deviceKlsIdQuery("12377812")
                .deviceCompositePartyIdQuery(COMPOSITE_PARTY_ID_DTAG.toString())
                .uplinkEndSzQuery(oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef().getEndSz())
                .uplinkTargetPortQuery(oltUplinkBusinessReferencen.getBngSourcePortEquipmentBusinessRef().getPortName())
                .uplinkAncpConfigurationQuery("1")
                .executeSqlQuery("1")
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("change BNG Port")
    public void changeBngPort(OltUplinkBusinessReferencen oltUplinkBusinessReferencen) {

        List<Uplink> uplinkList = uplinkResourceInventoryManagementClient.getClient().uplink().listUplink()
                .portsEquipmentBusinessRefEndSzQuery(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz())
                .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(uplinkList.size(), 1L, "uplinkList.size missmatch");

        log.info("111 uplink = {}", uplinkList.get(0));

        uplinkResourceInventoryManagementClient.getClient().uplink().changeBngPortBulkUplink()
                .body(new OltUplinkBusinessReferencenMapper().getChangeBngPorts(oltUplinkBusinessReferencen))
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        uplinkList = uplinkResourceInventoryManagementClient.getClient().uplink().listUplink()
                .portsEquipmentBusinessRefEndSzQuery(oltUplinkBusinessReferencen.getOltPortEquipmentBusinessRef().getEndSz())
                .executeAs(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
        Assert.assertEquals(uplinkList.size(), 1L, "uplinkList.size missmatch");

        log.info("222 uplink = {}", uplinkList.get(0));
        // Assert.assertEquals(uplinkList.get(0).getState(), UplinkState.ACTIVE,  "uplink not activ");
    }

    @Step("Clear device in olt-resource-inventory database")
    public void clearResourceInventoryDataBase(String endSz) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

}
