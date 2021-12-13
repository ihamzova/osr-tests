package com.tsystems.tm.acc.ta.data.mercury.wiremock.mappings;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.wiremock.AbstractStubMapping;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_OK_200;

public class RebellUewegeStub  extends AbstractStubMapping {

    public static final String GET_UEWEGE_URL = "/resource-order-resource-inventory/v1/uewege";
    public static final String PATH_TO_MOCK = "/team/mercury/rebell_uewege.json";

    public MappingBuilder getUewege200(OltDevice oltDevice) {
        try {
            return get(urlPathEqualTo(GET_UEWEGE_URL))
                    .withName("getUewege200")
                    .atPriority(4)
                    .willReturn(aDefaultResponseWithBody(prepareBody(oltDevice), HTTP_CODE_OK_200))
                    .withQueryParam("endsz", equalTo(oltDevice.getEndsz().replace("/","_")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String prepareBody(OltDevice oltDevice) throws IOException {

        String[] oltEndSz = oltDevice.getVpsz().split("/");
        String[] bngEndSz = oltDevice.getBngEndsz().split("/");

        String oltPortPostion = String.format("%s.0", oltDevice.getOltSlot()); // e.g. "19.0",
        String oltPortName = String.format("0/19/%s", oltDevice.getOltPort()); // e.g. "0/19/0"
        if(oltDevice.getBezeichnung().equals("SDX 6320-16")) {
            oltPortPostion = "GE1";
            oltPortName = String.format("10ge 0/%s", oltDevice.getOltPort());
        }

        String bngPortPostion = String.format("%s.0.2", oltDevice.getBngDownlinkSlot()); // e.g. "7.0.2"

        return FileUtils.readFileToString(new File(getClass()
                .getResource(PATH_TO_MOCK).getFile()), Charset.defaultCharset())
                .replace("$oltEndSz", oltDevice.getEndsz().replace("/", "_"))
                .replace("$olt_akz", oltEndSz[0])
                .replace("$olt_nkz", oltEndSz[1])
                .replace("$olt_vkz", oltEndSz[2])
                .replace("$olt_fsz", oltDevice.getFsz())
                .replace("$oltPortPostion", oltPortPostion)
                .replace("$oltPortName", oltPortName)
                .replace("$bngEndSz", oltDevice.getBngEndsz().replace("/", "_"))
                .replace("$bng_akz", bngEndSz[0])
                .replace("$bng_nkz", bngEndSz[1])
                .replace("$bng_vkz", bngEndSz[2])
                .replace("$bng_fsz", bngEndSz[3])
                .replace("$lsz", oltDevice.getLsz())
                .replace("$bngPortPostion", bngPortPostion)
                .replace("$bngPortName", oltDevice.getBngDownlinkPort());

    }
}
