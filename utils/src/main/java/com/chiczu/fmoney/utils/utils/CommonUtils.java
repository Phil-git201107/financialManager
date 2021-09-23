package com.chiczu.fmoney.utils.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {

    Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    // 獲取當前月之前後的某個月份(如:202105),參數:獲取當前月前後某個月,正值:往後;負值:往前;0:當前月
    public static String getResultMonth(int m) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 獲取指定的月份(含年份)
        cal.add(Calendar.MONTH,m);
        // 獲取指定西洋年月份字串
        String monthStr = sdf.format(cal.getTime());
        // 獲取西洋年,並轉為int
//        int monthInt= Integer.parseInt(monthStr.substring(0,4));
//        // 將西洋年數字調整為民國年數字後轉為字串,再加入月份資料
//        String resultMonth = Integer.toString(monthInt-1911)+monthStr.substring(4);
        return monthStr;
    }

    // 獲取當前日期字串,格式:20210915
    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        return date;
    }

}
