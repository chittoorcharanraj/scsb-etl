package org.recap.camel.datadump.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Body;
import org.springframework.stereotype.Component;

/**
 * Created by sudhishk on 6/7/17.
 */
@Slf4j
@Component
public class DataExportCompletionStatusActiveMQConsumer {


    public void onCompletionTopicMessage(@Body String body){
        log.info("Topic - Completion Message {}",body);
    }

}
