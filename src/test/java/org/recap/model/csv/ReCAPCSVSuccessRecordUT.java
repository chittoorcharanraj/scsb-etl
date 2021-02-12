package org.recap.model.csv;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class ReCAPCSVSuccessRecordUT extends BaseTestCaseUT {

    @Test
    public void testReCAPCSVSuccessRecord() {

        ReCAPCSVSuccessRecord reCAPCSVSuccessRecord = new ReCAPCSVSuccessRecord();

        reCAPCSVSuccessRecord.setSuccessReportReCAPCSVRecordList(Arrays.asList(new SuccessReportReCAPCSVRecord()));
        reCAPCSVSuccessRecord.setFileName("Record");
        reCAPCSVSuccessRecord.setInstitutionName("PUL");
        reCAPCSVSuccessRecord.setReportType("initial");
        reCAPCSVSuccessRecord.setReportFileName("ReportFile");
        reCAPCSVSuccessRecord.setTotalBibHoldingsLoaded(14662);
        reCAPCSVSuccessRecord.setTotalBibItemsLoaded(2677);
        reCAPCSVSuccessRecord.setTotalBibsLoaded(45234);
        reCAPCSVSuccessRecord.setTotalRecordsInFile(3352);
        reCAPCSVSuccessRecord.setTotalItemsLoaded(9473);
        reCAPCSVSuccessRecord.setTotalHoldingsLoaded(7355);

        assertNotNull(reCAPCSVSuccessRecord.getFileName());
        assertNotNull(reCAPCSVSuccessRecord.getInstitutionName());
        assertNotNull(reCAPCSVSuccessRecord.getSuccessReportReCAPCSVRecordList());
        assertNotNull(reCAPCSVSuccessRecord.getReportFileName());
        assertNotNull(reCAPCSVSuccessRecord.getReportType());
        assertNotNull(reCAPCSVSuccessRecord.getTotalRecordsInFile());
        assertNotNull(reCAPCSVSuccessRecord.getTotalBibHoldingsLoaded());
        assertNotNull(reCAPCSVSuccessRecord.getTotalBibItemsLoaded());
        assertNotNull(reCAPCSVSuccessRecord.getTotalBibsLoaded());
        assertNotNull(reCAPCSVSuccessRecord.getTotalItemsLoaded());
        assertNotNull(reCAPCSVSuccessRecord.getTotalHoldingsLoaded());

    }
}
