package com.cvte.scm.demo;

import com.ctrip.framework.apollo.ConfigService;
import com.cvte.csb.base.commons.OperatingSystem;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.test.base.impl.CsbModelTest;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:22
 */
@SpringBootTest(classes = TestStarter.class)
@Ignore
public class BaseJunitTest extends CsbModelTest {

    @Before
    public void initSystem() {
        String serverAppId = ConfigService.getConfig("application").getProperty("server.appId", "defaultValue");
        String serverSystemId = ConfigService.getConfig("application").getProperty("server.systemId", "defaultValue");
        Boolean isSingleLoad = ConfigService.getConfig("application").getBooleanProperty("server.isSingleLoad", true);

        OperatingSystem operatingSystem = new OperatingSystem();
        operatingSystem.setAppId(serverAppId);
        operatingSystem.setSystemId(serverSystemId);
        operatingSystem.setSingleLoad(isSingleLoad);
        CurrentContext.setCurrentSystem(operatingSystem);
    }
}