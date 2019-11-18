package com.dr.framework.core.process.service;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.bo.TaskObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象父类实现
 */
public abstract class AbstractHandlerProcessService implements ProcessService {
    final Map<ProcessHandler, Class<? extends IdEntity>> processHandlerClassMap;
    protected Logger logger = LoggerFactory.getLogger(ProcessService.class);

    public AbstractHandlerProcessService(List<ProcessHandler> handlers) {
        if (handlers != null) {
            this.processHandlerClassMap = new HashMap<>(handlers.size());
            handlers.forEach(h -> {
                readHandler(h);
            });
        } else {
            this.processHandlerClassMap = new HashMap<>();
        }
    }


    @Override
    public TaskObject start(ProcessObject processObject, Map<String, Object> formMap, Map<String, Object> variMap, Person person) {
        ProcessHandler processHandler = getProcessHandler(processObject.getKey());
        IdEntity formObject = newFormObject(processHandler, formMap);
        String name = processHandler.handleStart(formObject, processObject, variMap, person);
        return doStart(processObject, processHandler, formObject, name, variMap, person);
    }

    /**
     * @param processObject  启动对象
     * @param processHandler 流程处理器
     * @param formObject     表单内容
     * @param name           流程名称
     * @param variMap        环境变量
     * @param person         当前人员
     * @return
     */
    protected abstract TaskObject doStart(ProcessObject processObject, ProcessHandler processHandler, IdEntity formObject, String name, Map<String, Object> variMap, Person person);

    private void readHandler(ProcessHandler handler) {
        Type type = handler.getClass().getGenericSuperclass();
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            Type[] types1 = ((ParameterizedType) type).getActualTypeArguments();
            processHandlerClassMap.put(handler, (Class) types1[0]);
        }
    }

    protected ProcessHandler getProcessHandler(String key) {
        ProcessHandler processHandler = null;
        for (ProcessHandler processHandler1 : processHandlerClassMap.keySet()) {
            if (processHandler1.canHandle(key)) {
                processHandler = processHandler1;
                break;
            }
        }
        Assert.notNull(processHandler, "未找到指定的业务流程处理类");
        return processHandler;
    }

    /**
     * 通过反射创建新的表单对象
     *
     * @param processHandler
     * @param formMap
     * @return
     */
    protected IdEntity newFormObject(ProcessHandler processHandler, Map<String, Object> formMap) {
        Class<? extends IdEntity> clazz = processHandlerClassMap.get(processHandler);
        try {
            IdEntity o = clazz.newInstance();
            DataBinder dataBinder = new DataBinder(o);
            dataBinder.bind(new MutablePropertyValues(formMap));
            return (IdEntity) dataBinder.getTarget();
        } catch (Exception e) {
            logger.error("绑定表单数据失败", e);
        }
        return null;
    }
}

