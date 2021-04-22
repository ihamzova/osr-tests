package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.osr.RiAbstractionLayerClient;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_3_0.client.model.Device;
import io.qameta.allure.Step;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_OK_200;

public class RiAbstractionLayerRobot {
  private RiAbstractionLayerClient riAbstractionLayerClient = new RiAbstractionLayerClient();

  @Step("Get Device by EndSz")
  public Device getDeviceByEndsz(String endSz) {
    return riAbstractionLayerClient
            .getClient()
            .deviceController()
            .getOltByEndSZ()
            .endSzQuery(endSz).executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Get list of OLTs by VPSZ")
  public List<String> getOLtsByVpsz(String deviceType, String vpsz) {
    return riAbstractionLayerClient
            .getClient()
            .deviceController()
            .getOltsByEndSzSegment()
            .devicetypeQuery(deviceType)
            .vpszQuery(vpsz)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }
}
