package com.tsystems.tm.acc.ta.data.morpheus.mappers;

import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.external.client.model.BackhaulIdDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.external.client.model.ReferenceDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.external.client.model.ResourceAssociationDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.BackHaulViewDto;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.Page;

public class AccessLineResouceInventoryMapper {
    public Page getBackHaulViewDtoPage() {
        return new Page()
                .addContentItem(new BackHaulViewDto()
                        .backHaulId("#bhid-002")
                        .endSz("{{jsonPath request.body '$.endSz'}}")
                        .portNumber("{{jsonPath request.body '$.portNumber'}}")
                        .slotNumber("{{jsonPath request.body '$.slotNumber'}}")
                        .status(BackHaulViewDto.StatusEnum.CONFIGURED)
                );
    }

    public BackhaulIdDto getBackhaulIdDto() {
        return new BackhaulIdDto()
                .id(1L)
                .backhaulId("#bhid-002")
                .status(BackhaulIdDto.StatusEnum.CONFIGURED)
                .port(new ReferenceDto()
                        .id(2L)
                        .endSz("{{jsonPath request.body '$.endSz'}}")
                        .slotNumber("{{jsonPath request.body '$.slotNumber'}}")
                        .portNumber("{{jsonPath request.body '$.portNumber'}}")
                        .version(1L)
                )
                .version(1L)
                .resourceAssociation(new ResourceAssociationDto()
                        .id(3L)
                        .version(1L)
                        .partyId(4L)
                        .resourceId("1")
                );
    }
}