package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.ta.data.osr.models.A10nspA4Dto;

public class A10nspA4DtoMapper {

    public A10nspA4Dto getA10nspA4Dto(String carrierBsaReference, String rahmenvertragsnummer) {
        A10nspA4Dto a10nspA4Dto = new A10nspA4Dto();
        a10nspA4Dto.setCarrierBsaReference(carrierBsaReference);
        a10nspA4Dto.setRahmenvertragsnummer(rahmenvertragsnummer);
        return a10nspA4Dto;
    }

}
