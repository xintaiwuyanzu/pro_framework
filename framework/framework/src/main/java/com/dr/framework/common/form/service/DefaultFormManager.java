package com.dr.framework.common.form.service;

import com.dr.framework.common.form.FieldTypeHandler;
import com.dr.framework.common.form.FormTypeHandler;
import com.dr.framework.common.form.entity.FormDisplay;
import com.dr.framework.common.form.entity.FormDisplayGroup;
import com.dr.framework.common.form.entity.FormField;
import com.dr.framework.common.form.entity.FormTable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认表单管理器
 *
 * @author dr
 */
public class DefaultFormManager implements FormManager, ApplicationContextAware, InitializingBean {
    protected List<FormTypeHandler> formTypeHandlers;
    protected List<FieldTypeHandler> fieldTypeHandlers;
    protected ApplicationContext applicationContext;


    @Override
    public List<String> getFormTags() {
        return null;
    }

    @Override
    public void createFormDefine(FormTable table, List<FormField> formFields, boolean create) {

    }

    @Override
    public void updateFormDefine(FormTable table, List<FormField> formFields, boolean ddl) {

    }

    @Override
    public void dropFormDefine(String tableId) {

    }

    @Override
    public void createFormDisplay(FormDisplay display, List<FormDisplayGroup> displayGroups) {

    }

    @Override
    public void updateFormDisplay(FormDisplay display, List<FormDisplayGroup> displayGroups) {

    }

    @Override
    public List<FormTypeHandler> getFormTypeHandlers() {
        return formTypeHandlers == null ?
                Collections.emptyList()
                :
                new ArrayList<>(formTypeHandlers);
    }

    @Override
    public List<FieldTypeHandler> getFieldTypeHandlers() {
        return fieldTypeHandlers == null ?
                Collections.emptyList()
                :
                new ArrayList<>(fieldTypeHandlers);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(applicationContext, "spring上下文不能为空");
        formTypeHandlers = applicationContext.getBeansOfType(FormTypeHandler.class)
                .values()
                .stream()
                .sorted(Comparator.comparingInt(FormTypeHandler::getOrder))
                .collect(Collectors.toList());
        fieldTypeHandlers = applicationContext.getBeansOfType(FieldTypeHandler.class)
                .values()
                .stream()
                .sorted(Comparator.comparingInt(FieldTypeHandler::getOrder))
                .collect(Collectors.toList());
    }
}
