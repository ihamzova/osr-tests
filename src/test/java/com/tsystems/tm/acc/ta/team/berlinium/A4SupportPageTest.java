package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResilienceRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4SupportPageRobot;
import com.tsystems.tm.acc.ta.ui.BaseTest;
import com.tsystems.tm.acc.ta.util.driver.SelenideConfigurationManager;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContext;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.*;
import static com.tsystems.tm.acc.ta.wiremock.WireMockMappingsContextHooks.attachEventsToAllureReport;

@Slf4j
public class A4SupportPageTest extends BaseTest {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private WireMockMappingsContext wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "")).build();
    private final A4NemoUpdaterRobot a4NemoUpdater = new A4NemoUpdaterRobot();
    private final A4ResilienceRobot a4ResilienceRobot = new A4ResilienceRobot();
    private final A4SupportPageRobot a4SupportPageRobot = new A4SupportPageRobot();
    private final A4ResilienceRobot a4Resilience = new A4ResilienceRobot();
    List<String> uuids = new ArrayList();

    @BeforeMethod()
    public void doLogin() {
        Credentials loginData = osrTestContext.getData().getCredentialsDataProvider().get(CredentialsCase.RHSSOA4InventoryUi);
        SelenideConfigurationManager.get().setLoginData(loginData.getLogin(), loginData.getPassword());
    }

    @BeforeClass()
    public void init() {

        cleanUp();
    }

    @BeforeMethod
    public void setup() {
        uuids.add(UUID.randomUUID().toString());
    }

    @AfterMethod
    public void cleanUp() {
        uuids = new ArrayList();
        wiremock.close();
        wiremock
                .eventsHook(saveEventsToDefaultDir())
                .eventsHook(attachEventsToAllureReport());
    }

    @Test
    @Owner("Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Support Page - Unblock Queue")
    public void testUnblockQueue() throws IOException, InterruptedException {
        final long REDELIVERY_DELAY = a4Resilience.getRedeliveryDelayNemoUpdater();

        // wiremock with 500 error
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock500()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        // write things in queue
        a4NemoUpdater.triggerAsyncNemoUpdate(uuids);

        // check DLQ
        String count = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");

        // click unblock
        a4SupportPageRobot.openSupportPage();
        a4SupportPageRobot.clickCleanNemoQueueButton();
        a4SupportPageRobot.clickCleanNemoQueueButtonConfirm();

        TimeUnit.SECONDS.sleep(3);

        a4SupportPageRobot.checkCleanNemoQueueMsg();

        // check DLQ if +1
        TimeUnit.MILLISECONDS.sleep(REDELIVERY_DELAY + 5000);
        a4ResilienceRobot.checkMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo", (Integer.parseInt(count) + 1));


        //AFTER
        wiremock.close();
    }

    @Test
    @Owner("Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Support Page - Empty DLQ")
    public void testEmptyDlq() throws IOException {
        // wiremock with 400 error
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock400()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        // write things in queue
        a4NemoUpdater.triggerAsyncNemoUpdate(uuids);

        // check DLQ
        String count = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");

        // change wiremock
        wiremock.close();
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock()
                .build();
        wiremock.publish();

        // click Empty Dlq
        a4SupportPageRobot.openSupportPage();
        a4SupportPageRobot.clickMoveFromDlqButton();

        a4SupportPageRobot.checkMoveMessagesMsg();

        // check DLQ if empty
        a4ResilienceRobot.checkMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo", 0);

        //AFTER
        wiremock.close();
    }

}
