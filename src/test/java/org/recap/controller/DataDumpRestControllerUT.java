package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.controller.swagger.DataDumpRestController;
import org.recap.service.DataDumpSolrService;
import org.recap.service.DataExportHelperService;
import org.recap.service.DataExportValidateService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.DataDumpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by premkb on 19/8/16.
 */

@RunWith(Parameterized.class)
public class DataDumpRestControllerUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpRestControllerUT.class);

    @InjectMocks
    DataDumpRestController mockedDataDumpRestController;

    @Mock
    DataDumpSolrService mockedDataDumpSolrService;

    @Mock
    DataExportValidateService dataExportValidateService;

    @Mock
    DataExportHelperService dataExportHelperService;

    @Value("${scsb.solr.doc.url}")
    String solrClientUrl;
    @Value("${etl.data.dump.status.file.name}")
    String dataDumpStatusFileName;
    @Mock
    RestTemplate mockedRestTemplate;
    @Mock
    DataDumpExportService mockedDataDumpExportService;
    @Mock
    DynamicRouteBuilder dynamicRouteBuilder;
    @Mock
    DataDumpUtil dataDumpUtil;
    private ExecutorService executorService;
    private final String institutionCodes;
    private final String requestingInstitutionCode;
    private final String fetchType;
    private final String outputFormat;
    private final String date;
    private final String toDate;
    private final String collectionGroupIds;
    private final String transmissionType;
    private final String emailToAddress;
    private final String imsDepositoryCodes;
    private final String process;
    private final String request;
    private final String scenario;
    private final String userName;


    public DataDumpRestControllerUT(String institutionCodes, String requestingInstitutionCode, String fetchType, String outputFormat, String date, String toDate, String collectionGroupIds, String transmissionType, String emailToAddress, String imsDepositoryCodes, String process, String request, String scenario,String userName) {
        super();
        this.institutionCodes = institutionCodes;
        this.requestingInstitutionCode = requestingInstitutionCode;
        this.fetchType = fetchType;
        this.outputFormat = outputFormat;
        this.date = date;
        this.toDate = toDate;
        this.collectionGroupIds = collectionGroupIds;
        this.transmissionType = transmissionType;
        this.emailToAddress = emailToAddress;
        this.imsDepositoryCodes = imsDepositoryCodes;
        this.process = process;
        this.request = request;
        this.scenario = scenario;
        this.userName = userName;
    }

    @Parameterized.Parameters(name = "{index} : Scenario - {12}")
    public static Collection input() {
        return Arrays.asList(new Object[][]{
                {"CUL", "NYPL", "0", "0", new Date().toString(), null, "1,2", "1", "test@gmail.com", "RECAP", RecapCommonConstants.SUCCESS, null, "exportIncrementalMarcXmlFormatForHttp"},
                {"CUL", "PUL", "0", "1", new Date().toString(), null, "1,2", "1", "test@gmail.com", "RECAP", null, RecapCommonConstants.SUCCESS, "exportIncrementalSCSBXmlFormatForHttp"},
                {"NYPL", "PUL", "0", "1", new Date().toString(), null, "1,2", "0", "test@gmail.com", "HD", RecapConstants.DATADUMP_PROCESS_STARTED, null, "exportFullDataDumpMarcXmlFormat"},
                {"NYPL", "PUL", "0", "1", new Date().toString(), null, "1,2", "0", "test@gmail.com", "RECAP", RecapConstants.DATADUMP_PROCESS_STARTED, null, "exportFullDataDumpScsbXmlFormat"},
                {"NYPL,PUL", "NYPL", "1", "1", new Date().toString(), null, "1,2", "0", "test@gmail.com", "RECAP", RecapConstants.DATADUMP_PROCESS_STARTED, null, "exportIncrementalDataDump"},
                {"NYPL,PUL", "NYPL", "2", "2", new Date().toString(), null, "1,2", "0", "test@gmail.com", "RECAP", RecapConstants.DATADUMP_PROCESS_STARTED, null, "exportDeletedRecordsDataDump"},
                {"NYPL", "NYPL", "1", "1", new Date().toString(), null, "1,2", "0", "test@gmail.com", "RECAP", null, RecapConstants.DATADUMP_VALID_FETCHTYPE_ERR_MSG, "invalidFetchTypeParameters"},
                {"NYPL", "NYPL", "1", "1", "", null, "1,2", "0", "test@gmail.com", "RECAP", null, RecapConstants.DATADUMP_DATE_ERR_MSG, "invalidIncrementalDumpParameters"}
        });
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExportDataDump() throws Exception {
        Mockito.doNothing().when(dataDumpUtil).setDataDumpRequest(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),any());
        Mockito.when(mockedDataDumpExportService.startDataDumpProcess(Mockito.any())).thenReturn(process);
        Mockito.when(dataExportValidateService.validateIncomingRequest(Mockito.any())).thenReturn(request);
        Mockito.when(dataExportHelperService.checkForExistingRequestAndStart(any())).thenReturn("");
        String response = mockedDataDumpRestController.exportDataDump(institutionCodes, requestingInstitutionCode, fetchType, outputFormat, date, toDate, collectionGroupIds, transmissionType, emailToAddress, imsDepositoryCodes, userName);
        assertNotNull(response);
    }

}
