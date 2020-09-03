package com.dr;

import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.EnableAutoMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@SpringBootApplication
@EnableAutoMapper
public class Application {

    @Autowired
    OrganisePersonService organisePersonService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Order()
    InitializingBean initData() {
        return () -> {
            Organise organise = new Organise();
            organise.setOrganiseName("aaa");
            organise.setOrganiseCode("bbbb");
            organisePersonService.addOrganise(organise);
        };
    }
}
