package com.tsystems.tm.acc.ta.robot.osr;

import com.tsystems.tm.acc.domain.osr.csv.A4ResourceInventoryEntry;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceInventoryClient;
import com.tsystems.tm.acc.ta.data.osr.generators.A4NetworkElementGenerator;
import com.tsystems.tm.acc.ta.data.osr.generators.A4NetworkElementGroupGenerator;
import com.tsystems.tm.acc.ta.data.osr.generators.A4NetworkElementPortGenerator;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElement;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementGroup;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementPort;
import com.tsystems.tm.acc.ta.data.osr.models.A4TerminationPoint;
import com.tsystems.tm.acc.ta.pages.osr.a4resourceinventory.InstallationPage;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.inventory.internal.client.model.*;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;

public class A4ResourceInventoryRobot {
    private static final Integer HTTP_CODE_OK_200 = 200;
    private static final Integer HTTP_CODE_NO_CONTENT_204 = 204;

    private ApiClient a4ResourceInventory = new A4ResourceInventoryClient().getClient();

    @Step("Create network element group in A4 repository")
    public void createNetworkElementGroup(NetworkElementGroupDto networkElementGroup) {
        a4ResourceInventory
                .networkElementGroups()
                .createOrUpdateNetworkElementGroup()
                .body(networkElementGroup)
                .uuidPath(networkElementGroup.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Create new network element group in A4 repository")
    public void createNetworkElementGroup(A4NetworkElementGroup negData) {
        A4NetworkElementGroupGenerator a4NetworkElementGroupGenerator = new A4NetworkElementGroupGenerator();
        NetworkElementGroupDto negDto = a4NetworkElementGroupGenerator.generateAsDto(negData);

        a4ResourceInventory
                .networkElementGroups()
                .createOrUpdateNetworkElementGroup()
                .body(negDto)
                .uuidPath(negDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element group from A4 repository")
    public void deleteNetworkElementGroup(String uuid) {
        a4ResourceInventory
                .networkElementGroups()
                .deleteNetworkElementGroup()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete network element group from A4 repository")
    public void deleteNetworkElementGroup(A4NetworkElementGroup negData) {
        a4ResourceInventory
                .networkElementGroups()
                .deleteNetworkElementGroup()
                .uuidPath(negData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create network element in A4 repository")
    public void createNetworkElement(A4NetworkElement neData, A4NetworkElementGroup negData) {
        A4NetworkElementGenerator a4NetworkElementGenerator = new A4NetworkElementGenerator();
        NetworkElementDto neDto = a4NetworkElementGenerator.generateAsDto(neData, negData);

        a4ResourceInventory
                .networkElements()
                .createOrUpdateNetworkElement()
                .body(neDto)
                .uuidPath(neDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element from A4 repository")
    public void deleteNetworkElement(String uuid) {
        a4ResourceInventory
                .networkElements()
                .deleteNetworkElement()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete network element from A4 repository")
    public void deleteNetworkElement(A4NetworkElement neData) {
        a4ResourceInventory
                .networkElements()
                .deleteNetworkElement()
                .uuidPath(neData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create network element port in A4 repository")
    public void createNetworkElementPort(A4NetworkElementPort nepData, A4NetworkElement neData) {
        A4NetworkElementPortGenerator a4NetworkElementPortGenerator = new A4NetworkElementPortGenerator();
        NetworkElementPortDto nepDto = a4NetworkElementPortGenerator.generateAsDto(nepData, neData);

        a4ResourceInventory
                .networkElementPorts()
                .createOrUpdateNetworkElementPort()
                .body(nepDto)
                .uuidPath(nepDto.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete network element port from A4 repository")
    public void deleteNetworkElementPort(String uuid) {
        a4ResourceInventory
                .networkElementPorts()
                .deleteNetworkElementPort()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete network element port from A4 repository")
    public void deleteNetworkElementPort(A4NetworkElementPort nepData) {
        a4ResourceInventory
                .networkElementPorts()
                .deleteNetworkElementPort()
                .uuidPath(nepData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create termination point in A4 repository")
    public void createTerminationPoint(TerminationPointDto terminationPoint) {
        List<AdditionalAttributeDto> additionalAttributes = new ArrayList<>();
        terminationPoint
                .getAdditionalAttribute()
                .forEach(attribute -> additionalAttributes
                        .add(new AdditionalAttributeDto().key(attribute.getKey()).value(attribute.getValue())));

        a4ResourceInventory
                .terminationPoints()
                .createOrUpdateTerminationPoint()
                .body(terminationPoint)
                .uuidPath(terminationPoint.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete termination point from A4 repository")
    public void deleteTerminationPoint(String uuid) {
        a4ResourceInventory
                .terminationPoints()
                .deleteTerminationPoint()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Delete termination point from A4 repository")
    public void deleteTerminationPoint(A4TerminationPoint tpData) {
        a4ResourceInventory
                .terminationPoints()
                .deleteTerminationPoint()
                .uuidPath(tpData.getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    @Step("Create NEG, NE & NEP in A4 repository")
    public void setUpPrerequisiteElements(A4NetworkElementGroup negData, A4NetworkElement neData, A4NetworkElementPort nepData) {
        createNetworkElementGroup(negData);
        createNetworkElement(neData, negData);
        createNetworkElementPort(nepData, neData);
    }

    @Step("Delete NEG, NE & NEP from A4 repository")
    public void deletePrerequisiteElements(A4NetworkElementGroup negData, A4NetworkElement neData, A4NetworkElementPort nepData) {
        deleteNetworkElementPort(nepData);
        deleteNetworkElement(neData);
        deleteNetworkElementGroup(negData);
    }

    @Step("Check if one network service profile connected to termination point exists")
    public void checkNetworkServiceProfileConnectedToTerminationPointExists(String uuidTp) {
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesViaTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);
    }

    @Step("Delete network service profile connected to termination point")
    public void deleteNetworkServiceProfileConnectedToTerminationPoint(String uuidTp) {
        // First: Find all NSPs connected to given TP (expected: only 1 NSP connected)
        List<NetworkServiceProfileFtthAccessDto> nspList = getNetworkServiceProfilesViaTerminationPoint(uuidTp);
        Assert.assertEquals(nspList.size(), 1);

        // Second: Delete found NSP
        a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .deleteNetworkServiceProfileFtthAccess()
                .uuidPath(nspList.get(0).getUuid())
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
    }

    public void deletePortsOfNetworkElement(String neUuid) {
        List<NetworkElementPortDto> ports = a4ResourceInventory
                .networkElementPorts()
                .findNetworkElementPorts()
                .networkElementUuidQuery(neUuid)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        ports.stream().forEach(networkElementPortDto -> {
            deleteNetworkElementPort(networkElementPortDto.getUuid());
        });
    }

    private List<NetworkServiceProfileFtthAccessDto> getNetworkServiceProfilesViaTerminationPoint(String uuidTp) {
        return a4ResourceInventory
                .networkServiceProfilesFtthAccess()
                .findNetworkServiceProfilesFtthAccess()
                .terminationPointUuidQuery(uuidTp)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    @Step("Delete list of network Elements by vpsz/fsz")
    public void deleteNetworkElements(ArrayList<A4ResourceInventoryEntry> list) {
        list.forEach(a4ResourceInventoryEntry -> {
            List<NetworkElementDto> actualNeList = a4ResourceInventory
                    .networkElements()
                    .listNetworkElements()
                    .fszQuery(a4ResourceInventoryEntry.neFsz())
                    .vpszQuery(a4ResourceInventoryEntry.neVpsz())
                    .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

            actualNeList.stream().findFirst().ifPresent(networkElementDto -> {
                deletePortsOfNetworkElement(networkElementDto.getUuid());
                deleteNetworkElement(networkElementDto.getUuid());
            });
        });
    }

    @Step("Delete network group by name")
    public void deleteGroupByName(String groupName) {
        List<NetworkElementGroupDto> actualNegList = a4ResourceInventory
                .networkElementGroups()
                .listNetworkElementGroups()
                .nameQuery(groupName)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));

        actualNegList.stream().findFirst().ifPresent(networkElementGroupDto -> {
            a4ResourceInventory
                    .networkElementGroups()
                    .deleteNetworkElementGroup()
                    .uuidPath(networkElementGroupDto.getUuid())
                    .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));
        });
    }

    @Step("validate network elements")
    public void checkNetworkElementsViaUi(ArrayList<A4ResourceInventoryEntry> list) {
        //@TODO: maybe this method needs to be more flexible - here we expect the correct page to be already open
        InstallationPage installationPage = new InstallationPage();
        list.stream().findFirst().ifPresent(installationPage::checkNetworkElement);
        list.stream().skip(1).forEach(a4ResourceInventoryEntry -> {
            installationPage.resetSearch();
            installationPage.checkNetworkElement(a4ResourceInventoryEntry);
        });
    }
}
