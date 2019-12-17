package com.tsystems.tm.acc.ta.domain.commissioning;

import com.tsystems.tm.acc.data.models.credentials.Credentials;
import com.tsystems.tm.acc.data.models.oltdevice.OltDevice;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.osr.models.oltcommissioningresult.OltCommissioningResult;
import com.tsystems.tm.acc.data.osr.models.oltcommissioningresult.OltCommissioningResultCase;
import com.tsystems.tm.acc.data.osr.models.oltdevice.OltDeviceCase;
import com.tsystems.tm.acc.ta.api.OltResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.OltCommissioningRobot;
import com.tsystems.tm.acc.ta.ui.UITest;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.team.upiter.common.CommonTestData.HTTP_CODE_OK_200;

public class OltCommissioning5600 extends UITest {
    private OsrTestContext context = OsrTestContext.get();
    private OltCommissioningRobot oltCommissioningRobot = new OltCommissioningRobot();
    private OltResourceInventoryClient oltResourceInventoryClient = new OltResourceInventoryClient();

    @AfterClass
    public void teardown() {
        clearDataBase();
    }

    @BeforeMethod
    public void prepareData() {
        clearDataBase();
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOOltResourceInventoryUi);
        RHSSOAuthListener.resetLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) automatically case")
    @TmsLink("DIGIHUB-44733")
    @Description("Olt-Commissioning (MA5600T) automatically case")
    public void automaticallyOltCommissioning() {
        OltDevice deviceForAutoCommissioning = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.FSZ_76HA);
        OltCommissioningResult expectedResult = context.getData().getOltCommissioningResultDataProvider().get(OltCommissioningResultCase.autoCommissioningResults);

        oltCommissioningRobot.startAutomaticOltCommissioning(deviceForAutoCommissioning);
        oltCommissioningRobot.checkOltCommissioningResult(expectedResult);
    }

    @Test(description = "Olt-Commissioning (device : MA5600T) manually case")
    @TmsLink("DIGIHUB-37121")
    @Description("Olt-Commissioning (MA5600T) manually case")
    public void manuallyOltCommissioning() {
        OltDevice deviceForManualCommissioning = context.getData().getOltDeviceDataProvider().get(OltDeviceCase.FSZ_76H1);
        OltCommissioningResult expectedResult = context.getData().getOltCommissioningResultDataProvider().get(OltCommissioningResultCase.manualCommissioningResults);

        oltCommissioningRobot.startManualOltCommissioning(deviceForManualCommissioning);
        oltCommissioningRobot.checkOltCommissioningResult(expectedResult);
    }

    private void clearDataBase() {
        oltResourceInventoryClient.getClient().automaticallyFillDatabaseController().deleteDatabase()
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }
}
