package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.WgA4ProvisioningClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.wg.a4.provisioning.internal.client.model.TpRefDto;
import io.qameta.allure.Step;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class WgA4PreProvisioningRobot {
    private static final Integer HTTP_CODE_CREATED_201 = 201;

    private ApiClient wgA4ProvisioningClient = new WgA4ProvisioningClient().getClient();

    @Step("Start preprovisioning process")
    public void startPreProvisioning(TpRefDto tpRefDto) {
        wgA4ProvisioningClient
                .preProvisioningProcessController()
                .startAccessLinePreProvisioning()
                .body(tpRefDto)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));

    }

}
