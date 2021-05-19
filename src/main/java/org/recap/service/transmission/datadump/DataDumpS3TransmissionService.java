package org.recap.service.transmission.datadump;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.processor.aggregate.zipfile.ZipAggregationStrategy;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.model.export.DataDumpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/**
 * Created by premkb on 29/9/16.
 */
@Service
public class DataDumpS3TransmissionService implements DataDumpTransmissionInterface {

    @Value("${" + PropertyKeyConstants.ETL_DATA_DUMP_DIRECTORY + "}")
    private String dumpDirectoryPath;

    /**
     * The s3 data dump remote server.
     */
    @Value("${" + PropertyKeyConstants.S3_DATA_DUMP_DIR + "}")
    String s3DataDumpRemoteServer;

    @Autowired
    private CamelContext camelContext;

    /**
     * Returns true if transmission type is 'FTP' for data dump.
     * @param dataDumpRequest the data dump request
     * @return
     */
    @Override
    public boolean isInterested(DataDumpRequest dataDumpRequest) {
        return dataDumpRequest.getTransmissionType().equals(ScsbConstants.DATADUMP_TRANSMISSION_TYPE_S3);
    }

    /**
     * Transmit data dump file to the specified path after completion.
     * @param routeMap the route map
     * @throws Exception
     */
    @Override
    public void transmitDataDump(Map<String, String> routeMap) throws Exception {
        String requestingInstitutionCode = routeMap.get(ScsbConstants.REQUESTING_INST_CODE);
        String dateTimeFolder = routeMap.get(ScsbConstants.DATETIME_FOLDER);
        String fileName = routeMap.get(ScsbConstants.FILENAME);
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:"+ dumpDirectoryPath + File.separator + requestingInstitutionCode + File.separator + dateTimeFolder + "?antInclude=*.xml")
                        .routeId(ScsbConstants.DATADUMP_ZIPFTP_ROUTE_ID)
                        .aggregate(new ZipAggregationStrategy())
                        .constant(true)
                        .completionFromBatchConsumer()
                        .eagerCheckCompletion()
                        .setHeader(S3Constants.KEY, simple(s3DataDumpRemoteServer + requestingInstitutionCode + "/" + dateTimeFolder + "/" + fileName + ".zip"))
                        .to(ScsbConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                ;
            }
        });
    }
}
