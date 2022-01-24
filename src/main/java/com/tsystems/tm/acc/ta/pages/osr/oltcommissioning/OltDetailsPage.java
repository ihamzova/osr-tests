package com.tsystems.tm.acc.ta.pages.osr.oltcommissioning;

import com.codeborne.selenide.Condition;
import com.tsystems.tm.acc.ta.data.osr.enums.DevicePortLifeCycleStateUI;
import com.tsystems.tm.acc.ta.data.osr.models.OltDevice;
import com.tsystems.tm.acc.ta.helpers.CommonHelper;
import com.tsystems.tm.acc.ta.util.Assert;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.tsystems.tm.acc.ta.util.Assert.assertUrlContainsWithTimeout;
import static com.tsystems.tm.acc.ta.util.Locators.byQaData;
import static org.testng.Assert.assertEquals;

@Slf4j
public class OltDetailsPage {

  public static final Integer MAX_LATENCY_FOR_ELEMENT_APPEARS = 60_000;
  public static final Integer MAX_ANCP_COFIGURATION_TIME = 2 * 60_000;
  public static final Integer MAX_LATENCY_FOR_LIFECYCLE_CHANGE = 10000;
  private static final Integer PORTS_PER_GPON_CARD = 8;
  public static final String APP = "olt-resource-inventory-ui";
  public static final String ENDPOINT =  "/" + APP + "/detail";

  public static final By CARDS_VIEW_TAB_LOCATOR = byQaData("a-cards-view");
  public static final By CONFIGURATION_VIEW_TAB_LOCATOR = byQaData("a-configuration-view");

  public static final By UPLINK_ADD_BUTTON_LOCATOR = byQaData("button-add-uplink");

  public static final By UPLINK_UEWEG_SELECTION_0 =  byQaData("ueweg_selection_0");
  public static final By UPLINK_CREATE_CONFIRM_BUTTON_LOCATOR = byQaData("button-uplink-create-uplink");
  public static final By UPLINK_DELETE_BUTTON_LOCATOR = byQaData("button-delete-uplink");
  public static final By UPLINK_DELETE_CONFIRM_BUTTON_LOCATOR = byQaData("button-uplink-delete-uplink");

  public static final By ANCP_CONFIGURE_BUTTON_LOCATOR = byQaData("button-configure-ancp-session");
  public static final By ANCP_DE_CONFIGURE_BUTTON_LOCATOR = byQaData("button-de-configure-ancp-session");
  public static final By ANCP_SESSION_STATUS_UNKNOWN_LOCATOR = byQaData("a-ancpstatetest");
  public static final By ANCP_SESSION_STATUS_LOCATOR = byQaData("span-ancpstate");

