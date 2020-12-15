package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_6_0.client.invoker.JSON;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class PonInventoryStub extends AbstractStubMapping {
    public static final String GET_LLC_URL = "/resource-order-resource-inventory/v2/llc";
    public static final String PATH_TO_PO_MOCK = "/team/morpheus/ponInventory.json";
    public static final String PATH_TO_DOMAIN_MOCK = "/team/morpheus/ponInventoryDomain.json";

    public MappingBuilder getLlc200(Dpu dpu, OltDevice olt){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllc200")
                    .willReturn(aDefaultResponseWithBody(prepareBody(olt),200))
                    .withQueryParam("gfApFolId", equalTo(dpu.getGfApFolId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappingBuilder getLlc400(Dpu dpu, OltDevice olt){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllc400")
                    .willReturn(aDefaultResponseWithBody(FileUtils.readFileToString(new File(getClass().getResource(PATH_TO_PO_MOCK).getFile()), Charset.defaultCharset()),400))
                    .withQueryParam("gfApFolId", equalTo(dpu.getGfApFolId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappingBuilder getLlcForDomain200(DpuDevice dpu){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllcDomain200")
                    .willReturn(aDefaultResponseWithBody(prepareBodyForDomain(dpu),200))
                    .withQueryParam("gfApFolId", equalTo(dpu.getFiberOnLocationId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String prepareBody(OltDevice oltDevice) throws IOException {
        return FileUtils.readFileToString(new File(getClass()
                .getResource(PATH_TO_PO_MOCK).getFile()), Charset.defaultCharset())
                .replace("$vpsz", oltDevice.getVpsz())
                .replace("$vsz", oltDevice.getFsz());
    }

    private String prepareBodyForDomain(DpuDevice dpuDevice) throws IOException {
        return FileUtils.readFileToString(new File(getClass()
                .getResource(PATH_TO_DOMAIN_MOCK).getFile()), Charset.defaultCharset());
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setOffsetDateTimeFormat(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
