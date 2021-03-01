package org.recap.util.datadump;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.datadump.consumer.DataExportReportActiveMQConsumer;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Created by peris on 11/11/16.
 */
public class DataExportReportActiveMQConsumerTest {

    @InjectMocks
    DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer;
    @Mock
    ReportDetailRepository mockReportDetailsRepository;
    @Mock
    ProducerTemplate producerTemplate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processNewSuccessReportEntity() throws Exception {
        DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer = new DataExportReportActiveMQConsumer();
        HashMap values = new HashMap();
        values.put(RecapConstants.REQUESTING_INST_CODE, "PUL");
        values.put(RecapConstants.NUM_RECORDS, "12");
        values.put(RecapConstants.NUM_BIBS_EXPORTED, "Num Bibs Exported");
        values.put(RecapConstants.BATCH_EXPORT, "Batch Export");
        values.put(RecapCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");

        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity reportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);

        assertNotNull(reportEntity);


    }

    @Test
    public void getFetchTypeFullDump() {
        String fetchTypeNumber = "10";
        String fetchType = dataExportReportActiveMQConsumer.getFetchType(fetchTypeNumber);
        assertNotNull(fetchType);
        assertEquals("Full Dump", fetchType);
    }

    @Test
    public void getFetchTypeIncremental() {
        String fetchTypeNumber = "1";
        String fetchType = dataExportReportActiveMQConsumer.getFetchType(fetchTypeNumber);
        assertNotNull(fetchType);
        assertEquals("Incremental", fetchType);
    }

    @Test
    public void getFetchTypeDeleted() {
        String fetchTypeNumber = "2";
        String fetchType = dataExportReportActiveMQConsumer.getFetchType(fetchTypeNumber);
        assertNotNull(fetchType);
        assertEquals("Deleted", fetchType);
    }

    @Test
    public void getFetchTypeExport() {
        String fetchTypeNumber = "0";
        String fetchType = dataExportReportActiveMQConsumer.getFetchType(fetchTypeNumber);
        assertNotNull(fetchType);
        assertEquals("Export", fetchType);
    }

    @Test
    public void processExistingSuccessReportEntity() throws Exception {
        DataExportReportActiveMQConsumer dataExportReportActiveMQConsumer = new DataExportReportActiveMQConsumer();
        HashMap values = new HashMap();
        values.put(RecapConstants.REQUESTING_INST_CODE, "PUL");
        values.put(RecapConstants.NUM_RECORDS, "12");
        values.put(RecapConstants.NUM_BIBS_EXPORTED, "NoOfBibsExported");
        values.put(RecapConstants.BATCH_EXPORT, RecapConstants.BATCH_EXPORT_SUCCESS);
        values.put(RecapCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");
        values.put(RecapConstants.ITEM_EXPORTED_COUNT, 1);
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);


        Mockito.when(mockReportDetailsRepository.findByFileNameAndType("PUL-2017-12-12 11", RecapConstants.BATCH_EXPORT_SUCCESS)).thenReturn(Arrays.asList(savedReportEntity));
        values.put(RecapConstants.NUM_RECORDS, "10");
        ReportEntity updatedReportEntity = dataExportReportActiveMQConsumer.saveSuccessReportEntity(values);
        assertNotNull(updatedReportEntity);
        List<ReportDataEntity> updatedReportDataEntities = updatedReportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = updatedReportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity reportDataEntity = iterator.next();
            if (reportDataEntity.getHeaderName().equals("NoOfBibsExported")) {
                assertEquals("22", reportDataEntity.getHeaderValue());
            }
        }
    }

    @Test
    public void processNewFailureReportEntity() throws Exception {
        HashMap values = new HashMap();
        values.put(RecapConstants.REQUESTING_INST_CODE, "PUL");
        values.put(RecapConstants.FAILED_BIBS, "2");
        values.put(RecapConstants.INSTITUTION_CODES, "PUL");
        values.put(RecapConstants.FETCH_TYPE, "Full");
        values.put(RecapConstants.FAILURE_LIST, Arrays.asList("PUL\\*CUL", "RECALL\\*FAILED"));
        values.put(RecapConstants.FAILURE_CAUSE, "Bad happened");
        values.put(RecapConstants.BATCH_EXPORT, "Batch Export");
        values.put(RecapCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);
        assertNotNull(savedReportEntity);
    }

    @Test
    public void processNewFailureReportEntityWithFileName() throws Exception {
        HashMap values = new HashMap();
        values.put(RecapConstants.REQUESTING_INST_CODE, "PUL");
        values.put(RecapConstants.FAILED_BIBS, "2");
        values.put(RecapConstants.INSTITUTION_CODES, "PUL");
        values.put(RecapConstants.FETCH_TYPE, "Full");
        values.put(RecapConstants.FAILURE_LIST, Arrays.asList("PUL\\*CUL", "RECALL\\*FAILED"));
        values.put(RecapConstants.FAILURE_CAUSE, "Bad happened");
        values.put(RecapConstants.BATCH_EXPORT, "Batch Export");
        values.put(RecapCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");
        values.put(RecapConstants.NUM_RECORDS, "123");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);
        Mockito.when(mockReportDetailsRepository.findByFileNameAndType(any(), anyString())).thenReturn(Arrays.asList(getReportEntity()));
        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);
        assertNotNull(savedReportEntity);
    }

    @Test
    public void processExistingFailureReportEntity() throws Exception {
        HashMap values = new HashMap();
        values.put(RecapConstants.REQUESTING_INST_CODE, "PUL");
        values.put(RecapConstants.INSTITUTION_CODES, "PUL");
        values.put(RecapConstants.FETCH_TYPE, "Incremental");
        values.put(RecapConstants.FAILURE_LIST, Arrays.asList("PUL\\*CUL", "RECALL\\*FAILED"));
        values.put(RecapConstants.FAILED_BIBS, "2");
        values.put(RecapConstants.FAILURE_CAUSE, "Bad happened");
        values.put(RecapConstants.BATCH_EXPORT, "Batch Export");
        values.put(RecapCommonConstants.REQUEST_ID, "PUL-2017-12-12 11");
        dataExportReportActiveMQConsumer.setReportDetailRepository(mockReportDetailsRepository);

        ReportEntity savedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);


        Mockito.when(mockReportDetailsRepository.findByFileNameAndType("PUL-2017-12-12 11", RecapConstants.BATCH_EXPORT_FAILURE)).thenReturn(Arrays.asList(savedReportEntity));
        values.put(RecapConstants.NUM_RECORDS, "3");
        ReportEntity updatedReportEntity = dataExportReportActiveMQConsumer.saveFailureReportEntity(values);
        assertNotNull(updatedReportEntity);
        List<ReportDataEntity> updatedReportDataEntities = updatedReportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = updatedReportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity reportDataEntity = iterator.next();
            if (reportDataEntity.getHeaderName().equals(RecapConstants.FAILED_BIBS)) {
                assertEquals("5", reportDataEntity.getHeaderValue());
            }
        }
    }

    private ReportEntity getReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapConstants.FAILED_BIBS);
        reportDataEntity.setHeaderValue(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }
}