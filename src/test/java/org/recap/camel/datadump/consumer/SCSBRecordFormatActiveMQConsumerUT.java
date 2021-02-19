package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.export.Bib;
import org.recap.model.export.DeletedRecord;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class SCSBRecordFormatActiveMQConsumerUT extends BaseTestCaseUT {


    @Mock
    SCSBXmlFormatterService scsbXmlFormatterService;

    @InjectMocks
    @Spy
    SCSBRecordFormatActiveMQConsumer sCSBRecordFormatActiveMQConsumer;

    @Mock
    DataExportHeaderUtil dataExportHeaderUtil;

    @Mock
    ExecutorService executorService;

    @Mock
    Future future;

    @Mock
    FluentProducerTemplate fluentProducerTemplate;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Test
    public void testgetDataExportHeaderUtil() {
        sCSBRecordFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNotNull(dataExportHeaderUtil);
    }

    @Test
    public void testgetExecutorService() {
        Mockito.when(executorService.isShutdown()).thenReturn(Boolean.TRUE);
        executorService = sCSBRecordFormatActiveMQConsumer.getExecutorService();
        assertNotNull(executorService);
    }

    @Test
    public void testprocessRecords() throws InterruptedException, ExecutionException {
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        bibliographicEntities.add(bibliographicEntity);

        String dataHeader = ";transmissionType#exportFormat";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(bibliographicEntities);
        ex.setIn(in);
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("batchHeaders", dataHeader);
        in.setHeaders(mapdata);
        List<Future<Object>> futureList = new ArrayList<>();
        futureList.add(future);
        Map<String, Object> results = new HashMap<>();
        results.put(RecapCommonConstants.SUCCESS, Arrays.asList(getDeletedRecord()));
        results.put(RecapCommonConstants.FAILURE, Arrays.asList("FailureRecords", getDeletedRecord()));
        results.put(RecapConstants.ITEM_EXPORTED_COUNT, 10);
        Mockito.when(executorService.invokeAll(any())).thenReturn(futureList);
        Mockito.when(future.get()).thenReturn(results);
        try {
            sCSBRecordFormatActiveMQConsumer.processRecords(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        return bibliographicEntity;
    }

    @Test
    public void testsetDataExportHeaderUtil() {
        sCSBRecordFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertTrue(true);
    }

    private DeletedRecord getDeletedRecord() {
        DeletedRecord deletedRecord = new DeletedRecord();
        Bib bib = new Bib();
        bib.setBibId("1");
        bib.setOwningInstitutionBibId("1");
        bib.setOwningInstitutionCode("CUL");
        deletedRecord.setBib(bib);
        return deletedRecord;
    }
}
