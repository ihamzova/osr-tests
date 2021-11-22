package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;

public class A4ResourceInventoryStub extends AbstractStubMapping {

  public static final String A4_NSP_URL_WITH_PARAMETERS = "/resource-order-resource-inventory/v1/a4NetworkServiceProfilesFtthAccess";
  public static final String A4_NSP_URL = "/resource-order-resource-inventory/v1/a4NetworkServiceProfilesFtthAccess/.*";
  public static final String A4_NETWORK_ELEMENT_PORT_URL = "/resource-order-resource-inventory/v1/a4NetworkElementPorts/.*";
  public static final String A4_NETWORK_ELEMENTS_URL = "/resource-order-resource-inventory/v1/a4NetworkElements/*";

  public MappingBuilder getTPWith500() {
    return get(urlPathMatching("/resource-order-resource-inventory/v1/a4TerminationPoints/.*"))
            .withName("getTP500")
            .willReturn(aDefaultResponseWithBody("", HTTP_CODE_INTERNAL_SERVER_ERROR_500))
            .atPriority(1);
  }

  public MappingBuilder putTPWith201() {
    return put(urlPathMatching("/resource-order-resource-inventory/v1/a4TerminationPoints/.*"))
            .withName("putTP201")
            .willReturn(aDefaultResponseWithBody("{{{request.body}}}", HTTP_CODE_CREATED_201))
            .atPriority(1);
  }

  public MappingBuilder getNspBySnWithoutOntLastRegisteredOn() {
    return get(urlPathEqualTo(A4_NSP_URL_WITH_PARAMETERS))
            .withQueryParam("ontSerialNumber", serialNumberPattern)
            .withName("getA4NetworkServiceProfilesFtthAccessBySn")
            .willReturn(aDefaultResponseWithBody(serialize(new A4ResourceInventoryMapper().getListOfNspWithoutOntLastRegisteredOn()), HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public MappingBuilder getNspByUuidWithoutOntLastRegisteredOn() {
    return get(urlMatching(A4_NSP_URL))
            .withName("getA4NetworkServiceProfilesFtthAccessByUuid")
            .willReturn(aDefaultResponseWithBody(serialize(new A4ResourceInventoryMapper().getNspWithoutOntLastRegisteredOn()), HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public MappingBuilder getNspBySnEmpty() {
    return get(urlPathEqualTo(A4_NSP_URL_WITH_PARAMETERS))
            .withQueryParam("ontSerialNumber", serialNumberPattern)
            .withName("getEmptyA4NetworkServiceProfilesFtthAccessBySn")
            .willReturn(aDefaultResponseWithBody("[]", HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public MappingBuilder getNetworkElementPort(String endSz, String port) {
    return get(urlMatching(A4_NETWORK_ELEMENT_PORT_URL))
            .withName("getNetworkElementPortByUuid")
            .willReturn(aDefaultResponseWithBody(serialize(new A4ResourceInventoryMapper().getNetworkElementPortDto(endSz, port)), HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public MappingBuilder getA4NetworkElements() {
    return get(urlMatching(A4_NETWORK_ELEMENTS_URL)).withQueryParam("vpsz", vpsz)
            .withName("getA4NetworkElements")
            .willReturn(aDefaultResponseWithBody(serialize(new A4ResourceInventoryMapper().getNetworkElementDto()), HTTP_CODE_OK_200))
            .atPriority(0);

  }

  public MappingBuilder getA4NoNetworkElements() {
    return get(urlPathMatching(A4_NETWORK_ELEMENTS_URL)).withQueryParam("klsId",matching("123456"))
            .withName("getA4NetworkElements")
            .willReturn(aDefaultResponseWithBody("[]", HTTP_CODE_OK_200))
            .atPriority(0);

  }

  StringValuePattern serialNumberPattern = new RegexPattern("[A-Z0-9]{16}");
  StringValuePattern vpsz = new RegexPattern("[0-9]{1,6}\\\\/[0-9]{1,6}\\\\/[0-9]{1,6}");


  public static String serialize(Object obj) {
    JSON json = new JSON();
    json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
    return json.serialize(obj);
  }
}
