package org.recap.camel.datadump;

import org.apache.camel.*;
import org.apache.camel.impl.*;
import org.apache.camel.support.*;
import org.junit.*;
import org.recap.*;
import org.recap.camel.*;
import org.recap.model.csv.*;

import java.util.*;

import static org.junit.Assert.*;

public class FileNameProcessorForSuccessRecordUT {

    FileNameProcessorForSuccessRecord fileNameProcessorForSuccessRecord;

    @Test
    public void testProcess() {
        SuccessReportSCSBCSVRecord successReportSCSBCSVRecord = new SuccessReportSCSBCSVRecord();
        successReportSCSBCSVRecord.setFileName("test");
        successReportSCSBCSVRecord.setTotalRecordsInFile("test");
        successReportSCSBCSVRecord.setTotalBibsLoaded("10");
        successReportSCSBCSVRecord.setTotalHoldingsLoaded("10");
        successReportSCSBCSVRecord.setTotalBibHoldingsLoaded("10");
        successReportSCSBCSVRecord.setTotalItemsLoaded("10");
        successReportSCSBCSVRecord.setTotalBibItemsLoaded("10");

        SCSBCSVSuccessRecord SCSBCSVSuccessRecord = new SCSBCSVSuccessRecord();
        SCSBCSVSuccessRecord.setFileName("test.xml");
        SCSBCSVSuccessRecord.setInstitutionName("PUL");
        SCSBCSVSuccessRecord.setReportType(ScsbCommonConstants.FAILURE);
        SCSBCSVSuccessRecord.setSuccessReportSCSBCSVRecordList(Arrays.asList(successReportSCSBCSVRecord));
        SCSBCSVSuccessRecord.setReportFileName("test");

        String dataHeader="test";
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        Map<String,Object> mapData= new HashMap<>();
        ex.setProperty("CamelAggregationStrategy",mapData);
        Message in = ex.getIn();
        ex.setMessage(in);
        in.setBody(SCSBCSVSuccessRecord);
        ex.setIn(in);
        Map<String,Object> mapdata = new HashMap<>();
        mapdata.put("CamelFileName",dataHeader);
        in.setHeaders(mapdata);
        fileNameProcessorForSuccessRecord = new FileNameProcessorForSuccessRecord();
        try {
            fileNameProcessorForSuccessRecord.process(ex);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        assertTrue(true);
    }

}
