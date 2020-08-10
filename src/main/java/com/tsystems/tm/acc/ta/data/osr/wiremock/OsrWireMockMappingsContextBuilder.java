package com.tsystems.tm.acc.ta.data.osr.wiremock;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.PslStub;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.RebellStub;
import com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.SealStub;
import com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextBuilder;

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
}
