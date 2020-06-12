package com.cvte.scm.wip.infrastructure.user.service;

import com.alibaba.fastjson.JSON;
import com.cvte.scm.wip.infrastructure.BaseJunitTest;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:26
 */
@Slf4j
public class SysUserServiceTest extends BaseJunitTest {

    @Autowired
    private UserService sysUserService;

//    @Autowired
//    private UserRepository userRepository;
//
//    @Before
//    public void initMock() {
//        UserBaseEntity mockUser = new UserBaseEntity();
//        mockUser.setAccount("zhangsan");
//        mockUser.setAccountType("2");
//        mockUser.setEmail("test@cvte.com");
//
//        UserBaseEntity mockFeignResult = new UserBaseEntity();
//
//        Mockito.when(userRepository.getSysUserDetailByAccount(Mockito.any(String.class))).thenReturn(mockFeignResult);
//    }


//    @Test
    public void feignClientDemo() {
        String account = "wufeng5300";
        UserBaseEntity user = sysUserService.getUserByAccount(account);
        log.info("用户{}身份信息:{}", account, JSON.toJSONString(user));
    }
}
