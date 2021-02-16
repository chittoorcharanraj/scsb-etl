package org.recap.camel.datadump.consumer;

import org.apache.camel.Exchange;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.service.formatter.datadump.SCSBXmlFormatterService;
import org.recap.util.datadump.DataExportHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;

public class SCSBRecordFormatActiveMQConsumerIT extends BaseTestCase {

    @Autowired
    SCSBRecordFormatActiveMQConsumer scsbRecordFormatActiveMQConsumer;

    @Autowired
    SCSBXmlFormatterService scsbXmlFormatterService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private DataExportHeaderUtil dataExportHeaderUtil;

    @Autowired
    Exchange exchange;

    @Test
    public void processRecords(){
        try {
            scsbRecordFormatActiveMQConsumer.processRecords(exchange);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
