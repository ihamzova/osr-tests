package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.AccessLineResouceInventoryMapper;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.external.v4_4_11_0.client.invoker.JSON;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AccessLineResourceInventoryStub extends AbstractStubMapping {
    public static final String BACKHAULID_SEARCH_URL = "/resource-order-resource-inventory/v3/backhaulId/search";

    public MappingBuilder postBackhaulIdSearch200(OltDevice olt, Dpu dpu) {
        return post(urlEqualTo(BACKHAULID_SEARCH_URL))
                .withName("postBackhaulIdSearch200")
                .willReturn(aDefaultResponseWithBody(
                        serialize(Collections.singletonList(new AccessLineResouceInventoryMapper().getBackhaulIdDto())),
                        200
                ))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.endSz=='%s')]", olt.getEndsz())))
                .withRequestBody(matchingJsonPath("$.[?(@.slotNumber)]"))
                .withRequestBody(matchingJsonPath("$.[?(@.portNumber)]"));
    }

    public MappingBuilder postBackhaulIdSearch400(OltDevice olt, Dpu dpu) {
        return post(urlEqualTo(BACKHAULID_SEARCH_URL))
                .withName("postBackhaulIdSearch400")
                .willReturn(aDefaultResponseWithBody(
                        serialize(Collections.singletonList(new AccessLineResouceInventoryMapper().getBackhaulIdDto())),
                        400
                ))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.endSz=='%s')]", olt.getEndsz())))
                .withRequestBody(matchingJsonPath("$.[?(@.slotNumber)]"))
                .withRequestBody(matchingJsonPath("$.[?(@.portNumber)]"));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
