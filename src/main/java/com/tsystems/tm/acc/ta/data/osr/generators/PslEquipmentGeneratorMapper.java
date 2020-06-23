package com.tsystems.tm.acc.ta.data.osr.generators;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.model.*;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingRequest;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingResponse;
import io.qameta.allure.Step;
import org.apache.http.client.methods.HttpHead;

import java.util.*;


public class PslEquipmentGeneratorMapper {
    final String PSL_URL = "/resource-order-resource-inventory/v1/psl/read-equipment/";

    @Step("Generate PSL wiremock data")
    public StubMapping getData(EquipmentData equipmentData, A4NetworkElement networkElement) {

        networkElement.setPlannedMatNr(equipmentData.getSubmt());

        String endsz = networkElement.getVpsz() + "/" + networkElement.getFsz();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        List<HttpHeader> webhookHeaders = new ArrayList<>();
        webhookHeaders.add(new HttpHeader("X-Callback-Correlation-Id", "{{request.headers.X-Callback-Correlation-Id}}"));
        webhookHeaders.add(new HttpHeader("Content-Type", "application/json"));

        ReadEquipmentResponseHolder entity = new ReadEquipmentResponseHolder();
        entity.setError(null);
        entity.setSuccess(true);
        ReadEquipmentResponseData responseData = new ReadEquipmentResponseData();
        Header respHeader = new Header();
        respHeader.setAnfoKen("15758997480418591403");
        respHeader.setPartner("BS_DIGIOSS");
        responseData.setHeader(respHeader);

        List<Equipment> equipmentList = new ArrayList<>();
        Equipment equipment = new Equipment();
        equipment.setEqunr("498571123");
        equipment.setTplnr("000031-000000-001-004-002-021");
        equipment.setHequi("212879995");
        equipment.setHeqnr("0056");
        equipment.setSubmt(equipmentData.getSubmt());
        equipment.setEqart("G");
        equipment.setEndsz(endsz);
        equipment.setSerge("21023533106TG4900198");
        equipment.setAnzEbenen("1");
        equipment.setAdrId(equipmentData.getKlsId());
        equipment.setAsb("1");
        equipment.setGeba("1");
        equipment.setRaum("2");
        equipment.setReihe("3");
        equipment.setPlatz("4");
        equipmentList.add(equipment);

        responseData.setEquipment(equipmentList);
        ReadEquipmentResponse equipmentResponse = new ReadEquipmentResponse();
        equipmentResponse.setResponseData(responseData);
        entity.setResponse(equipmentResponse);

        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().setPrettyPrinting().serializeNulls().create());

        return new StubMapping()
                .priority(1)
                .request(new StubMappingRequest()
                        .method("POST")
                        .url(PSL_URL)
                        .headers(
                                Collections.singletonMap("X-Callback-Correlation-Id",
                                        Collections.singletonMap("contains", networkElement.getUuid()))))
                .response(new StubMappingResponse()
                        .status(202)
                        .headers(headers))
                .postServeActions(Collections.singletonMap("webhook", new WebhookDefinitionModel(
                        RequestMethod.POST,
                        "{{request.headers.X-Callback-Url}}",
                        webhookHeaders,
                        new Body(json.serialize(entity)),
                        5000,
                        null))
                );
    }

}
