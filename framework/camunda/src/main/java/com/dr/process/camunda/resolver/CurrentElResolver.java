package com.dr.process.camunda.resolver;

import org.camunda.bpm.engine.impl.javax.el.ELContext;
import org.camunda.bpm.engine.impl.javax.el.ELResolver;
import org.springframework.stereotype.Component;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

/**
 * 自定义表达式解析器，用来从上下文获取当前登录人相关信息
 */
@Component
public class CurrentElResolver extends ELResolver {
    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {

    }
}
