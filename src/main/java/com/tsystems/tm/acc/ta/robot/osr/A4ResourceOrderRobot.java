package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.api.AuthTokenProvider;
import com.tsystems.tm.acc.ta.api.RhssoClientFlowAuthTokenProvider;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderClient;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderOrchestratorClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A10nspA4DtoMapper;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A10nspA4Dto;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.helpers.RhssoHelper;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderItemDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderMainDataDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.invoker.ApiClient;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.api.ResponseSpecBuilders.*;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.A4_RESOURCE_INVENTORY_BFF_PROXY_MS;
import static com.tsystems.tm.acc.ta.data.osr.DomainConstants.WIREMOCK_MS_NAME;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.CARRIER_BSA_REFERENCE;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.RAHMEN_VERTRAGS_NR;
import static org.testng.Assert.*;


@Slf4j
public class A4ResourceOrderRobot {

    public static final String CB_PATH = "/test_url";

    private static final AuthTokenProvider getAuthTokenProviderA4ResourceOrder =
            new RhssoClientFlowAuthTokenProvider(WIREMOCK_MS_NAME,
                    RhssoHelper.getSecretOfGigabitHub(WIREMOCK_MS_NAME));

    private static final AuthTokenProvider authTokenProviderOrchestrator =
            new RhssoClientFlowAuthTokenProvider(A4_RESOURCE_INVENTORY_BFF_PROXY_MS,
                    RhssoHelper.getSecretOfGigabitHub(A4_RESOURCE_INVENTORY_BFF_PROXY_MS));

    private final ApiClient a4ResourceOrder =
            new A4ResourceOrderClient(getAuthTokenProviderA4ResourceOrder).getClient();

    private final com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator
            .client.invoker.ApiClient a4ResourceOrderOrchestratorClient =
            new A4ResourceOrderOrchestratorClient(authTokenProviderOrchestrator).getClient();

    private final A4ResourceOrderMapper resourceOrderMapper = new A4ResourceOrderMapper();
    private final A10nspA4DtoMapper a10Mapper = new A10nspA4DtoMapper();

