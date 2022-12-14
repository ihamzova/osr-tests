package com.tsystems.tm.acc.ta.robot.osr;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderClient;
import com.tsystems.tm.acc.ta.api.osr.A4ResourceOrderOrchestratorClient;
import com.tsystems.tm.acc.ta.data.osr.mappers.A10nspA4DtoMapper;
import com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper;
import com.tsystems.tm.acc.ta.data.osr.models.A10nspA4Dto;
import com.tsystems.tm.acc.ta.data.osr.models.A4NetworkElementLink;
import com.tsystems.tm.acc.ta.wiremock.WireMockFactory;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderItemDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.client.model.ResourceOrderMainDataDto;
import com.tsystems.tm.acc.tests.osr.a4.resource.order.orchestrator.tmf652.client.model.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static com.tsystems.tm.acc.ta.data.HttpConstants.*;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.CARRIER_BSA_REFERENCE;
import static com.tsystems.tm.acc.ta.data.osr.mappers.A4ResourceOrderMapper.FRAME_CONTRACT_ID;
import static com.tsystems.tm.acc.ta.robot.utils.MiscUtils.isNullOrEmpty;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.checkStatus;
import static de.telekom.it.magic.api.restassured.ResponseSpecBuilders.voidCheck;
import static org.testng.Assert.*;


@Slf4j
public class A4ResourceOrderRobot {

    public static final String CB_PATH = "/test_url";

    private static final String FIRST_ORDER_ITEM = "-1";
    private final A4ResourceOrderClient internalClient = new A4ResourceOrderClient();
    private final A4ResourceOrderOrchestratorClient externalClient = new A4ResourceOrderOrchestratorClient();
    private final A4ResourceOrderMapper resourceOrderMapper = new A4ResourceOrderMapper();
    private final A10nspA4DtoMapper a10Mapper = new A10nspA4DtoMapper();

    @Step("Send POST for A10nsp Resource Order")
    public String sendPostResourceOrder(ResourceOrder resourceOrder) {
        final String roId = internalClient.getClient()
                .resourceOrder()
                .createResourceOrder()
                .body(resourceOrder)
                .execute(checkStatus(HTTP_CODE_CREATED_201)).getBody().asString();

        log.info("+++ Resource-Order ID: " + roId);
        resourceOrder.setId(roId);
        return roId;
    }

    @Step("Send POST for A10nsp Resource Order")
    public Response sendPostResourceOrderWithoutChecks(ResourceOrder resourceOrder) {
        return internalClient.getClient()
                .resourceOrder()
                .createResourceOrder()
                .body(resourceOrder)
                .execute(voidCheck());
    }

    @Step("Send POST for A10nsp Resource Order - Error")
    public void sendPostResourceOrderError400(ResourceOrder resourceOrder) {
        internalClient.getClient()
                .resourceOrder()
                .createResourceOrder()
                .body(resourceOrder)
                .execute(checkStatus(HTTP_CODE_BAD_REQUEST_400));
    }

    public ResourceOrder buildResourceOrder() {
        return resourceOrderMapper.buildResourceOrder();
    }

