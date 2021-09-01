package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4equipment.A4EquipmentCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4Equipment;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4PhysicalInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("OS&R domain")
@Feature("Save Physical Resources in a4-physical-inventory")
@TmsLink("DIGIHUB-118755")
public class A4PhysicalInventoryTest extends GigabitTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4PhysicalInventoryRobot a4PhysicalInventory = new A4PhysicalInventoryRobot();

    private A4Equipment eqData;

    @BeforeClass
    public void init() {
        eqData = osrTestContext.getData().getA4EquipmentDataProvider()
                .get(A4EquipmentCase.defaultEquipment);

    }

    @BeforeMethod
    public void setup() {
    }

    @AfterMethod
    public void cleanup() {
        a4PhysicalInventory.deleteEquipment(eqData);
    }

    @Test(description = "DIGIHUB-37858 Create equipment in physical inventory ")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-118755")
    @Description("Create new equipment in physical inventory")
    public void testCreateEquipment() {
        a4PhysicalInventory.createEquipment(eqData);
    }

}