    @Step("Send POST for A10nsp Resource Order")
    public String sendPostResourceOrder(ResourceOrder resourceOrder) {
        return a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .body(resourceOrder)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_CREATED_201))).getBody().asString();
    }

    @Step("Send POST for A10nsp Resource Order")
    public Response sendPostResourceOrderWithoutChecks(ResourceOrder resourceOrder) {
        return a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .body(resourceOrder)
                .execute(voidCheck());
    }

    @Step("Send POST for A10nsp Resource Order - Error")
    public void sendPostResourceOrderError400(ResourceOrder resourceOrder) {

        a4ResourceOrder
                .resourceOrder()
                .createResourceOrder()
                .body(resourceOrder)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_BAD_REQUEST_400)));
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

    public ResourceOrderItemDto getResourceOrderItemByOrderItemDtoId(String orderItemId, List<ResourceOrderItemDto> roiList) {
        if (roiList != null) {
            for (ResourceOrderItemDto resourceOrderItem : roiList) {
                if (Objects.equals(resourceOrderItem.getId(), orderItemId))
                    return resourceOrderItem;
            }
        }
        return null;
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

    public ResourceOrderDto getResourceOrderFromDb(String id) {
        return a4ResourceOrderOrchestratorClient
                .resourceOrder()
                .getResourceOrder()
                .uuidPath(id)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_OK_200)))
                .getBody()
                .as(ResourceOrderDto.class);
    }

    public void checkResourceOrderDoesntExist(String id) {
        a4ResourceOrderOrchestratorClient
                .resourceOrder()
                .getResourceOrder()
                .uuidPath(id)
                .execute(validatedWith(shouldBeCode(HTTP_CODE_NOT_FOUND_404)));
    }

    public List<ResourceOrderMainDataDto> getResourceOrdersFromDb() {
        return a4ResourceOrderOrchestratorClient
                .resourceOrder()
                .listResourceOrders()
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    public List<ResourceOrderMainDataDto> getResourceOrderListByVuepFromDb(String vuep) {
        return a4ResourceOrderOrchestratorClient
                .resourceOrder()
                .listResourceOrders()
                .vuepPublicReferenceNrQuery(vuep)
                .executeAs(validatedWith(shouldBeCode(HTTP_CODE_OK_200)));
    }

    public void getResourceOrderFromDbAndCheckIfCompleted(ResourceOrder ro) {
        ResourceOrderDto roDb = getResourceOrderFromDb(ro.getId());

        assertEquals(ResourceOrderStateType.COMPLETED.toString(), roDb.getState());
        if (roDb.getOrderItem() != null && !roDb.getOrderItem().isEmpty())
            assertEquals(roDb.getOrderItem().get(0).getState(), ResourceOrderItemStateType.COMPLETED.toString());
    }

    public void getResourceOrdersFromDbAndCheckIfCompleted(ResourceOrder ro, String roUuid) {
        ResourceOrderMainDataDto roDb = searchCorrectRoInDb(ro.getExternalId());
        assertNotNull(roDb); // passende RO gefunden
        assertEquals(roDb.getId(), roUuid);
        ro.setId(roDb.getId()); // damit das Löschen der RO am Ende geht
        assertEquals(roDb.getState(), ResourceOrderStateType.COMPLETED.toString());

        // vollständige RO holen (ResourceOrderDto)
        ResourceOrderDto roDbDto = getResourceOrderFromDb(roDb.getId());
        if (roDbDto.getOrderItem() != null && !roDbDto.getOrderItem().isEmpty())
            assertEquals(roDbDto.getOrderItem().get(0).getState(), ResourceOrderItemStateType.COMPLETED.toString());
    }

    public void getResourceOrdersFromDbAndCheckIfRejected(ResourceOrder ro) {
        ResourceOrderMainDataDto roDb = searchCorrectRoInDb(ro.getExternalId());
        assertNotNull(roDb); // passende RO gefunden
        ro.setId(roDb.getId()); // damit das Löschen der RO am Ende geht
        assertEquals(ResourceOrderStateType.REJECTED.toString(), roDb.getState());
    }

    public void getResourceOrdersFromDbAndCheckIfNotInDb(ResourceOrder ro) {
        ResourceOrderMainDataDto roDb = searchCorrectRoInDb(ro.getExternalId());
        assertNull(roDb); // RO nicht in DB
    }

    public void checkResourceOrderItemHasCorrectState(String roId, String orderItemId, ResourceOrderItemStateType state) {
        ResourceOrderDto roDbDto = getResourceOrderFromDb(roId);

        if (roDbDto != null) {
            ResourceOrderItemDto roi = getResourceOrderItemByOrderItemDtoId(orderItemId, roDbDto.getOrderItem());
            assertEquals(roi.getState(), state.toString());
        } else
            fail("No callback resource order to check");
    }

    public ResourceOrderMainDataDto searchCorrectRoInDb(String externalId) {
        List<ResourceOrderMainDataDto> roDbList = getResourceOrdersFromDb();
        ResourceOrderMainDataDto roDb = null;
        for (ResourceOrderMainDataDto mainDataDto : roDbList) {
            if (Objects.equals(mainDataDto.getExternalId(), externalId))
                roDb = mainDataDto;
        }
        return roDb;
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

    public A10nspA4Dto getA10NspA4Dto(ResourceOrder ro) {
        ResourceOrderItem roi = Objects.requireNonNull(ro.getOrderItem()).get(0);
        String rvNumber = (String) getCharacteristic(RAHMEN_VERTRAGS_NR, roi).getValue();
        String cBsaRef = (String) getCharacteristic(CARRIER_BSA_REFERENCE, roi).getValue();

        return a10Mapper.getA10nspA4Dto(cBsaRef, rvNumber);
    }

    public void deleteA4TestDataRecursively(String roUuid) {
        deleteResourceOrder(roUuid); // no further instructions needed because of the cascaded data structure
    }
}