    public void addOrderItemAdd(String orderItemId, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, OrderItemActionType.ADD, nelData, ro);
    }

    public void addOrderItem(String orderItemId, OrderItemActionType actionType, A4NetworkElementLink nelData, ResourceOrder ro) {
        addOrderItem(orderItemId, actionType, nelData.getLbz(), ro);
    }

    public void addOrderItem(String orderItemId, OrderItemActionType actionType, String nelLbz, ResourceOrder ro) {
        addOrderItem(orderItemId, actionType, nelLbz, ro, UUID.randomUUID().toString(), "2", "3999");
    }

    public void addOrderItem(String orderItemId, OrderItemActionType actionType, String nelLbz, String carrierBsaReference, ResourceOrder ro) {
        addOrderItem(orderItemId, actionType, nelLbz, ro, carrierBsaReference, "2", "3999");
    }

    public void addOrderItem(String orderItemId, OrderItemActionType actionType, String nelLbz, ResourceOrder ro, String carrierBsaReference, String vlanRangeLower, String vlanRangeUpper) {
        final ResourceRefOrValue resource = new ResourceRefOrValue()
                .name(nelLbz)
                .resourceCharacteristic(resourceOrderMapper.buildResourceCharacteristicList(carrierBsaReference, vlanRangeLower, vlanRangeUpper));

        final ResourceOrderItem orderItem = new ResourceOrderItem()
                .action(actionType)
                .resource(resource)
                .id(orderItemId);

        ro.addOrderItemItem(orderItem);
    }

    public void setResourceName(String name, String orderItemId, ResourceOrder ro) {
        final ResourceOrderItem roi = getResourceOrderItemByOrderItemId(orderItemId, ro);
        Objects.requireNonNull(roi.getResource()).setName(name);
    }

    public void setCharacteristicValue(String characteristicName, Object value, String orderItemId, ResourceOrder ro) {
        final ResourceOrderItem roi = getResourceOrderItemByOrderItemId(orderItemId, ro);
        final Characteristic c = getCharacteristic(characteristicName, roi);
        c.setValue(value);
    }

    public Characteristic getCharacteristic(String name, ResourceOrderItem roi) {
        final List<Characteristic> rcList = Objects.requireNonNull(roi.getResource()).getResourceCharacteristic();

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
        final List<Characteristic> rcList = Objects.requireNonNull(getResourceOrderItemByOrderItemId(orderItemId, ro).getResource()).getResourceCharacteristic();

        if (rcList != null) {
            rcList.removeIf(characteristic -> characteristic.getName().equals(chName));
        }
    }

    public ResourceOrderItem getResourceOrderItemByOrderItemId(String orderItemId, ResourceOrder ro) {
        return Optional.ofNullable(ro.getOrderItem()).map(Collection::stream).orElseGet(Stream::empty)
                .filter(resourceOrderItem -> resourceOrderItem.getId().equals(orderItemId))
                .findFirst()
                .orElse(null);
    }

    public ResourceOrderItemDto getResourceOrderItemByOrderItemDtoId(String orderItemId, List<ResourceOrderItemDto> roiList) {
        return roiList != null ? roiList.stream()
                .filter(x -> !isNullOrEmpty(x.getId()))
                .filter(resourceOrderItem -> resourceOrderItem.getId().equals(orderItemId))
                .findAny().orElse(null) : null;
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
        return externalClient.getClient()
                .resourceOrder()
                .getResourceOrder()
                .uuidPath(id)
                .execute(checkStatus(HTTP_CODE_OK_200))
                .getBody()
                .as(ResourceOrderDto.class);
    }

    public void checkResourceOrderDoesntExist(String id) {
        externalClient.getClient()
                .resourceOrder()
                .getResourceOrder()
                .uuidPath(id)
                .execute(checkStatus(HTTP_CODE_NOT_FOUND_404));
    }

    public List<ResourceOrderMainDataDto> getResourceOrdersFromDb() {
        return externalClient.getClient()
                .resourceOrder()
                .listResourceOrders()
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    public List<ResourceOrderMainDataDto> getResourceOrderListByPublicReferenceIdFromDb(String publicReferenceId) {
        return externalClient.getClient()
                .resourceOrder()
                .listResourceOrders()
                .publicReferenceIdQuery(publicReferenceId)
                .executeAs(checkStatus(HTTP_CODE_OK_200));
    }

    public void checkResourceOrderState(ResourceOrder ro, String roUuid, ResourceOrderStateType checkedState) {
        String roId;
        String roState;
        if (isNullOrEmpty(ro.getId())) {
            final ResourceOrderMainDataDto roDb = searchCorrectRoInDb(ro.getExternalId());
            assertNotNull(roDb);
            roId = roDb.getId();
            roState = roDb.getState();
        } else {
            final ResourceOrderDto roDb = getResourceOrderFromDb(ro.getId());
            assertNotNull(roDb);
            roId = roDb.getId();
            roState = roDb.getState();
        }
        if (!isNullOrEmpty(roUuid)) assertEquals(roId, roUuid);
        assertEquals(roState, checkedState.toString());
    }

    public ResourceOrderItemStateType getRoiType(ResourceOrderStateType stateType) {
        return ResourceOrderItemStateType.valueOf(stateType.name());
    }

    public void getResourceOrdersFromDbAndCheckIfCompleted(ResourceOrder ro) {
        checkResourceOrderState(ro, ro.getId(), ResourceOrderStateType.COMPLETED);
    }

    public void getResourceOrdersFromDbAndCheckIfNotInDb(ResourceOrder ro) {
        final ResourceOrderMainDataDto roDb = searchCorrectRoInDb(ro.getExternalId());
        assertNull(roDb); // RO nicht in DB
    }

    public void checkResourceOrderItemState(String roId, String orderItemId, ResourceOrderItemStateType state) {
        final ResourceOrderDto roDbDto = getResourceOrderFromDb(roId);
        if (roDbDto == null) {
            fail("No resource order found to check");
            return;
        }
        final ResourceOrderItemDto roi = orderItemId.equals(FIRST_ORDER_ITEM) && !isNullOrEmpty(roDbDto.getOrderItem()) ?
                roDbDto.getOrderItem().get(0) :
                getResourceOrderItemByOrderItemDtoId(orderItemId, roDbDto.getOrderItem());
        assertNotNull(roi, "No ResourceOrderItem found");
        assertEquals(roi.getState(), state.toString());
    }

    public ResourceOrderMainDataDto searchCorrectRoInDb(String externalId) {
        return getResourceOrdersFromDb().stream()
                .filter(item -> Objects.equals(item.getExternalId(), externalId))
                .findAny()
                .orElse(null);
    }

    @Step("Delete A4 test data recursively by provided RO (item, characteristics etc)")
    public void deleteA4TestDataRecursively(ResourceOrder ro) {
        if (ro != null && ro.getId() != null)
            deleteA4TestDataRecursively(ro.getId());
    }

    @Step("Delete existing Resource Order from A4 resource order")
    public void deleteResourceOrder(String uuid) {
        if (isNullOrEmpty(uuid)) return;
        final Response r = externalClient.getClient()
                .resourceOrder()
                .deleteResourceOrder()
                .uuidPath(uuid)
                .execute(voidCheck());

        final int httpCode = r.getStatusCode();
        assertTrue(httpCode == 204 || httpCode == 404);
    }

    public A10nspA4Dto getA10NspA4Dto(ResourceOrder ro) {
        final ResourceOrderItem roi = Objects.requireNonNull(ro.getOrderItem()).get(0);
        final String rvNumber = (String) getCharacteristic(FRAME_CONTRACT_ID, roi).getValue();
        final String cBsaRef = (String) getCharacteristic(CARRIER_BSA_REFERENCE, roi).getValue();
        return a10Mapper.getA10nspA4Dto(cBsaRef, rvNumber);
    }

    public void deleteA4TestDataRecursively(String roUuid) {
        deleteResourceOrder(roUuid); // no further instructions needed because of the cascaded data structure
    }

}
