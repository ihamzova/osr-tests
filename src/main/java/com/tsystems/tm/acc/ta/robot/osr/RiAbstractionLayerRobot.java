package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.RiAbstractionLayerClient;
import com.tsystems.tm.acc.ta.data.osr.models.ExpectedAbstractDevice;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_10_0.client.model.AbstractDevice;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_10_0.client.model.Device;
import io.qameta.allure.Step;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.upiter.CommonTestData.HTTP_CODE_OK_200;

public class RiAbstractionLayerRobot {
  private RiAbstractionLayerClient riAbstractionLayerClient = new RiAbstractionLayerClient(authTokenProvider);
  private static final AuthTokenProvider authTokenProvider = new RhssoClientFlowAuthTokenProvider("wiremock-acc", RhssoHelper.getSecretOfGigabitHub("wiremock-acc"));

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

  @Step("Get Device by VPSZ")
  public List<AbstractDevice> getDeviceByVpsz(String vpsz) {
    return riAbstractionLayerClient
            .getClient()
            .deviceController()
            .getDeviceByEndSZ()
            .vpszQuery(vpsz)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Get Device by KLSId")
  public List<AbstractDevice> getDeviceByKlsId(Integer klsID) {
    return riAbstractionLayerClient
            .getClient()
            .deviceController()
            .getDeviceByEndSZ()
            .klsIdQuery(klsID)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  @Step("Get Device by FiberonLocation")
  public List<AbstractDevice> getDeviceByFiberOnLocation(String fiberOnLocation) {
    return riAbstractionLayerClient
            .getClient()
            .deviceController()
            .getDeviceByEndSZ()
            .fiberOnLocationIdQuery(fiberOnLocation)
            .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
  }

  public static ExpectedAbstractDevice mapToAbstractDevice(AbstractDevice abstractDevice) {
    ExpectedAbstractDevice device = new ExpectedAbstractDevice();
    if (abstractDevice.getId() != null) {
      device.setId(abstractDevice.getId().intValue());
    }
    device.setProductionPlatform(abstractDevice.getProductionPlatform().getValue());
    device.setEndSz(abstractDevice.getEndSz());
    device.setFiberOnLocationId(abstractDevice.getFiberOnLocationId());
    device.setKlsId(abstractDevice.getKlsId().intValue());
    device.setSerialNumber(abstractDevice.getSerialNumber());
    device.setEmsNbiName(abstractDevice.getEmsNbiName());
    device.setMaterialNumber(abstractDevice.getMaterialNumber());
    device.setAccessTransmissionMedium(abstractDevice.getAccessTransmissionMedium());
    device.setLifeCycleState(abstractDevice.getLifeCycleState().getValue());
    device.setVpSz(abstractDevice.getVpSz());
    device.setFSz(abstractDevice.getfSz());
    device.setCards(abstractDevice.getCards());
    device.setRelatedParty(abstractDevice.getRelatedParty());
    device.setPorts(abstractDevice.getPorts());
    return device;
  }}
