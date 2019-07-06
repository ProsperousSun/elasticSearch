package com.sun.es;

import com.sun.BaseTest;
import junit.runner.BaseTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.net.UnknownHostException;

public class EsTestTest  extends BaseTest {

    @Resource
    private EsClientConnect esTest;

    /**
     * 客户端连接
     * @throws UnknownHostException
     */
    @Test
    public void getEsClient() throws UnknownHostException {
        esTest.getEsClient();
    }
}