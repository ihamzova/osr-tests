package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.models.Credentials;
import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryImportRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResourceInventoryRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.RHSSOAuthListener;
import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

@Epic("OS&R") // Domain name
@Feature("Import Network Element (Group) CSV file into A4 Resource Inventory") // Feature under test
//@TmsLink("DIGIHUB-0") // Jira id of a TestSet (if applicable)
public class A4ResourceInventoryImportTest extends BaseTest {

    private A4ResourceInventoryRobot a4ResourceInventoryRobot = new A4ResourceInventoryRobot();
    private A4ResourceInventoryImportRobot a4ResourceInventoryImportRobot = new A4ResourceInventoryImportRobot();
    private OsrTestContext context = OsrTestContext.get();

    @BeforeMethod
    public void prepareData() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @Test(description = "DIGIHUB-0 Import Network Element (Group) CSV file into A4 Resource Inventory")
    @Owner("bela.kovac@t-systems.com, stefan.masztalerz@aoe.com") // Comma separated owners of this tests. Only required for Domain and higher test levels.
//    @TmsLink("DIGIHUB-12345") // Jira Id for this test in Xray
    @Description("Import Network Element (Group) CSV file into A4 Resource Inventory")
    public void testImportCsvFile() {
        // Given / Arrange
        Random random = new Random();
        String negName = UUID.randomUUID().toString().substring(0, 6);
        String neVpsz1 = String.format("%d/6151/%s", random.ints(1, 50).findFirst().getAsInt(), random.ints(0, 50).findFirst().getAsInt());

        ArrayList<A4ResourceInventoryEntry> list = new ArrayList<A4ResourceInventoryEntry>();
        A4ResourceInventoryEntry entry1 = new A4ResourceInventoryEntry()
                .negCno("operator")
                .negName(negName)
                .negDescription("test csv upload via ui group")
                .neDescription("first NE added via ui")
                .neFsz("A410")
                .neLocAddress("Address")
                .neLocKlsId("123456")
                .neLocRackId("RackId")
                .neLocRackPosition("RackPosition")
                .nePlanningDeviceName("dmst.spine.1")
                .neVpsz(neVpsz1)
                .neVsp("DT");

        list.add(entry1);

        String neVpsz2 = String.format("%d/6151/%s", random.ints(1, 50).findFirst().getAsInt(), random.ints(0, 50).findFirst().getAsInt());

        A4ResourceInventoryEntry entry2 = new A4ResourceInventoryEntry()
                .negCno("operator")
                .negName(negName)
                .negDescription("test csv upload via ui group")
                .neDescription("second NE added via ui")
                .neFsz("A410")
                .neLocAddress("Address")
                .neLocKlsId("123456")
                .neLocRackId("RackId")
                .neLocRackPosition("RackPosition")
                .nePlanningDeviceName("dmst.spine.1")
                .neVpsz(neVpsz2)
                .neVsp("DT");

        list.add(entry2);

        File csvFile = Paths.get( "target/","a4Testcase1.csv").toFile();
        a4ResourceInventoryImportRobot.generateCsv(list, csvFile);

        // When / Action
        a4ResourceInventoryImportRobot.importCsvFileViaUi(csvFile);

        // Then / Assert
        a4ResourceInventoryRobot.checkNetworkElementsViaUi(list);

        // After / Clean-up
        a4ResourceInventoryRobot.deleteNetworkElements(list);
        a4ResourceInventoryRobot.deleteGroupByName(negName);
    }

}
