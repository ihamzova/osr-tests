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

    public MappingBuilder getWorkorderDomain() {
        return get(urlMatching(GET_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new WorkorderMapper().getWorkorderDomain()),
                        200
                ))
                .withName("getWorkorderDomain");
    }

    public MappingBuilder getWorkorderGF_AP_INSTALLATION() {
        return get(urlMatching(GET_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new WorkorderMapper().getWorkorderGF_AP_INSTALLATION()),
                        200
                ))
                .withName("getWorkorderGF_AP_INSTALLATION");
    }

    public MappingBuilder getWorkorder404() {
        return get(urlMatching(GET_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(null,
                        404
                ))
                .withName("getWorkorder404");
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

    public MappingBuilder patchWorkorderInProgress404() {
        return patch(urlMatching(PATCH_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(null,
                        404
                ))
                .withName("patchWorkorderInProgress404")
                .withRequestBody(matchingJsonPath(String.format("$[?(@.status=='IN_PROGRESS')]")));
    }

    public MappingBuilder patchWorkorderCompleted200() {
        return patch(urlMatching(PATCH_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(
                        serialize(new WorkorderMapper().patchWorkorderCompleted()),
                        200
                ))
                .withName("patchWorkorderCompleted200")
                .withRequestBody(matchingJsonPath(String.format("$[?(@.status=='COMPLETED')]")));
    }

    public MappingBuilder patchWorkorderCompleted404() {
        return patch(urlMatching(PATCH_WORKORDER_URL + "/.*"))
                .willReturn(aDefaultResponseWithBody(null,
                        404
                ))
                .withName("patchWorkorderCompleted404")
                .withRequestBody(matchingJsonPath(String.format("$[?(@.status=='COMPLETED')]")));
    }



    private String serialize(Object obj) {
        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
        return json.serialize(obj);
    }

}
