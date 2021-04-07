package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.EquipmentData;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.tests.osr.psl.adapter.client.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PslMapper {

    //HUAWEI MA5600
    private static final int START_PON_SLOT = 1;
    private static final int START_ETHERNET_SLOT = 19;
    private static final AtomicInteger equipmentCount = new AtomicInteger(0);

    public ReadEquipmentResponseHolder getReadEquipmentResponseHolder(OltDevice oltDevice) {
        if(oltDevice.getBezeichnung().equals("SDX 6320-16")) {
            return  getReadEquipmentResponseHolderAdtran(oltDevice);
        }
        return getReadEquipmentResponseHolderMA5600(oltDevice);
    }

    public ReadEquipmentResponseHolder getReadEquipmentResponseHolderMA5600(OltDevice oltDevice) {
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
                                        .equipment(Stream.of(
                                                Collections.singletonList(new Equipment()
                                                        .equnr("123456700")
                                                        .tplnr(oltDevice.getTplnr())
                                                        .hequi("123456000")
                                                        .heqnr("0057")
                                                        .submt(String.valueOf(oltDevice.getMatNumber()))
                                                        .eqart("G")
                                                        .endsz(oltDevice.getEndsz())
                                                        .serge("21023533106TG410000" + String.format("%03d", equipmentCount.getAndIncrement()))
                                                        .anzEbenen("1")
                                                        .adrId(oltDevice.getVst().getAddress().getKlsId())
                                                        .asb("1")),
                                                Stream.concat(
                                                        IntStream.range(START_PON_SLOT, START_PON_SLOT + oltDevice.getNumberOfPonSlots())
                                                                .mapToObj(slot -> new Equipment()
                                                                        .equnr("123456700" + String.format("%03d", equipmentCount.getAndIncrement()))
                                                                        .hequi("123456700")
                                                                        .heqnr(StringUtils.leftPad(String.valueOf(slot), 4, '0'))
                                                                        .submt("40261742")
                                                                        .eqart("P")
                                                                        .endsz(oltDevice.getEndsz())
                                                                        .serge("21023533106TG410000" + equipmentCount)
                                                                        .anzEbenen("2")),
                                                        IntStream.range(START_ETHERNET_SLOT, START_ETHERNET_SLOT + oltDevice.getNumberOfEthernetSlots())
                                                                .mapToObj(slot -> new Equipment()
                                                                        .equnr("123456700" + String.format("%03d", equipmentCount.getAndIncrement()))
                                                                        .hequi("123456700")
                                                                        .heqnr(StringUtils.leftPad(String.valueOf(slot), 4, '0'))
                                                                        .submt("40247074")
                                                                        .eqart("P")
                                                                        .endsz(oltDevice.getEndsz())
                                                                        .serge("21023533106TG410000" + equipmentCount)
                                                                        .anzEbenen("2")))
                                                        .collect(Collectors.toList()))
                                                .flatMap(List::stream)
                                                .collect(Collectors.toList())
                                        )
                                )
                );
    }

    public ReadEquipmentResponseHolder getReadEquipmentResponseHolderAdtran(OltDevice oltDevice) {
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
                                                .equnr("212880011")
                                                .tplnr(oltDevice.getTplnr())
                                                .hequi("212879995")
                                                .heqnr("0056")
                                                .submt(String.valueOf(oltDevice.getMatNumber()))
                                                .eqart("G")
                                                .endsz(oltDevice.getEndsz())
                                                .serge("21023533106TG4900198")
                                                .anzEbenen("1")
                                                .adrId(oltDevice.getVst().getAddress().getKlsId())
                                                .asb("1"))
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
