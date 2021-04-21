package com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class AccessLineInventoryStub extends AbstractStubMapping {
  public static final String SEARCH_ACCESS_LINE_INFO_URL = "/resource-order-resource-inventory/v3/accessLine/accessLineCountTask";

  public MappingBuilder getAlCountTask200() {
      return post(urlPathEqualTo(SEARCH_ACCESS_LINE_INFO_URL))
              .withName("getAlCountTask200")
              .willReturn(aDefaultResponseWithBody("[]", HTTP_CODE_OK_200));
  }
}
