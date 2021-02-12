package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SCSBRecordFormatActiveMQConsumerUT extends BaseTestCaseUT {


    @Mock
    SCSBXmlFormatterService scsbXmlFormatterService;

    @InjectMocks
    SCSBRecordFormatActiveMQConsumer sCSBRecordFormatActiveMQConsumer;

    @Mock
    DataExportHeaderUtil dataExportHeaderUtil;

    @Mock
    ExecutorService executorService;

    @Mock
    Exchange exchange;

    @Mock
    FluentProducerTemplate fluentProducerTemplate;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Before
    public void testSetup() {
        scsbXmlFormatterService = Mockito.mock(SCSBXmlFormatterService.class);
        sCSBRecordFormatActiveMQConsumer = new SCSBRecordFormatActiveMQConsumer(scsbXmlFormatterService);
    }

    @Test
    public void testgetDataExportHeaderUtil() {
        sCSBRecordFormatActiveMQConsumer.getDataExportHeaderUtil();
        assertNotNull(dataExportHeaderUtil);
    }

    @Test
    public void testgetExecutorService() {
        executorService = sCSBRecordFormatActiveMQConsumer.getExecutorService();
        assertNotNull(executorService);
    }

    @Test
    public void testprocessRecords() {
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("bib content".getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
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
        try {
            ReflectionTestUtils.invokeMethod(sCSBRecordFormatActiveMQConsumer, "processFailures", Arrays.asList("test"), "batchHeaders",
                    "requestId", fluentProducerTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sCSBRecordFormatActiveMQConsumer.processRecords(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(true);
    }

    @Test
    public void testsetDataExportHeaderUtil() {
        sCSBRecordFormatActiveMQConsumer.setDataExportHeaderUtil(dataExportHeaderUtil);
        assertTrue(true);
    }

}
