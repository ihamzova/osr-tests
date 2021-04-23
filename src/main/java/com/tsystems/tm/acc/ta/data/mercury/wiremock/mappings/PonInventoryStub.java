package com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.DpuDevice;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class PonInventoryStub extends AbstractStubMapping {

  public static final String SEARCH_LLC_INFO_URL = "/resource-order-resource-inventory/v2/llc";
  public static final String PATH_TO_PO_MOCK = "/team/mercury/pon_inventory_response_endsz_empty_list.json";

  public MappingBuilder getLlcInfo200(OltDevice oltDevice) {
    try {
      return get(urlPathEqualTo(SEARCH_LLC_INFO_URL))
              .withName("getLlcInfo200")
              .willReturn(aDefaultResponseWithBody(prepareBody(), HTTP_CODE_OK_200))
              .withQueryParam("ponPortOltVpsz", equalTo(oltDevice.getVpsz()))
              .withQueryParam("ponPortOltFachSz", equalTo(oltDevice.getFsz()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String prepareBody() throws IOException {
    return FileUtils.readFileToString(new File(getClass()
            .getResource(PATH_TO_PO_MOCK).getFile()), Charset.defaultCharset());


  }
}
