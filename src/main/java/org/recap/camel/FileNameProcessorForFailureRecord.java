package org.recap.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.csv.SCSBCSVFailureRecord;

/**
 * Created by peris on 8/16/16.
 */
public class FileNameProcessorForFailureRecord implements Processor {

    /**
     * This method is invoked by route to set the data load report file name, directory name and report type in headers for failure data load.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        SCSBCSVFailureRecord SCSBCSVFailureRecord = (SCSBCSVFailureRecord) exchange.getIn().getBody();
        String fileName = FilenameUtils.removeExtension(SCSBCSVFailureRecord.getFileName());
        exchange.getIn().setHeader(ScsbCommonConstants.REPORT_FILE_NAME, fileName);
        exchange.getIn().setHeader(ScsbConstants.DIRECTORY_NAME, SCSBCSVFailureRecord.getInstitutionName());
        exchange.getIn().setHeader(ScsbConstants.REPORT_TYPE, SCSBCSVFailureRecord.getReportType());

    }
}
