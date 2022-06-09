package com.tsystems.tm.acc.ta.team.upiter.ontusage;

import com.tsystems.tm.acc.data.upiter.models.credentials.CredentialsCase;
import com.tsystems.tm.acc.data.upiter.models.ont.OntCase;
import com.tsystems.tm.acc.data.upiter.models.supplier.SupplierCase;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.Supplier;
import com.tsystems.tm.acc.ta.robot.osr.OntUsageRobot;
import com.tsystems.tm.acc.ta.team.upiter.UpiterTestContext;
import com.tsystems.tm.acc.ta.testng.GigabitTest;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.model.OntUsageEntity;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.model.OntUsagePutRequest;
import de.telekom.it.t3a.kotlin.log.annotations.ServiceLog;
import groovy.util.logging.Slf4j;
import io.qameta.allure.Epic;
import org.testng.annotations.Test;

import static com.tsystems.tm.acc.ta.data.upiter.UpiterConstants.*;

@Slf4j
@Epic("ONT Usage")
@ServiceLog({
        ONT_USAGE_MS,
        ONT_USAGE_BFF_PROXY_MS,
        ONT_USAGE_SUPPLIER_UI,
        ONT_USAGE_SUPPORT_UI
})
public class OntUsageTest extends GigabitTest {

    private UpiterTestContext context = UpiterTestContext.get();
    private OntUsageRobot ontUsageRobot = new OntUsageRobot();
    Ont ont = context.getData().getOntDataProvider().get(OntCase.randomONT);

    @Test
    public void createOntViaSupplierUi() {
        Credentials adminLogin = context.getData().getCredentialsDataProvider().get(CredentialsCase.ONTSupportUiAdminTelekom);
        ont = ontUsageRobot.randomizeSerialNumber(ont);
        Supplier supplier = context.getData().getSupplierDataProvider().get(SupplierCase.SupplierTelekom10013);
        setLoginDataForSupplierUser();
        ontUsageRobot.createOntViaSupplierUi(ont, supplier);
        ontUsageRobot.checkOntViaAPI(ont, OntUsageEntity.StatusEnum.CREATED, adminLogin);
        ontUsageRobot.deleteOntViaSupplierUi(ont, supplier);
    }

    @Test
    public void updateOntStateViaSupportUi() {
        Credentials adminLogin = context.getData().getCredentialsDataProvider().get(CredentialsCase.ONTSupportUiAdminTelekom);
        ont = ontUsageRobot.randomizeSerialNumber(ont);
        Supplier supplier = context.getData().getSupplierDataProvider().get(SupplierCase.SupplierTelekom10013);
        setLoginDataForSupplierUser();
        ontUsageRobot.createOntViaSupplierUi(ont, supplier);

        setLoginDataForAdminUser("telekom");
        ontUsageRobot.updateOntStatusViaSupportUi(ont, supplier, "Offline");
        ontUsageRobot.checkOntViaAPI(ont, OntUsageEntity.StatusEnum.OFFLINE, adminLogin);

        setLoginDataForSupplierUser();
        ontUsageRobot.deleteOntViaSupplierUi(ont, true, supplier); //user does not have permissions to do so

        setLoginDataForAdminUser("telekom");
        ontUsageRobot.deleteOntViaSupportUi(ont, supplier);
    }

    @Test
    public void workOrderTest() {
        Credentials adminLogin = context.getData().getCredentialsDataProvider().get(CredentialsCase.ONTSupportUiAdminTelekom);
        Supplier supplier = context.getData().getSupplierDataProvider().get(SupplierCase.SupplierTelekom10013);
        ont = ontUsageRobot.randomizeSerialNumber(ont);
        setLoginDataForSupplierUser();
        ontUsageRobot.createOntViaSupplierUi(ont, supplier);

        ontUsageRobot.placeWorkOrderViaAPI(ont, adminLogin, 123L, OntUsagePutRequest.StatusEnum.REGISTERED);
        ontUsageRobot.placeWorkOrderViaAPI(ont, adminLogin, 123L, OntUsagePutRequest.StatusEnum.ONLINE);

        //@TODO: validate history
        setLoginDataForAdminUser("telekom");
        ontUsageRobot.deleteWorkOrderViaSupportUi(ont, supplier);
        ontUsageRobot.deleteOntViaSupportUi(ont, supplier);
    }

    @Test
    public void supportUiOnlyForAdmins() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.ONTSupportUiBrokenAdmin);
        setCredentials(loginData.getLogin(), loginData.getPassword());
        ontUsageRobot.checkInsufficientPermissionsForSupportUi();
    }

    @Test
    public void changeSupplierTest() {
        ont = ontUsageRobot.randomizeSerialNumber(ont);
        Supplier oldSupplier = context.getData().getSupplierDataProvider().get(SupplierCase.SupplierTelekom10013);
        Supplier newSupplier = context.getData().getSupplierDataProvider().get(SupplierCase.SupplierTelekom10016);
        setLoginDataForSupplierUser();
        ontUsageRobot.createOntViaSupplierUi(ont, newSupplier);

        setLoginDataForAdminUser("telekom");
        ontUsageRobot.changeSupplierViaSupportUi(ont, oldSupplier, newSupplier);
        ontUsageRobot.checkOntViaSupportUi(ont, newSupplier, "Created");
        ontUsageRobot.deleteOntViaSupportUi(ont, newSupplier);
    }

    @Test
    public void checkCompositeOrganisationsDontSeeEachOther() {
        Supplier telekomSupplier = context.getData().getSupplierDataProvider().get(SupplierCase.SupplierTelekom10013);

        setLoginDataForAdminUser("gfnw");
        ontUsageRobot.checkSupplierNotVisible(telekomSupplier);

        setLoginDataForAdminUser("telekom");
        ontUsageRobot.checkSupplierIsVisible(telekomSupplier);
    }

    private void setLoginDataForAdminUser(String compositeOrganisation) {
        Credentials loginData;
        if (compositeOrganisation.equals("gfnw")) {
            loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.ONTSupportUiAdminGfnw);
        } else {
            loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.ONTSupportUiAdminTelekom);
        }
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }

    private void setLoginDataForSupplierUser() {
        Credentials loginData = context.getData().getCredentialsDataProvider().get(CredentialsCase.LeonhardWeissForDTAGSupplier);
        setCredentials(loginData.getLogin(), loginData.getPassword());
    }
}
