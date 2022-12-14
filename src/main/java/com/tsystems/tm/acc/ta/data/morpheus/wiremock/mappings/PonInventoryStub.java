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
    public static final String PATH_TO_PO_ADTRAN_MOCK = "/team/morpheus/ponInventory_adtran.json";
    public static final String PATH_TO_PO_MOCK_COM_DIFF_PORT = "/team/morpheus/ponInventory_negative_comissioning_different_ports.json";
    public static final String PATH_TO_PO_MOCK_DECOM_DIFF_SLOT = "/team/morpheus/ponInventory_negative_decomissioning_different_slots.json";
    public static final String PATH_TO_DOMAIN_MOCK = "/team/morpheus/ponInventoryDomain.json";
    public static final String PATH_TO_DOMAIN_MOCK_WITH_DPU_DEMANDS = "/team/morpheus/ponInventoryDomainWithDpuDemands.json";
    public static final String PATH_TO_DOMAIN_MOCK_BNG_FROM_MOBILE_DPU = "/team/morpheus/ponInventoryDomainBngPlatformFromMobileDpuUi.json";

    //TODO refactor this in inno sprint: return null is rough
    public MappingBuilder getLlc200(Dpu dpu, OltDevice olt){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllc200")
                    .willReturn(aDefaultResponseWithBody(prepareBody(olt, PATH_TO_PO_MOCK),200))
                    .withQueryParam("dpuEndSz", equalTo(dpu.getEndSz()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappingBuilder getLlcAdtran200(Dpu dpu, OltDevice olt){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllcAdtran200")
                    .willReturn(aDefaultResponseWithBody(prepareBody(olt, PATH_TO_PO_ADTRAN_MOCK),200))
                    .withQueryParam("dpuEndSz", equalTo(dpu.getEndSz()));
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
                    .withQueryParam("dpuEndSz", equalTo(dpu.getEndSz()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappingBuilder getLlcDiffPorts200(Dpu dpu, OltDevice olt){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllc200")
                    .willReturn(aDefaultResponseWithBody(prepareBody(olt, PATH_TO_PO_MOCK_COM_DIFF_PORT),200))
                    .withQueryParam("dpuEndSz", equalTo(dpu.getEndSz()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappingBuilder getLlcDiffSlots200(Dpu dpu, OltDevice olt){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllc200")
                    .willReturn(aDefaultResponseWithBody(prepareBody(olt, PATH_TO_PO_MOCK_DECOM_DIFF_SLOT),200))
                    .withQueryParam("dpuEndSz", equalTo(dpu.getEndSz()));
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
                    .withQueryParam("dpuEndSz", equalTo(dpu.getEndsz()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappingBuilder getLlcForDomainWithDpuDemands200(DpuDevice dpu){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllcDomainWithDpuDemands200")
                    .willReturn(aDefaultResponseWithBody(prepareBodyForDomainWithDpuDemands(dpu),200))
                    .withQueryParam("dpuEndSz", equalTo(dpu.getEndsz()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappingBuilder getLlcForDomainFromMobileDPU200(DpuDevice dpu){
        try {
            return get(urlPathEqualTo(GET_LLC_URL))
                    .withName("getllcDomainFromMobileDpu200")
                    .willReturn(aDefaultResponseWithBody(prepareBodyForDomainFromMobileDpu(dpu),200))
                    .withQueryParam("dpuEndSz", equalTo(dpu.getEndsz()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String prepareBody(OltDevice oltDevice, String mockPath) throws IOException {
        return FileUtils.readFileToString(new File(getClass()
                .getResource(mockPath).getFile()), Charset.defaultCharset())
                .replace("$vpsz", oltDevice.getVpsz())
                .replace("$vsz", oltDevice.getFsz());
    }

    private String prepareBodyForDomain(DpuDevice dpuDevice) throws IOException {
        return FileUtils.readFileToString(new File(getClass()
                .getResource(PATH_TO_DOMAIN_MOCK).getFile()), Charset.defaultCharset());
    }

    private String prepareBodyForDomainWithDpuDemands(DpuDevice dpuDevice) throws IOException {
        return FileUtils.readFileToString(new File(getClass()
                .getResource(PATH_TO_DOMAIN_MOCK_WITH_DPU_DEMANDS).getFile()), Charset.defaultCharset());
    }

    private String prepareBodyForDomainFromMobileDpu(DpuDevice dpuDevice) throws IOException {
        return FileUtils.readFileToString(new File(getClass()
                .getResource(PATH_TO_DOMAIN_MOCK_BNG_FROM_MOBILE_DPU).getFile()), Charset.defaultCharset());
    }
}
