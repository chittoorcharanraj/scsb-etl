package org.recap.camel.datadump.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.marc4j.marc.Record;
import org.recap.ScsbConstants;
import org.recap.report.CommonReportGenerator;
import org.recap.service.formatter.datadump.MarcXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by peris on 11/1/16.
 */
@Slf4j
public class MarcXMLFormatActiveMQConsumer extends CommonReportGenerator {


    private MarcXmlFormatterService marcXmlFormatterService;
    private DataExportHeaderUtil dataExportHeaderUtil;

    /**
     * Instantiates a new Marc xml format active mq consumer.
     *
     * @param marcXmlFormatterService the marc xml formatter service
     */
    public MarcXMLFormatActiveMQConsumer(MarcXmlFormatterService marcXmlFormatterService) {
        this.marcXmlFormatterService = marcXmlFormatterService;
    }

    /**
     * This method is invoked by the route to build the marc xml format string with the marc records list for data export.
     *
     * @param exchange the exchange
     * @return the string
     * @throws Exception the exception
     */
    public String processMarcXmlString(Exchange exchange) throws Exception {
        List<Record> records = (List<Record>) exchange.getIn().getBody();
        log.info("Num records to generate marc XMl for: {} " , records.size());
        long startTime = System.currentTimeMillis();

        String toMarcXmlString = null;
        String batchHeaders = (String) exchange.getIn().getHeader(ScsbConstants.BATCH_HEADERS);
        String requestId = getDataExportHeaderUtil().getValueFor(batchHeaders, "requestId");
        try {
            toMarcXmlString = marcXmlFormatterService.covertToMarcXmlString(records);
            processSuccessReportEntity(exchange, records, batchHeaders, requestId);
        } catch (Exception e) {
            log.error(ScsbConstants.ERROR,e);
            processFailureReportEntity(exchange, records, batchHeaders, requestId, e);
        }

        long endTime = System.currentTimeMillis();

        log.info("Time taken to generate marc xml for : {} is : {} seconds " , records.size() , (endTime - startTime) / 1000 );

        return toMarcXmlString;
    }

    /**
     * This method builds a map with the values for success report entity and sends to the route to save the report entity.
     * @param exchange
     * @param records
     * @param batchHeaders
     * @param requestId
     */
    private void processSuccessReportEntity(Exchange exchange, List<Record> records, String batchHeaders, String requestId) {
        HashMap<String, String> values = new HashMap<>();
        values.put(ScsbConstants.REQUESTING_INST_CODE, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.REQUESTING_INST_CODE));
        values.put(ScsbConstants.INSTITUTION_CODES, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.INSTITUTION_CODES));
        values.put(ScsbConstants.FETCH_TYPE, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.FETCH_TYPE));
        values.put(ScsbConstants.COLLECTION_GROUP_IDS, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.COLLECTION_GROUP_IDS));
        values.put(ScsbConstants.TRANSMISSION_TYPE, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.TRANSMISSION_TYPE));
        values.put(ScsbConstants.EXPORT_FROM_DATE, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.EXPORT_FROM_DATE));
        values.put(ScsbConstants.EXPORT_FORMAT, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.EXPORT_FORMAT));
        values.put(ScsbConstants.TO_EMAIL_ID, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.TO_EMAIL_ID));
        values.put(ScsbConstants.NUM_RECORDS, String.valueOf(records.size()));
        values.put(ScsbConstants.IMS_DEPOSITORY, getDataExportHeaderUtil().getValueFor(batchHeaders, ScsbConstants.IMS_DEPOSITORY));
        setValues(values, exchange, requestId);
        FluentProducerTemplate fluentProducerTemplate = generateFluentProducerTemplate(exchange, values, ScsbConstants.DATADUMP_SUCCESS_REPORT_Q);
        fluentProducerTemplate.send();

    }

    /**
     * This method builds a map with the values for failure report entity and sends to the route to save the report entity.
     * @param exchange
     * @param records
     * @param batchHeaders
     * @param requestId
     * @param e
     */
    private void processFailureReportEntity(Exchange exchange, List<Record> records, String batchHeaders, String requestId, Exception e) {
        HashMap<String, String> values = processReport(batchHeaders, requestId, getDataExportHeaderUtil());
        values.put(ScsbConstants.NUM_RECORDS, String.valueOf(records.size()));
        values.put(ScsbConstants.FAILURE_CAUSE, String.valueOf(e.getCause()));

        FluentProducerTemplate fluentProducerTemplate = generateFluentProducerTemplate(exchange, values, ScsbConstants.DATADUMP_FAILURE_REPORT_Q);
        fluentProducerTemplate.send();
    }

    /**
     * Gets data export header util.
     *
     * @return the data export header util
     */
    public DataExportHeaderUtil getDataExportHeaderUtil() {
        if (null == dataExportHeaderUtil) {
            dataExportHeaderUtil = new DataExportHeaderUtil();
        }
        return dataExportHeaderUtil;
    }

    /**
     * Sets data export header util.
     *
     * @param dataExportHeaderUtil the data export header util
     */
    public void setDataExportHeaderUtil(DataExportHeaderUtil dataExportHeaderUtil) {
        this.dataExportHeaderUtil = dataExportHeaderUtil;
    }
}

