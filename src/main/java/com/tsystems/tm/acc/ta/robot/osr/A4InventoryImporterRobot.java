package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.A4RebellSyncClient;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.tests.osr.a4.link.event.importer.client.model.Event;
import com.tsystems.tm.acc.tests.osr.a4.rebell.sync.client.model.SyncRebellLinks;
import io.qameta.allure.Step;
import com.tsystems.tm.acc.ta.api.osr.A4InventoryImporterClient;
import com.tsystems.tm.acc.ta.api.osr.A4RebellLinkEventClient;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

public class A4InventoryImporterRobot {
    private final A4RebellSyncClient a4RebellSync = new A4RebellSyncClient();
    private final A4InventoryImporterClient a4Importer = new A4InventoryImporterClient();
    private final A4RebellLinkEventClient a4LinkEvent = new A4RebellLinkEventClient();
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
    @Step("Create Horizon Event for Importer")
    public void sendNotification(Event event) {

        a4LinkEvent.getClient().event().linkImportEventCallback()
                .body(event)
                .execute(checkStatus(HTTP_CODE_OK_200));

    }


}
