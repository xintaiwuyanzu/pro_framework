package com.dr;

import com.dr.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.liquibase.enabled=false", classes = TestApplication.class)
public class BeanRegisterTest {
    @Autowired
    DataSource dataSource;
    @Autowired
    TestService testService;

    @Test
    public void test() {
        System.out.println(dataSource.getClass().getName());
    }

    @Test
    public void test1() throws Exception {
        System.out.println(dataSource.getClass().getName());
        testService.aaa();
    }
}
