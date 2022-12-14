package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A10nspInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryFillDbClient;
import com.tsystems.tm.acc.ta.api.osr.DeviceTestDataManagementClient;
import com.tsystems.tm.acc.ta.data.osr.models.A10nspCheckData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.model.A10nspDto;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.model.CheckLineIdResult;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.model.OltDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_NO_CONTENT_204;
import static com.tsystems.tm.acc.ta.data.mercury.MercuryConstants.EMS_NBI_NAME_MA5600;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static org.testng.Assert.*;

@Slf4j
public class A10nspCheckRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_ACCEPTED_202 = 202;
    private static final Integer HTTP_CODE_BAD_REQUEST_400 = 400;
    private static final Integer HTTP_CODE_NOT_FOUND_404 = 404;
    private static final int WAIT_TIME_FOR_ASYNC_RESPONSE = 50_000;

    private static final Long COMPOSITE_PARTY_ID_DTAG = 10001L;

    private final A10nspInventoryClient a10nspInventoryClient = new A10nspInventoryClient();
    private final AccessLineResourceInventoryFillDbClient accessLineResourceInventoryFillDbClient = new AccessLineResourceInventoryFillDbClient();
    private final DeviceTestDataManagementClient deviceTestDataManagementClient = new DeviceTestDataManagementClient();

    @Step("Check if a carrierConnection is found for a given LineId")
    public void checkLineIdTestFound(A10nspCheckData checkLineIdA10nsp) {

        CheckLineIdResult checkLineIdResult = new CheckLineIdResult();
        checkLineIdResult.setCarrierConnectionAvailable(false);

        for (int i = 0; i < 3; ++i) {
            checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalControllerV2().checkLineId()
                    .rahmenvertragsnummerQuery(checkLineIdA10nsp.getRahmenVertragsNr())
                    .xRequestIDHeader(checkLineIdA10nsp.getBngEndSz())
                    .body(checkLineIdA10nsp.getLineId())
                    .executeAs(checkStatus(HTTP_CODE_OK_200));


            if (checkLineIdResult.isCarrierConnectionAvailable()) {
                break;
            }
            waitForAsyncResponse();
        }
        assertTrue(checkLineIdResult.isCarrierConnectionAvailable());
    }

    @Step("Check if a carrierConnection is not available for a given LineId")
    public void checkLineIdTestNotFound(A10nspCheckData checkLineIdA10nspNotFound) {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalControllerV2().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nspNotFound.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nspNotFound.getBngEndSz())
                .body(checkLineIdA10nspNotFound.getLineId())
                .executeAs(checkStatus(HTTP_CODE_OK_200));

        assertFalse(checkLineIdResult.isCarrierConnectionAvailable());
    }

    @Step("Check error message for invalid input parameter")
    public void checkLineIdTestWrongLineId(A10nspCheckData checkLineIdA10nspWrongLineId) {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalControllerV2().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nspWrongLineId.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nspWrongLineId.getBngEndSz())
                .body(checkLineIdA10nspWrongLineId.getLineId())
                .executeAs(checkStatus(HTTP_CODE_BAD_REQUEST_400));

        assertNotNull(checkLineIdResult);
    }

    @Step("Find A10NSP's by OLT-Endsz list successfull")
    public void findA10NspByEndSzListFound(A10nspCheckData checkLineIdA10nsp) {

        List<OltDto> oltDtoList = a10nspInventoryClient.getClient().a10nspInternalControllerV2().findA10nspByOltEndSz()
                .endszQuery(checkLineIdA10nsp.getOltEndSz())
                .endszQuery(checkLineIdA10nsp.getOltEndSz2())
                .executeAs(checkStatus(HTTP_CODE_OK_200));

        Assert.assertEquals(oltDtoList.size(), 2L, "oltDtoList found but wrong size");
        OltDto oltDto = oltDtoList.get(0);
        Assert.assertEquals(oltDto.getOltEndSz(), checkLineIdA10nsp.getOltEndSz(), "OltDto found but wrong OLT EndSz");
        Assert.assertEquals(oltDto.getA10nspTerminationEndsz(), checkLineIdA10nsp.getBngEndSz(), "OltDto found but wrong BNG EndSz");
        Assert.assertEquals(oltDto.getCompositePartyId(), COMPOSITE_PARTY_ID_DTAG, "CompsitePartyId mismatch");
        List<A10nspDto> a10nspDtoList = oltDto.getA10nsps();
        Assert.assertTrue(a10nspDtoList.size() > 0, "nspDto List is empty");
        Assert.assertNotNull(a10nspDtoList.stream()
                .filter(a10nspDto -> checkLineIdA10nsp.getRahmenVertragsNr().equals(a10nspDto.getRahmenvertragsnummer()))
                .findAny()
                .orElse(null), "RahmenVertragsNr not found in a10nspDtoList");
    }

    @Step("Find A10NSP's by OLT-Endsz list not successfull")
    public void findA10NspByEndSzListNotFound(A10nspCheckData a10nsp) {

        a10nspInventoryClient.getClient().a10nspInternalControllerV2().findA10nspByOltEndSz()
                .endszQuery(a10nsp.getOltEndSz())
                .endszQuery(a10nsp.getOltEndSz2())
                .endszQuery(a10nsp.getOltEndSz3())
                .execute(checkStatus(HTTP_CODE_NOT_FOUND_404));

    }

    @Step("Find A10NSP's by OLT-Endsz list empty")
    public void findA10NspByEndSzListEmpty(A10nspCheckData checkLineIdA10nspEmptyList) {

        List<OltDto> oltDtoList = a10nspInventoryClient.getClient().a10nspInternalControllerV2().findA10nspByOltEndSz()
                .endszQuery(checkLineIdA10nspEmptyList.getOltEndSz2())
                .executeAs(checkStatus(HTTP_CODE_OK_200));

        Assert.assertEquals(oltDtoList.size(), 1L, "oltDtoList found but wrong size");
        OltDto oltDto = oltDtoList.get(0);
        Assert.assertEquals(oltDto.getOltEndSz(), checkLineIdA10nspEmptyList.getOltEndSz2(), "OltDto found but wrong OLT EndSz");
        Assert.assertEquals(oltDto.getA10nspTerminationEndsz(), checkLineIdA10nspEmptyList.getBngEndSz(), "OltDto found but wrong BNG EndSz");
        Assert.assertEquals(oltDto.getCompositePartyId(), COMPOSITE_PARTY_ID_DTAG, "CompsitePartyId mismatch");
        List<A10nspDto> a10nspDtoList = oltDto.getA10nsps();
        assertEquals(a10nspDtoList.size(), 0, "nspDto List is empty");

    }

    @Step("Fill access-line-resource-inventory database with test data\"")
    public void prepareAccessLineResourceInventoryDataBase() {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase().fillDatabaseForOltCommissioning()
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("fill olt-resource-inventory database with test data")
    public void fillDeviceInResourceInventory(OltDevice oltDevice) {

        deviceTestDataManagementClient.getClient().deviceTestDataManagement().createTestData()
                .deviceEmsNbiNameQuery(EMS_NBI_NAME_MA5600)
                .deviceEndSzQuery(oltDevice.getEndsz())
                .deviceSlotNumbersQuery("3,4,5,19")
                .deviceKlsIdQuery("12377812")
                .deviceCompositePartyIdQuery(COMPOSITE_PARTY_ID_DTAG.toString())
                .uplinkEndSzQuery(oltDevice.getBngEndsz())
                .uplinkTargetPortQuery(oltDevice.getBngDownlinkPort())
                .uplinkAncpConfigurationQuery("1")
                .executeSqlQuery("1")
                .execute(checkStatus(HTTP_CODE_OK_200));

        refreshA10nspInventory();        // trigger the a10nsp-inventory refresh

    }

    @Step("Restore accessline-resource-inventory Database state")
    public void restoreOsrDbState() {
        accessLineResourceInventoryFillDbClient.getClient().fillDatabase().deleteDatabase()
                .execute(checkStatus(HTTP_CODE_OK_200));
    }

    @Step("clear the OLT device in olt-resource-invemtory database.")
    public void deleteDeviceInResourceInventory(String endSz) {
        deviceTestDataManagementClient.getClient().deviceTestDataManagement().deleteTestData().deviceEndSzQuery(endSz)
                .execute(checkStatus(HTTP_CODE_NO_CONTENT_204));
    }

    @Step("Prepare the precondition. Clear and refresh the a10nsp-resource-inventory database.")
    private void refreshA10nspInventory() {
        // clear a10nsp-inventory database
        a10nspInventoryClient.getClient().databaseTestController().clearDatabase()
                .execute(checkStatus(HTTP_CODE_OK_200));

        // fill a10nsp-inventory database
        a10nspInventoryClient.getClient().inventoryController().refreshInventory()
                .execute(checkStatus(HTTP_CODE_ACCEPTED_202));

        waitForAsyncResponse();
    }

    @Step("wait for async response")
    private void waitForAsyncResponse() {
        try {
            Thread.sleep(WAIT_TIME_FOR_ASYNC_RESPONSE);   // Delay after calling the asynchronous request
        } catch (Exception e) {
            log.error("Interrupted");
        }
    }

}
