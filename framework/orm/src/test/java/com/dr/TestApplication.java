package com.dr;

import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.EnableAutoMapper;
import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.MultiDataSourceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableAutoMapper(databases = {
        @EnableAutoMapper.DataBase(name = "three", primary = true)
        /* @EnableAutoMapper.DataBase(name = "two")*/
})
public class TestApplication {
    MultiDataSourceProperties dataSourceProperties(Environment environment) {
        BindResult<MultiDataSourceProperties> bindResult = Binder.get(environment).bind("spring.datasource", MultiDataSourceProperties.class);
        return bindResult.get();
    }


    @Component
    static class B implements BeanPostProcessor {
        Logger logger = LoggerFactory.getLogger(TestApplication.class);

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            logger.info("{}===={}", beanName, bean.getClass().getName());
            return bean;
        }
    }
}
