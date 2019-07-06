package com.sun;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:ssm/spring-service.xml",
        "classpath:ssm/spring-mybaties.xml","classpath:ssm/dispacther-servlet.xml"})
public class BaseTest {
}
