package org.recap.camel.datadump.routebuilder;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.camel.datadump.FileNameProcessorForDataExport;
import org.recap.camel.datadump.ZipFileProcessor;
import org.recap.util.datadump.DataExportHeaderUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chenchulakshmig on 10/8/16.
 */
@Slf4j
@Component
public class DataDumpFtpRouteBuilder extends RouteBuilder {



    @Value("${" + PropertyKeyConstants.ETL_DATA_DUMP_FTP_STAGING_DIRECTORY + "}")
    private String s3StagingDir;

    /**
     * The File name processor for data export.
     */
    @Autowired
    FileNameProcessorForDataExport fileNameProcessorForDataExport;

    /**
     * The Zip file processor.
     */
    @Autowired
    ZipFileProcessor zipFileProcessor;

    /**
     * This method is to configure the route to zip the data export file and send it to FTP.
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {
        interceptFrom(ScsbConstants.DATADUMP_ZIPFILE_FTP_Q)
                .process(fileNameProcessorForDataExport);

        from(ScsbConstants.DATADUMP_ZIPFILE_FTP_Q)
                .onCompletion()
                .onWhen(new ExportFileDumpComplete())
                .process(zipFileProcessor)
                .end()
        .to("file:" + s3StagingDir);
    }

    private class ExportFileDumpComplete implements Predicate {

        /**
         * Evaluates the predicate on the message exchange and returns true if this exchange matches the predicate
         * This predicate evaluates the current page count with total pages count to identify if the exporting of data dump to a file is complete.
         *
         * @param exchange
         * @return
         */
        @Override
        public boolean matches(Exchange exchange) {
            String batchHeaders = (String) exchange.getIn().getHeader("batchHeaders");
            String totalPageCount = getValueFor(batchHeaders, "totalPageCount");
            String currentPageCount = getValueFor(batchHeaders, "currentPageCount");
            log.info("currentPageCount in DataDumpFtpRouteBuilder--->{}",currentPageCount);
            return totalPageCount.equals(currentPageCount);
        }

        /**
         * Get the value for the key from headers.
         * @param batchHeaderString
         * @param key
         * @return
         */
        private String getValueFor(String batchHeaderString, String key) {
            return new DataExportHeaderUtil().getValueFor(batchHeaderString, key);
        }
    }
}
