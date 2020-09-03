package com.dr.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间转换
 * 
 * @author dr
 */
public class DateTimeUtils {

    /**
     * 获取当前时间戳
     */
    public static Long getMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Date转时间戳
     */
    public static Long dateToMillis(Date date) {
        return date.getTime();
    }

    /**
     * Date转字符串
     */
    public static String dateToString(Date date, String formatStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        return simpleDateFormat.format(date);
    }

    /**
     * 字符串转时间戳
     */
    public static Long stringToMillis(String dateStr, String formatStr) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
            return simpleDateFormat.parse(dateStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 字符串转Date
     */
    public static Date stringToDate(String dateStr, String formatStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间戳转日期字符串
     */
    public static String longToDate(Long millSec, String dateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            Date date = new Date(millSec);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(millSec);
        }
    }

    /**
     * 时间戳转Date
     */
    public static Date longToDate(Long millSec) {
        return new Date(millSec);
    }

    /**
     * 获取上个季度
     *
     * @return
     */
    public static Map<String, String> getYearQuarter() {
        Map<String, String> map = new HashMap<>();
        String nowStr = dateToString(new Date(),"yyyyMMdd");
        int year = Integer.parseInt(nowStr.substring(0, 4));
        int month = Integer.parseInt(nowStr.substring(4, 6));
        String quarter;
        String halfYear;
        //TODO 暂时设置
        if (month >= 2 && month <= 12) {
            quarter = "4";
            halfYear = "2";
            month = 12;
        } else if (month >= 3 && month <= 5) {
            quarter = "1";
            halfYear = "2";
        } else if (month >= 6 && month <= 8) {
            quarter = "2";
            halfYear = "1";
        } else {
            quarter = "3";
            halfYear = "1";
        }
        /*if (month >= 1 && month <= 3) {
            quarter = "4";
            halfYear = "2";
            month = 12;
            year--;
        } else if (month >= 4 && month <= 6) {
            quarter = "1";
            halfYear = "2";
        } else if (month >= 7 && month <= 9) {
            quarter = "2";
            halfYear = "1";
        } else {
            quarter = "3";
            halfYear = "1";
        }*/
        String yearStr = String.valueOf(year);
        map.put("yearStr", yearStr);
        map.put("quarter", quarter);
        map.put("month", month + "");
        map.put("halfYear", halfYear);
        return map;
    }
}

