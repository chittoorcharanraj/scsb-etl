package org.recap.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FilenameUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.csv.SCSBCSVSuccessRecord;

/**
 * Created by angelind on 18/8/16.
 */
public class FileNameProcessorForSuccessRecord implements Processor {

    /**
     * This method is invoked by route to set the data load report file name, directory name and report type in headers for success data load.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        SCSBCSVSuccessRecord SCSBCSVSuccessRecord = (SCSBCSVSuccessRecord) exchange.getIn().getBody();
        String fileName = FilenameUtils.removeExtension(SCSBCSVSuccessRecord.getReportFileName());
        exchange.getIn().setHeader(ScsbCommonConstants.REPORT_FILE_NAME, fileName);
        exchange.getIn().setHeader(ScsbConstants.REPORT_TYPE, SCSBCSVSuccessRecord.getReportType());
        exchange.getIn().setHeader(ScsbConstants.DIRECTORY_NAME, SCSBCSVSuccessRecord.getInstitutionName());
    }
}
