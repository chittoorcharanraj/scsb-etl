package org.recap.service.transmission.datadump;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataDumpFileSystemTranmissionServiceIT extends BaseTestCase {

    @Autowired
    DataDumpFileSystemTranmissionService dataDumpFileSystemTranmissionService;

    @Value("${etl.data.dump.directory}")
    private String dumpDirectoryPath;

    @Autowired
    private CamelContext camelContext;

    String requestingInstitutionCode = "NYPL";

    String dateTimeString = null;


    @Test
    public void transmitDataDump() throws Exception {
        Map<String, String> routeMap = getRouteMap();
        try {
            dataDumpFileSystemTranmissionService.transmitDataDump(routeMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(RecapConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }

    private Map<String, String> getRouteMap() {
        dateTimeString = getDateTimeString();
        Map<String, String> routeMap = new HashMap<>();
        String fileName = RecapConstants.DATA_DUMP_FILE_NAME + requestingInstitutionCode;
        routeMap.put(RecapConstants.FILENAME, fileName);
        routeMap.put(RecapConstants.DATETIME_FOLDER, dateTimeString);
        routeMap.put(RecapConstants.REQUESTING_INST_CODE, requestingInstitutionCode);
        routeMap.put(RecapConstants.FILE_FORMAT, RecapConstants.XML_FILE_FORMAT);
        return routeMap;
    }

}
