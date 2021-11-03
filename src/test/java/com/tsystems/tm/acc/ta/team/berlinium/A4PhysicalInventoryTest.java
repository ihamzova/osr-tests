package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.a4connector.A4ConnectorCase;
import com.tsystems.tm.acc.data.osr.models.a4equipment.A4EquipmentCase;
import com.tsystems.tm.acc.data.osr.models.a4holder.A4HolderCase;
import com.tsystems.tm.acc.ta.data.osr.models.A4Equipment;
import com.tsystems.tm.acc.ta.data.osr.models.A4Holder;
import com.tsystems.tm.acc.ta.data.osr.models.A4Connector;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4PhysicalInventoryRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import io.qameta.allure.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;

@ServiceLog({A4_PHYSICAL_INVENTORY_MS})
@Epic("OS&R domain")
@Feature("Save Physical Resources in a4-physical-inventory")
@TmsLink("DIGIHUB-118755, DIGIHUB-118795")
public class A4PhysicalInventoryTest extends GigabitTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final A4PhysicalInventoryRobot a4PhysicalInventory = new A4PhysicalInventoryRobot();

    private A4Equipment defEqData;
    private A4Equipment eqData;
    private A4Holder hoData;
    private A4Connector coData;

    @BeforeClass
    public void init() {
        defEqData = osrTestContext.getData().getA4EquipmentDataProvider()
                .get(A4EquipmentCase.defaultEquipment);
        eqData = osrTestContext.getData().getA4EquipmentDataProvider()
                .get(A4EquipmentCase.equipmentOlt);
        hoData = osrTestContext.getData().getA4HolderDataProvider()
                .get(A4HolderCase.holderSFP);
        coData = osrTestContext.getData().getA4ConnectorDataProvider()
                .get(A4ConnectorCase.defaultSFP);
    }

    @BeforeMethod
    public void setup() {
    }

    @AfterMethod
    public void cleanup() {
    }


    @Test(description = "DIGIHUB-112313, 112315 Create, Delete connector in physical inventory")
    @Owner("heiko.schwanke@telekom.de")
    @TmsLink("DIGIHUB-124002")
    @Description("Create and Delete new connector in physical inventory")
    public void testCreateDeleteConnector()  {
        a4PhysicalInventory.createEquipment(eqData);
        a4PhysicalInventory.createConnector(coData, eqData.getUuid());
        a4PhysicalInventory.deleteConnector(coData);
        a4PhysicalInventory.deleteEquipment(eqData);
    }

    @Test(description = "DIGIHUB-112313 Create connector in physical inventory, Equipment not found")
    @Owner("heiko.schwanke@telekom.de")
    @TmsLink("DIGIHUB-124564")
    @Description("Create new connector in physical inventory, Equipment not found")
    public void testCreateConnectorEquipmentNotFound()  {
        a4PhysicalInventory.createConnectorEquipmentNotFound(coData, eqData.getUuid());
    }

    @Test(description = "DIGIHUB-112313 Create connector in physical inventory, without Equipment")
    @Owner("heiko.schwanke@telekom.de")
    @TmsLink("DIGIHUB-125614")
    @Description("Create connector in physical inventory, without Equipment")
    public void testCreateConnectorWithoutEquipment()  {
        a4PhysicalInventory.createConnectorWithoutEquipment(coData);
    }

    @Test(description = "DIGIHUB-112312 Create holder in physical inventory, without Equipment")
    @Owner("heiko.schwanke@telekom.de")
    @TmsLink("DIGIHUB-125619")
    @Description("Create holder in physical inventory, without Equipment")
    public void testCreateHolderWithoutEquipment()  {
        a4PhysicalInventory.createHolderWithoutEquipment(hoData);
    }

    @Test(description = "DIGIHUB-112315 Delete connector in physical inventory, not found")
    @Owner("heiko.schwanke@telekom.de")
    @TmsLink("DIGIHUB-124565")
    @Description("Delete connector in physical inventory, not found")
    public void testDeleteConnectorNotFound()  {
        a4PhysicalInventory.deleteConnectorNotFound(coData);
    }

    @Test(description = "DIGIHUB-37858 Create equipment in physical inventory ")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-118755")
    @Description("Create new equipment in physical inventory")
    public void testCreateEquipment() {
        a4PhysicalInventory.checkEquipmentCreated(defEqData);
        a4PhysicalInventory.deleteEquipment(defEqData);
    }

    @Test(description = "DIGIHUB-112143 Delete equipment in physical inventory - not found")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-118795")
    @Description("Delete Equipment in physical inventory - not Found")
    public void testDeleteEquipmentNotFound() {
        a4PhysicalInventory.deleteEquipmentNotFound(defEqData);
    }

    @Test(description = "DIGIHUB-112314 Delete holder in physical inventory - not found")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-119602")
    @Description("Delete Holder in physical inventory - not Found")
    public void testDeleteHolderNotFound() {
        a4PhysicalInventory.deleteHolderNotFound(hoData);
    }

    @Test(description = "DIGIHUB-112312 Create holder in physical inventory - Equipment not found")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-119631")
    @Description("Create Holder in physical inventory - Equipment not Found")
    public void testCreateHolderEquipmentNotFound() {
        a4PhysicalInventory.createHolderEquipmentNotFound(hoData, eqData.getUuid());
    }

    @Test(description = "DIGIHUB-112312 Create holder in physical inventory")
    @Owner("Swetlana.Okonetschnikow@telekom.de")
    @TmsLink("DIGIHUB-119615")
    @Description("Create Holder in physical inventory")
    public void testCreateHolder() {
        a4PhysicalInventory.createEquipment(eqData);
        a4PhysicalInventory.createHolder(hoData, eqData.getUuid());
        a4PhysicalInventory.deleteHolder(hoData);
        a4PhysicalInventory.deleteEquipment(eqData);
    }
}
