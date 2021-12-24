package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class OltRiStub extends AbstractStubMapping {
  public static final String OLT_DEVICE_FROM_OLT_RI = "/api/oltResourceInventory/v1/device/*";
  public static final String OLT_ABSTRACT_DEVICE_FROM_OLT_RI = "/resource-order-resource-inventory/v5/device";




  public MappingBuilder getNoDevicefromOltRi() {
    return get(urlPathMatching(OLT_ABSTRACT_DEVICE_FROM_OLT_RI))
            .withName("getAbsctractDeviceFromOltRi")
            .willReturn(aDefaultResponseWithBody("[]", HTTP_CODE_OK_200))
            .atPriority(0);
  }
  public static String serialize(Object obj) {
    JSON json = new JSON();
    json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
    return json.serialize(obj);
  }
}
