package com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class GigaAreasLocationStub   extends AbstractStubMapping {

    public static final String FIBRE_ON_LOCATIONS_SEARCH_URL = "/giga-areas-location/v2/fibreOnLocations/search";
    public static final String PATH_TO_PO_MOCK = "/team/mercury/fiber-locations-by-klsid-payload.json";

    public MappingBuilder getPageFibreOnLocation200(DpuDevice dpuDevice) {
        try {
        return get(urlPathEqualTo(FIBRE_ON_LOCATIONS_SEARCH_URL))
                .withName("getPageFibreOnLocation200")
                .willReturn(aDefaultResponseWithBody(prepareBody(dpuDevice), HTTP_CODE_OK_200))
                .withQueryParam("klsId", equalTo(dpuDevice.getKlsId()) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String prepareBody(DpuDevice dpuDevice) throws IOException {
        return FileUtils.readFileToString(new File(getClass()
                .getResource(PATH_TO_PO_MOCK).getFile()), Charset.defaultCharset())
                .replace("$fiberOnLocationId", dpuDevice.getFiberOnLocationId())
                .replace("$klsId", dpuDevice.getKlsId())
                .replace("$we", dpuDevice.getPonConnectionWe())
                .replace("$ge", dpuDevice.getPonConnectionGe());
    }
}
