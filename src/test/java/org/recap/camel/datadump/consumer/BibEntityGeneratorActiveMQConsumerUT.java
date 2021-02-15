package org.recap.camel.datadump.consumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.BibliographicDetailsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;

public class BibEntityGeneratorActiveMQConsumerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    BibEntityGeneratorActiveMQConsumer bibEntityGeneratorActiveMQConsumer;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ExecutorService executorService;

    @Mock
    Future future;

    @Test
    public void exportDataDumpForMarcXML() throws Exception {
        try {
            Map results = new HashMap();
            List<HashMap> dataDumpSearchResults = new ArrayList<>();
            HashMap hashMap = new HashMap();
            hashMap.put("bibId", 1);
            hashMap.put("itemIds", Arrays.asList(1));
            dataDumpSearchResults.add(hashMap);
            results.put("dataDumpSearchResults", dataDumpSearchResults);
            Exchange ex = getExchange(results);

            List<Future<Object>> futures= new ArrayList<>();
            futures.add(future);

            List<BibliographicEntity> bibliographicEntityList = new ArrayList<>();
            bibliographicEntityList.add(getBibliographicEntity());
            Mockito.when(future.get())
                    .thenReturn(CompletableFuture.completedFuture(Arrays.asList(getBibliographicEntity())));
            Mockito.when(executorService.invokeAll(any())).thenReturn(futures);
            Mockito.when(bibliographicDetailsRepository.getBibliographicEntityList(Mockito.anyList())).thenReturn(bibliographicEntityList);
            bibEntityGeneratorActiveMQConsumer.processBibEntities(ex);
        }catch (Exception e){}
    }

    @Test
    public void getExecutorServiceShutDown(){
        Mockito.when(executorService.isShutdown()).thenReturn(Boolean.TRUE);
        bibEntityGeneratorActiveMQConsumer.getExecutorService();
    }
    @Test
    public void getExecutorServiceNull(){
        BibEntityGeneratorActiveMQConsumer bibEntityGeneratorActiveMQConsumer = new BibEntityGeneratorActiveMQConsumer(bibliographicDetailsRepository);
        bibEntityGeneratorActiveMQConsumer.getExecutorService();
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity=new BibliographicEntity();
        bibliographicEntity.setId(1);
        return bibliographicEntity;
    }

    private Exchange getExchange(Map results) {
        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("batchHeaders", "Head001");
        ex.getIn().setBody(results);
        ex.setProperty("CamelSplitComplete", true);
        return ex;
    }

}
