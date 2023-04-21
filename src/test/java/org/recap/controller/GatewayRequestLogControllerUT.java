package org.recap.controller;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.model.jpa.ItemRequestReceivedInformationEntity;
import org.recap.repository.ItemRequestInformationRepository;
import org.recap.service.GatewayRequestLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Charan Raj C created on 30/03/23
 */
public class GatewayRequestLogControllerUT extends BaseTestCaseUT {

    @InjectMocks
    GatewayRequestLogController gatewayRequestLogController;

    @Value("${" + ScsbConstants.GATEWAY_REQUESTS_EMAIL_TO + "}")
    String emailRequestsLogFailed;

    @Value("${" + PropertyKeyConstants.REQUEST_PENDING_LIMIT + "}")
    Integer MAX_RECORDS_COUNT;

    @Value("${" + ScsbConstants.GATEWAY_REQUESTS_LOG_FREQUENCY_CHECK_IN_SEC + "}")
    Integer GATEWAY_REQUEST_LOG_FREQUENCY_CHECK_IN_SEC;

    @Mock
    ProducerTemplate producer;

    @Mock
    GatewayRequestLogServiceImpl gatewayRequestLogServiceImpl;

    @Mock
    ItemRequestInformationRepository itemRequestInformationRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRequestsLogEmailNotification() {
        ReflectionTestUtils.setField(gatewayRequestLogController, "MAX_RECORDS_COUNT", 10);
        ReflectionTestUtils.setField(gatewayRequestLogController, "GATEWAY_REQUEST_LOG_FREQUENCY_CHECK_IN_SEC", 60);
        ReflectionTestUtils.setField(gatewayRequestLogController, "emailRequestsLogFailed", "test@gmail.com");
        Date date = new Date();
        ResponseEntity<String> response = gatewayRequestLogController.requestsLogEmailNotification();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gatewayRequestLogServiceImpl, times(0)).updateGatewayRequestLogRequests(date);
    }

    @Test
    public void testRequestsLogEmailNotificationException() {
        ReflectionTestUtils.setField(gatewayRequestLogController, "MAX_RECORDS_COUNT", 10);
        ReflectionTestUtils.setField(gatewayRequestLogController, "GATEWAY_REQUEST_LOG_FREQUENCY_CHECK_IN_SEC", 60);
        ReflectionTestUtils.setField(gatewayRequestLogController, "emailRequestsLogFailed", "test@gmail.com");
        Date date = new Date();
        doNothing().when(gatewayRequestLogServiceImpl).updateGatewayRequestLogRequests(date);
        Optional<List<ItemRequestReceivedInformationEntity>> entityList = getItemRequestReceivedInformationEntityTest();
        Mockito.when(itemRequestInformationRepository.findAllByDateAndStatus(date, ScsbConstants.FAILED));
        ResponseEntity<String> response = gatewayRequestLogController.requestsLogEmailNotification();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gatewayRequestLogServiceImpl, times(0)).updateGatewayRequestLogRequests(date);
    }


    @Test
    public void testSendEmailNotificationWithLessThanMaxRecordsCount()  {

        try {
            ReflectionTestUtils.setField(gatewayRequestLogController, "MAX_RECORDS_COUNT", 10);
            List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                entityList.add(new ItemRequestReceivedInformationEntity());
            }
            ReflectionTestUtils.invokeMethod(gatewayRequestLogController, "sendEmailNotification", getItemRequestReceivedInformationEntity());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testSendEmailNotificationWithMoreThanMaxRecordsCount() {
        ReflectionTestUtils.setField(gatewayRequestLogController, "MAX_RECORDS_COUNT", 10);
        List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entityList.add(new ItemRequestReceivedInformationEntity());
        }
        ReflectionTestUtils.invokeMethod(gatewayRequestLogController, "sendEmailNotification", getItemRequestReceivedInformationEntity());
        verify(producer, never()).sendBodyAndHeaders(ArgumentMatchers.anyString(), any(), anyMap());
    }

    private Optional<List<ItemRequestReceivedInformationEntity>> getItemRequestReceivedInformationEntityTest() {
        List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();

        ItemRequestReceivedInformationEntity entity = new ItemRequestReceivedInformationEntity();
        entity.setRequestInstitution("requestInstitution");
        entity.setItemOwningInstitution("itemOwningInstitution");
        entity.setRequestRecieved("requestRecieved");
        entity.setRequestedItemBarcode("requestedItemBarcode");
        entity.setResponseMessage("message");
        entity.setItemOwningInstitution("itemOwningInstitution");
        entity.setValidationStatus("available");
        entity.setDate(new Date());
        entity.setStatusId(1);
        entity.setValidationStatus("available");
        entityList.add(entity);

        return Optional.of(entityList);
    }


    private List<ItemRequestReceivedInformationEntity> getItemRequestReceivedInformationEntity() {
        List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
        ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
        itemRequestReceivedInformationEntity.setRequestInstitution("TEST");
        itemRequestReceivedInformationEntity.setItemOwningInstitution("TEST");
        itemRequestReceivedInformationEntity.setRequestedItemBarcode("12445");
        itemRequestReceivedInformationEntity.setDate(new Date());
        itemRequestReceivedInformationEntity.setStatus("FAILED");
        itemRequestReceivedInformationEntity.setId(2);
        itemRequestReceivedInformationEntity.setRequestRecieved("{\"itemBarcodes\":[\"465654\"],\"titleIdentifier\":\"\\u003cPushto poems. 2002-2003\\u003e.      \",\"itemOwningInstitution\":\"TEST\",\"patronBarcode\":\"8765124321\",\"emailAddress\":\"\",\"requestingInstitution\":\"TEST\",\"requestType\":\"RETRIEVAL\",\"deliveryLocation\":\"TEST\",\"requestNotes\":\"\",\"author\":\"\",\"startPage\":\"\",\"endPage\":\"\",\"chapterTitle\":\"\",\"username\":\"dinakartest\",\"issue\":\"\",\"volume\":\"\"}");
        entityList.add(itemRequestReceivedInformationEntity);
        return entityList;
    }
}
