package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.osr.A10nspInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.CheckLineIdA10nsp;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.model.CheckLineIdResult;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.tests.osr.a10nsp.inventory.internal.client.invoker.ResponseSpecBuilders.shouldBeCode;
import static org.testng.Assert.*;

@Slf4j
public class A10nspCheckRobot {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_ACCEPTED_202 = 202;
    private static final Integer HTTP_CODE_BAD_REQUEST_400 = 400;
    private static final int WAIT_TIME_FOR_ASYNC_RESPONSE = 2_000;

    private A10nspInventoryClient a10nspInventoryClient;
    private OltResourceInventoryClient oltResourceInventoryClient;

    @Step("DIGIHUB-54119 test carrierConnection was found")
    public void CheckLineIdTestFound(CheckLineIdA10nsp checkLineIdA10nsp) {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalController().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nsp.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nsp.getBngEndSz())
                .body(checkLineIdA10nsp.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertTrue(checkLineIdResult.isCarrierConnectionAvailable());
    }

    @Step("DIGIHUB-54120  test carrierConnection was not found")
    public void CheckLineIdTestNotFound(CheckLineIdA10nsp checkLineIdA10nspNotFound) {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalController().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nspNotFound.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nspNotFound.getBngEndSz())
                .body(checkLineIdA10nspNotFound.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertFalse(checkLineIdResult.isCarrierConnectionAvailable());
    }

    @Step("DIGIHUB-54205 test invalid input parameter")
    public void CheckLineIdTestWrongLine(CheckLineIdA10nsp checkLineIdA10nspWrongLineId) {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalController().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nspWrongLineId.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nspWrongLineId.getBngEndSz())
                .body(checkLineIdA10nspWrongLineId.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));

        assertNotNull(checkLineIdResult);
    }

    public void init() {
        a10nspInventoryClient = new A10nspInventoryClient();
        oltResourceInventoryClient = new OltResourceInventoryClient();
    }

    /**
     * fill olt-resource-inventory database with test data
     */
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
        try {
            Thread.sleep(WAIT_TIME_FOR_ASYNC_RESPONSE);   // Delay after calling the asynchronous request
        } catch (Exception e) {
            log.error("Interrupted");
        }
    }

    /**
     * clears a device in olt-resource-invemtory database.
     * only one device will be deleted.
     *
     * @param endSz endsz of the olt device to be deleted
     */
    public void deleteDeviceInResourceInventory(String endSz) {
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    /**
     * init the a10nsp-inventory database
     *  asynchronous request to trigger the inventory refresh
     */
    private void refreshA10nspInventory() {
        a10nspInventoryClient.getClient().inventoryController().refreshInventory()
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    }
}
