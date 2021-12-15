package com.tsystems.tm.acc.ta.robot.osr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderClient;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderOrchestratorClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkServiceProfileA10Nsp;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderItemDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderMainDataDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.shouldBeCode;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.validatedWith;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@Slf4j
public class A4ResourceOrderRobot {

    public static final String CB_PATH = "/test_url";
    private final String cbUrl = new GigabitUrlBuilder(WIREMOCK_MS_NAME).buildUri() + CB_PATH; // Wiremock for merlin MS

    private static final AuthTokenProvider authTokenProviderDispatcher =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_ORDER_ORCHESTRATOR_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_ORDER_ORCHESTRATOR_MS)); //this will be merlin's service in the future

    private static final AuthTokenProvider authTokenProviderOrchestrator =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_INVENTORY_BFF_PROXY_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_INVENTORY_BFF_PROXY_MS));


    private final ApiClient a4ResourceOrder = new A4ResourceOrderClient(authTokenProviderDispatcher).getClient();
    private final com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.invoker.ApiClient a4ResourceOrderOrchestratorClient =
            new A4ResourceOrderOrchestratorClient(authTokenProviderOrchestrator).getClient();

    private final A4ResourceOrderMapper resourceOrderMapper = new A4ResourceOrderMapper();
    private final A4ResourceInventoryRobot a4ResourceInventory = new A4ResourceInventoryRobot();


    @Step("Send POST for A10nsp Resource Order")
    public void sendPostResourceOrder(ResourceOrder resourceOrder) {
        final String correlationId = UUID.randomUUID().toString();

        a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .xCallbackCorrelationIdHeader(correlationId)
                .xCallbackUrlHeader(cbUrl)
                .xCallbackIdHeader("1")
                .body(resourceOrder)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_ACCEPTED_202)));
    }

    public ResourceOrder buildResourceOrder() {
        return resourceOrderMapper.buildResourceOrder();
    }

    public void addOrderItemAdd(String orderItemId, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, OrderItemActionType.ADD, nelData, ro);
    }

    public void addOrderItemModify(String orderItemId, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, OrderItemActionType.MODIFY, nelData, ro);
    }

    public void addOrderItemDelete(String orderItemId, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, OrderItemActionType.DELETE, nelData, ro);
    }

    public void checkDefaultValuesNsp(A4NetworkServiceProfileA10Nsp nsp) {

         /*     values of nsp at start
    uuid: f0a7f68c-5237-41f3-9420-dec827bab332
    lifecycleState: PLANNING
    operationalState: NOT_WORKING
    description: NSP A10NSP created during osr-test integration test
    administrativeMode: ACTIVATED
    virtualServiceProvider: a Virtual Service Provider
    specificationVersion: 1
    carrierBsaReference: CarrierBsaReference
    mtuSize: 1590
    etherType: 0x88a8
    itAccountingKey: undefined
    lacpActive: true
    lacpMode: undefined
    minActiveLagLinks: 1
    sVlanRange: [class VlanRangeDto {
        vlanRangeLower: undefined
        vlanRangeUpper: undefined
    }]
    dataRate: undefined
    qosClasses: [class A10NspQosDto {
        qosPriority: undefined
        qosBandwidthUp: undefined
        qosBandwidthDown: undefined
    }]
    qosMode: TOLERANT
    lastUpdateTime: 2021-12-15T09:01:01.403+01:00
    creationTime: 2021-12-15T09:01:01.403+01:00
    terminationPointA10NspUuid: 12256701-548b-40aa-8b83-7175c1eb8887
    numberOfAssociatedNsps: undefined
    networkElementLinkUuid: null
    href: /resource-order-resource-inventory/v1/a4TerminationPoints/12256701-548b-40aa-8b83-7175c1eb8887
     */

        String lcs_new = a4ResourceInventory.getExistingNetworkServiceProfileA10Nsp(nsp.getUuid()).getLifecycleState();
        assertEquals(lcs_new, "PLANNING");
        // lastUpdateTime: 2021-12-15T09:01:01.403+01:00  ungleich    creationTime: 2021-12-15T09:01:01.403+01:00


        System.out.println("+++++++++++++++++++++++++++++++++++++");
        System.out.println("+++++++++ new values of nsp +++++++++");
        System.out.println("+++ lcs of nsp (default is PLANNING): "+lcs_new);
        System.out.println("+++  ");

        System.out.println("+++  ");

        System.out.println("+++  ");

        System.out.println("+++  ");

        System.out.println("+++  ");

        System.out.println("+++  ");
        System.out.println("+++  ");
        System.out.println("+++  ");

    /*
    A4NetworkServiceProfileA10Nsp(
    uuid=ace216e6-843d-4686-bb84-6823e9614a8d,
    operationalState=NOT_WORKING,
    lifecycleState=PLANNING,
    numberOfAssociatedNsps=null)




    +++++++ roi
    "uuid": "8637025e-188e-4563-981b-8022f9b0a073",
    "creationTime": "2021-12-14T12:37:52.149+01:00",
    "description": "NEL for integration test",
    "lastUpdateTime": "2021-12-14T12:37:58.89+01:00",
    "specificationVersion": null,
    "lsz": "4N4",
    "lbz": "4N4/1004-49/3926/0/7KCA-49/3752/0/7KH0",
    "orderNumber": "1004",
    "lifecycleState": "DEACTIVATED",
    "operationalState": "NOT_WORKING",
    "ueWegId": "430247, 658839",
    "pluralId": "2",
    "networkElementPortAUuid": "fe6a2122-885f-4905-ab79-0fba9aa908f0",
    "networkElementPortBUuid": "09973704-a935-4c32-9217-312a5fab2852",
    "endszA": "49/3926/0/7KCA",
    "endszB": "49/3752/0/7KH0"
    */




}

    public void addOrderItem(String orderItemId, OrderItemActionType actionType, A4NetworkElementLink nelData, ResourceOrder ro) {
        ResourceRefOrValue resource = new ResourceRefOrValue()
                .name(nelData.getLbz())
                .resourceCharacteristic(resourceOrderMapper.buildResourceCharacteristicList());

        ResourceOrderItem orderItem = new ResourceOrderItem()
                .action(actionType)
                .resource(resource)
                .id(orderItemId);

        ro.addOrderItemItem(orderItem);
    }

    public void setResourceName(String name, String orderItemId, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemByOrderItemId(orderItemId, ro);
        Objects.requireNonNull(roi.getResource()).setName(name);
    }

    public void setCharacteristicValue(String characteristicName, Object value, String orderItemId, ResourceOrder ro) {
        ResourceOrderItem roi = getResourceOrderItemByOrderItemId(orderItemId, ro);
        Characteristic c = getCharacteristic(characteristicName, roi);
        c.setValue(value);
    }

    public Characteristic getCharacteristic(String name, ResourceOrderItem roi) {
        List<Characteristic> rcList = Objects.requireNonNull(roi.getResource()).getResourceCharacteristic();

        if (rcList != null) {
            for (Characteristic characteristic : rcList) {
                if (characteristic.getName().equals(name)) {
                    return characteristic;
                }
            }
        }

        return null;
    }

    public void removeCharacteristic(String chName, String orderItemId, ResourceOrder ro) {
        List<Characteristic> rcList = Objects.requireNonNull(getResourceOrderItemByOrderItemId(orderItemId, ro).getResource()).getResourceCharacteristic();

        if (rcList != null) {
            rcList.removeIf(characteristic -> characteristic.getName().equals(chName));
        }
    }

    public ResourceOrderItem getResourceOrderItemByOrderItemId(String orderItemId, ResourceOrder ro) {
        List<ResourceOrderItem> roiList = ro.getOrderItem();

        if (roiList != null) {
            for (ResourceOrderItem resourceOrderItem : roiList) {
                if (resourceOrderItem.getId().equals(orderItemId))
                    return resourceOrderItem;
            }
        }

        return null;
    }

    private ResourceOrder getResourceOrderFromCallback() {
        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo(CB_PATH)));

        String response = ergList.get(0).getBodyAsString();

        return getResourceOrderObjectFromJsonString(response);
    }

    public void cleanCallbacksInWiremock() {
        cleanCallbacksInWiremock("POST", CB_PATH);
    }

    public void cleanCallbacksInWiremock(String method, String path) {
        WireMockFactory.get()
                .removeEvents(
                        newRequestPattern(
                                RequestMethod.fromString(method),
                                urlPathEqualTo(path)));
    }

    private ResourceOrder getResourceOrderObjectFromJsonString(String jsonString) {
        final ObjectMapper objectMapper = new ObjectMapper();

        // action property in json is e.g. "add". Needs to be mapped to enum ADD("add")
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        // some @... properties like e.g. @BaseType cannot be mapped (to atBaseType). Don't fail, isn't tested here
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // date-time comes in unix milliseconds. Need to be mapped to OffsetDateTime
        objectMapper.registerModule(new JavaTimeModule());

        try {
            return objectMapper.readValue(jsonString, ResourceOrder.class);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }

        return null;
    }

    public void checkResourceOrderIsCompleted() {
        checkResourceOrderHasState(ResourceOrderStateType.COMPLETED);
    }

    public void checkResourceOrderIsRejected() {
        checkResourceOrderHasState(ResourceOrderStateType.REJECTED);
    }

    private void checkResourceOrderHasState(ResourceOrderStateType state) {
        ResourceOrder roFromCb = getResourceOrderFromCallback();
        if (roFromCb != null)
            assertEquals(roFromCb.getState(), state);
        else
            fail("No callback resource order to check");
    }

    public void checkOrderItemIsCompleted(String orderItemId) {
        checkResourceOrderItemHasState(orderItemId, ResourceOrderItemStateType.COMPLETED);
    }

    public void checkOrderItemIsRejected(String orderItemId) {
        checkResourceOrderItemHasState(orderItemId, ResourceOrderItemStateType.REJECTED);
    }

    private void checkResourceOrderItemHasState(String orderItemId, ResourceOrderItemStateType state) {
        ResourceOrder roFromCb = getResourceOrderFromCallback();

        if (roFromCb != null) {
            ResourceOrderItem roi = getResourceOrderItemByOrderItemId(orderItemId, roFromCb);
            assertEquals(roi.getState(), state);
        } else
            fail("No callback resource order to check");
    }

    public ResourceOrderDto getResourceOrderFromDb(String id) {
        return a4ResourceOrderOrchestratorClient
                .resourceOrder()
                .getResourceOrder()
                .uuidPath(id)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                .getBody()
                .as(ResourceOrderDto.class);
    }

    public List<ResourceOrderMainDataDto> getResourceOrderListByVuepFromDb(String vuep) {
        return a4ResourceOrderOrchestratorClient
                .resourceOrder()
                .listResourceOrders()
                .vuepPublicReferenceNrQuery(vuep)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    public void getResourceOrderFromDbAndCheckIfCompleted(String id) {
        ResourceOrderDto ro = getResourceOrderFromDb(id);

        assertEquals(ResourceOrderStateType.COMPLETED.toString(), ro.getState());
        if (ro.getOrderItem() != null && !ro.getOrderItem().isEmpty())
            assertEquals(ro.getOrderItem().get(0).getState(), ResourceOrderItemStateType.COMPLETED.toString());
    }


    @Step("Delete A4 test data recursively by provided RO (item, characteristics etc)")
    public void deleteA4TestDataRecursively(ResourceOrder ro) {
        if (ro != null)
            deleteA4TestDataRecursively(ro.getId());
    }


    @Step("Delete existing Resource Order from A4 resource order")
    public void deleteResourceOrder(String uuid) {
        a4ResourceOrderOrchestratorClient
                .resourceOrder()
                .deleteResourceOrder()
                .uuidPath(uuid)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NO_CONTENT_204)));

    }

    private void deleteA4TestDataRecursively(String roUuid) {
        deleteResourceOrder(roUuid); // no further instructions needed because of the cascaded data structure
    }
}
