package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class PslMapper {
    private static final String DEFAULT_SHELF = "0";
    private static final String DEFAULT_PON_SLOT = "1";
    private static final String DEFAULT_ETHERNET_SLOT = "19";

    public ReadEquipmentResponseHolder getReadEquipmentResponseHolder(OltDevice oltDevice) {
        int equipmentCount = 0;
        return new ReadEquipmentResponseHolder()
                .success(true)
                .response(
                        new ReadEquipmentResponse()
                                .messageContext(new MessageContext()
                                        .correlationId("{{jsonPath request.body '$.messageContext.correlationId'}}")
                                        .sender("{{jsonPath request.body '$.messageContext.sender'}}")
                                        .target("{{jsonPath request.body '$.messageContext.target'}}")
                                )
                                .responseData(new ReadEquipmentResponseData()
                                        .header(new Header()
                                                .anfoKen("{{jsonPath request.body '$.requestData.header.anfoKen'}}")
                                                .partner("{{jsonPath request.body '$.requestData.header.partner'}}")
                                        )
                                        .status(new Status()
                                                .id("ZDIB")
                                                .logMsgNo("string")
                                                .logNo("0000000000")
                                                .message("Die Anforderung wurde ausgeführt.")
                                                .messageV1("")
                                                .number("000")
                                                .system("Linux")
                                                .type("S")
                                        )
                                        .addEquipmentItem(new Equipment()
                                                .equnr("123456700")
                                                .tplnr(oltDevice.getTplnr())
                                                .hequi("123456000")
                                                .heqnr("0057")
                                                .submt("40247069")
                                                .eqart("G")
                                                .endsz(oltDevice.getEndsz())
                                                .serge("21023533106TG410000" + ++equipmentCount)
                                                .anzEbenen("1")
                                                .adrId(oltDevice.getVst().getAddress().getKlsId())
                                                .asb("1")
                                        )
                                        .addEquipmentItem(new Equipment()
                                                .equnr("123456700" + ++equipmentCount)
                                                .hequi("123456700")
                                                .heqnr(StringUtils.leftPad(DEFAULT_PON_SLOT, 4, '0'))
                                                .submt("40261742")
                                                .eqart("P")
                                                .endsz(oltDevice.getEndsz())
                                                .serge("21023533106TG410000" + equipmentCount)
                                                .anzEbenen("2")
                                        )
                                        .addEquipmentItem(new Equipment()
                                                .equnr("123456700" + ++equipmentCount)
                                                .hequi("123456700")
                                                .heqnr(StringUtils.leftPad(DEFAULT_ETHERNET_SLOT, 4, '0'))
                                                .submt("40261742")
                                                .eqart("P")
                                                .endsz(oltDevice.getEndsz())
                                                .serge("21023533106TG410000" + equipmentCount)
                                                .anzEbenen("2")
                                        )
                                )
                );
    }

    public ReadEquipmentResponseHolder getReadEquipmentResponseHolder(EquipmentData equipmentData, A4NetworkElement networkElement) {
        return new ReadEquipmentResponseHolder()
                .success(true)
                .response(
                        new ReadEquipmentResponse()
                                .messageContext(new MessageContext()
                                        .correlationId("{{jsonPath request.body '$.messageContext.correlationId'}}")
                                        .sender("{{jsonPath request.body '$.messageContext.sender'}}")
                                        .target("{{jsonPath request.body '$.messageContext.target'}}")
                                )
                                .responseData(new ReadEquipmentResponseData()
                                        .header(new Header()
                                                .anfoKen("{{jsonPath request.body '$.requestData.header.anfoKen'}}")
                                                .partner("{{jsonPath request.body '$.requestData.header.partner'}}")
                                        )
                                        .status(new Status()
                                                .id("ZDIB")
                                                .logMsgNo("string")
                                                .logNo("0000000000")
                                                .message("Die Anforderung wurde ausgeführt.")
                                                .messageV1("")
                                                .number("000")
                                                .system("Linux")
                                                .type("S")
                                        )
                                        .addEquipmentItem(new Equipment()
                                                .equnr("498571123")
                                                .tplnr("000031-000000-001-004-002-021")
                                                .hequi("212879995")
                                                .heqnr("0056")
                                                .submt(equipmentData.getSubmt())
                                                .eqart("G")
                                                .endsz(networkElement.getVpsz() + "/" + networkElement.getFsz())
                                                .serge("21023533106TG4900198")
                                                .anzEbenen("1")
                                                .adrId(equipmentData.getKlsId())
                                                .asb("1")
                                                .geba("1")
                                                .raum("2")
                                                .reihe("3")
                                                .platz("4")
                                        )
                                )
                );
    }
}
