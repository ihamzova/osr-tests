package com.tsystems.tm.acc.ta.data.mercury.wiremock;

import com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings.GigaAreasLocationStub;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
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
}
