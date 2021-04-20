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

public class AccessLineInventoryStub extends AbstractStubMapping {
  public static final String SEARCH_ACCESS_LINE_INFO_URL = "/resource-order-resource-inventory/v3/accessLine/accessLineCountTask";
  public static final String PATH_TO_PO_MOCK = "/team/mercury/accessLineCountTask-by-endSz_http200.json";

  public MappingBuilder getAlCountTask200() {
    try {
      return post(urlPathEqualTo(SEARCH_ACCESS_LINE_INFO_URL))
              .withName("getAlCountTask200")
              .willReturn(aDefaultResponseWithBody(prepareBody(), HTTP_CODE_OK_200));
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
