package org.recap.model.csv;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class ReCAPCSVFailureRecordUT extends BaseTestCaseUT {

    @Test
    public void testReCAPCSVFailureRecord() {
        ReCAPCSVFailureRecord reCAPCSVFailureRecord = new ReCAPCSVFailureRecord();

        reCAPCSVFailureRecord.setFailureReportReCAPCSVRecordList(Arrays.asList(new FailureReportReCAPCSVRecord()));
        reCAPCSVFailureRecord.setLocalItemId("23456");
        reCAPCSVFailureRecord.setCollectionGroupDesignation("CGD");
        reCAPCSVFailureRecord.setCustomerCode("23456");
        reCAPCSVFailureRecord.setCreateDateItem(new Date());
        reCAPCSVFailureRecord.setFileName("Record");
        reCAPCSVFailureRecord.setErrorDescription("Error");
        reCAPCSVFailureRecord.setInstitutionName("PUL");
        reCAPCSVFailureRecord.setExceptionMessage("Exception");
        reCAPCSVFailureRecord.setItemBarcode("456823");
        reCAPCSVFailureRecord.setLastUpdatedDateItem(new Date());
        reCAPCSVFailureRecord.setOwningInstitution("PUL");
        reCAPCSVFailureRecord.setOwningInstitutionBibId("1");
        reCAPCSVFailureRecord.setOwningInstitutionHoldingsId("1");
        reCAPCSVFailureRecord.setReportType("initial");
        reCAPCSVFailureRecord.setTitle("Title");

        assertNotNull(reCAPCSVFailureRecord.getExceptionMessage());
        assertNotNull(reCAPCSVFailureRecord.getFailureReportReCAPCSVRecordList());
        assertNotNull(reCAPCSVFailureRecord.getCollectionGroupDesignation());
        assertNotNull(reCAPCSVFailureRecord.getCustomerCode());
        assertNotNull(reCAPCSVFailureRecord.getCreateDateItem());
        assertNotNull(reCAPCSVFailureRecord.getErrorDescription());
        assertNotNull(reCAPCSVFailureRecord.getFileName());
        assertNotNull(reCAPCSVFailureRecord.getCreateDateItem());
        assertNotNull(reCAPCSVFailureRecord.getInstitutionName());
        assertNotNull(reCAPCSVFailureRecord.getItemBarcode());
        assertNotNull(reCAPCSVFailureRecord.getLastUpdatedDateItem());
        assertNotNull(reCAPCSVFailureRecord.getLocalItemId());
        assertNotNull(reCAPCSVFailureRecord.getOwningInstitution());
        assertNotNull(reCAPCSVFailureRecord.getOwningInstitutionBibId());
        assertNotNull(reCAPCSVFailureRecord.getOwningInstitutionHoldingsId());
        assertNotNull(reCAPCSVFailureRecord.getReportType());
        assertNotNull(reCAPCSVFailureRecord.getTitle());

    }
}
