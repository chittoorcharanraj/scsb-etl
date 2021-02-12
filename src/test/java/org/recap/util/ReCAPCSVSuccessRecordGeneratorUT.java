package org.recap.util;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.csv.SuccessReportReCAPCSVRecord;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ReCAPCSVSuccessRecordGeneratorUT extends BaseTestCaseUT {

    @InjectMocks
    ReCAPCSVSuccessRecordGenerator reCAPCSVSuccessRecordGenerator;

    @Test
    public void prepareFailureReportReCAPCSVRecordInnerException() {
        ReportEntity reportEntity = getReportEntity();
        SuccessReportReCAPCSVRecord successReportReCAPCSVRecord = reCAPCSVSuccessRecordGenerator.prepareSuccessReportReCAPCSVRecord(reportEntity);
        assertNotNull(successReportReCAPCSVRecord);
    }

    @Test
    public void getGetterMethod() {
        Method method = reCAPCSVSuccessRecordGenerator.getGetterMethod(RecapConstants.HEADER_FETCH_TYPE);
        assertNull(method);
    }

    private ReportEntity getReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapConstants.HEADER_FETCH_TYPE);
        reportDataEntity.setHeaderValue(RecapConstants.DATADUMP_FETCHTYPE_INCREMENTAL);
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        reportEntity.setCreatedDate(new Date());
        return reportEntity;
    }
}
