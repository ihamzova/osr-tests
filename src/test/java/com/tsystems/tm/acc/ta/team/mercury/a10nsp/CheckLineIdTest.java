package com.tsystems.tm.acc.ta.team.mercury.a10nsp;

import com.tsystems.tm.acc.a10nsp.inventory.internal.client.model.CheckLineIdResult;
import com.tsystems.tm.acc.data.osr.models.checklineida10nsp.CheckLineIdA10nsp;
import com.tsystems.tm.acc.data.osr.models.checklineida10nsp.CheckLineIdA10nspCase;
import com.tsystems.tm.acc.ta.api.ResponseSpecBuilders;
import com.tsystems.tm.acc.ta.api.osr.A10nspInventoryClient;
import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.a10nsp.inventory.internal.client.invoker.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
@Epic("OS&R")
@Feature("Description a10nsp check LineId")
@TmsLink("DIGIHUB-52132") // This is the Jira id of TestSet
public class CheckLineIdTest extends BaseTest {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_OK_400 = 400;

    private OltResourceInventoryClient oltResourceInventoryClient;
    private A10nspInventoryClient a10nspInventoryClient;

    private CheckLineIdA10nsp checkLineIdA10nsp;
    private CheckLineIdA10nsp checkLineIdA10nspWrongLineId;
    private CheckLineIdA10nsp checkLineIdA10nspNotFound;

    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        a10nspInventoryClient = new A10nspInventoryClient();

        // load test data
        checkLineIdA10nsp = OsrTestContext.get().getData()
                .getCheckLineIdA10nspDataProvider()
                .get(CheckLineIdA10nspCase.checkLineIdA10nsp);

        checkLineIdA10nspWrongLineId = OsrTestContext.get().getData()
                .getCheckLineIdA10nspDataProvider()
                .get(CheckLineIdA10nspCase.checkLineIdA10nspWrongLineId);

        checkLineIdA10nspNotFound = OsrTestContext.get().getData()
                .getCheckLineIdA10nspDataProvider()
                .get(CheckLineIdA10nspCase.checkLineIdA10nspNotFound);

         // init test data
        //deleteDeviceInResourceInventory(checkLineIdA10nsp.getBngEndSz());
        deleteDeviceInResourceInventory(checkLineIdA10nsp.getOltEndSz());
        fillDeviceInResourceInventory();
    }

    @Test(description = "DIGIHUB-12345 test carrierConnection was found")
    public void CheckLineIdTestFound() throws Exception {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalController().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nsp.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nsp.getBngEndSz())
                .body(checkLineIdA10nsp.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertTrue(checkLineIdResult.isCarrierConnectionAvailable());
    }

    @Test(description = "DIGIHUB-12345  test carrierConnection was not found")
    public void CheckLineIdTestNotFound() throws Exception {

        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalController().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nspNotFound.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nspNotFound.getBngEndSz())
                .body(checkLineIdA10nspNotFound.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        assertTrue(!checkLineIdResult.isCarrierConnectionAvailable());
    }

    @Test(description = "DIGIHUB-12345 test invalid input parameter")
    public void CheckLineIdTestWrongLine() throws Exception {
        CheckLineIdResult checkLineIdResult = a10nspInventoryClient.getClient().a10nspInternalController().checkLineId()
                .rahmenvertragsnummerQuery(checkLineIdA10nspWrongLineId.getRahmenVertragsNr())
                .xRequestIDHeader(checkLineIdA10nspWrongLineId.getBngEndSz())
                .body(checkLineIdA10nspWrongLineId.getLineId())
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_400)));

        assertNotNull(checkLineIdResult);
    }


    /**
     * clears a device in olt-resource-invemtory database.
     * only one device will be deleted.
     *
     * @param endSz
     */
    private void deleteDeviceInResourceInventory(String endSz) {
        oltResourceInventoryClient.getClient().testDataManagementController().deleteDevice().endszQuery(endSz)
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }

    /**
     * fill olt-resource-inventory database with test data
     */
    private void fillDeviceInResourceInventory() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().fillDatabaseForOltCommissioning()
                .execute(validatedWith(ResponseSpecBuilders.shouldBeCode(HTTP_CODE_OK_200)));
    }
}
