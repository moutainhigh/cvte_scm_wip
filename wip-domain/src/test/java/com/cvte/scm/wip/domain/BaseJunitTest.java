package com.cvte.scm.wip.domain;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 11:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestStarter.class)
public class BaseJunitTest {

    @Before
    public void initSystem() {
    }
}