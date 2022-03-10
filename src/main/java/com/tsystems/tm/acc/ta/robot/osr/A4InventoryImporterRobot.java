package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4RebellSyncClient;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.a4.rebell.sync.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.rebell.sync.client.model.SyncRebellLinks;
import io.qameta.allure.Step;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_ORDER_ORCHESTRATOR_MS;

public class A4InventoryImporterRobot {

    private static final AuthTokenProvider authTokenProvider =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_ORDER_ORCHESTRATOR_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_ORDER_ORCHESTRATOR_MS));

    private final ApiClient a4RebellSync = new A4RebellSyncClient(authTokenProvider).getClient();
   // private final ApiClient a4Importer = new A4InventoryImporterClient().getClient();

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
/*
    @Step("import ne list of neg from Plural")
    public void doPluralImport(String negName) {

        System.out.println("+++ A4InventoryImporter: frage folgende NEG bei Plural an: "+negName);
        //  request an Importer
        // /pluralAlignment?nameNeg=49/6808/1/POD/01
        // https://a4-inventory-importer-app-berlinium-03.priv.cl01.tmagic-dev.telekom.de/pluralAlignment?nameNEG=49/30/111/POD/02

        a4Plural
                .syncRebellLinks()
                .syncRebellLinks()
                .
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));


 */





}
