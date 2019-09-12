package com.dr.framework.common.form.service;

import com.dr.framework.common.form.FieldTypeHandler;
import com.dr.framework.common.form.FormTypeHandler;
import com.dr.framework.common.form.entity.FormDisplay;
import com.dr.framework.common.form.entity.FormDisplayGroup;
import com.dr.framework.common.form.entity.FormField;
import com.dr.framework.common.form.entity.FormTable;

import java.util.List;

/**
 * 表单管理器，能够管理自定义表单的所有操作
 *
 * @author dr
 */
public interface FormManager {
    /**
     * 根据条件查询字段定义列表/分页
     * 添加/删除/修改 字段定义
     * <p>
     * 创建/删除 表模板定义
     * <p>
     * <p>
     * 根据查询条件查询表模板
     */

    /**
     * 获取表单定义
     *
     * @return
     */
    List<String> getFormTags();

    /**
     * 创建表单定义
     *
     * @param table
     * @param formFields
     * @param create
     */
    void createFormDefine(FormTable table, List<FormField> formFields, boolean create);

    /**
     * 更新表单定义
     *
     * @param table
     * @param formFields
     * @param ddl
     */
    void updateFormDefine(FormTable table, List<FormField> formFields, boolean ddl);

    /**
     * 删除表单定义，
     * 因为表单定义已经删除了，没有能操作实体表的入口了，所以也会删除物理表结构定义
     *
     * @param tableId
     */
    void dropFormDefine(String tableId);

    /**
     * 添加表单显示方案定义
     *
     * @param display
     * @param displayGroups
     */
    void createFormDisplay(FormDisplay display, List<FormDisplayGroup> displayGroups);

    /**
     * 更新表单显示方案定义
     *
     * @param display
     * @param displayGroups
     */
    void updateFormDisplay(FormDisplay display, List<FormDisplayGroup> displayGroups);

    /**
     * 获取表单分类管理器
     *
     * @return
     */
    List<FormTypeHandler> getFormTypeHandlers();

    /**
     * 获取字段分类管理器
     *
     * @return
     */
    List<FieldTypeHandler> getFieldTypeHandlers();


}
