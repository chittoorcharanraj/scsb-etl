package org.recap.controller.swagger;

import org.recap.ScsbConstants;
import org.recap.controller.DataExportTriggerController;
import org.recap.model.export.DataDumpRequest;
import org.recap.service.DataExportHelperService;
import org.recap.service.DataExportValidateService;
import org.recap.service.preprocessor.DataDumpExportService;
import org.recap.util.datadump.DataDumpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by premkb on 19/8/16.
 */
@RestController
@RequestMapping("/dataDump")
public class DataDumpRestController {

    @Autowired private DataDumpExportService dataDumpExportService;
    @Autowired private DataExportValidateService dataExportValidateService;
    @Autowired DataExportHelperService dataExportHelperService;
    @Autowired DataDumpUtil dataDumpUtil;
    @Autowired
    private DataExportTriggerController demoServicePropertiesExample;


    /**
     * API to initiate the data dump export.
     *
     * @param institutionCodes          the institution codes
     * @param requestingInstitutionCode the requesting institution code
     * @param fetchType                 the fetch type
     * @param outputFormat              the output format
     * @param date                      the date
     * @param toDate                    the toDate
     * @param collectionGroupIds        the collection group ids
     * @param transmissionType          the transmission type
     * @param emailToAddress            the email to address
     * @return string
     */

    @GetMapping("/exportDataDump")
    @ResponseBody
    public String exportDataDump(@RequestParam String institutionCodes,
                                         @RequestParam String requestingInstitutionCode,
                                         @RequestParam String imsDepositoryCodes,
                                         @RequestParam String fetchType,
                                         @RequestParam String outputFormat,
                                         @RequestParam(required=false) String date,
                                         @RequestParam(required=false) String toDate,
                                         @RequestParam(required=false) String collectionGroupIds,
                                         @RequestParam(required=false) String transmissionType,
                                         @RequestParam(required=false) String emailToAddress,
                                         @RequestParam(required=false) String userName
    ){
        ScsbConstants.EXPORT_SCHEDULER_CALL = false;
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setRequestFromSwagger(true);
        dataDumpUtil.setDataDumpRequest(dataDumpRequest,fetchType,institutionCodes,date, toDate, collectionGroupIds,transmissionType,requestingInstitutionCode,emailToAddress,outputFormat,imsDepositoryCodes,userName);
        String responseMessage = dataExportValidateService.validateIncomingRequest(dataDumpRequest);
        if(responseMessage !=null) {
            return responseMessage;
        }
        return dataExportHelperService.checkForExistingRequestAndStart(dataDumpRequest,false);
    }

    @GetMapping("/exportDataDumpTriggerManually")
    @ResponseBody
    public String exportDataDumpTrigger(){
        demoServicePropertiesExample.dataExportTrigger();
        return "It will validate and if any request is waiting will start process and will send an email notification upon completion";
    }
}
