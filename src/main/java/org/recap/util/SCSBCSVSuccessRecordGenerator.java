package org.recap.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.ScsbConstants;
import org.recap.model.csv.SuccessReportSCSBCSVRecord;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by angelind on 18/8/16.
 */@Slf4j
public class SCSBCSVSuccessRecordGenerator {



    /**
     * Prepare success records csv report for initial data load.
     *
     * @param reportEntity the report entity
     * @return the success report re capcsv record
     */
    public SuccessReportSCSBCSVRecord prepareSuccessReportReCAPCSVRecord(ReportEntity reportEntity) {

        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();

        SuccessReportSCSBCSVRecord successReportSCSBCSVRecord = new SuccessReportSCSBCSVRecord();

        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerName = report.getHeaderName();
            String headerValue = report.getHeaderValue();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(successReportSCSBCSVRecord, headerValue);
                } catch (Exception e) {
                    log.error(ScsbConstants.ERROR,e);
                }
            }
        }
        return successReportSCSBCSVRecord;
    }

    /**
     * Gets setter method for the given name.
     *
     * @param propertyName the property name
     * @return the setter method
     */
    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, SuccessReportSCSBCSVRecord.class));
        } catch (IntrospectionException e) {
            log.error(ScsbConstants.ERROR,e);
        }
        return null;
    }

    /**
     * Gets getter method for the given name.
     *
     * @param propertyName the property name
     * @return the getter method
     */
    public Method getGetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, SuccessReportSCSBCSVRecord.class));
        } catch (IntrospectionException e) {
            log.error(ScsbConstants.ERROR,e);
        }
        return null;
    }

}
