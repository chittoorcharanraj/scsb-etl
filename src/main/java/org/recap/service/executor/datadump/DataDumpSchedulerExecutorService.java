package org.recap.service.executor.datadump;

import org.apache.commons.lang3.StringUtils;
import org.recap.ScsbConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.DataExportValidateService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.PropertyUtil;
import org.recap.util.datadump.DataDumpUtil;
import org.recap.util.datadump.JobDataParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@Service
public class DataDumpSchedulerExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpSchedulerExecutorService.class);

    @Autowired private DataDumpExportService dataDumpExportService;
    @Autowired private JobDataParameterUtil jobDataParameterUtil;
    @Autowired DataExportValidateService dataExportValidateService;
    @Autowired DataDumpUtil dataDumpUtil;
    @Autowired PropertyUtil propertyUtil;

    /**
     * Gets job data parameter util
     * @return the job data parameter util
     */
    public JobDataParameterUtil getJobDataParameterUtil() {
        return jobDataParameterUtil;
    }

    /**
     * This method initiates the data export (Incremental and Deleted data only) to run in sequence for all the institutions.
     *
     * @param date
     * @param requestingInstitutionCode
     * @param fetchType
     * @return
     */
    public String initiateDataDumpForScheduler(String date, String requestingInstitutionCode, String fetchType) {
        logger.info("Starting sequencial Incremental and Deleted Dump");
        logger.info("Export data dump for {} from {}", requestingInstitutionCode, date);
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setIncrementalSequence(true);
        Map<String, String> requestParameterMap = getJobDataParameterUtil().buildJobRequestParameterMap(ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION);
        if (StringUtils.isBlank(fetchType)) {
            fetchType = requestParameterMap.get(ScsbConstants.FETCH_TYPE);
        }
        dataDumpUtil.setDataDumpRequest(dataDumpRequest, fetchType, requestParameterMap.get(ScsbConstants.INSTITUTION_CODES), date, null, requestParameterMap.get(ScsbConstants.COLLECTION_GROUP_IDS), requestParameterMap.get(ScsbConstants.TRANSMISSION_TYPE), requestingInstitutionCode, getToEmailAddress(requestingInstitutionCode), requestParameterMap.get("outputFormat"),requestParameterMap.get("imsDepositoryCodes"),"Scheduler");
        String responseMessage = dataExportValidateService.validateIncomingRequest(dataDumpRequest);
        if (responseMessage != null) {
            ScsbConstants.EXPORT_SCHEDULER_CALL = false;
            ScsbConstants.EXPORT_DATE_SCHEDULER = "";
            ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION = "";
            return responseMessage;
        }
        responseMessage = dataDumpExportService.startDataDumpProcess(dataDumpRequest);
        return responseMessage;
    }


    /**
     * Gets to email address based on institution.
     * @param requestingInstitutionCode
     * @return
     */
    private String getToEmailAddress(String requestingInstitutionCode) {
        ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(requestingInstitutionCode);
        return ilsConfigProperties.getEmailDataDumpTo();
    }
}
