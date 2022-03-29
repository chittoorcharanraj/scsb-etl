package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.camel.RecordProcessor;
import org.recap.model.etl.EtlLoadRequest;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.report.ReportGenerator;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.XmlRecordRepository;
import org.recap.repositoryrw.ReportDetailRepository;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by chenchulakshmig on 14/7/16.
 */

public class EtlDataLoadControllerUT extends BaseTestCaseUT{

    @InjectMocks
    EtlDataLoadController etlDataLoadController;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    XmlRecordRepository xmlRecordRepository;
    @Mock
    ReportDetailRepository reportDetailRepository;
    @Mock
    MultipartFile multipartFile;
    @Mock
    Model model;
    @Mock
    BindingResult bindingResult;
    @Mock
    RecordProcessor recordProcessor;
    @Mock
    CamelContext camelContext;
    @Mock
    ReportGenerator reportGenerator;
    @Mock
    private CommonUtil commonUtil;
    @Value("${etl.report.directory}")
    private String reportDirectory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void uploadFiles() throws Exception {
        assertNotNull(etlDataLoadController);

        URL resource = getClass().getResource("SampleRecord.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);

        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        assertNotNull(multipartFile);

        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFile(multipartFile);

        etlDataLoadController.uploadFiles(etlLoadRequest, bindingResult, model);
    }

    @Test
    public void getInstitution() {
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(Arrays.asList("PUL", "CUL", "NYPL", "HL"));
        List<String> institutionListExceptSupportInstitution = etlDataLoadController.getInstitution();
        assertNotNull(institutionListExceptSupportInstitution);
    }

    @Test
    public void uploadFiles1() throws Exception {
        assertNotNull(etlDataLoadController);
        URL resource = getClass().getResource("SampleRecord.xml");
        assertNotNull(resource);
        File file = new File(resource.toURI());
        assertNotNull(file);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        assertNotNull(multipartFile);
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlDataLoadController.uploadFiles(etlLoadRequest, bindingResult, model);
    }

    @Test
    public void testReports() throws Exception {
        String[] filename = {"test", ""};
        for (String file :
                filename) {
            Mockito.when(reportGenerator.generateReport(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(file);
            String fileName = "test.xml";
            List<ReportDataEntity> reportDataEntities = new ArrayList<>();
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setFileName(fileName);
            reportEntity.setCreatedDate(new Date());
            reportEntity.setType(ScsbCommonConstants.FAILURE);
            reportEntity.setInstitutionName("NYPL");

            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName(ScsbCommonConstants.ITEM_BARCODE);
            reportDataEntity.setHeaderValue("103");
            reportDataEntities.add(reportDataEntity);

            reportEntity.setReportDataEntities(reportDataEntities);

            Calendar cal = Calendar.getInstance();
            Date from = reportEntity.getCreatedDate();
            cal.setTime(from);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            from = cal.getTime();
            Date to = reportEntity.getCreatedDate();
            cal.setTime(to);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            to = cal.getTime();

            EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
            etlLoadRequest.setReportFileName(fileName);
            etlLoadRequest.setReportType(ScsbCommonConstants.FAILURE);
            etlLoadRequest.setDateFrom(from);
            etlLoadRequest.setDateTo(to);
            etlLoadRequest.setTransmissionType(ScsbCommonConstants.FILE_SYSTEM);
            etlLoadRequest.setOwningInstitutionName("NYPL");
            etlLoadRequest.setReportInstitutionName("NYPL");
            etlLoadRequest.setOperationType("ETL");
            String dateString = new SimpleDateFormat(ScsbCommonConstants.DATE_FORMAT_FOR_FILE_NAME).format(new Date());
            String reportFileName = "test" + "-Failure" + "-" + dateString + ".csv";

            etlDataLoadController.generateReport(etlLoadRequest, bindingResult, model);
            Thread.sleep(1000);

            assertNotNull(reportFileName);
        }
    }

    @Test
    public void testEtlLoadRequest() {
        EtlLoadRequest etlLoadRequest = new EtlLoadRequest();
        etlLoadRequest.setFileName("test");
        etlLoadRequest.setFile(multipartFile);
        etlLoadRequest.setUserName("john");
        etlLoadRequest.setReportFileName("test");
        etlLoadRequest.setReportType(ScsbCommonConstants.FAILURE);
        etlLoadRequest.setDateFrom(new Date());
        etlLoadRequest.setDateTo(new Date());
        etlLoadRequest.setTransmissionType(ScsbCommonConstants.FILE_SYSTEM);
        etlLoadRequest.setOwningInstitutionName("NYPL");
        etlLoadRequest.setReportInstitutionName("NYPL");
        etlLoadRequest.setOperationType("ETL");
        etlLoadRequest.setBatchSize(1);
        assertNotNull(etlLoadRequest.getFileName());
        assertNotNull(etlLoadRequest.getBatchSize());
        assertNotNull(etlLoadRequest.getFile());
        assertNotNull(etlLoadRequest.getUserName());
        assertNotNull(etlLoadRequest.getOwningInstitutionName());
        assertNotNull(etlLoadRequest.getReportFileName());
        assertNotNull(etlLoadRequest.getReportType());
        assertNotNull(etlLoadRequest.getOperationType());
        assertNotNull(etlLoadRequest.getTransmissionType());
        assertNotNull(etlLoadRequest.getReportInstitutionName());
        assertNotNull(etlLoadRequest.getDateFrom());
        assertNotNull(etlLoadRequest.getDateTo());
    }

}