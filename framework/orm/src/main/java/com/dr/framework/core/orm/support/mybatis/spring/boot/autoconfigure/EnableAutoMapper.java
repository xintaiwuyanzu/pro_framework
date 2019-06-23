package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author dr
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({MapperBeanDefinitionRegistrar.class})
public @interface EnableAutoMapper {
    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Filter[] includeFilters() default {};

    Filter[] excludeFilters() default {};

    DataBase[] databases() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface DataBase {

        String name();

        /**
         * 是否是默认数据源
         *
         * @return true表示是
         */
        boolean primary() default false;

        String prefix() default MapperBeanDefinitionProcessor.DEFAULT_PREFIX;

        @AliasFor("basePackages")
        String[] value() default {};

        @AliasFor("value")
        String[] basePackages() default {};

        Class<?>[] basePackageClasses() default {};

        Filter[] includeFilters() default {};

        Filter[] excludeFilters() default {};
    }
}
