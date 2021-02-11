package org.recap.model.csv;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class FailureReportReCAPCSVRecordUT extends BaseTestCaseUT {

    @Test
    public void testFailureReportReCAPCSVRecord() {
        FailureReportReCAPCSVRecord failureReportReCAPCSVRecord = new FailureReportReCAPCSVRecord();

        failureReportReCAPCSVRecord.setCollectionGroupDesignation("CGD");
        failureReportReCAPCSVRecord.setCustomerCode("1333");
        failureReportReCAPCSVRecord.setCreateDateItem("test");
        failureReportReCAPCSVRecord.setErrorDescription("Error");
        failureReportReCAPCSVRecord.setExceptionMessage("Exception");
        failureReportReCAPCSVRecord.setItemBarcode("23452");
        failureReportReCAPCSVRecord.setLastUpdatedDateItem(new Date().toString());
        failureReportReCAPCSVRecord.setOwningInstitution("23452");
        failureReportReCAPCSVRecord.setLocalItemId("23452");
        failureReportReCAPCSVRecord.setOwningInstitutionBibId("1");
        failureReportReCAPCSVRecord.setOwningInstitutionHoldingsId("1");
        failureReportReCAPCSVRecord.setTitle("Title");

        assertNotNull(failureReportReCAPCSVRecord.getExceptionMessage());
        assertNotNull(failureReportReCAPCSVRecord.getOwningInstitutionBibId());
        assertNotNull(failureReportReCAPCSVRecord.getCollectionGroupDesignation());
        assertNotNull(failureReportReCAPCSVRecord.getCustomerCode());
        assertNotNull(failureReportReCAPCSVRecord.getCreateDateItem());
        assertNotNull(failureReportReCAPCSVRecord.getErrorDescription());
        assertNotNull(failureReportReCAPCSVRecord.getItemBarcode());
        assertNotNull(failureReportReCAPCSVRecord.getLocalItemId());
        assertNotNull(failureReportReCAPCSVRecord.getOwningInstitutionHoldingsId());
        assertNotNull(failureReportReCAPCSVRecord.getOwningInstitution());
        assertNotNull(failureReportReCAPCSVRecord.getLastUpdatedDateItem());
        assertNotNull(failureReportReCAPCSVRecord.getTitle());
    }
}
