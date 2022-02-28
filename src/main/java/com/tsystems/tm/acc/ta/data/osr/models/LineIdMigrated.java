package com.tsystems.tm.acc.ta.data.osr.models;

import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.LineIdDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

public class LineIdMigrated {
    private List<LineIdDto> lineIdDtoList;
}