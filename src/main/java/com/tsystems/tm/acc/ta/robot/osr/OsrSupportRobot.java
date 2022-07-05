package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.OsrSupportClient;
import com.tsystems.tm.acc.tests.osr.osr.support.v1_2_0.client.model.ExecuteScriptDto;

import java.util.HashMap;

import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;

public class OsrSupportRobot {
    private final OsrSupportClient osrSupportClient = new OsrSupportClient();

    public void forceDeleteAccessLineByEndSz(String endSz) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("endSz", endSz);
        ExecuteScriptDto executeScriptDto = new ExecuteScriptDto();
        executeScriptDto.setAction("force_delete_al_by_endSz");
        executeScriptDto.setParameters(parameters);

        osrSupportClient
                .getClient()
                .osrSupportController()
                .execute()
                .body(executeScriptDto)
                .executeAs(checkStatus(HTTP_CODE_ACCEPTED_202));
    }
}
