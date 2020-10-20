package com.dr;

import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.EnableAutoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoMapper
public class Application {

    @Autowired
    OrganisePersonService organisePersonService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
