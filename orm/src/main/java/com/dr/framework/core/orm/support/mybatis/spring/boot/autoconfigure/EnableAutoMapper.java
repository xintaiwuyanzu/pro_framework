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
    /**
     * mapper所在的包，如果为空则扫描启动类所在包的
     *
     * @return
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * @return
     * @see #value()
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * mapper所在包的类
     *
     * @return
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 指定扫描条件
     *
     * @return
     */
    Filter[] includeFilters() default {};

    /**
     * 指定不扫描的条件
     *
     * @return
     */
    Filter[] excludeFilters() default {};

    /**
     * 数据源配置
     *
     * @return
     */
    DataBase[] databases() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface DataBase {
        /**
         * 数据源的名称，跟property中的名称对应
         *
         * @return
         */
        String name();

        /**
         * 是否是默认数据源
         *
         * @return true表示是
         */
        boolean primary() default false;

        /**
         * 数据源前缀，默认值为{@link MapperBeanDefinitionProcessor.DEFAULT_PREFIX}
         * 和${@link #name()}一起拼接成变量
         * <p>
         * 比如 spring.datasource.name
         *
         * @return
         * @see #name()
         */
        String prefix() default MapperBeanDefinitionProcessor.DEFAULT_PREFIX;

        /**
         * 实体类扫描包
         *
         * @return
         */
        @AliasFor("basePackages")
        String[] value() default {};

        @AliasFor("value")
        String[] basePackages() default {};

        Class<?>[] basePackageClasses() default {};

        Filter[] includeFilters() default {};

        Filter[] excludeFilters() default {};
    }
}
