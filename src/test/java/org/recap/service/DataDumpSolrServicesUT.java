package org.recap.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 19/4/17.
 */

public class DataDumpSolrServicesUT extends BaseTestCaseUT {

    @Mock
    DataDumpSolrService dataDumpSolrService;

    @Mock
    RestTemplate restTemplate;


    @Value("${scsb.solr.doc.url}")
    String solrClientUrl = "http://test/recap/datadump";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(dataDumpSolrService, "solrClientUrl", solrClientUrl);
    }

    @Test
    public void testDataDumpSolrService() {
        HttpHeaders headers = new HttpHeaders();
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        headers.set("api_key", "test");
        HttpEntity<SearchRecordsRequest> requestEntity = new HttpEntity<>(searchRecordsRequest, headers);
        String url = solrClientUrl + "searchService/searchRecords";
        Map map = new HashMap();
        ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(map, HttpStatus.OK);
        Mockito.when(dataDumpSolrService.getSolrClientUrl()).thenReturn(solrClientUrl);
        Mockito.when(dataDumpSolrService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(dataDumpSolrService.getRestTemplate().postForEntity(url, requestEntity, Map.class)).thenReturn(responseEntity);
        try {
            Map response = dataDumpSolrService.getResults(searchRecordsRequest);
            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}