package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.DeviceResourceInventoryMapper;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.OltResourceInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_6_0.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.olt.resource.inventory.external.v4_6_0.client.model.AncpSessionDto;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class DeviceResourceInventoryStub extends AbstractStubMapping {

    public static final String GET_DPU_ANCP_SESSION_URL = "/resource-order-resource-inventory/v5/ancpSession";
    public static final String GET_ETHERNET_LINK_URL = "/resource-order-resource-inventory/v5/uplink";

    public MappingBuilder getDpuAncpSession200(Dpu dpu){
        return get(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new DeviceResourceInventoryMapper().ancpSessionDPU())),
                        200
                ))
                .withName("getDpuAncpSession200")
                .withQueryParam("accessNodeEquipmentBusinessRef.endSz", equalTo(dpu.getEndSz()));
    }


    public MappingBuilder getOltAncpSession200(OltDevice olt){
        return get(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new DeviceResourceInventoryMapper().ancpSessionOLT())),
                        200
                ))
                .withName("getOltAncpSession200")
                .withQueryParam("accessNodeEquipmentBusinessRef.endSz", equalTo(olt.getEndsz()));
    }

    public MappingBuilder getDpuAncpSession400(Dpu dpu){
        return get(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new DeviceResourceInventoryMapper().ancpSessionDPU())),
                        200
                ))
                .withName("getDpuAncpSession400")
                .withQueryParam("accessNodeEquipmentBusinessRef.endSz", equalTo(dpu.getEndSz()));
    }


    public MappingBuilder getOltAncpSession400(OltDevice olt){
        return get(urlPathEqualTo(GET_DPU_ANCP_SESSION_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new DeviceResourceInventoryMapper().ancpSessionOLT())),
                        200
                ))
                .withName("getOltAncpSession400")
                .withQueryParam("accessNodeEquipmentBusinessRef.endSz", equalTo(olt.getEndsz()));
    }

    public MappingBuilder getEthernetLink200(OltDevice oltDevice) {
        return get(urlPathEqualTo(GET_ETHERNET_LINK_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(Arrays.asList(new DeviceResourceInventoryMapper().uplink(oltDevice))),
                        200
                ))
                .atPriority(0)
                .withName("getEthernetLink200")
                .withQueryParam("portsEquipmentBusinessRef.endSz", equalTo(oltDevice.getVpsz() + "/" + oltDevice.getFsz()));
    }


    public MappingBuilder getEthernetLink400(OltDevice oltDevice) {
        return get(urlPathEqualTo(GET_ETHERNET_LINK_URL))
                .willReturn(aDefaultResponseWithBody(null,
                        400
                ))
                .withName("getEthernetLink400")
                .withQueryParam("portsEquipmentBusinessRef.endSz", equalTo(oltDevice.getVpsz() + "/" + oltDevice.getFsz()));
    }



    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setOffsetDateTimeFormat(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }

}
