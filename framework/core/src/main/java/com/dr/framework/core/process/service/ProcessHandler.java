package com.dr.framework.core.process.service;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.process.bo.ProcessObject;

import java.util.Map;

/**
 * 用来具体处理每个业务
 * 每一个业务逻辑都需要实现一个此类
 *
 * @param <T>
 */
public abstract class ProcessHandler<T extends IdEntity> {
    /**
     * 判断能否处理指定的业务类型
     *
     * @param processKey
     * @return
     */
    public abstract boolean canHandle(String processKey);


    public abstract String handleStart(T formObject, ProcessObject processObject, Map<String, Object> variMap, Person person);
}
