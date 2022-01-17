package com.tsystems.tm.acc.ta.team.upiter.ral;


import com.tsystems.tm.acc.data.upiter.models.expectedabstractdevice.ExpectedAbstractDeviceCase;
import com.tsystems.tm.acc.data.upiter.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.data.osr.models.ExpectedAbstractDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.robot.osr.RiAbstractionLayerRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_10_0.client.model.AbstractDevice;
import com.tsystems.tm.acc.tests.osr.ri.abstraction.layer.v1_10_0.client.model.Device;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.RI_ABSTRACTION_LAYER_MS;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachStubsToAllureReport;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.savePublishedToDefaultDir;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@ServiceLog(RI_ABSTRACTION_LAYER_MS)
@Epic("RI Abstraction Layer")
public class RalTest extends GigabitTest {

  private OltDevice oltDeviceForRal;
  private OltDevice oltDevice;
  private OltDevice a4DeviceForRal;
  private RiAbstractionLayerRobot riAbstractionLayerRobot = new RiAbstractionLayerRobot();
  private UpiterTestContext context = UpiterTestContext.get();
  private List<String> oltList;
  private WireMockMappingsContext mappingsContext;
  private ExpectedAbstractDevice expectedA4device = new ExpectedAbstractDevice();
  private ExpectedAbstractDevice expectedOltBngDevice = new ExpectedAbstractDevice();

  @BeforeMethod
  public void loadContext() {
    oltDevice = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.OltDevice);
    oltDeviceForRal = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.OltDeviceForRal);
    a4DeviceForRal = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.A4DataForRal);
    expectedA4device = context.getData().getExpectedAbstractDeviceDataProvider().get(ExpectedAbstractDeviceCase.ExpectedA4Device);
    expectedOltBngDevice = context.getData().getExpectedAbstractDeviceDataProvider().get(ExpectedAbstractDeviceCase.ExpectedOltBngDevice);
  }

  @Test
  @TmsLink("DIGIHUB-51273")
  @Description("Gets common Device by EndSz")
  public void getDeviceByEndSz() {
    Device response = riAbstractionLayerRobot.getDeviceByEndsz(oltDevice.getEndsz());
    assertEquals(response.getEndSz(), oltDevice.getEndsz());
    assertEquals(response.getEmsNbiName(), oltDevice.getBezeichnung());
    assertEquals(response.getEquipmentHolders().get(0).getCard().getCardType().toString(), oltDevice.getCardType());
    assertEquals(response.getType().toString(), oltDevice.getDeviceType());
    assertEquals(response.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortType().toString(), oltDevice.getPortType());
    assertEquals(response.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortNumber(), oltDevice.getOltPort());
  }

  @Test
  @TmsLink("DIGIHUB-51269")
  @Description("Gets A4 Device by EndSz")
  public void getA4DeviceByEndSz() {
    Device response = riAbstractionLayerRobot.getDeviceByEndsz(a4DeviceForRal.getEndsz());
    assertEquals(response.getEndSz(), a4DeviceForRal.getEndsz());
    assertEquals(response.getType().toString(), a4DeviceForRal.getDeviceType());
    assertEquals(response.getEquipmentHolders().get(0).getCard().getCardType().toString(), a4DeviceForRal.getCardType());
    assertEquals(response.getEquipmentHolders().get(0).getSlotNumber(), a4DeviceForRal.getOltSlot());
    assertEquals(response.getEquipmentHolders().get(0).getCard().getPorts().get(0).getPortType().toString(), a4DeviceForRal.getPortType());
  }

  @Test
  @TmsLink("DIGIHUB-51268")
  @Description("Get list of OLTs by VPSZ")
  public void getListofOltsbyVpsz() {
    OltDevice olt1 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.Ral76H1);
    OltDevice olt2 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.Ral76H2);
    OltDevice olt3 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.Ral76H3);
    OltDevice olt4 = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.Ral7KH4);
    oltList = new ArrayList<>(Arrays.asList(olt1.getEndsz(), olt2.getEndsz(), olt3.getEndsz(), olt4.getEndsz()));
    List<String> response = riAbstractionLayerRobot.getOLtsByVpsz(oltDeviceForRal.getDeviceType(), oltDeviceForRal.getVpsz());
    assertEquals(response, oltList);

  }

  @Test
  @TmsLink("DIGIHUB-128026")
  @Description("Get abstract device by VPSZ. Not found in OLT RI, found in A4 RI")
  public void getAbstractDevicebyVpsz() {
    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "GetAbsctractDevice"))
            .addDeviceFromA4RiMock()
            .addOltNoDeviceMock()
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    List<AbstractDevice> deviceList = riAbstractionLayerRobot.getDeviceByVpsz(a4DeviceForRal.getVpsz());
    mappingsContext.deleteStubs();
    final List<ExpectedAbstractDevice> expectedDeviceList = deviceList.stream().map(RiAbstractionLayerRobot::mapToAbstractDevice).collect(Collectors.toList());
    assertTrue(expectedDeviceList.contains(expectedA4device));

  }

  @Test
  @TmsLink("DIGIHUB-128027")
  @Description("Get abstract device by KLSID. Found in OLT RI, not found in A4 RI")
  public void getAbstractDevicebyKlsID() {
    mappingsContext = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "GetAbsctractDevice"))
            .addNoDeviceFromA4RiMock()
            .build()
            .publish()
            .publishedHook(savePublishedToDefaultDir())
            .publishedHook(attachStubsToAllureReport());

    List<AbstractDevice> deviceList = riAbstractionLayerRobot.getDeviceByKlsId(expectedOltBngDevice.getKlsId());
    mappingsContext.deleteStubs();
    final List<ExpectedAbstractDevice> expectedDeviceList = deviceList.stream().map(RiAbstractionLayerRobot::mapToAbstractDevice).collect(Collectors.toList());
    assertTrue(expectedDeviceList.contains(expectedOltBngDevice));
  }

  @Test
  @TmsLink("DIGIHUB-126828")
  @Description("Get abstract device by fiberOnLocationId.Found in OLT RI and in A4 RI")
  public void getAbstractDevicebyFiberOnLocation() {
    List<AbstractDevice> deviceList = riAbstractionLayerRobot.getDeviceByFiberOnLocation(expectedA4device.getFiberOnLocationId());
    final List<ExpectedAbstractDevice> expectedDeviceList = deviceList.stream().map(RiAbstractionLayerRobot::mapToAbstractDevice).collect(Collectors.toList());
    assertTrue(expectedDeviceList.contains(expectedA4device));
    assertTrue(expectedDeviceList.contains(expectedOltBngDevice));
  }
}
