package com.tsystems.tm.acc.ta.pages.osr.oltmaintenance;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.tsystems.tm.acc.ta.util.Assert.assertContains;

public class SearchResultsPage {
    private static final By HEADER_LOCATOR = byXpath("//h1//strong[contains(text(),'OLT')]");
    private static final By DISCOVERY_DATE_LOCATOR = byXpath("//table[@class='table']//span");
    private static final By ENDSZ_LOCATOR = byXpath("//table[@class='table']/tbody/tr/td[1]");
    private static final By NAME_LOCATOR = byXpath("//table[@class='table']/tbody/tr/td[2]");
    private static final By IP_ADDRESS_LOCATOR = byXpath("//table[@class='table']/tbody/tr/td[3]");
    private static final By MATERIAL_NUMBER_LOCATOR = byXpath("//table[@class='table']/tbody/tr/td[4]");
    private static final By DISCOVERY_STARTEN_BUTTON_LOCATOR = byXpath("//button[contains(text(),'Discovery starten')]");
    private static final By REPORT_BUTTON_LOCATOR = byXpath("//button[@id='reportButton']");

    private static final By SYNC_BUTTON_LOCATOR = byXpath("//button[@title='Synchronisieren']");
    private static final By UPLINK_ANLEGEN_BUTTON_LOCATOR = byXpath("//button[contains(text(),'Uplink anlegen')]");
    private static final By UPLINK_TABLE_UPLINK_STATUS_LOCATOR = byXpath("//link-info[@ng-reflect-device='[object Object]']//tbody//tr/td[2]");
    private static final By UPLINK_TABLE_OLT_SLOT_NUM_LOCATOR = byXpath("//link-info[@ng-reflect-device='[object Object]']//tbody//tr/td[3]");
    private static final By UPLINK_TABLE_OLT_PORT_LOCATOR = byXpath("//link-info[@ng-reflect-device='[object Object]']//tbody//tr/td[4]");
    private static final By UPLINK_TABLE_BNG_ENDSZ_LOCATOR = byXpath("//link-info[@ng-reflect-device='[object Object]']//tbody//tr/td[5]");
    private static final By UPLINK_TABLE_BNG_SLOT_NUM_LOCATOR = byXpath("//link-info[@ng-reflect-device='[object Object]']//tbody//tr/td[6]");
    private static final By UPLINK_TABLE_BNG_PORT_LOCATOR = byXpath("//link-info[@ng-reflect-device='[object Object]']//tbody//tr/td[7]");
    private static final By EDIT_UPLINK_BUTTON_LOCATOR = byXpath("//button[@title='Edit']");
    private static final By EDIT_ANCP_CONFIG_BUTTON_LOCATOR = byXpath("//button[@title='Start ANCP session Configuration']");
    private static final By EXPAND_SESSIONS_BUTTON_LOCATOR = byXpath("//i[@class='icon icon-navigation-down']");
    private static final By EXPANDABLE_MESSAGE_LOCATOR = byXpath("//div[contains(@class, 'notification')]");

