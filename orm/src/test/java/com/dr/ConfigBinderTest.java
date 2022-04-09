package com.dr;

import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.MultiDataSourceProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest(properties = "spring.liquibase.enabled=false", classes = TestApplication.class)
public class ConfigBinderTest {
    @Autowired
    Environment environment;

    @Test
    public void dataSourceProperties() {
        BindResult<MultiDataSourceProperties> bindResult = Binder.get(environment).bind("spring.datasource", MultiDataSourceProperties.class);
        MultiDataSourceProperties multiDataSourceProperties = bindResult.get();
    }

}
