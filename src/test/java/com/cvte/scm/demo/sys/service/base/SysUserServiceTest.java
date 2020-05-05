package com.cvte.scm.demo.sys.service.base;

import com.alibaba.fastjson.JSON;
import com.cvte.csb.core.interfaces.enums.DefaultStatusEnum;
import com.cvte.scm.demo.BaseJunitTest;
import com.cvte.scm.demo.client.common.dto.FeignResult;
import com.cvte.scm.demo.client.sys.base.SysUserApiClient;
import com.cvte.scm.demo.client.sys.base.dto.UserBaseDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:26
 */
@Slf4j
public class SysUserServiceTest extends BaseJunitTest {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserApiClient sysUserApiClient;

    @Before
    public void initMock() {
        UserBaseDTO mockUser = new UserBaseDTO();
        mockUser.setAccount("zhangsan");
        mockUser.setAccountType("2");
        mockUser.setEmail("test@cvte.com");

        FeignResult<UserBaseDTO> mockFeignResult = new FeignResult<>();
        mockFeignResult.setStatus(DefaultStatusEnum.OK.getCode());
        mockFeignResult.setData(mockUser);

        Mockito.when(sysUserApiClient.getSysUserDetailByAccount(Mockito.any(String.class))).thenReturn(mockFeignResult);
    }


    @Test
    public void feignClientDemo() {
        String account = "wufeng5300";
        UserBaseDTO user = sysUserService.getUserByAccount(account);
        log.info("用户{}身份信息:{}", account, JSON.toJSONString(user));
    }
}
