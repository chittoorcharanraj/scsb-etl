package org.recap.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.ScsbConstants;
import org.recap.model.csv.FailureReportSCSBCSVRecord;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SheikS on 8/8/2016.
 */
@Slf4j
public class SCSBCSVFailureRecordGenerator {



    /**
     * Prepare failure records csv report for initial data load.
     *
     * @param reportEntity the report entity
     * @return the failure report re capcsv record
     */
    public FailureReportSCSBCSVRecord prepareFailureReportReCAPCSVRecord(ReportEntity reportEntity) {

        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();

        FailureReportSCSBCSVRecord failureReportSCSBCSVRecord = new FailureReportSCSBCSVRecord();

        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerName = report.getHeaderName();
            String headerValue = report.getHeaderValue();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(failureReportSCSBCSVRecord, headerValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(ScsbConstants.ERROR,e);
                }
            }
        }
        return failureReportSCSBCSVRecord;
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
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, FailureReportSCSBCSVRecord.class));
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
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, FailureReportSCSBCSVRecord.class));
        } catch (IntrospectionException e) {
            log.error(ScsbConstants.ERROR,e);
        }
        return null;
    }
}