    private static final By DATA_TABLE_VLAN_ID_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[1]");
    private static final By DATA_TABLE_PARTITION_ID_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[2]");
    private static final By DATA_TABLE_SESSION_ID_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[3]");
    private static final By DATA_TABLE_SESSION_TYPE_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[4]");
    private static final By DATA_TABLE_SESSION_STATUS_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[5]");
    private static final By DATA_TABLE_SUBNET_MASK_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[6]");
    private static final By DATA_TABLE_IP1_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[7]");
    private static final By DATA_TABLE_IP2_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[8]");
    private static final By DATA_TABLE_IP3_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]/td[9]");
    private static final By DATA_TABLE_CHECK_STATUS_BUTTON_LOCATOR = byXpath("//div[@class='dataTeble']//tr[2]//td[10]//button[1]");
    private static final By DATA_TABLE_STATUS_LOCATOR = byXpath("//span[@class='badge badge-brand']");

    private static final By CLOSE_RESULTS_BUTTON_LOCATOR = byXpath("//a[@class='btn btn-default btn-icon btn-brand']");
    private static final By SYNC_WAIT_ELEMENT_LOCATOR = byXpath("//div[contains(@class,'col-l-12')]//div//div[contains(@class,'inline text loader')]");

    private static final String ENDPOINT = "/olt";
    private static final String MSK_TIME_ZONE = "+00:00";

    // Device remove
    private static final By REMOVE_BUTTON_LOCATOR = byXpath("//button[@id='deviceRemoveButton']");
    private static final By REMOVE_BANNER_HEADER_LOCATOR = byXpath("//h4[@id='removeDeviceHeader']");
    private static final By REMOVE_BANNER_TEXT_LOCATOR = byXpath("//div[@id='modal-remove-device']//div[@class='modal-body']/span");
    private static final By CONFIRM_REMOVE_BUTTON_LOCATOR = byXpath("//div[@id='modal-remove-device']//button[@type='button'][contains(text(),'OK')]");

    // Report
    private static final By CLOSE_REPORT_BUTTON_LOCATOR = byXpath("//div[@id='modal-report']//span[@aria-hidden='true'][contains(text(),'x')]");
    private static final String REPORT_HEADER_STRING = "Discovery report";


    public CardsTable pressDiscoveryButton() {
        $(DISCOVERY_STARTEN_BUTTON_LOCATOR).click();
        $(DISCOVERY_DATE_LOCATOR).shouldBe(exist);
        return new CardsTable();

    }

    public void pressReportButton() {
        $(REPORT_BUTTON_LOCATOR).click();
    }

    public void validateReport() {
        $(byXpath("//h4[@id='exampleModalLabel']")).shouldHave(text(REPORT_HEADER_STRING));
    }

    public void closeReport() {
        $(CLOSE_REPORT_BUTTON_LOCATOR).click();
    }

    @Step("Validate search level")
    public void validate() {
        assertContains(url(), ENDPOINT);
        $(HEADER_LOCATOR).shouldBe(visible);
        $(ENDSZ_LOCATOR).shouldBe(visible);
    }

    public Date getDiscoveryDate() {
        $(DISCOVERY_DATE_LOCATOR).shouldBe(visible);
        $(DISCOVERY_DATE_LOCATOR).should(have(matchText(".+")));
        String dateStr = $(DISCOVERY_DATE_LOCATOR).text();
        dateStr = dateStr.split("[.]")[0] + MSK_TIME_ZONE;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssXXX");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public String getENDSZ() {
        return $(ENDSZ_LOCATOR).text();
    }

    public String getName() {
        return $(NAME_LOCATOR).text();
    }

    public String getIpaddress() {
        return $(IP_ADDRESS_LOCATOR).text();
    }

    public String getMaterienNummer() {
        return $(MATERIAL_NUMBER_LOCATOR).text();
    }

    public String getUplinkStatus() {
        return $(UPLINK_TABLE_UPLINK_STATUS_LOCATOR).text();
    }

    public void waitUplinkStatus(String textStr) {
        $(UPLINK_TABLE_UPLINK_STATUS_LOCATOR).shouldHave(text(textStr));
    }

    public String getOLTSlotNum() {
        return $(UPLINK_TABLE_OLT_SLOT_NUM_LOCATOR).text();
    }

    public String getOLTPortNum() {
        return $(UPLINK_TABLE_OLT_PORT_LOCATOR).text();
    }

    public String getBNGENDSz() {
        return $(UPLINK_TABLE_BNG_ENDSZ_LOCATOR).text();
    }

    public String getBNGSlotNum() {
        return $(UPLINK_TABLE_BNG_SLOT_NUM_LOCATOR).text();
    }

    public String getBNGPortNum() {
        return $(UPLINK_TABLE_BNG_PORT_LOCATOR).text();
    }

    public void pressSync() {
        $(SYNC_BUTTON_LOCATOR).click();
        $(SYNC_WAIT_ELEMENT_LOCATOR).shouldNot(exist);
        $(DISCOVERY_DATE_LOCATOR).shouldBe(exist);
    }

    public void waitForSync(String textStr) {
        $(NAME_LOCATOR).shouldHave(text(textStr));
    }

    public OLTAddUplinkPage pressEditUplinkButton() {
        $(EDIT_UPLINK_BUTTON_LOCATOR).click();
        return new OLTAddUplinkPage();
    }

    public boolean isEditUplinkButtonPresent() {
        return !$$(EDIT_UPLINK_BUTTON_LOCATOR).isEmpty();
    }

    public void waitSyncButtonDisabledStatus(boolean state) {
        if (state) {
            $(SYNC_BUTTON_LOCATOR).shouldHave(attribute("disabled"));
        } else {
            $(SYNC_BUTTON_LOCATOR).shouldNotHave(attribute("disabled"));
        }
    }

    public OLTAddUplinkPage pressUplinkAnlegen() {
        $(UPLINK_ANLEGEN_BUTTON_LOCATOR).click();
        return new OLTAddUplinkPage();
    }

    public boolean isUplinkAnlegenPresent() {
        return !$$(UPLINK_ANLEGEN_BUTTON_LOCATOR).isEmpty();
    }

    public String pressANCPConfigButton() {
        $(EDIT_ANCP_CONFIG_BUTTON_LOCATOR).click();
        $(EXPANDABLE_MESSAGE_LOCATOR).shouldBe(visible);
        return $(EXPANDABLE_MESSAGE_LOCATOR).text();
    }

    public OLTAddUplinkPage pressExpandSessionsButton() {
        $(EXPAND_SESSIONS_BUTTON_LOCATOR).click();
        return new OLTAddUplinkPage();
    }

    public String getVlan() {
        return $(DATA_TABLE_VLAN_ID_LOCATOR).text();
    }

    public String getPartitionId() {
        return $(DATA_TABLE_PARTITION_ID_LOCATOR).text();
    }

    public String getSessionId() {
        return $(DATA_TABLE_SESSION_ID_LOCATOR).text();
    }

    public String getSessionType() {
        return $(DATA_TABLE_SESSION_TYPE_LOCATOR).text();
    }

    public String getSessionStatus() {
        return $(DATA_TABLE_SESSION_STATUS_LOCATOR).text();
    }

    public String getUplinkSessionSubnetMask() {
        return $(DATA_TABLE_SUBNET_MASK_LOCATOR).text();
    }

    public String getUplinkSessionIp1() {
        return $(DATA_TABLE_IP1_LOCATOR).text();
    }

    public String getUplinkSessionIp2() {
        return $(DATA_TABLE_IP2_LOCATOR).text();
    }

    public String getUplinkSessionIp3() {
        return $(DATA_TABLE_IP3_LOCATOR).text();
    }

    public void pressUplinkCheckStatusButton() {
        $(DATA_TABLE_CHECK_STATUS_BUTTON_LOCATOR).click();
    }

    public String getUplinkSessionStatus() {
        return $(DATA_TABLE_STATUS_LOCATOR).text();
    }

    public void pressClosePageButton() {
        $(CLOSE_RESULTS_BUTTON_LOCATOR).click();
    }

    public void pressRemoveButton() {
        $(REMOVE_BUTTON_LOCATOR).click();
        $(REMOVE_BANNER_HEADER_LOCATOR).shouldBe(visible);
    }

    public String getRemoveMessageText() {
        return $(REMOVE_BANNER_TEXT_LOCATOR).text();
    }

    public void pressConfirmRemoveButton() {
        $(CONFIRM_REMOVE_BUTTON_LOCATOR).click();
    }

    public class CardsTable implements Iterator<CardsTable.CardsTableRecord> {
        private static final int RECORD_COLUMNS_COUNT = 8;
        private final By CARTE_TABLE_INFO_LOCATOR = byXpath("//olt-page/div[1]/div[3]//table[@class='table border']/tbody/tr");
        private final String[] HOME_ID_ENABLED_MATERIAL_IDS = {"40251139", "40294954", "40261742"};
        private Iterator<SelenideElement> iterator;


        public CardsTable() {
            iterator = $$(CARTE_TABLE_INFO_LOCATOR).iterator();
        }

        boolean isPresent() {
            return !$$(CARTE_TABLE_INFO_LOCATOR).isEmpty();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public CardsTableRecord next() {
            if (hasNext()) {
                final SelenideElement record = iterator.next();
                List<WebElement> recordElements = record.findElements(byXpath("./td"));
                if (recordElements.size() != RECORD_COLUMNS_COUNT) {
                    throw new NoSuchElementException("Table structure changed, unable to continue");
                }
                WebElement deleteButton = null;
                WebElement generateHomeIdButton = null;
                final String materialNum = recordElements.get(3).getText();
                if (!materialNum.isEmpty()) {
                    deleteButton = recordElements.get(7).findElement(byXpath("./button[1]"));
                    if (Arrays.asList(HOME_ID_ENABLED_MATERIAL_IDS).contains(materialNum)) {
                        generateHomeIdButton = recordElements.get(7).findElement(byXpath("./button[2]"));
                    }
                }

                return new CardsTableRecord(recordElements.get(0).getText(),
                        recordElements.get(1).getText(),
                        recordElements.get(2).getText(),
                        recordElements.get(3).getText(),
                        recordElements.get(4).getText(),
                        recordElements.get(5).getText(),
                        deleteButton,
                        generateHomeIdButton
                );
            } else {
                throw new NoSuchElementException();
            }
        }

        @AllArgsConstructor
        public class CardsTableRecord {
            private static final String SUCCESSFUL_HOMEID_GENERATION_TITLE_STRING = "Success";

            @Getter
            private final String slotnummer;
            @Getter
            private final String bezeichnung;
            @Getter
            private final String serialnummer;
            @Getter
            private final String materialnummer;
            @Getter
            private final String softwareVersion;
            @Getter
            private final String anzahlPorts;
            private final WebElement deleteButton;
            private WebElement generateHomeIdButton;

            public void clickDelete() {
                if (deleteButton != null) {
                    deleteButton.click();
                } else {
                    throw new NoSuchElementException("Unable to click Generate Home Id button for this element!");
                }
            }

            public void clickGenerateHomeId() {
                if (generateHomeIdButton != null) {
                    generateHomeIdButton.click();
                } else {
                    throw new NoSuchElementException("Unable to click Generate Home Id button for this element!");
                }
            }

            public void ensureHomeIdSuccessfullyGenerated() {
                $(generateHomeIdButton).shouldHave(attribute("title", SUCCESSFUL_HOMEID_GENERATION_TITLE_STRING));
            }
        }
    }
}
