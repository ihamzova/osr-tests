package com.tsystems.tm.acc.ta.team.upiter.subscribernetworkline;

import com.tsystems.tm.acc.data.upiter.models.accessline.AccessLineCase;
import com.tsystems.tm.acc.data.upiter.models.networklineprofiledata.NetworkLineProfileDataCase;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.data.osr.models.NetworkLineProfileData;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import com.tsystems.tm.acc.ta.robot.osr.AccessLineRiRobot;
import com.tsystems.tm.acc.ta.robot.osr.NetworkLineProfileManagementRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_8_0.client.model.*;
import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@ServiceLog({
        NETWORK_LINE_PROFILE_MANAGEMENT_MS,
        ACCESS_LINE_RESOURCE_INVENTORY_MS,
        WG_ACCESS_PROVISIONING_MS,
        ACCESS_LINE_MANAGEMENT_MS,
        DECOUPLING_MS,
        GATEWAY_ROUTE_MS
})
public class SubscriberNetworklineProfile extends GigabitTest {

    private AccessLineRiRobot accessLineRiRobot = new AccessLineRiRobot();
    private NetworkLineProfileManagementRobot networkLineProfileManagementRobot = new NetworkLineProfileManagementRobot();
    private UpiterTestContext context = UpiterTestContext.get();
    private NetworkLineProfileData subscriberProfile;
    private AccessLine accessLine;

    @BeforeClass
    public void init() throws InterruptedException {
        accessLineRiRobot.clearDatabase();
        Thread.sleep(1000);
        accessLineRiRobot.fillDatabaseForOltCommissioning();
    }

    @AfterClass
    public void clearData() {
        accessLineRiRobot.clearDatabase();
    }

    @Test
    @TmsLink("DIGIHUB-91193")
    @Description("Subscriber NetworkLine Profile creation: Retail case")
    public void createSubscriberNetworkLineProfileRetail() {
        subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.NetworkLineProfileModifySuccess);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLP);
        networkLineProfileManagementRobot.updateSubscriberNetworklineProfile(subscriberProfile.getResourceOrder(), accessLine);
        Assert.assertEquals(accessLineRiRobot.getSubscriberNLProfile(accessLine.getLineId()).getState(), ProfileState.ACTIVE);
        Assert.assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
    }

    @Test(dependsOnMethods = "createSubscriberNetworkLineProfileRetail")
    @TmsLink("DIGIHUB-91193")
    @Description("Subscriber NetworkLine Profile deletion: Retail case")
    public void deleteSubscriberNetworkLineProfileRetail() {
        subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileDeleteSuccess);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLP);
        networkLineProfileManagementRobot.updateSubscriberNetworklineProfile(subscriberProfile.getResourceOrder(), accessLine);
        AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0);
        Assert.assertNull(accessLineDto.getSubscriberNetworkLineProfile());
        Assert.assertEquals(accessLineDto.getDefaultNetworkLineProfile().getState(), ProfileState.ACTIVE);
    }

    @Test
    @TmsLink("DIGIHUB-91194")
    @Description("Subscriber NetworkLine Profile creation: Wholesale case")
    public void createSubscriberNetworkLineProfileWS() {
        subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileModifySuccessWS);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLPWS);
        networkLineProfileManagementRobot.updateSubscriberNetworklineProfile(subscriberProfile.getResourceOrder(), accessLine);
        SubscriberNetworkLineProfileDto subscriberNetworkLineProfile = accessLineRiRobot.getSubscriberNLProfile(accessLine.getLineId());
        Assert.assertEquals(subscriberNetworkLineProfile.getDownBandwidth().intValue(),123000);
        Assert.assertEquals(subscriberNetworkLineProfile.getUpBandwidth().intValue(),123000);
        Assert.assertEquals(subscriberNetworkLineProfile.getState(), ProfileState.ACTIVE);
        Assert.assertEquals(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0).getDefaultNetworkLineProfile().getState(), ProfileState.INACTIVE);
    }

    @Test(dependsOnMethods = "createSubscriberNetworkLineProfileWS")
    @TmsLink("DIGIHUB-91194")
    @Description("Subscriber NetworkLine Profile deletion: Wholesale case")
    public void deleteSubscriberNetworkLineProfileWS() {
        subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileDeleteSuccessWS);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLPWS);
        networkLineProfileManagementRobot.updateSubscriberNetworklineProfile(subscriberProfile.getResourceOrder(), accessLine);
        AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).get(0);
        Assert.assertNull(accessLineDto.getSubscriberNetworkLineProfile());
        Assert.assertEquals(accessLineDto.getDefaultNetworkLineProfile().getState(), ProfileState.ACTIVE);
    }

    @Test
    @TmsLink("DIGIHUB-49568")
    @Description("Subscriber NetworkLine Profile creation: Wholebuy case")
    public void createSubscriberNetworkLineProfileWB() {
        subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileAddSuccess);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLPWB);
        networkLineProfileManagementRobot.updateSubscriberNetworklineProfile(subscriberProfile.getResourceOrder(), accessLine);
        AccessLineDto accessLineDto = accessLineRiRobot.getAccessLinesByLineId(this.accessLine.getLineId()).get(0);
        Assert.assertNotNull(accessLineDto.getSubscriberNetworkLineProfile(),
                "There is no Subscriber networkline profile");
        Assert.assertEquals(accessLineDto.getStatus(), AccessLineStatus.ASSIGNED);
        Assert.assertEquals(accessLineDto.getSubscriberNetworkLineProfile().getState(), ProfileState.ACTIVE);
    }


    @Test(dependsOnMethods = "createSubscriberNetworkLineProfileWB")
    @TmsLink("DIGIHUB-50901")
    @Description("Subscriber NetworkLine Profile deletion: Wholebuy case")
    public void deleteSubscriberNetworkLineProfileWB() {
        subscriberProfile = context.getData().getNetworkLineProfileDataDataProvider().get(NetworkLineProfileDataCase.networkLineProfileDeleteSuccessWB);
        accessLine = context.getData().getAccessLineDataProvider().get(AccessLineCase.accesslineForSubscriberNLPWB);
        networkLineProfileManagementRobot.updateSubscriberNetworklineProfile(subscriberProfile.getResourceOrder(), accessLine);
        Assert.assertTrue(accessLineRiRobot.getAccessLinesByLineId(accessLine.getLineId()).isEmpty(), "WB Access line is not deleted");
    }
}
