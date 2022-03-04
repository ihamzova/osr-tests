package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4InventoryImporterClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.inventory.importer.client.invoker.ApiClient;
import io.qameta.allure.Step;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;


import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_ORDER_ORCHESTRATOR_MS;

public class A4PluralImporterRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_ORDER_ORCHESTRATOR_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_ORDER_ORCHESTRATOR_MS));

    //private final ApiClient a4RebellSync = new A4RebellSyncClient(authTokenProvider).getClient();
    private final ApiClient a4Plural = new A4InventoryImporterClient(authTokenProvider).getClient();
/*
    @Step("Sync all NELs for NE (identified by VPSZ & FSZ) with Links from REBELL")
    public void doRebellSync(A4NetworkElement neData) {
        SyncRebellLinks srl = new SyncRebellLinks();
        srl.setVpsz(neData.getVpsz());
        srl.setFsz(neData.getFsz());

        a4RebellSync
                .syncRebellLinks()
                .syncRebellLinks()
                .body(srl)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }


    @Step("Plural")
    public void doPluralImport(A4NetworkElement neData) {
        SyncRebellLinks srl = new SyncRebellLinks();
        srl.setVpsz(neData.getVpsz());
        srl.setFsz(neData.getFsz());

        a4RebellSync
                .syncRebellLinks()
                .syncRebellLinks()
                .body(srl)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }*/

    @Step("x")
    public void getPluralAllignment(String negName) {

        a4Plural.pluralAlignment().pluralAlignment().nameNEGQuery(negName).execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        /*
        a4ResourceOrderOrchestratorClient
                .resourceOrder()
                .deleteResourceOrder()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));

         */

    }
}
