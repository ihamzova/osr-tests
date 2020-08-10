package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.AccessLineManagementMapper;
import com.tsystems.tm.acc.ta.data.osr.models.Dpu;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.external.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AccessLineManagementStub extends AbstractStubMapping {
    public static final String ASSIGN_ONU_ID_TASK_URL = "/resource-order-resource-inventory/v1/assignOnuIdTask";

    public MappingBuilder postAssignOnuIdTask200(Dpu dpu) {
        return post(urlEqualTo(ASSIGN_ONU_ID_TASK_URL))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new AccessLineManagementMapper().getOnuIdDto()),
                        200
                ))
                .withName("postAssignOnuIdTask200")
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuEndSz=='%s')]", dpu.getEndSz())));
    }

    public MappingBuilder postAssignOnuIdTask400(Dpu dpu) {
        return post(urlEqualTo(ASSIGN_ONU_ID_TASK_URL))
                .withName("postAssignOnuIdTask400")
                .willReturn(aDefaultResponseWithBody(
                        serialize(new AccessLineManagementMapper().getOnuIdDto()),
                        400
                ))
                .withRequestBody(matchingJsonPath(String.format("$[?(@.dpuEndSz=='%s')]", dpu.getEndSz())));
    }

    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }
}
