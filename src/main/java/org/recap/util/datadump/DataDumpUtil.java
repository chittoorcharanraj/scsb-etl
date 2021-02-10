package org.recap.util.datadump;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.ETLRequestLogEntity;
import org.recap.model.jpa.ExportStatusEntity;
import org.recap.repository.CollectionGroupDetailsRepository;
import org.recap.repository.ETLRequestLogDetailsRepository;
import org.recap.repository.ExportStatusDetailsRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DataDumpUtil {

    @Autowired
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    ETLRequestLogDetailsRepository etlRequestLogDetailsRepository;

    @Autowired
    ExportStatusDetailsRepository exportStatusDetailsRepository;

    public String getFetchType(String fetchTypeNumber) {
        String fetchType ="";
        switch (fetchTypeNumber) {
            case RecapConstants.DATADUMP_FETCHTYPE_FULL:
                fetchType= RecapConstants.EXPORT_TYPE_FULL;
                break;
            case RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL:
                fetchType= RecapConstants.INCREMENTAL;
                break;
            case RecapConstants.DATADUMP_FETCHTYPE_DELETED:
                fetchType= RecapConstants.DELETED;
                break;
            default:
                fetchType= "Export";
        }
        return fetchType;
    }

    public String getOutputformat(String outputFileFormat) {
        String format ="";
        switch (outputFileFormat) {
            case RecapConstants.DATADUMP_XML_FORMAT_MARC:
                format= RecapConstants.MARC;
                break;
            case RecapConstants.DATADUMP_XML_FORMAT_SCSB:
                format= RecapConstants.SCSB;
                break;
            case RecapConstants.DATADUMP_DELETED_JSON_FORMAT:
                format= RecapConstants.JSON;
                break;
        }
        return format;
    }

    public String getTransmissionType(String transmissionType) {
        String type ="";
        switch (transmissionType) {
            case RecapConstants.DATADUMP_TRANSMISSION_TYPE_FTP:
                type= "FTP";
                break;
            case RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP:
                type= "HTTP";
                break;
            case RecapConstants.DATADUMP_TRANSMISSION_TYPE_FILESYSTEM:
                type= "Filesystem";
                break;
        }
        return type;
    }

    @Transactional
    public void saveETlRequestToDB(DataDumpRequest dataDumpRequest,String status) {
        ETLRequestLogEntity etlRequestLogEntity =new ETLRequestLogEntity();
        etlRequestLogEntity.setImsRepositoryCodes(dataDumpRequest.getImsDepositoryCodes()!=null?String.join(",",dataDumpRequest.getImsDepositoryCodes()): RecapConstants.IMS_DEPOSITORY_RECAP);
        String collectionGroupIds = dataDumpRequest.getCollectionGroupIds().stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        String defaultCgds = Arrays.asList(RecapConstants.DATADUMP_CGD_SHARED, RecapConstants.DATADUMP_CGD_OPEN).stream().map(String::valueOf).collect(Collectors.joining(","));
        etlRequestLogEntity.setCollectionGroupIds(collectionGroupIds!=null?collectionGroupIds:defaultCgds );
        etlRequestLogEntity.setEmailIds(dataDumpRequest.getToEmailAddress());
        etlRequestLogEntity.setRequestedTime(new Date());
        etlRequestLogEntity.setFetchType(dataDumpRequest.getFetchType());
        etlRequestLogEntity.setOutputFormat(dataDumpRequest.getOutputFileFormat());
        etlRequestLogEntity.setRequestingInstCode(dataDumpRequest.getRequestingInstitutionCode());
        etlRequestLogEntity.setInstCodeToExport(String.join(",",dataDumpRequest.getInstitutionCodes()));
        etlRequestLogEntity.setTransmissionType(dataDumpRequest.getTransmissionType()!=null? dataDumpRequest.getTransmissionType() : "0");
        ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(status);
        etlRequestLogEntity.setEtlStatusId(exportStatusEntity.getId());
        etlRequestLogEntity.setExportStatusEntity(exportStatusEntity);
        etlRequestLogEntity.setUserName(dataDumpRequest.getUserName()!=null?dataDumpRequest.getUserName():RecapConstants.SWAGGER);
        etlRequestLogEntity.setProvidedDate(dataDumpRequest.getDate()!=null?DateUtil.getDateFromString(dataDumpRequest.getDate(),RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM):null);
        ETLRequestLogEntity savedEtlRequestLog = etlRequestLogDetailsRepository.saveAndFlush(etlRequestLogEntity);
        dataDumpRequest.setEtlRequestId(savedEtlRequestLog.getId());
    }

    @Transactional
    public void updateStatusInETLRequestLog(DataDumpRequest dataDumpRequest, String outputString) {
        Optional<ETLRequestLogEntity> etlRequestLogEntity = etlRequestLogDetailsRepository.findById(dataDumpRequest.getEtlRequestId());
        etlRequestLogEntity.ifPresent(exportLog ->{
            if(outputString.contains(RecapConstants.DATADUMP_EXPORT_FAILURE)){
                ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(RecapConstants.INVALID);
                exportLog.setEtlStatusId(exportStatusEntity.getId());
                exportLog.setExportStatusEntity(exportStatusEntity);
                exportLog.setMessage(outputString);
            }
            else{
                ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(outputString);
                exportLog.setEtlStatusId(exportStatusEntity.getId());
                exportLog.setExportStatusEntity(exportStatusEntity);
            }
            exportLog.setCompleteTime(new Date());
            etlRequestLogDetailsRepository.saveAndFlush(exportLog);
        });
    }

    public boolean checkIfExportProcessIsRunning(DataDumpRequest dataDumpRequest) {
        ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(RecapConstants.IN_PROGRESS);
        List<ETLRequestLogEntity> etlRequestLogEntities = etlRequestLogDetailsRepository.findAllByEtlStatusId(exportStatusEntity.getId());
        return etlRequestLogEntities.isEmpty();

    }

    public boolean checkIfAnyExportProcessIsAwaiting(DataDumpRequest dataDumpRequest) {
        ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(RecapConstants.AWAITING);
        List<ETLRequestLogEntity> allByStatusOrderByRequestedTime = etlRequestLogDetailsRepository.findAllByEtlStatusIdOrderByRequestedTime(exportStatusEntity.getId());
        return allByStatusOrderByRequestedTime.isEmpty();
    }

    private DataDumpRequest prepareDataDumpReq(ETLRequestLogEntity etlRequestLogEntity) {
        DataDumpRequest dataDumpRequestForAwaiting=new DataDumpRequest();
        dataDumpRequestForAwaiting.setImsDepositoryCodes(Arrays.asList(etlRequestLogEntity.getImsRepositoryCodes()));
        dataDumpRequestForAwaiting.setFetchType(etlRequestLogEntity.getFetchType());
        dataDumpRequestForAwaiting.setOutputFileFormat(etlRequestLogEntity.getOutputFormat());
        dataDumpRequestForAwaiting.setTransmissionType(etlRequestLogEntity.getTransmissionType());
        List<Integer> collectionGroupIds = Arrays.stream(etlRequestLogEntity.getCollectionGroupIds()
                .split(",")).map(Integer::parseInt)
                .collect(Collectors.toList());
        dataDumpRequestForAwaiting.setCollectionGroupIds(collectionGroupIds);
        dataDumpRequestForAwaiting.setImsDepositoryCodes(Arrays.asList(etlRequestLogEntity.getImsRepositoryCodes()));
        dataDumpRequestForAwaiting.setRequestingInstitutionCode(etlRequestLogEntity.getRequestingInstCode());
        dataDumpRequestForAwaiting.setInstitutionCodes(Arrays.asList(etlRequestLogEntity.getInstCodeToExport()));
        dataDumpRequestForAwaiting.setDate(etlRequestLogEntity.getProvidedDate()!=null?String.valueOf(etlRequestLogEntity.getProvidedDate()):null);
        dataDumpRequestForAwaiting.setDateTimeString(DateUtil.getDateTimeString());
        dataDumpRequestForAwaiting.setRequestFromSwagger(true);
        dataDumpRequestForAwaiting.setEtlRequestId(etlRequestLogEntity.getId());
        dataDumpRequestForAwaiting.setUserName(etlRequestLogEntity.getUserName()!=null?etlRequestLogEntity.getUserName():RecapConstants.SWAGGER);
        return dataDumpRequestForAwaiting;
    }


    public DataDumpRequest prepareRequestForExistinAwaiting() {
        ExportStatusEntity exportStatusEntity = exportStatusDetailsRepository.findByExportStatusCode(RecapConstants.AWAITING);
        List<ETLRequestLogEntity> allByStatusOrderByRequestedTime = etlRequestLogDetailsRepository.findAllByEtlStatusIdOrderByRequestedTime(exportStatusEntity.getId());
        return prepareDataDumpReq(allByStatusOrderByRequestedTime.get(0));
    }
}
