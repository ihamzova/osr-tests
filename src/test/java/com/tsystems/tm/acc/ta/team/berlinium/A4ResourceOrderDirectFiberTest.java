package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.ta.robot.A4ResourceOrderDirectFiberRobot;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.ResourceOrder;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.direct.fiber.client.model.OrderItemActionType;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_BAD_REQUEST_400;
import static com.tsystems.tm.acc.ta.data.HttpConstants.HTTP_CODE_CREATED_201;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.getPrefixWithRandom;
import static org.testng.Assert.*;

public class A4ResourceOrderDirectFiberTest {

    private final A4ResourceOrderDirectFiberRobot orderDirectFiberRobot = new A4ResourceOrderDirectFiberRobot();

    private ResourceOrder ro;
    private String orderItemId;


    @BeforeMethod
    public void setup() {
        orderItemId = getPrefixWithRandom("orderItemId-", 6);

        ro = orderDirectFiberRobot.buildResourceOrder();

    }

    @AfterMethod
    public void cleanup() {
        ro = null;
    }

    @Test
    @Owner("DL_Berlinium@telekom.de")
    @TmsLink("DIGIHUB-153040")
    @Description("Szenario 1: send RO with -add- and empty ressource order ID")
    public void test_post_ro() {
        // GIVEN
        ro.addOrderItemItem(orderDirectFiberRobot.createOrderItem(orderItemId, OrderItemActionType.ADD,null));

        // THEN
        String uuid = orderDirectFiberRobot.sendPostResourceOrderDirectFiber(ro, HTTP_CODE_CREATED_201) ;
        assertNotNull(uuid);
    }

    @Test
    @Owner("DL_Berlinium@telekom.de")
    @TmsLink("DIGIHUB-153040")
    @Description("Szenario 3: send RO with -add- and request has filled ressource order ID")
    public void test_post_ro_with_roId() {
        // GIVEN
        ro.addOrderItemItem(orderDirectFiberRobot.createOrderItem(orderItemId, OrderItemActionType.ADD,null));
        ro.setId(UUID.randomUUID().toString());

        // THEN
        orderDirectFiberRobot.sendPostResourceOrderDirectFiber(ro, HTTP_CODE_BAD_REQUEST_400) ;
    }

    @Test
    @Owner("DL_Berlinium@telekom.de")
    @TmsLink("DIGIHUB-153040")
    @Description("Szenario 2: send invalid RO without ressource order item")
    public void test_post_ro_without_orderitem() {
        // GIVEN

        // THEN
        orderDirectFiberRobot.sendPostResourceOrderDirectFiber(ro, HTTP_CODE_BAD_REQUEST_400) ;
    }

}
