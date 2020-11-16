package com.cvte.scm.wip.common.utils;

import com.cvte.csb.toolkit.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/18 11:49
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public class BatchProcessUtils {

    public static String getKey(String... keys) {
        List<String> keyList = new ArrayList<>();
        for (String key : keys) {
            if (StringUtils.isBlank(key)) {
                keyList.add("null");
                continue;
            }
            keyList.add(key);
        }
        return String.join("_", keyList);
    }

}
