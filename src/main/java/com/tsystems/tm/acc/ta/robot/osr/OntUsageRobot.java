package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.CachedRhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.OntUsageClient;
import com.tsystems.tm.acc.ta.data.osr.models.Credentials;
import com.tsystems.tm.acc.ta.data.osr.models.Ont;
import com.tsystems.tm.acc.ta.data.osr.models.Supplier;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.helpers.upiter.UserTokenProvider;
import com.tsystems.tm.acc.ta.pages.osr.ontusage.OntUsagePage;
import com.tsystems.tm.acc.ta.pages.osr.ontusage.OntUsageSupportPage;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.model.OntUsageEntity;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.model.OntUsagePostRequest;
import com.tsystems.tm.acc.tests.osr.ont.usage.client.model.OntUsagePutRequest;
import de.telekom.it.magic.api.keycloak.AuthorizationCodeTokenProvider;
import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import org.apache.commons.lang.RandomStringUtils;

import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static de.telekom.it.magic.api.keycloak.AuthorizationCodeTokenProviderKt.getPublicAuthorizationCodeTokenProvider;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class OntUsageRobot {
    private char[] hexArray = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final AuthTokenProvider authTokenProvider = new CachedRhssoClientFlowAuthTokenProvider("wiremock-acc");
    private OntUsageClient ontUsageClient = new OntUsageClient(authTokenProvider);

    @Step("Create ONT via supplier ui")
    public void createOntViaSupplierUi(Ont ont, Supplier supplier) {
        OntUsagePage.
                openPage(supplier.getAcid()).
                validate().
                clickCreateONT().
                createONT(ont).
                logout();
    }

    @Step("delete ONT via supplier Ui")
    public void deleteOntViaSupplierUi(Ont ont, Supplier supplier) {
        deleteOntViaSupplierUi(ont, false, supplier);
    }

    @Step("delete ONT via supplier Ui")
    public void deleteOntViaSupplierUi(Ont ont, boolean withoutPermission, Supplier supplier) {
        OntUsagePage.
                openPage(supplier.getAcid()).
                validate().
                deleteOnt(ont, withoutPermission).
                logout();
    }

    @Step("delete ONT via support Ui")
    public void deleteOntViaSupportUi(Ont ont, Supplier supplier) {
        OntUsageSupportPage.
                openPage().
                validate().
                selectSupplier(supplier).
                filterBySerialNumber(ont).
                deleteOnt(ont).
                logout();
    }

    @Step("update ONT Status via support Ui")
    public void updateOntStatusViaSupportUi(Ont ont, Supplier supplier, String status) {
        OntUsageSupportPage.
                openPage().
                validate().
                selectSupplier(supplier).
                updateStatusOfOnt(ont, status).
                logout();
    }

    @Step("check ONT via API")
    public void checkOntViaAPI(Ont ontTestData, OntUsageEntity.StatusEnum status, Credentials adminLogin) {
        ontUsageClient.setUserTokenProvider(new UserTokenProvider(
                adminLogin.getLogin(),
                adminLogin.getPassword(),
                "ont-usage-support-ui"));
        List<OntUsageEntity> ontResponse = ontUsageClient.
                getClient().
                ontUsageCrudOperations().
                getOntUsages().
                serialNumberQuery(ontTestData.getSerialNumber()).
                executeAs(validatedWith(shouldBeCode(200)));

        assertEquals(ontResponse.size(), 1);
        OntUsageEntity actualOnt = ontResponse.get(0);
        assertEquals(actualOnt.getSerialNumber(), ontTestData.getSerialNumber());
        if (ontTestData.getAssignedEmployee() == null) {
            assertFalse(actualOnt.getUseForOnlyOneEmployee());
        }
        assertEquals(actualOnt.getStatus(), status);
    }

    @Step("place work order via API")
    public void placeWorkOrderViaAPI(Ont ontTestData, Credentials adminLogin, Long workOrderId, OntUsagePutRequest.StatusEnum desiredStatus) {
        ontUsageClient.setUserTokenProvider(new UserTokenProvider(
                adminLogin.getLogin(),
                adminLogin.getPassword(),
                "ont-usage-support-ui"));
        List<OntUsageEntity> getOntResponse = ontUsageClient.
                getClient().
                ontUsageCrudOperations().
                getOntUsages().
                serialNumberQuery(ontTestData.getSerialNumber()).
                executeAs(validatedWith(shouldBeCode(200)));

        OntUsageEntity actualOnt = getOntResponse.get(0);

        OntUsagePutRequest putRequest = new OntUsagePutRequest();
        putRequest.setWorkOrderId(workOrderId);
        putRequest.setStatus(desiredStatus);
        putRequest.setPartyId(actualOnt.getPartyId());
        putRequest.setIdmNameOfEmployee(actualOnt.getIdmNameOfEmployee());
        putRequest.setUseForOnlyOneEmployee(actualOnt.getUseForOnlyOneEmployee());

        ontUsageClient.
                getClient().
                ontUsageCrudOperations().
                putOntUsage().
                body(putRequest).
                serialNumberPath(ontTestData.getSerialNumber()).
                executeAs(validatedWith(shouldBeCode(200)));
    }

    @Owner("TMI")
    @Step("Create ONT via supplier API")
    public void createOntViaSupplierApi(Ont ont, Supplier supplier, boolean useForOnlyOneEmployee, String username, String password, String realm) {
        AuthorizationCodeTokenProvider tokenProvider = getPublicAuthorizationCodeTokenProvider(username, password, realm);
        OntUsageClient client = new OntUsageClient(authTokenProvider, tokenProvider);
        OntUsagePostRequest postRequest = new OntUsagePostRequest();

        postRequest.setPartyId(Long.parseLong(supplier.getAtomicOrganizationId()));
        postRequest.setIdmNameOfEmployee(ont.getAssignedEmployee());
        postRequest.setSerialNumber(ont.getSerialNumber());
        postRequest.setUseForOnlyOneEmployee(useForOnlyOneEmployee);

        client.
                getClient().
                ontUsageCrudOperations().
                postOntUsage().
                body(postRequest).
                executeAs(validatedWith(shouldBeCode(200)));
    }

    @Step("check ONT via SupportUi")
    public void checkOntViaSupportUi(Ont ont, Supplier supplier, String desiredState) {
        OntUsageSupportPage.
                openPage().
                validate().
                selectSupplier(supplier).
                filterBySerialNumber(ont).
                checkOntDetails(ont, supplier, desiredState).
                logout();
    }

    @Step("delete WorkOrder via SupportUi")
    public void deleteWorkOrderViaSupportUi(Ont ont, Supplier supplier) {
        OntUsageSupportPage.
                openPage().
                validate().
                selectSupplier(supplier).
                filterBySerialNumber(ont).
                deleteWorkOrder(ont).
                logout();
    }

    @Step("changeSupplierViaSupportUi")
    public void changeSupplierViaSupportUi(Ont ont, Supplier oldSupplier, Supplier newSupplier) {
        OntUsageSupportPage.
                openPage().
                validate().
                selectSupplier(oldSupplier).
                changeSupplier(ont, newSupplier).
                logout();
    }

    @Step("checkInsufficientPermissionsForSupportUi")
    public void checkInsufficientPermissionsForSupportUi() {
        OntUsageSupportPage.
                openPage().
                checkInsufficientPermissionsErrorMessage();
    }

    @Step("checkSupplierNotVisible")
    public void checkSupplierNotVisible(Supplier supplier) {
        OntUsageSupportPage.
                openPage().
                checkSupplierNotVisible(supplier).
                logout();
    }

    @Step("checkSupplierIsVisible")
    public void checkSupplierIsVisible(Supplier supplier) {
        OntUsageSupportPage.
                openPage().
                checkSupplierIsVisible(supplier).
                logout();
    }

    @Step("randomizeSerialNumber")
    public Ont randomizeSerialNumber(Ont ont) {
        String serialNumber = RandomStringUtils.random(16, 0, hexArray.length - 1, true, true, hexArray);
        ont.setSerialNumber(serialNumber);
        return ont;
    }
}