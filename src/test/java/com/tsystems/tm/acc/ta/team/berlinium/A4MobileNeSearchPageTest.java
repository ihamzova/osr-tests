package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class A4MobileNeSearchPageTest extends BaseTest {

    private final A4MobileUiRobot a4MobileUiRobot = new A4MobileUiRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private A4NetworkElementGroup a4NetworkElementGroup;
    private A4NetworkElement a4NetworkElement;
    private A4NetworkElement a4NetworkElementInstalling;

    @BeforeClass()
    public void init() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);
        a4NetworkElement = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.defaultNetworkElement);
        a4NetworkElementInstalling = osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingOlt01);

        a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(a4NetworkElement);
        a4ResourceInventoryRobot.deleteNetworkElementGroups(a4NetworkElementGroup);
    }

    @BeforeMethod
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);
        a4ResourceInventoryRobot.createNetworkElement(a4NetworkElement, a4NetworkElementGroup);
        a4ResourceInventoryRobot.createNetworkElement(a4NetworkElementInstalling, a4NetworkElementGroup);
    }

    @AfterMethod
    public void cleanUp() {
        a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(a4NetworkElement);
        a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(a4NetworkElementInstalling);
        a4ResourceInventoryRobot.deleteNetworkElementGroups(a4NetworkElementGroup);
    }

    @Test
    @Owner("Phillip.Moeller@t-systems.com, Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile NE-search-page of installation process")
    public void testNeSearchByVpsz() throws InterruptedException {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();
        a4MobileUiRobot.enterVpsz(a4NetworkElement.getVpsz());
        a4MobileUiRobot.clickSearchButton();
        a4MobileUiRobot.enterFsz(a4NetworkElement.getFsz());
        a4MobileUiRobot.clickSearchButton();
        Thread.sleep(5000);
    }
}
