package com.tsystems.tm.acc.ta.data.osr.generators;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.QueryParameter;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.EndSz;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Endpoint;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Ueweg;
import com.tsystems.tm.acc.tests.osr.seal.client.invoker.JSON;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingRequest;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingResponse;
import org.apache.http.HttpStatus;

import java.util.*;

public class RebellUewegGeneratorMapper {

    public StubMapping getData(A4NetworkElement neData) {

        String endSz = neData.getVpsz() + "/" + neData.getFsz();
        endSz = endSz.replace("/", "_");

        Ueweg ueweg = new Ueweg()
                .id(1)
                .lsz("LSZ")
                .lszErg("LBZ " + UUID.randomUUID().toString().substring(0, 4))
                .ordNr("Order Number")
                .pluralId("Plural ID")
                .status("ignored")
                .uewegId(UUID.randomUUID().toString().substring(0, 6) + ", " + UUID.randomUUID().toString().substring(0, 6))
                .validFrom("ignored")
                .validUntil("ignored")
                .version("ignored")
                .versionId("Description NEL")
                .endPointA(new Endpoint()
                        .deviceHostName("ignored")
                        .portName("ignored")
                        .portPosition("ignored")
                        .vendorPortName("physicalLabelA")
                        .endSz("ignored")
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored")))
                .endPointB(new Endpoint()
                        .deviceHostName("ignored")
                        .portName("ignored")
                        .portPosition("ignored")
                        .vendorPortName("physicalLabelB")
                        .endSz("ignored")
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored")));

        StubMappingRequest request = new StubMappingRequest();
        request.setMethod("GET");
//        request.setUrlPattern("/resource-order-resource-inventory/v1/uewege");
//        request.setUrlPattern("/resource-order-resource-inventory/v1/uewege?endsz=" + endSz);
        request.setUrl("/resource-order-resource-inventory/v1/uewege");
//        request.setQueryParameters(new QueryParameter("endsz", Collections.singletonList(endSz)));
//        request.setUrlPath("/resource-order-resource-inventory/v1/uewege");

//        stubFor().
        request.setQueryParameters(new QueryParameter("endsz", Arrays.asList("matches", endSz)));


        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().setPrettyPrinting().serializeNulls().create());

        Map<String, String> respHeaders = new HashMap<>();
        respHeaders.put("Content-Type", "application/json");

        StubMappingResponse response = new StubMappingResponse();
        response.setStatus(HttpStatus.SC_OK);
        response.setHeaders(respHeaders);
//        response.setJsonBody(json.serialize(ueweg));
        response.setBody(json.serialize(ueweg));

        List<HttpHeader> webhookHeaders = new ArrayList<>(2);
        webhookHeaders.add(new HttpHeader("X-Callback-Correlation-Id", "{{request.headers.X-Callback-Correlation-Id}}"));
        webhookHeaders.add(new HttpHeader("Content-Type", "application/json"));

        WebhookDefinitionModel webhook = new WebhookDefinitionModel(RequestMethod.POST,
                "{{request.headers.X-Callback-Url}}",
                webhookHeaders,
                new Body(json.serialize(ueweg)),
                0,
                null);

        StubMapping mapping = new StubMapping();
        mapping.setRequest(request);
        mapping.setPostServeActions(Collections.singletonMap("webhook", webhook));
        mapping.setResponse(response);
        mapping.setPriority(1);

        return mapping;
    }

}
