package org.recap.camel.datadump;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.ScsbConstants;
import org.recap.model.ILSConfigProperties;
import org.recap.service.executor.datadump.DataDumpSchedulerExecutorService;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@Component
public class DataDumpSequenceProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpSequenceProcessor.class);

    @Autowired
    private DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    PropertyUtil propertyUtil;

    /**
     * This method is invoked by route when data export completion message is available and initiates the other institutions data export in sequence.
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String institution = (String) exchange.getIn().getBody();
        String fetchTypeString = ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION.contains(ScsbConstants.INCREMENTAL) ? ScsbConstants.INCREMENTAL : ScsbConstants.DELETED;
        logger.info("Completed {} export for {} institution", fetchTypeString, institution);

        List<String> allInstitutionCodesExceptSupportInstitution = commonUtil.findAllInstitutionCodesExceptSupportInstitution();
        int i = allInstitutionCodesExceptSupportInstitution.indexOf(institution);
        String nextInstitution =(i<allInstitutionCodesExceptSupportInstitution.size()-1) ? allInstitutionCodesExceptSupportInstitution.get(i + 1) : "";
        ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(institution);

            if (ilsConfigProperties.getEtlIncrementalDump().equalsIgnoreCase(ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION)) {
                ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION = ilsConfigProperties.getEtlDeletedDump();
                dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(ScsbConstants.EXPORT_DATE_SCHEDULER, institution, ScsbConstants.DATADUMP_FETCHTYPE_DELETED);
            } else if(!nextInstitution.isEmpty() && ilsConfigProperties.getEtlDeletedDump().equalsIgnoreCase(ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION) ) {
                ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION = ScsbConstants.EXPORT_INCREMENTAL+nextInstitution;
                dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(ScsbConstants.EXPORT_DATE_SCHEDULER, nextInstitution, null);
            }
            else  {
                ScsbConstants.EXPORT_SCHEDULER_CALL = false;
                ScsbConstants.EXPORT_DATE_SCHEDULER = "";
                ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION = "";
            }
    }
}
