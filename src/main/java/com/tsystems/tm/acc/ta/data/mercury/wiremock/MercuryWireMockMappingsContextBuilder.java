package com.tsystems.tm.acc.ta.data.mercury.wiremock;

import com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings.AccessLineInventoryStub;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings.DeviceDiscoveryCallbackStub;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings.GigaAreasLocationStub;
import com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings.PonInventoryStub;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.ExtendedWireMock;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextBuilder;

public class MercuryWireMockMappingsContextBuilder extends WireMockMappingsContextBuilder {
    public MercuryWireMockMappingsContextBuilder(ExtendedWireMock wireMock) {
        super(wireMock);
    }

    public MercuryWireMockMappingsContextBuilder(WireMockMappingsContext context) {
        super(context);
    }

    public MercuryWireMockMappingsContextBuilder addGigaAreasLocationMock(DpuDevice dpuDevice) {
        context.add(new GigaAreasLocationStub().getPageFibreOnLocation200(dpuDevice));
        return this;
    }

    public MercuryWireMockMappingsContextBuilder addDiscoveryCallbackReceiver() {
        context.add(new DeviceDiscoveryCallbackStub().addDiscoveryCallbackReceiver200());
        return this;
    }

    public MercuryWireMockMappingsContextBuilder addPonInventoryMock(OltDevice oltDevice){
        context.add(new PonInventoryStub().getLlcInfo200(oltDevice));
        return this;
    }

    public MercuryWireMockMappingsContextBuilder addAccessLineInventoryMock(){
        context.add(new AccessLineInventoryStub().getAlCountTask200());
        return this;
    }
}
