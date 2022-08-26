package com.tsystems.tm.acc.ta.data.osr.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.tsystems.tm.acc.ta.data.osr.mappers.UplinkResourceInventoryMapper;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.client.invoker.JSON;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.RORI_V5_PATH;

public class UplinkResourceInventoryStub extends AbstractStubMapping {

  public static final String UPLINK_RESOURCE_INVENTORY_URL = RORI_V5_PATH + "uplink";

  public MappingBuilder getUplinks(String endSz, String state1, String state2, String state3) {
    return get(urlPathEqualTo(UPLINK_RESOURCE_INVENTORY_URL))
            .withQueryParam("portsEquipmentBusinessRef.endSz", matching(endSz.replace("/", "\\/")))
            .withName("getUplinks")
            .willReturn(aDefaultResponseWithBody(serialize(new UplinkResourceInventoryMapper().getUplinks(endSz, state1, state2, state3)), HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public MappingBuilder findAndImportUplinks(String endSz, String state1, String state2, String state3) {
    return post(urlPathEqualTo(UPLINK_RESOURCE_INVENTORY_URL + "/findAndImport"))
            .withName("findAndImportUplinks")
            .willReturn(aDefaultResponseWithBody(serialize(new UplinkResourceInventoryMapper().getUplinks(endSz, state1, state2, state3)), HTTP_CODE_OK_200))
            .atPriority(0);
  }

  public static String serialize(Object obj) {
    JSON json = new JSON();
    json.setGson(json.getGson().newBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create());
    return json.serialize(obj);
  }
}
