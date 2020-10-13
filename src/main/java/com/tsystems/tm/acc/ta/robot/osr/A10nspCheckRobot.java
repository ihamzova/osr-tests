package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.osr.A10nspInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.AccessLineResourceInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.CheckLineIdA10nsp;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.model.A10nspDto;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.model.CheckLineIdResult;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.model.ErrorResponse;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.model.OltDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.invoker.ResponseSpecBuilders.shouldBeCode;
import static org.testng.Assert.*;

@Slf4j
public class A10nspCheckRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_ACCEPTED_202 = 202;
    private static final Integer HTTP_CODE_BAD_REQUEST_400 = 400;
    private static final Integer HTTP_CODE_NOT_FOUND_404 = 404;
    private static final int WAIT_TIME_FOR_ASYNC_RESPONSE = 2_000;

    private static final Long COMPOSITE_PARTY_ID_DTAG = 10001L;

    private A10nspInventoryClient a10nspInventoryClient = new A10nspInventoryClient();
    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();
    private AccessLineResourceInventoryClient accessLineResourceInventoryClient = new AccessLineResourceInventoryClient();

    @Step("DIGIHUB-54119 test carrierConnection was found")
    public void checkLineIdTestFound(CheckLineIdA10nsp checkLineIdA10nsp) {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalControllerV2().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nsp.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nsp.getBngEndSz())
                .body(checkLineIdA10nsp.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertTrue(checkLineIdResult.isCarrierConnectionAvailable());
    }

    @Step("DIGIHUB-54120  test carrierConnection was not found")
    public void checkLineIdTestNotFound(CheckLineIdA10nsp checkLineIdA10nspNotFound) {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalControllerV2().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nspNotFound.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nspNotFound.getBngEndSz())
                .body(checkLineIdA10nspNotFound.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertFalse(checkLineIdResult.isCarrierConnectionAvailable());
    }

    @Step("DIGIHUB-54205 test invalid input parameter")
    public void checkLineIdTestWrongLineId(CheckLineIdA10nsp checkLineIdA10nspWrongLineId) {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalControllerV2().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nspWrongLineId.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nspWrongLineId.getBngEndSz())
                .body(checkLineIdA10nspWrongLineId.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));

        assertNotNull(checkLineIdResult);
    }

    @Step("Find A10NSP's by OLT-Endsz list successfull")
    public void findA10NspByEndSzListFound(CheckLineIdA10nsp checkLineIdA10nsp) {

        List<OltDto> oltDtoList = a10nspInventoryClient.getClient().a10nspInternalControllerV2().findA10nspByOltEndSz()
                .endszQuery(checkLineIdA10nsp.getOltEndSz())
                .endszQuery(checkLineIdA10nsp.getOltEndSz2())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

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

    @Step("DIGIHUB-xxxx  ")
    public void findA10NspByEndSzListNotFound(CheckLineIdA10nsp a10nsp) {

        a10nspInventoryClient.getClient().a10nspInternalControllerV2().findA10nspByOltEndSz()
                .endszQuery(a10nsp.getOltEndSz())
                .endszQuery(a10nsp.getOltEndSz2())
                .endszQuery(a10nsp.getOltEndSz3())
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_NOT_FOUND_404)));


    }

    @Step("fill olt-resource-inventory database with test data")
    public void fillDeviceInResourceInventory(OltDevice oltDevice) {

        oltResourceInventoryClient.getClient().testDataManagementController().createDevice()
                ._01EmsNbiNameQuery("MA5600T")
                ._02EndszQuery(oltDevice.getEndsz())
                ._03SlotNumbersQuery("3,4,5,19")
                ._06KLSIdQuery("12377812")
                ._07CompositePartyIDQuery("10001")
                ._08UplinkEndszQuery(oltDevice.getBngEndsz())
                ._10ANCPConfQuery("1")
                ._11RunSQLQuery("1")
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        refreshA10nspInventory();        // trigger the a10nsp-inventory refresh

    }

    @Step("Restore accessline-resource-inventory Database state")
    public void restoreOsrDbState() {
        accessLineResourceInventoryClient.getClient().fillDatabase().deleteDatabase()
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("clear the OLT device in olt-resource-invemtory database.")
    public void deleteDeviceInResourceInventory(String endSz) {
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("clear and refresh the a10nsp-resource-invemtory database.")
    private void refreshA10nspInventory() {
        // clear a10nsp-inventory database
        a10nspInventoryClient.getClient().databaseTestController().clearDatabase()
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));

        // fill a10nsp-inventory database
        a10nspInventoryClient.getClient().inventoryController().refreshInventory()
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_ACCEPTED_202)));
        try {
            Thread.sleep(WAIT_TIME_FOR_ASYNC_RESPONSE);   // Delay after calling the asynchronous request
        } catch (Exception e) {
            log.error("Interrupted");
        }
    }
}
