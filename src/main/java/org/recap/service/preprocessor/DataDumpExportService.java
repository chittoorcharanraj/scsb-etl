package org.recap.service.preprocessor;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.email.datadump.DataDumpEmailService;
import org.recap.service.executor.datadump.DataDumpExecutorService;
import org.recap.util.datadump.DataDumpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by premkb on 27/9/16.
 */
@Service
public class DataDumpExportService {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpExportService.class);

    @Value("${etl.data.dump.status.file.name}") private String dataDumpStatusFileName;

    @Autowired private DataDumpExecutorService dataDumpExecutorService;
    @Autowired private DataDumpEmailService dataDumpEmailService;
    @Autowired private ConsumerTemplate consumerTemplate;
    @Autowired private ProducerTemplate producerTemplate;
    @Autowired DataDumpUtil dataDumpUtil;

    /**
     * Start the data dump process.
     *
     * @param dataDumpRequest the data dump request
     * @return the response message
     */
    public String startDataDumpProcess(DataDumpRequest dataDumpRequest) {
        String outputString = null;
        String responseMessage = null;
        try {
            new Thread(() -> {
                try {
                    dataDumpExecutorService.generateDataDump(dataDumpRequest);
                } catch (Exception e) {
                    logger.error(RecapConstants.ERROR,e);
                }
            }).start();

            outputString = sendEmailAndGetResponse(dataDumpRequest);
            responseMessage = getResponseMessage(outputString, dataDumpRequest);
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR,e);
            responseMessage = RecapConstants.DATADUMP_EXPORT_FAILURE;
        }
        return responseMessage;
    }

    private String sendEmailAndGetResponse(DataDumpRequest dataDumpRequest) {
        String outputString;
        if(dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP)){
            String message = getMessageFromIsRecordAvailableQ();
            if (message.equals(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS)) {
                outputString = getMessageFromHttpQ();
                dataDumpEmailService.sendEmailNotification(dataDumpRequest);
            } else{
                outputString = message;
            }
        }else{
            outputString = getMessageFromIsRecordAvailableQ();
            sendEmailForS3FetchType(dataDumpRequest, outputString);
        }
        return outputString;
    }

    private void sendEmailForS3FetchType(DataDumpRequest dataDumpRequest, String outputString) {
        if(!outputString.equals(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS)){
            sendEmailForNoDataAvailable(dataDumpRequest);
            if (RecapConstants.EXPORT_SCHEDULER_CALL) {
                producerTemplate.sendBody(RecapConstants.DATA_DUMP_COMPLETION_FROM, dataDumpRequest.getRequestingInstitutionCode());
            }
        }
        else {
            dataDumpEmailService.sendEmailNotification(dataDumpRequest);
        }
    }

    private void sendEmailForNoDataAvailable(DataDumpRequest dataDumpRequest) {
        dataDumpEmailService.sendEmail(dataDumpRequest.getInstitutionCodes(),
                Integer.valueOf(0),
                Integer.valueOf(0),
                dataDumpRequest.getTransmissionType(),
                null,
                dataDumpRequest.getToEmailAddress(),
                RecapConstants.DATADUMP_NO_DATA_AVAILABLE,
                Integer.valueOf(0),
                dataDumpRequest.getFetchType(), dataDumpRequest.getRequestingInstitutionCode(), dataDumpRequest.getImsDepositoryCodes());
    }

    /**
     * Gets the message from HTTP queue for the status of the data dump process.
     * @return
     */
    private String getMessageFromHttpQ(){
        return getMessageFrom(consumerTemplate, RecapConstants.DATADUMP_HTTP_Q);
    }

    /**
     * Gets the message from record available queue to identity if the records are available for data dump processing.
     * @return
     */
    private String getMessageFromIsRecordAvailableQ(){
        return getMessageFrom(consumerTemplate, RecapConstants.DATADUMP_IS_RECORD_AVAILABLE_Q);
    }

  /**
     * Sets the data dump export status to a file.
     */
    private void setDataExportCurrentStatus(){
        File file = new File(dataDumpStatusFileName);
        File parentFile = file.getParentFile();
        try {
            if (file.exists()) {
                String dataDumpStatus = FileUtils.readFileToString(file, Charset.defaultCharset());
                if (dataDumpStatus.contains(RecapConstants.COMPLETED)) {
                    writeStatusToFile(file, RecapConstants.IN_PROGRESS);
                }
            } else {
                parentFile.mkdirs();
                boolean newFile = file.createNewFile();
                if(newFile) {
                    writeStatusToFile(file, RecapConstants.IN_PROGRESS);
                }
            }
        } catch (IOException e) {
            logger.error(RecapConstants.ERROR,e);
            logger.error("Exception while creating or updating the file : " + e.getMessage());
        }
    }

    /**
     * Writes data dump status to a file.
     * @param file
     * @param status
     * @throws IOException
     */
    private void writeStatusToFile(File file, String status) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.append(status);
            fileWriter.flush();
        } catch (IOException e) {
            logger.error(RecapConstants.EXCEPTION, e);
        }
    }

    /**
     * Gets response message for the data dump process.
     * @param outputString
     * @param dataDumpRequest
     * @return
     * @throws Exception
     */
    private String getResponseMessage(String outputString, DataDumpRequest dataDumpRequest) throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        String date = new Date().toString();
        if (dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_S3)) {
            if (outputString.equals(RecapConstants.DATADUMP_RECORDS_AVAILABLE_FOR_PROCESS)||outputString.equals(RecapConstants.DATADUMP_PROCESS_STARTED)) {
                logger.info("Writing to data-dump status file as 'In Progress' on Dump-Type:{} Requesting Inst : {}",dataDumpRequest.getFetchType(),dataDumpRequest.getRequestingInstitutionCode());
                if(!dataDumpRequest.isRequestFromSwagger()){
                    setDataExportCurrentStatus();
                }
                else{
                    dataDumpUtil.updateStatusInETLRequestLog(dataDumpRequest,RecapConstants.IN_PROGRESS);
                }
                    outputString = RecapConstants.DATADUMP_PROCESS_STARTED;
            }
            responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, date);
            return outputString;
        }else if (dataDumpRequest.getTransmissionType().equals(RecapConstants.DATADUMP_TRANSMISSION_TYPE_HTTP) && outputString != null) {
            responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, date);
            if(dataDumpRequest.isRequestFromSwagger()){
            dataDumpUtil.updateStatusInETLRequestLog(dataDumpRequest,outputString.contains("100")?outputString:RecapConstants.COMPLETED);}
            return outputString;
        } else {
            if(dataDumpRequest.isRequestFromSwagger()){
            dataDumpUtil.updateStatusInETLRequestLog(dataDumpRequest,RecapConstants.DATADUMP_EXPORT_FAILURE);}
            responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, date);
            return RecapConstants.DATADUMP_EXPORT_FAILURE;
        }
    }

    public String getMessageFrom(ConsumerTemplate consumerTemplate, String queue)
    {
        String outputString;
        Exchange receive = consumerTemplate.receive(queue);
        Object body = receive.getIn().getBody();
        while (null == body) {
            receive = consumerTemplate.receive(queue);
            body = receive.getIn().getBody();
        }
        outputString = (String) receive.getIn().getBody();
        return outputString;
    }

}
