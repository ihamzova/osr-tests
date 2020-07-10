package com.tsystems.tm.acc.ta.data.osr.generators;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.WebhookDefinitionModel;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.invoker.JSON;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.model.*;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMapping;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingRequest;
import com.tsystems.tm.acc.tests.wiremock.client.model.StubMappingResponse;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PslGetEquipmentStubGeneratorMapper {
    private static volatile AtomicInteger equipmentCount = new AtomicInteger(0);

    public StubMapping getData(OltDevice olt) {
        ReadEquipmentResponseHolder entity = new ReadEquipmentResponseHolder();
        entity.setError(null);
        entity.setSuccess(true);
        MessageContext messageContext = new MessageContext();
        messageContext.setCorrelationId("string");
        messageContext.setPriority(0);
        messageContext.setRequestId("string");
        messageContext.setSender("string");
        messageContext.setTarget("string");
        messageContext.setTimeLeft("string");
        ReadEquipmentResponse equipmentResponse = new ReadEquipmentResponse();
        equipmentResponse.setMessageContext(messageContext);
        ReadEquipmentResponseData responseData = new ReadEquipmentResponseData();
        Header respHeader = new Header();
        respHeader.setAnfoKen("15758997480418591403");
        respHeader.setPartner("BS_DIGIOSS");
        responseData.setHeader(respHeader);
        Status respStatus = new Status();
        respStatus.setId("ZDIB");
        respStatus.setLogMsgNo("string");
        respStatus.setLogNo("0000000000");
        respStatus.setMessage("Die Anforderung wurde ausgeführt.");
        respStatus.setMessageV1("");
        respStatus.setNumber("000");
        respStatus.setSystem("Linux");
        respStatus.setType("S");
        responseData.setStatus(respStatus);

        String name = olt.getVpsz() + '/' + olt.getFsz();
        List<Equipment> equipmentList = new ArrayList<>();
        Equipment equipment = new Equipment();
        equipment.setEqunr("498571123");
        equipment.setTplnr(olt.getTplnr());
        equipment.setHequi("4930179");
        equipment.setHeqnr("0057");
        equipment.setSubmt("40247069");
        equipment.setEqart("G");
        equipment.setEndsz(olt.getVpsz() + '/' + olt.getFsz());
        equipment.setSerge("21023533106TG4900198" + equipmentCount.getAndIncrement());
        equipment.setAnzEbenen("1");
        equipment.setAdrId(olt.getVst().getAddress().getKlsId());
        equipment.setAsb("1");
        equipmentList.add(equipment);

        Equipment equipmentCard = new Equipment();
        equipmentCard.setEqunr("16123000");
        equipmentCard.setHequi("4985711232");
        equipmentCard.setHeqnr("0001");
        equipmentCard.setSubmt("40261742");
        equipmentCard.setEqart("P");
        equipmentCard.setEndsz(olt.getVpsz() + '/' + olt.getFsz());
        equipmentCard.setSerge("121BQW10B6123000" + equipmentCount.getAndIncrement());
        equipmentCard.setAnzEbenen("2");
        equipmentList.add(equipmentCard);

        responseData.setEquipment(equipmentList);
        equipmentResponse.setResponseData(responseData);
        entity.setResponse(equipmentResponse);

        StubMapping mapping = new StubMapping();
        StubMappingRequest request = new StubMappingRequest();
        request.setMethod("POST");
        request.setUrlPattern("/v1/psl/read-equipment/");

        Map<String, String> bodyPattern = new HashMap<>();
        bodyPattern.put("expression", "$.requestData.requestEquipment[0].endsz");
        bodyPattern.put("equalTo", name);
        request.addBodyPatternsItem(Collections.singletonMap("matchesJsonPath", bodyPattern));

        mapping.setRequest(request);
        StubMappingResponse response = new StubMappingResponse();
        response.setStatus(202);
        Map<String, String> respHeaders = new HashMap<>();
        respHeaders.put("Content-Type", "application/json");
        response.setHeaders(respHeaders);
        List<HttpHeader> webhookHeaders = new ArrayList<>(2);
        webhookHeaders.add(new HttpHeader("X-Callback-Correlation-Id", "{{request.headers.X-Callback-Correlation-Id}}"));
        webhookHeaders.add(new HttpHeader("Content-Type", "application/json"));

        JSON json = new JSON();
        json.setGson(json.getGson().newBuilder().setPrettyPrinting().serializeNulls().create());

        WebhookDefinitionModel webhook = new WebhookDefinitionModel(RequestMethod.POST,
                "{{request.headers.X-Callback-Url}}",
                webhookHeaders,
                new Body(json.serialize(entity)),
                5000,
                null);
        mapping.setPostServeActions(Collections.singletonMap("webhook", webhook));
        mapping.setResponse(response);
        mapping.setPriority(2);
        return mapping;
    }
}
