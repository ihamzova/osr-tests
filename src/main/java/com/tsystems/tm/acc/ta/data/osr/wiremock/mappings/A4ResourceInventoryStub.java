package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.RORI_V1_PATH;

public class A4ResourceInventoryStub extends AbstractStubMapping {

  public static final String A4_NSP_URL_WITH_PARAMETERS = RORI_V1_PATH + "a4NetworkServiceProfilesFtthAccess/*";
  public static final String A4_NSP_URL = RORI_V1_PATH + "a4NetworkServiceProfilesFtthAccess/.*";
  public static final String A4_NETWORK_ELEMENT_PORT_URL = RORI_V1_PATH + "a4NetworkElementPorts/.*";
  public static final String A4_NETWORK_ELEMENTS_URL = RORI_V1_PATH + "a4NetworkElements/*";
  public static final String A4_TERMINATION_POINTS_URL = RORI_V1_PATH + "a4TerminationPoints/.*";
  final StringValuePattern vpsz = new RegexPattern("[0-9]{1,6}\\\\/[0-9]{1,6}\\\\/[0-9]{1,6}");

  public MappingBuilder getTPWith500() {
    return get(urlPathMatching(A4_TERMINATION_POINTS_URL))
            .withName("getTP500")
            .willReturn(aDefaultResponseWithBody("", HTTP_CODE_INTERNAL_SERVER_ERROR_500))
            .atPriority(1);
  }

  public MappingBuilder putTPWith201() {
    return put(urlPathMatching(A4_TERMINATION_POINTS_URL))
            .withName("putTP201")
            .willReturn(aDefaultResponseWithBody("{{{request.body}}}", HTTP_CODE_CREATED_201))
            .atPriority(1);
  }

  public MappingBuilder getNspBySnWithoutOntLastRegisteredOn() {
    return get(urlPathMatching(A4_NSP_URL_WITH_PARAMETERS))
            .withName("getA4NetworkServiceProfilesFtthAccessByParameters")
            .willReturn(aDefaultResponseWithBody(serialize(new A4ResourceInventoryMapper().getListOfNspWithoutOntLastRegisteredOn()), HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public MappingBuilder getNspByUuidWithoutOntLastRegisteredOnWorking() {
    return get(urlMatching(A4_NSP_URL))
            .withName("getA4NetworkServiceProfilesFtthAccessByUuid")
            .willReturn(aDefaultResponseWithBody(serialize(new A4ResourceInventoryMapper().getNspWithoutOntLastRegisteredOnWorking()), HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public MappingBuilder getNspBySnEmpty() {
    return get(urlPathMatching(A4_NSP_URL_WITH_PARAMETERS))
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
            .willReturn(aDefaultResponseWithBody(serialize(new A4ResourceInventoryMapper().getDefaultNetworkElementData()), HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public MappingBuilder getA4NoNetworkElements() {
    return get(urlPathMatching(A4_NETWORK_ELEMENTS_URL)).withQueryParam("klsId",matching("123456"))
            .withName("getA4NetworkElements")
            .willReturn(aDefaultResponseWithBody("[]", HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public static String serialize(Object obj) {
    JSON json = new JSON();
    json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
    return json.serialize(obj);
  }

}
