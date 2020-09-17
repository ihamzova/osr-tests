package com.tsystems.tm.acc.ta.team.berlinium;

import com.codeborne.selenide.WebDriverRunner;
import com.tsystems.tm.acc.data.osr.models.a4networkelement.A4NetworkElementCase;
import com.tsystems.tm.acc.data.osr.models.a4networkelementgroup.A4NetworkElementGroupCase;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.helpers.log.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.A4MobileUiRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;

import static com.tsystems.tm.acc.ta.data.berlinium.BerliniumConstants.*;

@ServiceLog(A4_RESOURCE_INVENTORY)
@ServiceLog(A4_RESOURCE_INVENTORY_UI)
@ServiceLog(A4_RESOURCE_INVENTORY_BFF_PROXY)
@Slf4j
public class A4MobileMonitoringPageTest extends BaseTest {

    private final A4MobileUiRobot a4MobileUiRobot = new A4MobileUiRobot();
    private final A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private final OsrTestContext osrTestContext = OsrTestContext.get();

    private A4NetworkElementGroup a4NetworkElementGroup;

    private Map<String, A4NetworkElement> a4NetworkElements = new HashMap<>();

    final int WAITING_INTERVAL = 0;

    final String A4_NE_INSTALLING_OLT_01 = "a4NetworkElementInstallingOlt01";
    final String A4_NE_INSTALLING_SPINE_01 = "a4NetworkElementInstallingSpine01";
    final String A4_NE_OPERATING_BOR_01 = "a4NetworkElementOperatingBor01";
    final String A4_NE_PLANNING_LEAFSWITCH_01 = "a4NetworkElementPlanningLeafSwitch01";
    final String A4_NE_RETIRING_PODSERVER_01 = "a4NetworkElementRetiringPodServer01";

    @BeforeMethod()
        public void doLogin() {
            Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
            SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
        }

    @BeforeClass()
    public void init() {

        a4NetworkElementGroup = osrTestContext.getData().getA4NetworkElementGroupDataProvider()
                .get(A4NetworkElementGroupCase.defaultNetworkElementGroup);

        a4NetworkElements.put(A4_NE_INSTALLING_OLT_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingOlt01));

        a4NetworkElements.put(A4_NE_INSTALLING_SPINE_01, osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementInstallingSpine01));

        a4NetworkElements.put(A4_NE_OPERATING_BOR_01,osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementOperatingBor01));

        a4NetworkElements.put(A4_NE_PLANNING_LEAFSWITCH_01,osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementPlanningLeafSwitch01));

        a4NetworkElements.put(A4_NE_RETIRING_PODSERVER_01,osrTestContext.getData().getA4NetworkElementDataProvider()
                .get(A4NetworkElementCase.networkElementRetiringPodServer01));

        cleanUp();
    }

    @BeforeClass
    public void setup() {
        a4ResourceInventoryRobot.createNetworkElementGroup(a4NetworkElementGroup);

        a4NetworkElements.forEach((k, networkElement)->
               a4ResourceInventoryRobot.createNetworkElement(networkElement, a4NetworkElementGroup));
    }

    @AfterClass
    public void cleanUp() {

        a4NetworkElements.forEach((k,v)->
                a4ResourceInventoryRobot.deleteA4NetworkElementsIncludingChildren(v));

        a4ResourceInventoryRobot.deleteNetworkElementGroups(a4NetworkElementGroup);
    }

    @Test
    @Owner("Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Mobile Monitoring page")
    public void testMonitoring() {
        a4MobileUiRobot.openNetworkElementMobileSearchPage();


        Map<String, A4NetworkElement> a4NeFilteredList = new HashMap<>();

        //we assume it's always the same VPSZ so it doesn't matter which element the VPSZ was taken from
        a4MobileUiRobot.enterVpsz(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getVpsz());
        a4MobileUiRobot.enterCategory(a4NetworkElements.get(A4_NE_OPERATING_BOR_01).getCategory());
        a4MobileUiRobot.clickSearchButton();
        a4MobileUiRobot.checkRadioButton("1");
        a4MobileUiRobot.clickInbetriebnahmeButton();
        a4MobileUiRobot.enterZtpIdent("ztp");
        a4MobileUiRobot.clickFinishButton();
        a4NeFilteredList.put(A4_NE_OPERATING_BOR_01, a4NetworkElements.get(A4_NE_OPERATING_BOR_01));


//                .entrySet()
//                .stream()
//                .filter(map -> map.getValue().getCategory()
//                        .equals(a4NetworkElements.get(A4_NE_INSTALLING_OLT_01).getCategory()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        a4MobileUiRobot.clickMonitoringButton();
        a4MobileUiRobot.checkMonitoring(a4NeFilteredList);

        System.out.println("-------------------------------------------------");
        // remove all entries
        try {
            a4NeFilteredList.forEach((k, a4NetworkElement) -> {
                a4MobileUiRobot.clickRemoveButton();
                System.out.println("a4MobileUiRobot.clickRemoveButton");

                try {

                    WebDriver driver  = WebDriverRunner.getWebDriver();// new ChromeDriver(capabilities);
                    WebDriverWait wait = new WebDriverWait(driver, 5000);
                    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                    //window().manage().getCookies();
                    driver.switchTo().alert();
                    alert.accept();
                    System.out.println("ALERT ACCEPTED");
                } catch (NoAlertPresentException e) {
                    System.out.println("JM EXCEPTION " + e.getCause());
                }
                a4NeFilteredList.remove(k);
            });
        } catch (ConcurrentModificationException eee) {
            System.out.println("ConcurrentModificationException is ok");
        };

        System.out.println("-------------------------------------------------");
        a4MobileUiRobot.checkEmptyMonitoring(a4NeFilteredList);
    }



}
