package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.OltResourceInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_2_0.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_2_0.client.model.AncpSessionDto;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_2_0.client.model.Device;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_2_0.client.model.Port;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class OltResourceInventoryStub extends AbstractStubMapping {
    public static final String DPU_DEVICE_URL = "/resource-order-resource-inventory/v1/device";
    public static final String DPU_PORT_URL = "/resource-order-resource-inventory/v1/port";
    public static final String GET_DPU_PON_CONNECTION_URL = "/resource-order-resource-inventory/v1/dpu/dpuPonConnection";
    public static final String GET_ETHERNET_LINK_URL = "/resource-order-resource-inventory/v1/ethernetlink/findEthernetLinksByEndsz";
    public static final String GET_DPU_ANCP_SESSION_URL = "/resource-order-resource-inventory/v1/ancp/session/endsz";
    public static final String DPU_AT_OLT_CONF_URL = "/resource-order-resource-inventory/v1/dpu/dpuAtOltConfiguration";
    public static final String DPU_EMS_CONFIGURATION_URL = "/resource-order-resource-inventory/v1/dpu/dpuEmsConfiguration";

    public MappingBuilder getDpuDevice200(Dpu dpu) {
        return get(urlPathEqualTo(DPU_DEVICE_URL))
                .withName("getDpuDevice200")
                .willReturn(aDefaultResponseWithBody(
                        serialize(Collections.singletonList(new OltResourceInventoryMapper().getDevice(dpu.getLifeCycleDpu(), dpu.getLifeCycleUplink()))),
                        200
                ))
                .withQueryParam("endsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder getDpuDevice400(Dpu dpu) {
        return get(urlPathEqualTo(DPU_DEVICE_URL))
                .withName("getDpuDevice400")
                .willReturn(aDefaultResponseWithBody(
                        serialize(Collections.singletonList(new OltResourceInventoryMapper().getDevice(dpu.getLifeCycleDpu(), dpu.getLifeCycleUplink()))),
                        400
                ))
                .withQueryParam("endsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder patchDpuDevice200(Dpu dpu) {
        return patch(urlMatching(DPU_DEVICE_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new OltResourceInventoryMapper().getDevice(Device.LifeCycleStateEnum.INSTALLING, Port.LifeCycleStateEnum.INSTALLING)),
                        200
                ))
                .withName("patchDpuDevice200");
    }

    public MappingBuilder patchDpuPort200(Dpu dpu) {
        return patch(urlMatching(DPU_PORT_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new OltResourceInventoryMapper().getPort(Port.LifeCycleStateEnum.INSTALLING)),
                        200
                ))
                .withName("patchDpuPort200");
    }

    public MappingBuilder getDpuPonConnection200(OltDevice oltDevice, Dpu dpu) {
        return get(urlPathEqualTo(GET_DPU_PON_CONNECTION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new OltResourceInventoryMapper().getDpuPonConnection(oltDevice, dpu))),
                        200
                ))
                .withName("getDpuPonConnection200")
                .withQueryParam("dpuPonPortEndsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder getDpuPonConnection400(OltDevice oltDevice, Dpu dpu) {
        return get(urlPathEqualTo(GET_DPU_PON_CONNECTION_URL))
                .withName("getDpuPonConnection400")
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new OltResourceInventoryMapper().getDpuPonConnection(oltDevice, dpu))),
                        400
                ))
                .withQueryParam("dpuPonPortEndsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder getEthernetLink200(OltDevice oltDevice, Dpu dpu) {
        return get(urlPathEqualTo(GET_ETHERNET_LINK_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new OltResourceInventoryMapper().getEthernetLink(oltDevice, dpu))),
                        200
                ))
                .withName("getEthernetLink200")
                .withQueryParam("oltEndSz", equalTo(oltDevice.getVpsz() + "/" + oltDevice.getFsz()));
    }

    public MappingBuilder getEthernetLink400(OltDevice oltDevice, Dpu dpu) {
        return get(urlPathEqualTo(GET_ETHERNET_LINK_URL))
                .withName("getEthernetLink400")
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new OltResourceInventoryMapper().getEthernetLink(oltDevice, dpu))),
                        400
                ))
                .withQueryParam("oltEndSz", equalTo(oltDevice.getVpsz() + "/" + oltDevice.getFsz()));
    }

    public MappingBuilder getDpuAncpSession200(Dpu dpu) {
        return get(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new OltResourceInventoryMapper().getAncpSessionDto(AncpSessionDto.SessionTypeEnum.DPU)),
                        200
                ))
                .withName("getDpuAncpSession200")
                .withQueryParam("endsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder getDpuAncpSession400(Dpu dpu) {
        return get(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL))
                .withName("getDpuAncpSession400")
                .willReturn(aDefaultResponseWithBody(serialize(new OltResourceInventoryMapper().getAncpSessionDto(AncpSessionDto.SessionTypeEnum.DPU)), 400))
                .withQueryParam("endsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder getOltAncpSession200(OltDevice olt, Dpu dpu) {
        return get(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new OltResourceInventoryMapper().getAncpSessionDto(AncpSessionDto.SessionTypeEnum.OLT)),
                        200
                ))
                .withName("getOltAncpSession200")
                .withQueryParam("endsz", equalTo(olt.getEndsz()));
    }

    public MappingBuilder getDpuAtOltConfExist200(Dpu dpu) {
        return get(urlPathEqualTo(DPU_AT_OLT_CONF_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(
                                Collections.singletonList(
                                        new OltResourceInventoryMapper().getDpuAtOltConfigurationDto(false)
                                )
                        ),
                        200
                ))
                .withName("getDpuAtOltConfExist200")
                .withQueryParam("dpuEndsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder getDpuAtOltConfNew200(Dpu dpu) {
        return get(urlPathEqualTo(DPU_AT_OLT_CONF_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Collections.emptyList()),
                        200
                ))
                .withName("getDpuAtOltConfNew200")
                .withQueryParam("dpuEndsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder postDpuAtOltConf200(Dpu dpu) {
        return post(urlPathEqualTo(DPU_AT_OLT_CONF_URL))
                .withName("postDpuAtOltConf200")
                .willReturn(aDefaultResponseWithBody(
                        serialize(new OltResourceInventoryMapper().getDpuAtOltConfigurationDto(true)),
                        200
                ))
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.dpuEndsz=='%s')]", dpu.getEndSz())));
    }

    public MappingBuilder putDpuAtOltConf200(Dpu dpu) {
        return put(urlMatching(DPU_AT_OLT_CONF_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new OltResourceInventoryMapper().getDpuAtOltConfigurationDto(true)),
                        200
                ))
                .withName("putDpuAtOltConf200")
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.dpuEndsz=='%s')]", dpu.getEndSz())));
    }

    public MappingBuilder getDpuEmsConfExist200(Dpu dpu) {
        return get(urlPathEqualTo(DPU_EMS_CONFIGURATION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(
                                Collections.singletonList(
                                        new OltResourceInventoryMapper().getDpuEmsConfigurationDto(false)
                                )
                        ),
                        200
                ))
                .withName("getDpuEmsConfExist200")
                .withQueryParam("dpuEndsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder getDpuEmsConfNew200(Dpu dpu) {
        return get(urlPathEqualTo(DPU_EMS_CONFIGURATION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Collections.emptyList()),
                        200
                ))
                .withName("getDpuEmsConfNew200")
                .withQueryParam("dpuEndsz", equalTo(dpu.getEndSz()));
    }

    public MappingBuilder postDpuEmsConf200(Dpu dpu) {
        return post(urlPathEqualTo(DPU_EMS_CONFIGURATION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new OltResourceInventoryMapper().getDpuEmsConfigurationDto(true)),
                        200
                ))
                .withName("postDpuEmsConf200")
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.dpuEndsz=='%s')]", dpu.getEndSz())));
    }

    public MappingBuilder putDpuEmsConf200(Dpu dpu) {
        return put(urlMatching(DPU_EMS_CONFIGURATION_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new OltResourceInventoryMapper().getDpuEmsConfigurationDto(true)),
                        200
                ))
                .withName("putDpuEmsConf200")
                .withRequestBody(matchingJsonPath(String.format("$.[?(@.dpuEndsz=='%s')]", dpu.getEndSz())));
    }

    public MappingBuilder deleteDpuEmsConf201() {
        return delete(urlMatching(DPU_EMS_CONFIGURATION_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(null,
                        201
                ))
                .withName("deleteDpuEmsConf201");
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setOffsetDateTimeFormat(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
