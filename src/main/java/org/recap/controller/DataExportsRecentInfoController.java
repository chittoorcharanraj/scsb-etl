package org.recap.controller;

import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.model.export.S3RecentDataExportInfo;
import org.recap.model.export.S3RecentDataExportInfoList;
import org.recap.repository.InstitutionDetailsRepository;
import org.recap.service.RecentDataExportsInfoService;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DataExportsRecentInfoController {

    private static final Logger logger = LoggerFactory.getLogger(DataExportsRecentInfoController.class);

    @Autowired
    RecentDataExportsInfoService recentDataExportsInfoService;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    PropertyUtil propertyUtil;

    @GetMapping("/getRecentDataExportsInfo")
    public S3RecentDataExportInfoList getRecentDataExportsInfo() {
        List<S3RecentDataExportInfo> recentDataExportInfoFinalList = new ArrayList<>();
        S3RecentDataExportInfoList s3RecentDataExportInfoList = new S3RecentDataExportInfoList();

        try {
            List<String> allInstitutionCodesExceptSupportInstitution = commonUtil.findAllInstitutionCodesExceptSupportInstitution();
            for (String institution : allInstitutionCodesExceptSupportInstitution) {
                String bibDataFormat = propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_BIBDATA_FORMAT);
                List<S3RecentDataExportInfo> recentDataExportInfoList = recentDataExportsInfoService.generateRecentDataExportsInfo(allInstitutionCodesExceptSupportInstitution, institution, bibDataFormat);
                if (!recentDataExportInfoList.isEmpty()) {
                    recentDataExportInfoFinalList.addAll(recentDataExportInfoList);
                    s3RecentDataExportInfoList.setRecentDataExportInfoList(recentDataExportInfoFinalList);
                }
            }
        } catch (Exception e) {
            logger.error(ScsbCommonConstants.LOG_ERROR, e);
        }
        return s3RecentDataExportInfoList;
    }
}
