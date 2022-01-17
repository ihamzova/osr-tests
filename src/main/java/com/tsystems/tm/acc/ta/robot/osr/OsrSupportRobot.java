package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.OsrSupportClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.osr.support.v1_2_0.client.model.ExecuteScriptDto;

import java.util.HashMap;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_ACCEPTED_202;

public class OsrSupportRobot {
    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wiremock-acc", RhssoHelper.getSecretOfGigabitHub("wiremock-acc"));
    private OsrSupportClient osrSupportClient = new OsrSupportClient(authTokenProvider);

    public void forceDeleteAccessLineByEndSz(String endSz) {

        System.out.println("authTokenProvider = " + authTokenProvider.getToken().getIdToken());
        HashMap <String, String> parameters = new HashMap<>();
        parameters.put("endSz", endSz);
        ExecuteScriptDto executeScriptDto = new ExecuteScriptDto();
        executeScriptDto.setAction("force_delete_al_by_endSz");
        executeScriptDto.setParameters(parameters);
        System.out.println(executeScriptDto);

        osrSupportClient
                .getClient()
                .osrSupportController()
                .execute()
                .body(executeScriptDto)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    }
}
