package org.recap.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by premkb on 20/8/16.
 */
@Slf4j
public class DateUtil {



    private DateUtil() {}

    /**
     * Get date from string date.
     *
     * @param inputDateString the input date string
     * @param dateFormat      the date format
     * @return the date
     */
    public static Date getDateFromString(String inputDateString,String dateFormat){
        Date outputDate=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            if(inputDateString != null) {
                outputDate = sdf.parse(inputDateString);
            }
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return outputDate;
    }

    /**
     * Get date time from string date.
     *
     * @param inputDateTimeString the input date time string
     * @param dateTimeFormat      the date time format
     * @return the date
     */
    public static Date getDateTimeFromString(String inputDateTimeString, String dateTimeFormat){
        Date outputDateTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        try{
            LocalDateTime parsedDateTime = LocalDateTime.parse(inputDateTimeString, formatter);
            outputDateTime = Date.from(parsedDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }catch (Exception e){
            log.error(ScsbConstants.ERROR,e);
        }
        return outputDateTime;
    }

    /**
     * Gets UTC formatted date string.
     *
     * @param inputDateString the input date string
     * @return the formatted date string
     */
    public static String getFormattedDateString(String inputDateString, String inputToDateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ScsbCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        String dateString = null;
        try {
            DateFormat format = new SimpleDateFormat(ScsbCommonConstants.UTC_DATE_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone(ScsbCommonConstants.UTC));

            Date fromDate = simpleDateFormat.parse(inputDateString);
            String fromDateStr = format.format(fromDate);
            dateString = fromDateStr + ScsbCommonConstants.SOLR_DATE_RANGE_TO_NOW;
            if (StringUtils.isNotBlank(inputToDateString)) {
                Date toDate = simpleDateFormat.parse(inputToDateString);
                String toDateStr = format.format(toDate);
                dateString = fromDateStr + " TO " + toDateStr;
            }
            log.info("Date range for solr : {} " , dateString);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return dateString;
    }

    public static String getDateTimeString() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ScsbConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }
}
