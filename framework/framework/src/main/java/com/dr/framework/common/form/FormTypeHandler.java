package com.dr.framework.common.form;

import com.dr.framework.core.orm.module.Module;

/**
 * 表单类型处理器
 *
 * @author dr
 */
public interface FormTypeHandler {

    /**
     * 获取名称
     *
     * @return
     */
    String getName();


    /**
     * 获取中文备注
     *
     * @return
     */
    String getComment();

    /**
     * 获取模块
     *
     * @return
     */
    default String getModule() {
        return Module.DEFAULT_MODULE;
    }

    /**
     * 获取排序
     *
     * @return
     */
    default int getOrder() {
        return 0;
    }

}
