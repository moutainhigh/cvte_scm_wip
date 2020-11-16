package com.cvte.scm.wip.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/9 21:00
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public class DateUtils {

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
    }

    public static Date getMinutesBeforeTime(LocalDateTime time, Integer minutes) {
        LocalDateTime minutesBefore = time.minusMinutes(minutes);
        return Date.from(minutesBefore.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
    }

    public static Date getMinutesBeforeTime(Date time, Integer minutes) {
        LocalDateTime localDateTime = dateToLocalDateTime(time);
        return getMinutesBeforeTime(localDateTime, minutes);
    }

    /**
     * 获取月底最后{days}天 {hour}点的时间
     * @since 2020/10/23 5:36 下午
     * @author xueyuting
     * @param
     */
    public static Date getBeforeEndOfMonth(Integer days, Integer hour) {
        Calendar dayBeforeEndOfMonth = Calendar.getInstance();
        dayBeforeEndOfMonth.set(Calendar.DAY_OF_MONTH, dayBeforeEndOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH) - days - 1);
        if (Objects.nonNull(hour)) {
            dayBeforeEndOfMonth.setLenient(false);
            dayBeforeEndOfMonth.set(Calendar.HOUR_OF_DAY, hour);
        }
        return dayBeforeEndOfMonth.getTime();
    }

}
