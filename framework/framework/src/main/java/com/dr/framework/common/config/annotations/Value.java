package com.dr.framework.common.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.dr.framework.common.config.annotations.Value.Name.AUTO;

/**
 * @author 字段映射注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    /**
     * @return
     */
    String code() default "";

    /**
     * 字段映射{@link com.dr.framework.common.config.entity.CommonConfig}
     * 的字段名称
     *
     * @return
     */
    Name name() default AUTO;

    /**
     * 与{@link #code()}相同，简写
     *
     * @return
     */
    Name value() default AUTO;

    enum Name {
        AUTO,
        id,
        createDate,
        createPerson,
        updateDate,
        updatePerson,
        orderBy,
        status,
        name,
        code,
        type,
        description,

        refId,
        refType,
        parentId,
        rootId,

        value,
        value1,
        value2,
        value3,

        longValue,
        longValue1,
        longValue2
    }

}
