package org.recap.util.datadump;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.ETLRequestLogEntity;
import org.recap.model.jpa.ExportStatusEntity;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.repository.ETLRequestLogDetailsRepository;
import org.recap.repository.ExportStatusDetailsRepository;
import org.recap.repository.ImsLocationDetailsRepository;
import org.recap.service.DataExportDBService;
import org.recap.service.preprocessor.DataDumpExportService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

public class DataDumpUtilUT extends BaseTestCaseUT {

    @InjectMocks
    DataDumpUtil dataDumpUtil;

    @Mock
    ExportStatusDetailsRepository exportStatusDetailsRepository;

    @Mock
    ETLRequestLogDetailsRepository etlRequestLogDetailsRepository;

    @Mock
    ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    DataExportDBService dataExportDBService;

    @Mock
    DataDumpExportService dataDumpExportService;

    @Test
    public void setDataDumpRequest() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        String fetchType = "fetch";
        String institutionCodes = "PUL,CUL,NYPL";
        String date = new Date().toString();
        String toDate = new Date().toString();
        String collectionGroupIds = "2451";
        String transmissionType = "FULL";
        String requestingInstitutionCode = "1";
        String toEmailAddress = "test@gmail.com";
        String outputFormat = "xml";
        String imsDepositoryCodes = "2321";
        String userName = "test";
        dataDumpUtil.setDataDumpRequest(dataDumpRequest, fetchType, institutionCodes, date, toDate, collectionGroupIds, transmissionType, requestingInstitutionCode, toEmailAddress, outputFormat, imsDepositoryCodes,userName);
    }

    @Test
    public void setDataDumpRequestWithoutTramission() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        String fetchType = "fetch";
        String institutionCodes = "PUL,CUL,NYPL";
        String date = new Date().toString();
        String toDate = new Date().toString();
        String collectionGroupIds = null;
        String transmissionType = null;
        String requestingInstitutionCode = "1";
        String toEmailAddress = "test@gmail.com";
        String outputFormat = "xml";
        String imsDepositoryCodes = null;
        String userName = "test";
        ImsLocationEntity imsLocationEntity = getImsLocationEntity();
        CollectionGroupEntity collectionGroupEntity = getCollectionGroupEntity(RecapConstants.COLLECTION_GROUP_SHARED,RecapConstants.DATADUMP_CGD_SHARED);
        Mockito.when(imsLocationDetailsRepository.findByImsLocationCode(RecapConstants.IMS_DEPOSITORY_RECAP)).thenReturn(imsLocationEntity);
        Mockito.when(collectionGroupDetailsRepository.findByCollectionGroupCode(any())).thenReturn(collectionGroupEntity);
        dataDumpUtil.setDataDumpRequest(dataDumpRequest, fetchType, institutionCodes, date, toDate, collectionGroupIds, transmissionType, requestingInstitutionCode, toEmailAddress, outputFormat, imsDepositoryCodes,userName);
    }

    @Test
    public void prepareRequestForAwaiting() {
        DataDumpRequest dataDumpRequest = getDataDumpRequest();
        String status = "Complete";
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        Mockito.when(exportStatusDetailsRepository.findByExportStatusCode(any())).thenReturn(exportStatusEntity);
        ETLRequestLogEntity etlRequestLogEntity = dataDumpUtil.prepareRequestForAwaiting(dataDumpRequest, status);
        assertNotNull(etlRequestLogEntity);
    }

    @Test
    public void updateStatusInETLRequestLogForFailure() {
        DataDumpRequest dataDumpRequest = getDataDumpRequest();
        String outputString = RecapConstants.DATADUMP_EXPORT_FAILURE;
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        Mockito.when(etlRequestLogDetailsRepository.findById(dataDumpRequest.getEtlRequestId())).thenReturn(Optional.of(etlRequestLogEntity));
        Mockito.when(exportStatusDetailsRepository.findByExportStatusCode(any())).thenReturn(exportStatusEntity);
        dataDumpUtil.updateStatusInETLRequestLog(dataDumpRequest, outputString);
    }

    @Test
    public void updateStatusInETLRequestLogFor100() {
        DataDumpRequest dataDumpRequest = getDataDumpRequest();
        String outputString = "100";
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        Mockito.when(dataExportDBService.findByExportStatusCode(RecapConstants.AWAITING)).thenReturn(exportStatusEntity);
        Mockito.when(dataExportDBService.findAllStatusById(exportStatusEntity.getId())).thenReturn(Arrays.asList(etlRequestLogEntity));
        Mockito.when(etlRequestLogDetailsRepository.findById(dataDumpRequest.getEtlRequestId())).thenReturn(Optional.of(etlRequestLogEntity));
        Mockito.when(exportStatusDetailsRepository.findByExportStatusCode(any())).thenReturn(exportStatusEntity);
        dataDumpUtil.updateStatusInETLRequestLog(dataDumpRequest, outputString);
    }

    @Test
    public void updateStatusInETLRequestLog() {
        DataDumpRequest dataDumpRequest = getDataDumpRequest();
        String outputString = "test";
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        Mockito.when(etlRequestLogDetailsRepository.findById(dataDumpRequest.getEtlRequestId())).thenReturn(Optional.of(etlRequestLogEntity));
        Mockito.when(exportStatusDetailsRepository.findByExportStatusCode(any())).thenReturn(exportStatusEntity);
        dataDumpUtil.updateStatusInETLRequestLog(dataDumpRequest, outputString);
    }

    @Test
    public void checkAndPrepareAwaitingReqIfAny() {
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        Mockito.when(dataExportDBService.findByExportStatusCode(RecapConstants.AWAITING)).thenReturn(exportStatusEntity);
        Mockito.when(dataExportDBService.findAllStatusById(exportStatusEntity.getId())).thenReturn(Arrays.asList(etlRequestLogEntity));
        DataDumpRequest dataDumpRequest = dataDumpUtil.checkAndPrepareAwaitingReqIfAny();
        assertNotNull(dataDumpRequest);
    }

    @Test
    public void checkAndPrepareAwaitingReqIfAnyWithEmptyLog() {
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        Mockito.when(dataExportDBService.findByExportStatusCode(RecapConstants.AWAITING)).thenReturn(exportStatusEntity);
        Mockito.when(dataExportDBService.findAllStatusById(exportStatusEntity.getId())).thenReturn(Collections.EMPTY_LIST);
        DataDumpRequest dataDumpRequest = dataDumpUtil.checkAndPrepareAwaitingReqIfAny();
        assertNull(dataDumpRequest);
    }

    @Test
    public void prepareRequestForExistingAwaiting() {
        ExportStatusEntity exportStatusEntity = getExportStatusEntity();
        ETLRequestLogEntity etlRequestLogEntity = getEtlRequestLogEntity();
        Mockito.when(dataExportDBService.findByExportStatusCode(RecapConstants.AWAITING)).thenReturn(exportStatusEntity);
        Mockito.when(dataExportDBService.findAllStatusById(exportStatusEntity.getId())).thenReturn(Arrays.asList(etlRequestLogEntity));
        DataDumpRequest dataDumpRequest = dataDumpUtil.prepareRequestForExistingAwaiting();
        assertNotNull(dataDumpRequest);
    }

    @Test
    public void getCollectionGroupCodes(){
        Mockito.when(collectionGroupDetailsRepository.findAllByIds(any())).thenReturn(getCollectionGroupEntityList());
        List<Integer> collectionGroupIds = getCollectionGroupEntityList().stream().map(CollectionGroupEntity::getId).collect(Collectors.toList());
        List<String> collectionGroupCodes = dataDumpUtil.getCollectionGroupCodes(collectionGroupIds);
        assertEquals(Arrays.asList(RecapConstants.COLLECTION_GROUP_SHARED,RecapConstants.COLLECTION_GROUP_OPEN,RecapConstants.COLLECTION_GROUP_PRIVATE),collectionGroupCodes);
    }

    @Test
    public void getOutputformatMarc(){
        String outputFileFormat =RecapConstants.DATADUMP_XML_FORMAT_MARC;
        String format = dataDumpUtil.getOutputformat(outputFileFormat);
        assertEquals(RecapConstants.MARC,format);
    }

    @Test
    public void getOutputformatSCSB(){
        String outputFileFormat =RecapConstants.DATADUMP_XML_FORMAT_SCSB;
        String format = dataDumpUtil.getOutputformat(outputFileFormat);
        assertEquals(RecapConstants.SCSB,format);
    }

    @Test
    public void getOutputformatJSON(){
        String outputFileFormat =RecapConstants.DATADUMP_DELETED_JSON_FORMAT;
        String format = dataDumpUtil.getOutputformat(outputFileFormat);
        assertEquals(RecapConstants.JSON,format);
    }

    @Test
    public void getTransmissionTypeS3(){
        String transmissionType = RecapConstants.DATADUMP_TRANSMISSION_TYPE_S3;
        String type = dataDumpUtil.getTransmissionType(transmissionType);
        assertEquals("FTP",type);
    }

    @Test
    public void getTransmissionTypeHTTP(){
        String transmissionType = RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP;
        String type = dataDumpUtil.getTransmissionType(transmissionType);
        assertEquals("HTTP",type);
    }

    @Test
    public void getTransmissionTypeFS(){
        String transmissionType = RecapConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM;
        String type = dataDumpUtil.getTransmissionType(transmissionType);
        assertEquals("Filesystem",type);
    }

    @Test
    public void getFetchTypeFULL(){
        String fetchTypeNumber = RecapConstants.DATADUMP_FETCHTYPE_FULL;
        String fetchType = dataDumpUtil.getFetchType(fetchTypeNumber);
        assertEquals(RecapConstants.EXPORT_TYPE_FULL,fetchType);
    }

    @Test
    public void getFetchTypeIncremental(){
        String fetchTypeNumber = RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL;
        String fetchType = dataDumpUtil.getFetchType(fetchTypeNumber);
        assertEquals(RecapConstants.INCREMENTAL,fetchType);
    }

    @Test
    public void getFetchTypeDeleted(){
        String fetchTypeNumber = RecapConstants.DATADUMP_FETCHTYPE_DELETED;
        String fetchType = dataDumpUtil.getFetchType(fetchTypeNumber);
        assertEquals(RecapConstants.DELETED,fetchType);
    }

    private ETLRequestLogEntity getEtlRequestLogEntity() {
        ETLRequestLogEntity etlRequestLogEntity = new ETLRequestLogEntity();
        etlRequestLogEntity.setId(1);
        etlRequestLogEntity.setExportStatusEntity(new ExportStatusEntity());
        etlRequestLogEntity.setCompleteTime(new Date());
        etlRequestLogEntity.setCollectionGroupIds("001,002,003");
        etlRequestLogEntity.setEmailIds("emailids");
        etlRequestLogEntity.setRequestedTime(new Date());
        etlRequestLogEntity.setFetchType("Pull");
        etlRequestLogEntity.setExportStatusId(1);
        etlRequestLogEntity.setImsRepositoryCodes("IMRC");
        etlRequestLogEntity.setMessage("msg");
        etlRequestLogEntity.setInstCodeToExport("ECExport");
        etlRequestLogEntity.setOutputFormat("Format");
        etlRequestLogEntity.setRequestingInstCode("22");
        etlRequestLogEntity.setUserName("test");
        etlRequestLogEntity.setTransmissionType("transmission");
        etlRequestLogEntity.setProvidedDate(new Date());
        return etlRequestLogEntity;
    }

    private ExportStatusEntity getExportStatusEntity() {
        ExportStatusEntity exportStatusEntity = new ExportStatusEntity();
        exportStatusEntity.setId(1);
        exportStatusEntity.setExportStatusCode("Complete");
        exportStatusEntity.setExportStatusDesc("Complete");
        return exportStatusEntity;
    }

    private CollectionGroupEntity getCollectionGroupEntity(String collectionGroupCode,Integer collectionGroupId) {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setId(collectionGroupId);
        collectionGroupEntity.setCollectionGroupCode(collectionGroupCode);
        collectionGroupEntity.setCollectionGroupDescription(collectionGroupCode);
        return collectionGroupEntity;
    }

    private List<CollectionGroupEntity> getCollectionGroupEntityList(){
        List<CollectionGroupEntity> collectionGroupEntityList=new ArrayList<>();
        collectionGroupEntityList.add(getCollectionGroupEntity(RecapConstants.COLLECTION_GROUP_SHARED,RecapConstants.DATADUMP_CGD_SHARED));
        collectionGroupEntityList.add(getCollectionGroupEntity(RecapConstants.COLLECTION_GROUP_OPEN,RecapConstants.DATADUMP_CGD_OPEN));
        collectionGroupEntityList.add(getCollectionGroupEntity(RecapConstants.COLLECTION_GROUP_PRIVATE,RecapConstants.DATADUMP_CGD_PRIVATE));
        return collectionGroupEntityList;
    }

    private ImsLocationEntity getImsLocationEntity() {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setId(1);
        imsLocationEntity.setImsLocationCode("HD");
        imsLocationEntity.setImsLocationName("HD");
        return imsLocationEntity;
    }

    private DataDumpRequest getDataDumpRequest() {
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode("NYPL");
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("CUL");
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFileFormat(RecapConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        dataDumpRequest.setEtlRequestId(1);
        return dataDumpRequest;
    }

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
}
