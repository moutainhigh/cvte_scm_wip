package com.cvte.scm.wip.common.utils;

import com.cvte.csb.toolkit.ObjectUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author zy
 * email zhongyuan@cvte.com
 * date 2020-06-30 15:32
 **/
public class CalendarUtils {

    private CalendarUtils() { }

    /**
     * 获取日期零点
     *
     * @param date
     * @return java.util.Date
     **/
    public static Date getDateZero(Date date) {
        if (ObjectUtils.isNull(date)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }





}
