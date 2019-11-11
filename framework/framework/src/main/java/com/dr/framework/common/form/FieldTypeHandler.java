package com.dr.framework.common.form;

/**
 * 表单字段处理器
 *
 * @author dr
 */
public interface FieldTypeHandler {
    /**
     * 获取字段类型
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
     * 获取排序
     *
     * @return
     */
    default int getOrder() {
        return 0;
    }


}
