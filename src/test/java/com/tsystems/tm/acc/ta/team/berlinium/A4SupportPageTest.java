package com.tsystems.tm.acc.ta.team.berlinium;

import com.tsystems.tm.acc.data.osr.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.wiremock.OsrWireMockMappingsContextBuilder;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;
import com.tsystems.tm.acc.ta.robot.osr.A4NemoUpdaterRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4ResilienceRobot;
import com.tsystems.tm.acc.ta.robot.osr.A4SupportPageRobot;
import com.tsystems.tm.acc.ta.testng.GigabitTest;

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
import static org.testng.Assert.assertEquals;

@Slf4j
public class A4SupportPageTest extends GigabitTest {

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
        setCredentials(loginData.getLogin(), loginData.getPassword());
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
    @Owner("Thea.John@telekom.de, heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Support Page - Unblock Queue")
    public void testUnblockQueue() throws IOException, InterruptedException {

        String count0 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq at start: "+count0);

        // check DLQ is empty
        a4ResilienceRobot.removeAllMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        String count1 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq after remove: "+count1);

        // wiremock with 500 error
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock500()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        // write error500 in normal-queue
        a4NemoUpdater.triggerAsyncNemoUpdate(uuids);

        // check DLQ
       // String count2 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
       // System.out.println("+++ entries in dlq after 500: "+count2);

        // click unblock
        a4SupportPageRobot.openSupportPage();
        a4SupportPageRobot.clickUnblockNemoQueueButton();
        a4SupportPageRobot.clickMoveMessageToDlqConfirm();
        a4SupportPageRobot.checkCleanNemoQueueMsg(); // check of UI-Message

        final long REDELIVERY_DELAY = a4Resilience.getRedeliveryDelayNemoUpdater();
        TimeUnit.MILLISECONDS.sleep(REDELIVERY_DELAY + 5000);

        // check DLQ+1
       // a4ResilienceRobot.checkMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo", (Integer.parseInt(count2) + 1));

        String count4 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq should be one more: "+count4);
        assertEquals(Integer.parseInt(count4), Integer.parseInt(count1) + 1);

        //AFTER
        wiremock.close();
    }

    @Test
    @Owner("Thea.John@telekom.de, heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Support Page - Empty DLQ")
    public void testEmptyDlq() throws IOException, InterruptedException {

        String count0 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq at start: "+count0);

        a4ResilienceRobot.removeAllMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        // check DLQ is empty
        String count1 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq after remove: "+count1);

        // wiremock with 400 error
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock400()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        // write things in normal queue
        a4NemoUpdater.triggerAsyncNemoUpdate(uuids);

        // check DLQ
        TimeUnit.SECONDS.sleep(8);
        String count2 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq after 400: "+count2);

        // change wiremock
        wiremock.close();
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock()
                .build();
        wiremock.publish();

        // check messages +1 in dlq
        assertEquals(Integer.parseInt(count2), Integer.parseInt(count1) + 1);

        // click Empty Dlq
        a4SupportPageRobot.openSupportPage();
        System.out.println("+++ clickMoveFromDlqButton");
        a4SupportPageRobot.clickMoveFromDlqButton();
        a4SupportPageRobot.clickMoveFromDlqConfirmButton();
        TimeUnit.SECONDS.sleep(8);         // wegen async

        // check DLQ (DLQ soll wieder leer sein)
        String count3 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq after move: "+count3);

        a4SupportPageRobot.checkMoveMessagesMsg();

        // check DLQ if empty
        a4ResilienceRobot.checkMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo", 0);

        // AFTER
        wiremock.close();

        // check DLQ (DLQ am Ende)
        TimeUnit.SECONDS.sleep(10);
        String count4 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq at the end: "+count4);
    }

    @Test
    @Owner("Karin.Penne@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Support Page - List Queue")
    public void testListQueue() throws IOException, InterruptedException {

        // Test: Anzahl auf UI entspricht Anzahl in DLQ

        // check DLQ (DLQ am Anfang)
        String count1 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq at start: "+count1);

        // wiremock with 400 error
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock400()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        // write things in normal queue
        a4NemoUpdater.triggerAsyncNemoUpdate(uuids);

        // check DLQ+1
        String count2 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        assertEquals(Integer.parseInt(count2), Integer.parseInt(count1) + 1);

        // change wiremock
        wiremock.close();
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock()
                .build();
        wiremock.publish();

        // click Empty Dlq
        a4SupportPageRobot.openSupportPage();
        a4SupportPageRobot.clickListQueueButton();

        TimeUnit.SECONDS.sleep(8);

        // check DLQ
        String count3 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ entries in dlq after error 400: "+count3);

        // Anzahl der Einträge wird übergeben, UI-Anzahl wird in checkTable gelesen
        a4SupportPageRobot.checkTable(Integer.parseInt(count3));

        //AFTER
        wiremock.close();

    }

}
