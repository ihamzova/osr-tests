package com.tsystems.tm.acc.ta.data.morpheus.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.morpheus.mappers.WorkorderMapper;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.external.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WorkorderStub extends AbstractStubMapping {

    public static final String GET_WORKORDER_URL = "/giga-project-dioss/v5/workorders";
    public static final String PATCH_WORKORDER_URL = "/giga-project-dioss/v5/workorders";

    public MappingBuilder getWorkorder200() {
        return get(urlMatching(GET_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new WorkorderMapper().getWorkorder()),
                        200
                ))
                .withName("getWorkorder200");
    }

    public MappingBuilder patchWorkorderInProgress200() {
        return patch(urlMatching(PATCH_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new WorkorderMapper().patchWorkorderInProgress()),
                        200
                ))
                .withName("patchWorkorderInProgress200")
                .withRequestBody(matchingJsonPath(String.format("$[?(@.status=='IN_PROGRESS')]")));
    }

    public MappingBuilder patchWorkorderCreated200() {
        return patch(urlMatching(PATCH_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new WorkorderMapper().patchWorkorderCreated()),
                        200
                ))
                .withName("patchWorkorderCreated200")
                .withRequestBody(matchingJsonPath(String.format("$[?(@.status=='CREATED')]")));
    }



    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }

}
