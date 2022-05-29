package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.recap.ScsbConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.DataExportHelperService;
import org.recap.util.PropertyUtil;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DataExportTriggerController {
    @Value("${datadump.dynamic.trigger}")
    private Boolean isActive;
    @Autowired
    private DataExportHelperService dataExportHelperService;
    @Autowired
    private DataDumpUtil dataDumpUtil;

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
    private ProducerTemplate producer;

    public void dataExportTrigger() {
        if (isActive) {
            if (validateDatadumpTrigger()) {
                DataDumpRequest dataDumpRequest = dataDumpUtil.prepareRequestForExistingAwaiting();
                log.info("data export process started for etlrequest id:" + dataDumpRequest.getEtlRequestId() +
                        " requesting institution:" + dataDumpRequest.getRequestingInstitutionCode() +
                        " institutions:" + dataDumpRequest.getInstitutionCodes() +
                        " transmissionType:" + dataDumpRequest.getTransmissionType() +
                        " fetchType:" + dataDumpRequest.getFetchType() +
                        " imsRepository:" + dataDumpRequest.getImsDepositoryCodes() +
                        " fileFormat:" + dataDumpRequest.getOutputFileFormat() +
                        " date:" + dataDumpRequest.getDateTimeString()
                );
                try {
                    sendEmailForDataDumpTrigger(dataDumpRequest);
                } catch (Exception e) {
                    log.info("exception occurred while sending email for data dump export job trigger");
                }
                dataExportHelperService.checkForExistingRequestAndStart(dataDumpRequest, true);
            }
        }
    }

    private void sendEmailForDataDumpTrigger(DataDumpRequest dataDumpRequest) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setTo(propertyUtil.getILSConfigProperties(dataDumpRequest.getRequestingInstitutionCode()).getEmailDataDumpTo());
        emailPayLoad.setFetchType(dataDumpRequest.getFetchType());
        emailPayLoad.setRequestingInstitution(dataDumpRequest.getRequestingInstitutionCode());
        emailPayLoad.setInstitutionsRequested(dataDumpRequest.getInstitutionCodes());
        emailPayLoad.setCollectionGroupCodes(dataDumpUtil.getCollectionGroupCodes(dataDumpRequest.getCollectionGroupIds()));
        emailPayLoad.setTransmissionType(dataDumpRequest.getTransmissionType());
        emailPayLoad.setOutputFileFormat(dataDumpRequest.getOutputFileFormat());
        emailPayLoad.setImsDepositoryCodes(dataDumpRequest.getImsDepositoryCodes());
        emailPayLoad.setSubject("Data Dump Export Triggered with JOB");
        producer.sendBodyAndHeader(ScsbConstants.EMAIL_Q, emailPayLoad, ScsbConstants.DATADUMP_EMAILBODY_FOR, ScsbConstants.DATADUMP_EXPORT_NOTIFICATION);
    }

    public Boolean validateDatadumpTrigger() {
        int count = 0;
        for (int i = 0; i < 2; i++) {
            if (isDataDumpTriggerValid()) {
                count++;
                try {
                    log.info("validating datadump trigger check:" + (i + 1));
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (count == 2) {
            log.info("validation is successfull, data dump export is triggered.");
            return true;
        } else {
            log.info("data dump in progress or no data dump export requests are present.so,data dump export not triggered");
            return false;
        }
    }

    public Boolean isDataDumpTriggerValid() {
        if (!dataExportHelperService.checkIfAnyExportIsInProgress()) {
            if (dataExportHelperService.checkIfAnyExportIsAwaiting()) {
                DataDumpRequest dataDumpRequest = dataDumpUtil.prepareRequestForExistingAwaiting();
                if (dataDumpRequest != null) {
                    return true;
                }
            }
        }
        return false;
    }

}