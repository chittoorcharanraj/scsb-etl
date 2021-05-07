package org.recap.report;

import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jparw.ReportEntity;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Created by angelind on 18/8/16.
 */
@Component
public class CSVSuccessReportGenerator extends CommonReportGenerator implements ReportGeneratorInterface {

    /**
     * Returns true if report type is 'Success'.
     *
     * @param reportType the report type
     * @return
     */
    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(ScsbCommonConstants.SUCCESS);
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
     * Returns true if operation type is 'ETL'.
     *
     * @param operationType the operation type
     * @return
     */
    @Override
    public boolean isOperationType(String operationType) {
        return operationType.equalsIgnoreCase(ScsbConstants.OPERATION_TYPE_ETL);
    }

    /**
     * Generates CSV report with success records for initial data load.
     *
     * @param reportEntities the report entities
     * @param fileName       the file name
     * @return the file name
     */
    @Override
    public String generateReport(List<ReportEntity> reportEntities, String fileName) {
        return generateSuccessReport(reportEntities, fileName, ScsbConstants.CSV_SUCCESS_Q);
    }
}
