package org.recap.service;

import org.recap.RecapConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.model.export.DataDumpRequest;
import org.recap.model.jpa.ETLRequestLogEntity;
import org.recap.model.jpa.ExportStatusEntity;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.DataDumpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataExportHelperService {

    private static final Logger logger = LoggerFactory.getLogger(DataExportHelperService.class);

    @Autowired DataDumpUtil dataDumpUtil;
    @Autowired private DynamicRouteBuilder dynamicRouteBuilder;
    @Autowired private DataExportDBService dataExportDBService;
    @Autowired private DataDumpExportService dataDumpExportService;

    public String checkForExistingRequestAndStart(DataDumpRequest dataDumpRequest) {
        if (dataDumpRequest.getTransmissionType().equalsIgnoreCase(RecapConstants.DATADUMP_TRANSMISSION_TYPE_S3) && checkIfAnyExportIsInProgress()) {
            saveRequestToDB(dataDumpRequest,RecapConstants.AWAITING);
            return RecapConstants.EXPORT_MESSAGE;
        }
        else if(dataDumpRequest.getTransmissionType().equalsIgnoreCase(RecapConstants.DATADUMP_TRANSMISSION_TYPE_S3) && checkIfAnyExportIsAwaiting()){
            saveRequestToDB(dataDumpRequest,RecapConstants.AWAITING);
            dynamicRouteBuilder.addDataDumpExportRoutes();
            dataDumpExportService.startDataDumpProcess(dataDumpUtil.prepareRequestForExistingAwaiting());
            return RecapConstants.EXPORT_MESSAGE;
        }
        else{
            saveRequestToDB(dataDumpRequest,RecapConstants.INITIATED);
            dynamicRouteBuilder.addDataDumpExportRoutes();
            return dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        }
    }

    private void saveRequestToDB(DataDumpRequest dataDumpRequest,String status) {
        ETLRequestLogEntity savedETLRequestLogEntity = dataExportDBService.saveETLRequestToDB(dataDumpUtil.prepareRequestForAwaiting(dataDumpRequest,status));
        dataDumpRequest.setEtlRequestId(savedETLRequestLogEntity.getId());
        logger.info("ETL Request ID - created : {}", savedETLRequestLogEntity.getId());
    }

    public boolean checkIfAnyExportIsInProgress() {
        ExportStatusEntity exportStatusEntity = dataExportDBService.findByExportStatusCode(RecapConstants.IN_PROGRESS);
        List<ETLRequestLogEntity> etlRequestLogEntityList = dataExportDBService.findByExportStatusIdAndTransmissionType(exportStatusEntity.getId(),RecapConstants.DATADUMP_TRANSMISSION_TYPE_S3);
        return  !etlRequestLogEntityList.isEmpty();
    }

    public boolean checkIfAnyExportIsAwaiting() {
        ExportStatusEntity exportStatusEntity = dataExportDBService.findByExportStatusCode(RecapConstants.AWAITING);
        List<ETLRequestLogEntity> allStatusOrderByRequestedTime = dataExportDBService.findByExportStatusIdAndTransmissionType(exportStatusEntity.getId(),RecapConstants.DATADUMP_TRANSMISSION_TYPE_S3);
        return !allStatusOrderByRequestedTime.isEmpty();
    }

}
