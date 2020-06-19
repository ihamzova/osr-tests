package com.tsystems.tm.acc.ta.data.osr.generators;

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

    @Step("Generate REBELL wiremock data")
    public StubMapping getData(UewegData uewegData) {
        String endSz = uewegData.getEndSz();
        endSz = endSz.replace("/", "_");

        List<Ueweg> uewegList = new ArrayList<>();

        Ueweg ueweg = new Ueweg()
                .id(1)
                .lsz("LSZ")
                .lszErg(uewegData.getLbz())
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
                        .vendorPortName(uewegData.getVendorPortNameB())
                        .endSz("ignored")
                        .endSzParts(new EndSz()
                                .akz("ignored")
                                .fsz("ignored")
                                .nkz("ignored")
                                .vkz("ignored")));
        uewegList.add(ueweg);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new StubMapping()
                .priority(1)
                .request(new StubMappingRequest()
                        .method("GET")
                        .url(REBELL_URL.replace("{endSz}", endSz)))
                .response(new StubMappingResponse()
                        .status(200)
                        .headers(headers)
                        .jsonBody(uewegList));
    }

}
