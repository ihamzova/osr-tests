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
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.url.GigabitUrlBuilder;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderMainDataDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.queue.dispatcher.client.model.*;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly;
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

    public static final  String cbPath = "/test_url";
    private final String cbUrl = new GigabitUrlBuilder(WIREMOCK_MS_NAME).buildUri() + cbPath; // Wiremock for merlin MS

    private static final AuthTokenProvider authTokenProviderDispatcher =
            new RhssoClientFlowAuthTokenProvider(DECOUPLING_MS,
                    RhssoHelper.getSecretOfGigabitHub(DECOUPLING_MS)); //this will be merlin's service in the future

    private static final AuthTokenProvider authTokenProviderOrchestrator =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_INVENTORY_BFF_PROXY_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_INVENTORY_BFF_PROXY_MS));


    private final ApiClient a4ResourceOrder = new A4ResourceOrderClient(authTokenProviderDispatcher).getClient();
    private final com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.invoker.ApiClient a4ResourceOrderOrchestratorClient =
            new A4ResourceOrderOrchestratorClient(authTokenProviderOrchestrator).getClient();

    private final A4ResourceOrderMapper resourceOrderMapper = new A4ResourceOrderMapper();

    @Step("Send POST for A10nsp Resource Order")
    public void sendPostResourceOrder(ResourceOrder resourceOrder) {
        final String correlationId = UUID.randomUUID().toString();

        a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .xCallbackCorrelationIdHeader(correlationId)
                .xCallbackUrlHeader(cbUrl)
                .body(resourceOrder)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201)));
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
                                urlPathEqualTo(cbPath)));

        String response = ergList.get(0).getBodyAsString();

        return getResourceOrderObjectFromJsonString(response);
    }

    public void cleanCallbacksInWiremock() {
        List<LoggedRequest> ergList = WireMockFactory.get()
                .retrieve(moreThanOrExactly(0),
                        newRequestPattern(
                                RequestMethod.fromString("POST"),
                                urlPathEqualTo(cbPath)));

        int count = 0; //count to break loop in emergency after 5 tries
        while (!ergList.isEmpty() && count < 5) {
            ergList = WireMockFactory.get()
                    .retrieve(moreThanOrExactly(0),
                            newRequestPattern(
                                    RequestMethod.fromString("POST"),
                                    urlPathEqualTo(cbPath)));
            count++;
        }
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
        if(ro.getOrderItem() != null && !ro.getOrderItem().isEmpty())
            assertEquals(ro.getOrderItem().get(0).getState(), ResourceOrderItemStateType.COMPLETED.toString());
    }


    @Step("Delete A4 test data recursively by provided RO (item, characteristics etc)")
    public void deleteA4TestDataRecursively(ResourceOrder ro) {
        if (ro!=null)
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

    private void deleteA4TestDataRecursively(String roUuid){
        deleteResourceOrder(roUuid); // no further instructions needed because of the cascaded data structure
    }
 }
