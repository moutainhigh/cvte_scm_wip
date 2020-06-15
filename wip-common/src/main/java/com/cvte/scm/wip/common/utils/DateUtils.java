package com.cvte.scm.wip.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

}
