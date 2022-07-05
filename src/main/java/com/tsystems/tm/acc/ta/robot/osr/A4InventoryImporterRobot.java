package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4RebellSyncClient;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.tests.osr.a4.rebell.sync.client.model.SyncRebellLinks;
import io.qameta.allure.Step;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

public class A4InventoryImporterRobot {
    private final A4RebellSyncClient a4RebellSync = new A4RebellSyncClient();

    @Step("Sync all NELs for NE (identified by VPSZ & FSZ) with Links from REBELL")
    public void doRebellSync(A4NetworkElement neData) {
        SyncRebellLinks srl = new SyncRebellLinks();
        srl.setVpsz(neData.getVpsz());
        srl.setFsz(neData.getFsz());

        a4RebellSync.getClient()
                .syncRebellLinks()
                .syncRebellLinks()
                .body(srl)
                .execute(checkStatus(HTTP_CODE_OK_200));
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
                .execute(checkStatus(HTTP_CODE_OK_200));


 */


}
