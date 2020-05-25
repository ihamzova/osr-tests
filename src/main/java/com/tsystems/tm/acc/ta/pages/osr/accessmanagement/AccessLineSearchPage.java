package com.tsystems.tm.acc.ta.pages.osr.accessmanagement;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.tsystems.tm.acc.ta.data.osr.models.AccessLine;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.OCUrlBuilder;
import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.internal.client.model.AccessLineViewDto;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;

@Slf4j
public class AccessLineSearchPage {
    private static final String APP = "access-management-support-ui";
    private static final String ENDPOINT = "/search";

    private static final By PORT_ADDRESS_TAB = byQaData("sc-port-address-tab-a");
    private static final By ENDSZ_INPUT = byQaData("pac-end-sz-input");
    private static final By SLOT_NUMBER_INPUT = byQaData("pac-slot-number-input");
    private static final By PORT_NUMBER_INPUT = byQaData("pac-port-number-input");
    private static final By P_SEARCH_TABLE = byQaData("sc-search-table-ptable");
    private static final By PAGINATOR_DROPDOWN = By.tagName("p-dropdown");
    private static final By ASSIGNED_STATUS = byQaData("ucc-assigned-rdq-seal-filter-label");
    private static final By WALLED_GARDEN_STATUS = byQaData("ucc-walled-garden-filter-label");
    private static final By HOMEID_TAB = byQaData("sc-home-id-tab-a");
    private static final By HOMEID_INPUT = byQaData("hic-home-id-input");
    private static final By SEARCH_BUTTON = byQaData("search-button");
    private static final By LINEID_TAB = byQaData("sc-line-id-tab-a");
    private static final By LINEID_INPUT = byQaData("hic-line-id-input");


    @Step("Open Access-line-Search page")
    public static AccessLineSearchPage openPage() {
        URL url = new OCUrlBuilder(APP).withEndpoint(ENDPOINT).build();
        log.info("Opening url " + url.toString());
        return open(url, AccessLineSearchPage.class);
    }

    @Step("Validate Url")
    public void validateUrl() {
        assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
        assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
    }

    @Step("Search Access lines by port address's parameters")
    public AccessLineSearchPage searchAccessLinesByPortAddress(AccessLine accessLine) {
        $(PORT_ADDRESS_TAB).click();
        $(ENDSZ_INPUT).click();
        $(ENDSZ_INPUT).val(accessLine.getEndSz());
        if (!accessLine.getSlotNumber().isEmpty()) {
            $(SLOT_NUMBER_INPUT).click();
            $(SLOT_NUMBER_INPUT).val(accessLine.getSlotNumber());
        }
        if (!accessLine.getPortNumber().isEmpty()) {
            $(PORT_NUMBER_INPUT).click();
            $(PORT_NUMBER_INPUT).val(accessLine.getPortNumber());
        }
        $(SEARCH_BUTTON).click();
        $(SEARCH_BUTTON).find(By.tagName("i")).shouldNot(Condition.cssClass("spinner"));
        return this;
    }

    @Step ("Search Access lines by HomeID")
    public AccessLineSearchPage searchAccessLinesByHomeID (AccessLine accessLine) {
        $(HOMEID_TAB).click();
        $(HOMEID_INPUT).click();
        $(HOMEID_INPUT).val(accessLine.getHomeId());
        $(SEARCH_BUTTON).click();
        $(SEARCH_BUTTON).find(By.tagName("i")).shouldNot(Condition.cssClass("spinner"));
        return this;
    }

    @Step ("Search Access lines by LineID")
    public AccessLineSearchPage searchAccessLinesByLineID (AccessLine accessLine) {
        $(LINEID_TAB).click();
        $(LINEID_INPUT).click();
        $(LINEID_INPUT).val(accessLine.getLineId());
        $(SEARCH_BUTTON).click();
        $(SEARCH_BUTTON).find(By.tagName("i")).shouldNot(Condition.cssClass("spinner"));
        return this;
    }

    @Step("Get table headers")
    public List<String> getTableHeaders() {
        return $(P_SEARCH_TABLE).findAll(By.tagName("th")).stream()
                .map(SelenideElement::text)
                .collect(Collectors.toList());
    }

    @Step("Get a message about found access lines")
    public String getTableMessage() {
        return $(By.className("am-count")).text();
    }

    @Step("Get all table rows")
    public List<AccessLineViewDto> getTableLines() {
        return getTableRows().stream()
                .map(element -> {
                    ElementsCollection tds = element.findAll(By.tagName("td"));
                    AccessLineViewDto accessLineInfo = new AccessLineViewDto();
                    accessLineInfo.setEndSz(tds.get(1).getText());
                    accessLineInfo.setSlotNumber(tds.get(2).getText());
                    accessLineInfo.setPortNumber(tds.get(3).getText());
                    accessLineInfo.setLineId(tds.get(4).getText());
                    accessLineInfo.setHomeId(tds.get(5).getText());
                    accessLineInfo.setStatus(AccessLineViewDto.StatusEnum.valueOf(tds.get(7).getText()));
                    return accessLineInfo;
                })
                .collect(Collectors.toList());
    }

    @Step("Get paginator's sizes")
    public List<String> getPaginatorSizes() {
        $(P_SEARCH_TABLE).find(PAGINATOR_DROPDOWN).click();
        return $(P_SEARCH_TABLE).find(PAGINATOR_DROPDOWN)
                .findAll(By.tagName("li"))
                .stream()
                .map(SelenideElement::text)
                .collect(Collectors.toList());
    }

    @Step("Click magnifying glass to Access Lines Management Page")
    public AccessLinesManagementPage clickMagnifyingGlassForLine(int rowNumber) {
        getTableRows().get(rowNumber).find(By.tagName("i")).click();
        switchTo().window(new AccessLinesManagementPage().getPageTitle());
        return new AccessLinesManagementPage();
    }

    @Step("Set page size of paginator")
    public AccessLineSearchPage setPageSize(int pageSize) {
        $(P_SEARCH_TABLE).find(PAGINATOR_DROPDOWN).click();
        $(P_SEARCH_TABLE).find(PAGINATOR_DROPDOWN)
                .findAll(By.tagName("li"))
                .find(Condition.text(String.valueOf(pageSize)))
                .click();
        $(P_SEARCH_TABLE).find(By.tagName("tbody")).findAll(By.tagName("tr")).shouldHaveSize(pageSize);
        return this;
    }

    @Step("Set assigned(alle) status")
    public AccessLineSearchPage setAssignedStatus() {
        $(ASSIGNED_STATUS).click();
        return this;
    }

    @Step("Set walled garden status")
    public AccessLineSearchPage setWalledGardenStatus() {
        $(WALLED_GARDEN_STATUS).click();
        return this;
    }

    private List<SelenideElement> getTableRows() {
        return $(P_SEARCH_TABLE).find(By.tagName("tbody")).findAll(By.tagName("tr"));
    }

    public enum ProfileTypes {
        NE_PROFILE, NETWORK_LINE_PROFILE
    }

    public enum ProfileNames {
        DEFAULT_PROFILE, SUBSCRIBER_PROFILE
    }

}
