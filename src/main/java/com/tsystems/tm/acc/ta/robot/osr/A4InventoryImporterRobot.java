package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4RebellSyncClient;
import com.tsystems.tm.acc.tests.osr.a4.rebell.sync.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.rebell.sync.client.model.SyncRebellLinks;
import io.qameta.allure.Step;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class A4InventoryImporterRobot {

    private final ApiClient a4RebellSync = new A4RebellSyncClient().getClient();

    @Step("Sync all NELs for NE (identified by VPSZ & FSZ) with Links from REBELL")
    public void doRebellSync(String vpsz, String fsz) {
        SyncRebellLinks srl = new SyncRebellLinks();
        srl.setVpsz(vpsz);
        srl.setFsz(fsz);

        a4RebellSync
                .syncRebellLinks()
                .syncRebellLinks()
                .body(srl)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

}