  public static final int[] AVAILABLE_LINE_CARD_SLOTS_ARRAY = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 13, 14, 15, 16, 17, 18};
  public String cardCommissioningStartButtonLocator = "button-card-commissioning-slot-%d";
  public String cardDeCommissioningStartButtonLocator = "button-card-de-commissioning-slot-%d";

  //validation
  private static final By ENDSZ_LOCATOR = byQaData("span-olt-endsz");
  private static final By BEZEICHNUNG_LOCATOR = byQaData("span-olt-emsnbiname");
  private static final By KLSID_LOCATOR = byQaData("span-olt-klsid");

  private static final By DEVICE_LIFE_CYCLE_STATE_LOCATOR = byQaData("device_lifecyclestate");
  public String slotPortViewLocator = "a-card-portview-slot-%s";
  public String portLifeCycleStateLocator = "slot_%s_port_%s_lifecyclestate";
  public String portLifeCycleStateLocatorEmptySlot = "port_%s_ethernet_lifecyclestate";
  public String ponPortLifeCycleStateLocatorEmptySlot = "port_%s_pon_lifecyclestate";

  public static final By DEVICE_FUNCTION_BUTTON_LOCATOR = byQaData("device_functions");
  public static final By DELETE_DEVICE_BUTTON_LOCATOR = byQaData("device_functions_option_3");
  public static final By START_EDIT_DEVICE_BUTTON_LOCATOR = byQaData("device_functions_action");

  public static final By DELETE_CARD_BUTTON_LOCATOR = byQaData("button-card-deletion-slot-1");
  public static final By DELETE_CARD_BUTTON_CONFIRM_LOCATOR = byQaData("card_delete_perform");

  public static final By START_CARDS_DEPROVISIONING_FROM_DEVICEBUTTON_LOCATOR = byQaData("device_functions_option_2");
  public static final By START_CARDS_PROVISIONING_FROM_DEVICEBUTTON_LOCATOR = byQaData("device_functions_option_1");

  @Step("Validate Url")
  public void validateUrl() {
    assertUrlContainsWithTimeout(APP, CommonHelper.commonTimeout);
    assertUrlContainsWithTimeout(ENDPOINT, CommonHelper.commonTimeout);
  }

  @Step("Open port view")
  public OltDetailsPage openPortView(String slot) {
    $(CARDS_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    if (slot != null && !slot.isEmpty()) {
      if (!($(byQaData(String.format(portLifeCycleStateLocator, slot, "0"))).isDisplayed())) {
        $(byQaData(String.format(slotPortViewLocator, slot))).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
      }
    }
    return this;
  }

  @Step("Configure uplink")
  public OltDetailsPage startUplinkConfiguration() {
    $(CONFIGURATION_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(UPLINK_ADD_BUTTON_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    return this;
  }

  @Step("Save uplink configuration")
  public OltDetailsPage saveUplinkConfiguration() {
    String radio_value = $(UPLINK_UEWEG_SELECTION_0).should(exist , Duration.ofMillis(1000)).getValue();
    assertEquals(radio_value, "on", "Start uplink configuration radio value not on");
    $(UPLINK_CREATE_CONFIRM_BUTTON_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    return new OltDetailsPage();
  }

  @Step("Deconfigure uplink")
  public OltDetailsPage deleteUplinkConfiguration() {
    $(CONFIGURATION_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(UPLINK_DELETE_BUTTON_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(UPLINK_DELETE_CONFIRM_BUTTON_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    return this;
  }

  @Step("Configure ANCP session")
  public OltDetailsPage configureAncpSession() {
    $(CONFIGURATION_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(ANCP_CONFIGURE_BUTTON_LOCATOR).click();
    $(ANCP_DE_CONFIGURE_BUTTON_LOCATOR).shouldBe(visible, Duration.ofMillis(MAX_ANCP_COFIGURATION_TIME)).isDisplayed();
    return this;
  }

  @Step("Start configure ANCP session")
  public OltDetailsPage configureAncpSessionStart() {
    $(CONFIGURATION_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(ANCP_CONFIGURE_BUTTON_LOCATOR).click();
    $(DEVICE_LIFE_CYCLE_STATE_LOCATOR).shouldHave(exactTextCaseSensitive(DevicePortLifeCycleStateUI.INSTALLING.toString()), Duration.ofMillis(MAX_LATENCY_FOR_LIFECYCLE_CHANGE));
    return this;
  }


  @Step("Deconfigure ANCP session")
  public OltDetailsPage deconfigureAncpSession() {
    $(CONFIGURATION_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(ANCP_DE_CONFIGURE_BUTTON_LOCATOR).click();
    $(DEVICE_LIFE_CYCLE_STATE_LOCATOR).shouldHave(exactTextCaseSensitive(DevicePortLifeCycleStateUI.RETIRING.toString()), Duration.ofMillis(MAX_LATENCY_FOR_LIFECYCLE_CHANGE));
    return this;
  }


  @Step("Update ANCP Session State")
  public OltDetailsPage updateAncpSessionStatus() {
    $(CONFIGURATION_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(ANCP_SESSION_STATUS_UNKNOWN_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    return this;
  }

  @Step("Check ANCP Session State is displayed")
  public OltDetailsPage checkAncpSessionStatus() {
    $(CONFIGURATION_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(ANCP_SESSION_STATUS_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).isDisplayed();
    return this;
  }

  @Step("Access lines provisioning")
  public OltDetailsPage startAccessLinesProvisioning(Integer timeout) {
    $(CARDS_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    for (int slot : AVAILABLE_LINE_CARD_SLOTS_ARRAY) {
      if ($(byQaData(String.format(cardCommissioningStartButtonLocator, slot))).isDisplayed()) {
        $(byQaData(String.format(cardCommissioningStartButtonLocator, slot))).click();
        $(byQaData(String.format(cardDeCommissioningStartButtonLocator, slot))).shouldBe(visible, Duration.ofMillis(timeout)).isDisplayed();
      }
    }
    return this;
  }

  @Step("Access lines provisioning from Device")

  public OltDetailsPage startAccessLinesProvisioningFromDevice(Integer timeout) {
    $(DEVICE_FUNCTION_BUTTON_LOCATOR).click();
    $(START_CARDS_PROVISIONING_FROM_DEVICEBUTTON_LOCATOR).click();
    $(START_EDIT_DEVICE_BUTTON_LOCATOR).click();
    $(CARDS_VIEW_TAB_LOCATOR).shouldBe(visible, Duration.ofMillis(timeout)).isDisplayed();
    return this;
  }

  @Step("Check GPON Ports LifeCycleState on UI")
  public OltDetailsPage checkGponPortLifeCycleState(OltDevice oltDevice, String portLifeCycleState) {
    $(CARDS_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    if ($(byQaData(String.format(ponPortLifeCycleStateLocatorEmptySlot, "1"))).exists()) {
      for (int port = 1; port < oltDevice.getNumberOfPonPorts(); ++port) {
        Assert.assertContains($(byQaData(String.format(ponPortLifeCycleStateLocatorEmptySlot, port))).getText(), portLifeCycleState);
      }
    } else {
      for (int slot : AVAILABLE_LINE_CARD_SLOTS_ARRAY) {
        if ($(byQaData(String.format(cardCommissioningStartButtonLocator, slot))).isDisplayed()) {
          if (!($(byQaData(String.format(portLifeCycleStateLocator, slot, "0"))).isDisplayed())) {
            $(byQaData(String.format(slotPortViewLocator, slot))).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
          }
          for (int port = 0; port < PORTS_PER_GPON_CARD; ++port) {
            Assert.assertContains($(byQaData(String.format(portLifeCycleStateLocator, slot, port))).getText(), portLifeCycleState);
          }
        }
      }
    }
    return this;
  }


  @Step("Delete device")
  public OltDetailsPage deleteDevice() {
    $(DEVICE_FUNCTION_BUTTON_LOCATOR).click();
    $(DELETE_DEVICE_BUTTON_LOCATOR).click();
    $(START_EDIT_DEVICE_BUTTON_LOCATOR).click();
    return this;
  }

  @Step("Delete card")

  public OltDetailsPage deleteGponCard() {
    $(CARDS_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    $(DELETE_CARD_BUTTON_LOCATOR).click();
    $(DELETE_CARD_BUTTON_CONFIRM_LOCATOR).click();
    return this;
  }

  @Step("Access lines deprovisioning from Gpon LC")

  public OltDetailsPage startAccessLinesDeProvisioningFromCard(Integer timeout) {
    $(CARDS_VIEW_TAB_LOCATOR).should(appear, Duration.ofMillis(MAX_LATENCY_FOR_ELEMENT_APPEARS)).click();
    for (int slot : AVAILABLE_LINE_CARD_SLOTS_ARRAY) {
      if ($(byQaData(String.format(cardDeCommissioningStartButtonLocator, slot))).isDisplayed()) {
        $(byQaData(String.format(cardDeCommissioningStartButtonLocator, slot))).click();
        $(byQaData(String.format(cardCommissioningStartButtonLocator, slot))).shouldBe(visible, Duration.ofMillis(timeout)).isDisplayed();
      }
    }
    return this;
  }

  @Step("Access lines deprovisioning from Device")

  public OltDetailsPage startAccessLinesDeProvisioningFromDevice(Integer timeout) {
    $(DEVICE_FUNCTION_BUTTON_LOCATOR).click();
    $(START_CARDS_DEPROVISIONING_FROM_DEVICEBUTTON_LOCATOR).click();
    $(START_EDIT_DEVICE_BUTTON_LOCATOR).click();
    $(CARDS_VIEW_TAB_LOCATOR).shouldBe(visible, Duration.ofMillis(timeout)).isDisplayed();
    return this;
  }


  @Step("Get EndSz")
  public String getEndsz() {
    return $(ENDSZ_LOCATOR).getText();
  }

  @Step("Get Bezeichnung")
  public String getBezeichnung() {
    return $(BEZEICHNUNG_LOCATOR).getText();
  }

  @Step("Get KLS-ID")
  public String getKlsID() {
    return $(KLSID_LOCATOR).getText();
  }

  @Step("Get device life cycle state")
  public String getDeviceLifeCycleState() {
    return $(DEVICE_LIFE_CYCLE_STATE_LOCATOR).getText();
  }

  @Step("Get port life cycle state")
  public String getPortLifeCycleState(String slot, String port) {
    if (slot != null && !slot.isEmpty()) {
      return $(byQaData(String.format(portLifeCycleStateLocator, slot, port))).getText(); // HUAWEI
    } else {
      return $(byQaData(String.format(portLifeCycleStateLocatorEmptySlot, port))).getText(); // Adtran without Slots
    }

  }
}
