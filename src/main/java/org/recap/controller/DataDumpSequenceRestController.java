package org.recap.controller;

import org.recap.ScsbConstants;
import org.recap.camel.dynamicrouter.DynamicRouteBuilder;
import org.recap.service.executor.datadump.DataDumpSchedulerExecutorService;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by rajeshbabuk on 7/7/17.
 */
@RestController
@RequestMapping("/dataDumpSequence")
public class DataDumpSequenceRestController {

    @Autowired
    private DataDumpSchedulerExecutorService dataDumpSchedulerExecutorService;

    @Autowired
    private DynamicRouteBuilder dynamicRouteBuilder;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * Gets dynamic route builder.
     *
     * @return the dynamic route builder
     */
    public DynamicRouteBuilder getDynamicRouteBuilder() {
        return dynamicRouteBuilder;
    }

    /**
     * API to initiate the data dump export for scheduler to run in sequence.
     *
     * @param date the date
     * @return string
     */
    @GetMapping(value = "/exportDataDumpSequence")
    @ResponseBody
    public String exportDataDump(@RequestParam String date) {
        List<String> allInstitutionCodesExceptSupportInstitution = commonUtil.findAllInstitutionCodesExceptSupportInstitution();
        Optional<String> firstInstitution = allInstitutionCodesExceptSupportInstitution.stream().findFirst();
        ScsbConstants.EXPORT_SCHEDULER_CALL = true;
        ScsbConstants.EXPORT_DATE_SCHEDULER = date;
        ScsbConstants.EXPORT_FETCH_TYPE_INSTITUTION = ScsbConstants.EXPORT_INCREMENTAL+ (firstInstitution.isPresent() ? firstInstitution.get() : "");
        getDynamicRouteBuilder().addDataDumpExportRoutes();
        return dataDumpSchedulerExecutorService.initiateDataDumpForScheduler(date,  (firstInstitution.isPresent() ? firstInstitution.get() : ""), null);
    }
}
