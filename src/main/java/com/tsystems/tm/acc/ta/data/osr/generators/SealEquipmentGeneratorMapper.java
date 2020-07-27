package com.tsystems.tm.acc.ta.data.osr.generators;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.seal.client.invoker.JSON;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingRequest;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class SealEquipmentGeneratorMapper {

    /**
     * Mapper for OLT Discovery wiremock stub
     * @param oltDevice
     * @return
     */
    public StubMapping getDataFromFile(OltDevice oltDevice) {

        String endsz = oltDevice.getVpsz() + "/" + oltDevice.getFsz();
        endsz = endsz.replaceAll("/", "_");
        String sealUrl = "/resource-order-resource-inventory/v1/inventory-retrieval/";
        sealUrl = sealUrl + endsz + "/";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        List<HttpHeader> webhookHeaders = new ArrayList<>();
        webhookHeaders.add(new HttpHeader("X-Callback-Correlation-Id", "{{request.headers.X-Callback-Correlation-Id}}"));
        webhookHeaders.add(new HttpHeader("Content-Type", "application/json"));

        String stubTemplateFolder = System.getProperty("user.dir") + "/src/test/resources/domain/osr/wiremock/seal/";
        File jsonBody = new File(stubTemplateFolder + "Inventory_MA5600.json");
        String content = getTemplateContent(jsonBody);
        content = content.replace("###ENDSZ###",endsz);

        return new StubMapping()
                .priority(1)
                .request(new StubMappingRequest()
                        .method("GET")
                        .url(sealUrl))
                .response(new StubMappingResponse()
                        .status(202)
                        .headers(headers))
                .postServeActions(Collections.singletonMap("webhook", new WebhookDefinitionModel(
                        RequestMethod.POST,
                        "{{request.headers.X-Callback-Url}}",
                        webhookHeaders,
                        new Body( content),
                        5000,
                        null))
                );
    }

    private String getTemplateContent(File jsonTemplate) {
        String content;
        try {
            content = FileUtils.readFileToString(jsonTemplate, "UTF-8");
        } catch (IOException e) {
            log.error("could not parse json template");
            throw new RuntimeException();
        }
        return content;
    }
}
