package com.tsystems.tm.acc.ta.team.upiter.ral;


import com.tsystems.tm.acc.data.upiter.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.RiAbstractionLayerRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_3_0.client.model.Device;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.RI_ABSTRACTION_LAYER_MS;
import static org.testng.Assert.assertEquals;

@ServiceLog(RI_ABSTRACTION_LAYER_MS)
public class RalTest extends GigabitTest {

  private OltDevice deviceForRal;
  private RiAbstractionLayerRobot riAbstractionLayerRobot = new RiAbstractionLayerRobot();
  private UpiterTestContext context = UpiterTestContext.get();
  private List<String> oltList;

  @BeforeMethod
  public void loadContext() {
    deviceForRal = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.OltDeviceForRal);
  }

  @Test
  @TmsLink("DIGIHUB-51269")
  @Description("Gets common Device by EndSz")
  public void getDeviceByEndSz() {
    Device response = riAbstractionLayerRobot.getDeviceByEndsz(deviceForRal.getEndsz());
    assertEquals(response.getEndSz(), deviceForRal.getEndsz());
    assertEquals(response.getDeviceName(), deviceForRal.getBezeichnung());
    assertEquals(response.getEquipmentHolders().get(2).getCard().getCardType().toString(), deviceForRal.getCardType());
    assertEquals(response.getEquipmentHolders().get(0).getSlotNumber(), deviceForRal.getOltSlot());
    assertEquals(response.getType().toString(), deviceForRal.getDeviceType());
    assertEquals(response.getEquipmentHolders().get(0).getCard().getPorts().get(3).getPortType().toString(), deviceForRal.getPortType());
    assertEquals(response.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortNumber(), deviceForRal.getOltPort());
  }

  @Test
  @TmsLink("DIGIHUB-51268")
  @Description("Get list of OLTs by VPSZ")
  public void getListofOltsbyVpsz() {
    OltDevice olt1 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.Ral76H1);
    OltDevice olt2 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.Ral76H2);
    OltDevice olt3 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.Ral76H3);
    OltDevice olt4 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.Ral7KH4);
    List oltList = new ArrayList<>(Arrays.asList(olt1.getEndsz(), olt2.getEndsz(), olt3.getEndsz(), olt4.getEndsz()));
    List<String> response = riAbstractionLayerRobot.getOLtsByVpsz(deviceForRal.getDeviceType(), deviceForRal.getVpsz());
    assertEquals(response, oltList);

  }
}