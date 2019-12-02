package com.tsystems.tm.acc.ta.team.upiter.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioning;
import com.tsystems.tm.acc.data.osr.models.oltcommissioning.OltCommissioningCase;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioning;
import com.tsystems.tm.acc.data.osr.models.portprovisioning.PortProvisioningCase;
import com.tsystems.tm.acc.olt.resource.inventory.internal.client.model.*;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.ui.UITest;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltCommissioningPage;
import com.tsystems.tm.acc.ta.ui.pages.oltcommissioning.OltSearchPage;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.*;

public class OltCommissioning5600 extends UITest {

    private static final Integer LATENCY_FOR_DEVICE_COMMISSIONING = 15 * 60_000;

    private OltResourceInventoryClient oltResourceInventoryClient;

    private OltCommissioning oltCommissioning5600;


    @BeforeClass
    public void init() {
        oltResourceInventoryClient = new OltResourceInventoryClient();
        OsrTestContext context = OsrTestContext.get();
        oltCommissioning5600 = context.getData().getOltCommissioningDataProvider().get(OltCommissioningCase.MA5600);
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @BeforeMethod
    public void prepareData() {
        clearDataBase();
    }

    @AfterMethod
    public void clearData() {
        clearDataBase();
    }

    @Test
    public void automaticallyOltCommissioning() throws InterruptedException{

        OltSearchPage oltSearchPage = OltSearchPage.openSearchPage();
        oltSearchPage.validateUrl();
        oltSearchPage.searchOltByParameters(oltCommissioning5600);

        OltCommissioningPage oltCommissioningPage = oltSearchPage.openOltCommissioningAutomaticallyPage();
        oltCommissioningPage.validateUrl();
        oltCommissioningPage.startOltCommissioning(oltCommissioning5600);

        Thread.sleep(LATENCY_FOR_DEVICE_COMMISSIONING);

        checkingTestResult();
    }

    private void checkingTestResult(){

        Device deviceAfterCommissioning = getDevice();

        List<Integer> countResults = new ArrayList<>();

        deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard).map(Card::getPorts)
                .forEach(ports -> ports.forEach(port -> countResults.add(port.getAccessLines().size())));
        Assert.assertEquals(countResults.stream().mapToInt(Integer::intValue).sum(),256);
        countResults.clear();

        deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard).map(Card::getPorts)
                .forEach(ports -> ports.forEach(port -> countResults.add(port.getLineIdPools().size())));
        Assert.assertEquals(countResults.stream().mapToInt(Integer::intValue).sum(),512);
        countResults.clear();

        deviceAfterCommissioning.getEquipmentHolders().stream().map(EquipmentHolder::getCard).map(Card::getPorts)
                .forEach(ports -> ports.forEach(port -> countResults.add(port.getHomeIdPools().size())));
        Assert.assertEquals(countResults.stream().mapToInt(Integer::intValue).sum(),512);
    }

    private Device getDevice() {
        return oltResourceInventoryClient.getClient().deviceInternalController()
                .getOltByEndSZ().endSZQuery("49/8571/0/76HA").executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    private void clearDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
