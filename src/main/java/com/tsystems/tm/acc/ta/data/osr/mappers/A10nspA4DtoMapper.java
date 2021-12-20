package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A10nspA4Dto;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.UewegData;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.EndSz;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Endpoint;
import com.tsystems.tm.acc.tests.osr.rebell.client.model.Ueweg;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper.nelLsz;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceInventoryMapper.nelOrderNumber;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getEndsz;

public class A10nspA4DtoMapper {

    public A10nspA4Dto getA10nspA4Dto(String carrierBsaReference, String rahmenvertragsnummer) {

        A10nspA4Dto a10nspA4Dto = new A10nspA4Dto();
        a10nspA4Dto.setCarrierBsaReference(carrierBsaReference);
        a10nspA4Dto.setRahmenvertragsnummer(rahmenvertragsnummer);
        return a10nspA4Dto;
    }

}
