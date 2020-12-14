package com.tsystems.tm.acc.ta.data.osr.mappers;

import com.google.gson.annotations.SerializedName;
import com.tsystems.tm.acc.tests.osr.area.data.management.external.client.model.FibreOnLocationV2DTO;

public class FibreOnLocationIdV2DTO extends FibreOnLocationV2DTO {

    @SerializedName("id2")
    String id;
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
