package com.tsystems.tm.acc.ta.team.mercury.migration;

import com.tsystems.tm.acc.ta.api.osr.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServiceLog("olt-resource-inventory")
@ServiceLog("olt-discovery")
@ServiceLog("ancp-configuration")
public class FTTHMigrationTest extends BaseTest {

    private static final Integer HTTP_CODE_OK_200 = 200;
    private OltResourceInventoryClient oltResourceInventoryClient;
    private OltDevice oltDevice;

    private WireMockMappingsContext mappingsContext;


}
