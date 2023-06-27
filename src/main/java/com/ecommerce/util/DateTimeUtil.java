package com.ecommerce.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Converts a string representation of a date to a Date object using a specified format.
     *
     * @param dateTimeStr The string representation of the date.
     * @param formatStr   The format of the date string.
     * @return The converted Date object.
     */
    public static Date strToDate(String dateTimeStr, String formatStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * Converts a Date object to a string representation using a specified format.
     *
     * @param date      The Date object to be converted.
     * @param formatStr The format of the resulting string.
     * @return The string representation of the date.
     */
    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    /**
     * Converts a string representation of a date to a Date object using the standard format "yyyy-MM-dd HH:mm:ss".
     *
     * @param dateTimeStr The string representation of the date.
     * @return The converted Date object.
     */
    public static Date strToDate(String dateTimeStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * Converts a Date object to a string representation using the standard format "yyyy-MM-dd HH:mm:ss".
     *
     * @param date The Date object to be converted.
     * @return The string representation of the date.
     */
    public static String dateToStr(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
