package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.Characteristic;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.OrderItemActionType;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceOrderStateType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getRandomDigits;

public class A4ResourceOrderDirectFiberMapper {

    public static final String CHAR_VALUETYPE_STRING = "String";
    public static final String RO_STATE = ResourceOrderStateType.INPROGRESS.toString();
    public static final String RO_EXTERNAL_ID = "externalId";
    public static final String ITEM_ID_VALUE = "1";
    public static final String ITEM_ACTION_VALUE = OrderItemActionType.ADD.getValue();
    public static final String ITEM_STATE_VALUE = ResourceOrderStateType.COMPLETED.getValue();
    public static final String NE_PORTADRESSE_GF_VALUE = "ne_portadresse_gf";
    public static final String CRM_AUFTRAGSNUMMER_VALUE = "crm_auftragsnummer";
    public static final String CRM_AUFTRAGSPOSITIONSNUMMER_VALUE = "crm_auftragspositionsnummer";
    public static final String NEG_NAME_VALUE = "neg_name";
    public static final String NOTE_DATE = "1900-12-06T10:25:04.289Z";
    public static final String NOTE_TEXT = "note_text";
    public static final String NOTE_ID = "note_id";
    public static final String RESOURCE_REFORVALUE_ID = "reof_uuid";

    public static final String LINE_ID ="LineId";
    public static final String NE_PORTADRESSE_GF = "NePortadresseGf";
    public static final String CRM_AUFTRAGSNUMMER = "CrmAuftragsnummer";
    public static final String CRM_AUFTRAGSPOSITIONSNUMMER = "CrmAufragspositionsnummer";
    public static final String NEG_UUID = "NegUuid";
    public static final String NEG_NAME = "NegName";


    public ResourceOrder buildResourceOrder() {
        return new ResourceOrder()
                .externalId("RMK_id_" + UUID.randomUUID())
                .description("resource order direct fiber of osr-tests")
                .name("resource order direct fiber name");
    }



    public List<Characteristic> buildResourceCharacteristicList() {
        List<Characteristic> cList = new ArrayList<>();

        addCharacteristic(LINE_ID, getRandomDigits(8), cList);
        addCharacteristic(NEG_UUID, UUID.randomUUID().toString(), cList);
        addCharacteristic(NEG_NAME, NEG_NAME_VALUE, cList);
        addCharacteristic(NE_PORTADRESSE_GF, NE_PORTADRESSE_GF_VALUE, cList);
        addCharacteristic(CRM_AUFTRAGSNUMMER, CRM_AUFTRAGSNUMMER_VALUE, cList);
        addCharacteristic(CRM_AUFTRAGSPOSITIONSNUMMER, CRM_AUFTRAGSPOSITIONSNUMMER_VALUE, cList);
        return cList;
    }


    private void addCharacteristic(String name, Object value, List<Characteristic> cList) {
        cList.add(new Characteristic()
                .name(name)
                .value(value)
                .valueType(A4ResourceOrderDirectFiberMapper.CHAR_VALUETYPE_STRING)
        );
    }


}
