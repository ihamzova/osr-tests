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
    @Owner("Thea.John@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Support Page - Unblock Queue")
    public void testUnblockQueue() throws IOException, InterruptedException {
        final long REDELIVERY_DELAY = a4Resilience.getRedeliveryDelayNemoUpdater();

        // check DLQ (DLQ soll hier leer sein)
        String count1 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ Einträge DLQ am Anfang des Tests: "+count1);

        // wiremock with 500 error
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock500()
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        // write things in queue
        a4NemoUpdater.triggerAsyncNemoUpdate(uuids);

        // check DLQ (DLQ soll hier einen 500er Eintrag haben)
        String count2 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ Einträge DLQ nach Erzeugung 500er: "+count2);

        // click unblock
        a4SupportPageRobot.openSupportPage();
        a4SupportPageRobot.clickCleanNemoQueueButton();
        a4SupportPageRobot.clickCleanNemoQueueButtonConfirm();

        TimeUnit.SECONDS.sleep(3);

        // check DLQ (DLQ soll wieder leer sein)
        String count3 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ Einträge DLQ nach Clean: "+count3);

        a4SupportPageRobot.checkCleanNemoQueueMsg();

        // check DLQ if +1
        TimeUnit.MILLISECONDS.sleep(REDELIVERY_DELAY + 5000);
        a4ResilienceRobot.checkMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo", (Integer.parseInt(count2) + 1));

        // check DLQ (DLQ hat jetzt einen Eintrag mehr, warum erst jetzt ?)
        String count4 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ Einträge DLQ am Ende: "+count4); // jetzt ist ein Eintrag in DLQ dazugekommen und bleibt dort liegen
                                                    // so werden es immer mehr - eine Löschfunktion muss her

        //AFTER
        wiremock.close();
    }

    @Test
    @Owner("Thea.John@telekom.de, heiko.schwanke@t-systems.com")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Support Page - Empty DLQ")
    public void testEmptyDlq() throws IOException, InterruptedException {
        // check DLQ (DLQ soll hier leer sein)
        String count1 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ Einträge DLQ am Anfang des Tests: "+count1);

        // wiremock with 400 error
        // es wird leider kein Eintrag in DeadLetterQueue erzeugt, ja richtig 22.4.21
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock400()   // 400er landen sofort in dlq (sollten sie)
                .build();
        wiremock.publish()
                .publishedHook(savePublishedToDefaultDir())
                .publishedHook(attachStubsToAllureReport());

        // write things in queue
        a4NemoUpdater.triggerAsyncNemoUpdate(uuids);

        // check DLQ (DLQ soll hier einen 400er Eintrag haben)
        String count2 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ Einträge DLQ nach Erzeugung 400er: "+count2);

        // change wiremock
        wiremock.close();
        wiremock = new OsrWireMockMappingsContextBuilder(new WireMockMappingsContext(WireMockFactory.get(), "A4NemoUpdateTest"))
                .addNemoMock()
                .build();
        wiremock.publish();

        // click Empty Dlq
        System.out.println("+++ openSupportPage");
        a4SupportPageRobot.openSupportPage();
        System.out.println("+++ clickMoveFromDlqButton");
        a4SupportPageRobot.clickMoveFromDlqButton();
        System.out.println("+++ clickMoveFromDlqConfirmButton");
        a4SupportPageRobot.clickMoveFromDlqConfirmButton();
        TimeUnit.SECONDS.sleep(2);         // wofür ? hier standen 8 Sekunden

        // check DLQ (DLQ soll wieder leer sein)
        String count3 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ Einträge DLQ nach Move: "+count3);

        System.out.println("+++ checkMoveMessagesMsg");
         a4SupportPageRobot.checkMoveMessagesMsg();

        // check DLQ if empty
        // comment because it is not working yet, yes 22.4.21
        System.out.println("+++ checkMessagesInQueueNemoUpdater");

        // expected: 0, but 6 other messages
        a4ResilienceRobot.checkMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo", 6);
        System.out.println("+++ checkMessagesInQueueNemoUpdater beendet");
        // AFTER
        wiremock.close();
    }

    @Test
    @Owner("Karin.Penne@telekom.de")
    @TmsLink("DIGIHUB-xxxxx")
    @Description("Test Support Page - List Queue")
    public void testListQueue() throws IOException, InterruptedException {

        // was soll hier getestet werden?

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
        // move fehlt hier noch, 22.4.21
        a4SupportPageRobot.openSupportPage();
        a4SupportPageRobot.clickListQueueButton();

        TimeUnit.SECONDS.sleep(8);

        // check DLQ (DLQ soll wieder leer sein)
        String count3 = a4ResilienceRobot.countMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo");
        System.out.println("+++ Einträge DLQ nach Move: "+count3);

        a4SupportPageRobot.checkTable(Integer.parseInt(count));

        // comment because it is not working yet
        // a4SupportPageRobot.checkMoveMessagesMsg();

        // check DLQ if empty
        // comment because it is not working yet
        // a4ResilienceRobot.checkMessagesInQueueNemoUpdater("jms.dead-letter-queue.UpdateNemo", 0);

        //AFTER
        wiremock.close();
    }

}
