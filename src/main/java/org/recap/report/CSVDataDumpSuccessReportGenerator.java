package org.recap.report;

import org.apache.commons.io.FilenameUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.csv.DataDumpSuccessReport;
import org.recap.model.jparw.ReportEntity;
import org.recap.util.datadump.DataDumpSuccessReportGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by premkb on 29/9/16.
 */
@Component
public class CSVDataDumpSuccessReportGenerator extends CommonReportGenerator  implements ReportGeneratorInterface {

    /**
     * Returns true if report type is 'BatchExportSuccess'.
     *
     * @param reportType the report type
     * @return
     */
    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(ScsbConstants.BATCH_EXPORT_SUCCESS);
    }

    /**
     * Returns true if transmission type is 'FileSystem'.
     *
     * @param transmissionType the transmission type
     * @return
     */
    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(ScsbCommonConstants.FILE_SYSTEM);
    }

    /**
     * Returns true if operation type is 'BatchExport'.
     *
     * @param operationType the operation type
     * @return
     */
    @Override
    public boolean isOperationType(String operationType) {
        return operationType.equalsIgnoreCase(ScsbConstants.BATCH_EXPORT);
    }

    /**
     * Generates CSV report with success records for data dump.
     *
     * @param reportEntities the report entities
     * @param fileName       the file name
     * @return the file name
     */
    @Override
    public String generateReport(List<ReportEntity> reportEntities, String fileName) {
        if(!CollectionUtils.isEmpty(reportEntities)) {
            DataDumpSuccessReport dataDumpSuccessReport = new DataDumpSuccessReport();
            List<DataDumpSuccessReport> dataDumpSuccessReportList = new ArrayList<>();
            for (ReportEntity reportEntity : reportEntities) {
                DataDumpSuccessReport dataDumpSuccessReport1 = new DataDumpSuccessReportGenerator().prepareDataDumpCSVSuccessRecord(reportEntity);
                dataDumpSuccessReportList.add(dataDumpSuccessReport1);
            }
            ReportEntity reportEntity = reportEntities.get(0);
            dataDumpSuccessReport.setReportType(reportEntity.getType());
            dataDumpSuccessReport.setInstitutionName(reportEntity.getInstitutionName());
            dataDumpSuccessReport.setFileName(fileName);
            dataDumpSuccessReport.setDataDumpSuccessReportList(dataDumpSuccessReportList);
            producerTemplate.sendBody(ScsbConstants.DATADUMP_SUCCESS_REPORT_CSV_Q, dataDumpSuccessReport);
            DateFormat df = new SimpleDateFormat(ScsbCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
            return FilenameUtils.removeExtension(dataDumpSuccessReport.getFileName()) + "-" + dataDumpSuccessReport.getReportType() + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }
}
