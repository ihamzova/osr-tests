package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.HomeIdManagementClient;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.home.id.management.v1_3_0.client.model.PoolHomeId;
import com.tsystems.tm.acc.tests.osr.home.id.management.v1_3_0.client.model.SingleHomeId;
import io.qameta.allure.Step;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_BAD_REQUEST_400;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_CREATED_201;

public class HomeIdManagementRobot {
    private HomeIdManagementClient homeIdManagementClient = new HomeIdManagementClient(authTokenProvider);
    private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wg-access-provisioning", RhssoHelper.getSecretOfGigabitHub("wg-access-provisioning"));


    @Step ("Generate new homeId")
    public SingleHomeId generateHomeid () {
        return homeIdManagementClient.getClient()
                .homeIdGeneratorController()
                .generate()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step ("Generate batch of homeIds")
    public PoolHomeId generateBatchHomeids (Integer numberHomeIds) {
        return homeIdManagementClient.getClient()
                .homeIdGeneratorController()
                .generateBatch()
                .numberHomeIdsQuery(numberHomeIds)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
    }

    @Step ("Negative generate batch of homeIds")
    public PoolHomeId generateBatchHomeidsNeg (Integer numberHomeIds) {
        return homeIdManagementClient.getClient()
                .homeIdGeneratorController()
                .generateBatch()
                .numberHomeIdsQuery(numberHomeIds)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
    }

}
