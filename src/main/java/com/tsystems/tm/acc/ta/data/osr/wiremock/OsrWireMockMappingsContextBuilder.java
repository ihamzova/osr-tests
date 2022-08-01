package com.tsystems.tm.acc.ta.data.osr.wiremock;

import com.tsystems.tm.acc.ta.data.osr.models.*;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.*;
import com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OsrWireMockMappingsContextBuilder extends WireMockMappingsContextBuilder {

    public OsrWireMockMappingsContextBuilder(ExtendedWireMock wireMock) {
        super(wireMock);
    }

    public OsrWireMockMappingsContextBuilder(WireMockMappingsContext context) {
        super(context);
    }

  /*
  public OsrWireMockMappingsContextBuilder addPluralTnpMock(PluralTnpData pluralTnpData) {
    context.add(new PluralStub().postPluralResponce201(pluralTnpData));
    return this;
  }
  public OsrWireMockMappingsContextBuilder addPluralMock() {
    context.add(new PluralStub().postPluralResponce());
    return this;
  }
   */

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

    public OsrWireMockMappingsContextBuilder addPslMockXML(OltDevice oltDevice) {
        try {
            context.add(new PslStub().postReadEquipmentXml202(oltDevice));
        } catch (Exception e) {
            log.warn("addPslMockXML endSz = " + oltDevice.getEndsz() + " exception " + e.getMessage());
        }
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

    public OsrWireMockMappingsContextBuilder addSealAccessLineConfigNegativeMock() {
        context.add(new SealStub().postAccessLineConfigurationCallbackError());
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

    public OsrWireMockMappingsContextBuilder addNemoMock500(String uuid) {
        context.add(new NemoStub().deleteNemoUpdate500(uuid));
        return this;
    }

    public OsrWireMockMappingsContextBuilder addNemoMock400(String uuid) {
        context.add(new NemoStub().deleteNemoUpdate400(uuid));
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

    public OsrWireMockMappingsContextBuilder addA4NspByUuidWithoutOntLastRegisteredOnWorking() {
        context.add(new A4ResourceInventoryStub().getNspByUuidWithoutOntLastRegisteredOnWorking());
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

    public OsrWireMockMappingsContextBuilder addDhcp4oltGetOltNotFoundMock(OltDevice oltDevice) {
        context.add(new Dhcp4oltStub().getOlt200OltNotFound(oltDevice));
        return this;
    }

    public OsrWireMockMappingsContextBuilder addDhcp4oltGetOltMock(OltDevice oltDevice) {
        context.add(new Dhcp4oltStub().getOlt200(oltDevice));
        return this;
    }

    public OsrWireMockMappingsContextBuilder addDhcp4oltGetBngMock(OltDevice oltDevice) {
        context.add(new Dhcp4oltStub().getBng200(oltDevice));
        return this;
    }

    public OsrWireMockMappingsContextBuilder addOltBasicConfigurationMock(OltDevice oltDevice) {
        context.add(new SealStub().postOltBasicConfiguration202CallbackSuccess(oltDevice));
        return this;
    }

    public OsrWireMockMappingsContextBuilder addOltBasicConfigurationErrorMock(OltDevice oltDevice) {
        context.add(new SealStub().postOltBasicConfiguration202CallbackError(oltDevice, true));
        return this;
    }
}
