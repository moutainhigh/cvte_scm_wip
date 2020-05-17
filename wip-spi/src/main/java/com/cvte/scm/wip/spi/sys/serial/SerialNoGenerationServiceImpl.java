package com.cvte.scm.wip.spi.sys.serial;

import com.alibaba.fastjson.JSON;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.serial.SerialNoGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.cvte.csb.toolkit.CollectionUtils.isEmpty;
import static java.lang.String.format;

/**
 * @author : jf
 * Date    : 2019.02.20
 * Time    : 13:15
 * Email   ：jiangfeng7128@cvte.com
 */
@Slf4j
@Service
public class SerialNoGenerationServiceImpl implements SerialNoGenerationService {

    @Value("${csb.serialno.url.server}")
    private String csbSerialnoUrlServer;

    @Value("${csb.serialno.url.serialNo}")
    private String csbSerialnoUrlSerialNo;

    @Value("${server.appId}")
    private String appId;

    @Override
    public String getNextSerialNumberByCode(String serialCode) {
        return getNextSerialNumberByCode(serialCode, null);
    }

    @Override
    public String getNextSerialNumberByCode(String serialCode, List<String> params) {
        StringBuilder url = new StringBuilder(csbSerialnoUrlServer).append(format(csbSerialnoUrlSerialNo, appId, serialCode));
        String body = isEmpty(params) ? "" : JSON.toJSONString(params);
        try {
            String response = RestCallUtils.callRest(RestCallUtils.RequestMethod.POST, url.toString(), body);
            Map<String, Object> resultMap = JSON.parseObject(response);
            if (!"0".equals(resultMap.get("status"))) {
                throw new RuntimeException("获取流水号失败：" + response);
            }
            return (String) resultMap.get("data");
        } catch (Exception e) {
            log.error("调用流水号接口获取序列号失败，url = {}", url.toString(), e);
            return null;
        }
    }
}
