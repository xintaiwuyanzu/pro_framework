package com.dr;

import com.dr.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

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
    public void test1() {
        System.out.println(dataSource.getClass().getName());
        testService.aaa();
    }
}
