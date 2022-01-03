package com.tsystems.tm.acc.ta.data.osr.wiremock;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.*;
import com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextBuilder;

import java.util.ArrayList;
import java.util.List;

public class OsrWireMockMappingsContextBuilder extends WireMockMappingsContextBuilder {

  public OsrWireMockMappingsContextBuilder(ExtendedWireMock wireMock) {
    super(wireMock);
  }

  public OsrWireMockMappingsContextBuilder(WireMockMappingsContext context) {
    super(context);
  }

  public OsrWireMockMappingsContextBuilder addRebellMock(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
    context.add(new RebellStub().getUeweg200(uewegData, neA, neB));
    return this;
  }

  public OsrWireMockMappingsContextBuilder addRebellMock(A4NetworkElement neA, UewegData uewegData, A4NetworkElement neB, UewegData uewegData2, A4NetworkElement neB2) {
    List<UewegData> ueWegeList = new ArrayList<>();
    ueWegeList.add(uewegData);
    ueWegeList.add(uewegData2);
    List<A4NetworkElement> a4NetworkElements = new ArrayList<>();
    a4NetworkElements.add(neB);
    a4NetworkElements.add(neB2);
    context.add(new RebellStub().getUewegMultiple200(neA, ueWegeList, a4NetworkElements));
    return this;
  }

  public OsrWireMockMappingsContextBuilder addRebellMockEmpty(A4NetworkElement neA) {
    context.add(new RebellStub().getUewegEmpty(neA));
    return this;
  }

  public OsrWireMockMappingsContextBuilder addPslMock(EquipmentData equipmentData, A4NetworkElement networkElement) {
    context.add(new PslStub().postReadEquipment202(equipmentData, networkElement));
    return this;
  }

  public OsrWireMockMappingsContextBuilder addPslMock(OltDevice oltDevice) {
    context.add(new PslStub().postReadEquipment202(oltDevice));
    return this;
  }

  public OsrWireMockMappingsContextBuilder addSealMock(OltDevice oltDevice) {
    context.add(new SealStub().getAccessNodesConfiguration202(oltDevice));
    return this;
  }

  public OsrWireMockMappingsContextBuilder addSealNoEmsEventsMock() {
    context.add(new SealStub().getEmptyListOfEmsEvents());
    return this;
  }
  public OsrWireMockMappingsContextBuilder addOltNoDeviceMock() {
    context.add(new OltRiStub().getNoDevicefromOltRi());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addWgA4ProvisioningMock() {
    context.add(new PreProvisioningStub().getAccessLine500());
    context.add(new PreProvisioningStub().getAccessLine201());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addNemoMock() {
    context.add(new NemoStub().putNemoUpdate201());
    context.add(new NemoStub().deleteNemoUpdate204());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addMerlinMock() {
    context.add(new MerlinStub().postMerlinCallbackResponce202());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addNemoMock500() {
    context.add(new NemoStub().deleteNemoUpdate500());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addNemoMock400() {
    context.add(new NemoStub().deleteNemoUpdate400());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addA4ResourceInventoryMock500() {
    context.add(new A4ResourceInventoryStub().getTPWith500());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addA4ResourceInventoryMock201() {
    context.add(new A4ResourceInventoryStub().putTPWith201());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addA4NspProfileBySnMockWithoutOntLastRegisteredOn() {
    context.add(new A4ResourceInventoryStub().getNspBySnWithoutOntLastRegisteredOn());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addA4NspBySnMockEmpty() {
    context.add(new A4ResourceInventoryStub().getNspBySnEmpty());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addA4NspByUuidWithoutOntLastRegisteredOn() {
    context.add(new A4ResourceInventoryStub().getNspByUuidWithoutOntLastRegisteredOn());
    return this;
  }

  public OsrWireMockMappingsContextBuilder addA4NetworkElementPort(String endSz, String port) {
    context.add(new A4ResourceInventoryStub().getNetworkElementPort(endSz, port));
    return this;
  }
  public OsrWireMockMappingsContextBuilder addDeviceFromA4RiMock() {
    context.add(new A4ResourceInventoryStub().getA4NetworkElements());
    return this;
  }
  public OsrWireMockMappingsContextBuilder addNoDeviceFromA4RiMock() {
    context.add(new A4ResourceInventoryStub().getA4NoNetworkElements());
    return this;
  }
}
