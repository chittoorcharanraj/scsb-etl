package org.recap.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class RecentDataExportsInfoServiceUT extends BaseTestCaseUT {

    @InjectMocks
    RecentDataExportsInfoService recentDataExportsInfoService;

    @Mock
    AmazonS3 s3client;

    @Mock
    ObjectListing objectListing;

    @Mock
    S3Object s3Object;

    @Mock
    S3ObjectInputStream s3ObjectInputStream;

    private final String scsbBucketName = "testName";

    private final String s3DataExportBasePath = "testExportPath";

    @Before
    public void setup() {
        ReflectionTestUtils.setField(recentDataExportsInfoService, "scsbBucketName", scsbBucketName);
        ReflectionTestUtils.setField(recentDataExportsInfoService, "s3DataExportBasePath", s3DataExportBasePath);
    }


    @Test
    public void generateRecentDataExportsInfo() {
        String institution = "PUL";
        String bibDataFormat = "XML";
        S3ObjectSummary s3ObjectSummary = getS3ObjectSummary();
        Mockito.when(objectListing.getObjectSummaries()).thenReturn(Arrays.asList(s3ObjectSummary));
        Mockito.when(s3client.listObjects(any(ListObjectsRequest.class))).thenReturn(objectListing);
        Mockito.when(s3client.getObject(anyString(), any())).thenReturn(s3Object);
        Mockito.when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        recentDataExportsInfoService.generateRecentDataExportsInfo(institution, bibDataFormat);
    }

    private S3ObjectSummary getS3ObjectSummary() {
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setKey("t/es/t,Ke/y.csv");
        s3ObjectSummary.setSize(10);
        s3ObjectSummary.setLastModified(new Date());
        return s3ObjectSummary;
    }
}
