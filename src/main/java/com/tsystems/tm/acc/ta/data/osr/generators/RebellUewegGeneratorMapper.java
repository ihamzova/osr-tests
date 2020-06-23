package com.tsystems.tm.acc.ta.data.osr.generators;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.EndSz;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Endpoint;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Ueweg;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingRequest;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingResponse;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RebellUewegGeneratorMapper {
    final String REBELL_URL = "/resource-order-resource-inventory/v1/uewege?endsz={endSz}";
//    final String REBELL_URL = "/resource-order-resource-inventory/v1/uewege";

    @Step("Generate REBELL wiremock data")
    public StubMapping getData(UewegData uewegData, A4NetworkElement neA, A4NetworkElement neB) {
        String endSzA = neA.getVpsz() + "/" + neA.getFsz();
        endSzA = endSzA.replace("/", "_");

        String endSzB = neB.getVpsz() + "/" + neB.getFsz();
        endSzB = endSzB.replace("/", "_");

        String endSzQueryParam = endSzA;

        Ueweg ueweg = new Ueweg()
                .id(1)
                .lsz("LSZ")
                .lszErg("LszErg")
                .ordNr("Order Number")
                .pluralId("Plural ID")
                .status("ignored")
                .uewegId(uewegData.getUewegId())
                .validFrom("ignored")
                .validUntil("ignored")
                .version("ignored")
                .versionId("Description NEL")
                .endPointA(new Endpoint()
                        .deviceHostName("ignored")
                        .portName("ignored")
                        .portPosition("ignored")
                        .vendorPortName(uewegData.getVendorPortNameA())
                        .endSz(endSzA)
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored")))
                .endPointB(new Endpoint()
                        .deviceHostName("ignored")
                        .portName("ignored")
                        .portPosition("ignored")
                        .vendorPortName(uewegData.getVendorPortNameB())
                        .endSz(endSzB)
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored")));

        List<Ueweg> uewegList = new ArrayList<>();
        uewegList.add(ueweg);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new StubMapping()
                .priority(1)
                .request(new StubMappingRequest()
                        .method("GET")
                        .url(REBELL_URL.replace("{endSz}", endSzQueryParam)))
                .response(new StubMappingResponse()
                        .status(200)
                        .headers(headers)
                        .jsonBody(uewegList));

//        JSON json = new JSON();
//        json.setGson(json.getGson().newBuilder().setPrettyPrinting().serializeNulls().create());
//
//        return WireMock.get(REBELL_URL)
//                .withQueryParam("endsz", equalTo(endSzQueryParam))
//                .willReturn(
//                        aResponse()
//                                .withStatus(HttpStatus.SC_OK)
//                                .withBody(json.serialize(uewegList))
//                                .withHeader("Content-Type", "application/json")
//                )
////                .withPostServeAction("webhook", webhook()
////                        .withMethod(POST)
////                        .withUrl("{{request.headers.X-Callback-Url}}")
////                        .withHeader("Content-Type", "application/json")
////                        .withBody(json.serialize(ueweg)))
//                .atPriority(1)
//                .build();
    }

}
