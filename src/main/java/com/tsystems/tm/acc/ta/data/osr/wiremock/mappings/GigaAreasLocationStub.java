package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.mappers.GigaAreasLocationMapper;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;
import static com.tsystems.tm.acc.ta.data.osr.wiremock.mappings.StubUtils.serialize;

public class GigaAreasLocationStub   extends AbstractStubMapping {

    public static final String FIBRE_ON_LOCATIONS_SEARCH_URL = "/giga-areas-location/v2/fibreOnLocations/search";

    public MappingBuilder getPageFibreOnLocation200(DpuDevice dpuDevice) {

        return get(urlPathEqualTo(FIBRE_ON_LOCATIONS_SEARCH_URL))
                .withName("getPageFibreOnLocation200")
                .willReturn(aDefaultResponseWithBody(serialize(new GigaAreasLocationMapper().getPageGigaAreaV2DTO(dpuDevice)), HTTP_CODE_OK_200))
                .withQueryParam("klsId", equalTo(dpuDevice.getKlsId()) );
    }

}
