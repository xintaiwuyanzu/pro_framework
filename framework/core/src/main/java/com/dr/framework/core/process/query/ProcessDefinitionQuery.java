package com.dr.framework.core.process.query;

/**
 * 流程定义查询
 *
 * @author dr
 */
public class ProcessDefinitionQuery extends AbsProcessQuery<ProcessDefinitionQuery> {
    /**
     * 流程定义名称
     */
    private String name;
    /**
     * 流程定义Id，当作类型使用
     */
    private String type;
    /**
     * 流程定义描述，这个可能会用表达式
     */
    private String description;
    /**
     * 是否带着启动人信息
     *
     * @see com.dr.framework.core.process.bo.ProcessDefinition#startUser
     */
    private boolean withStartUser = false;

    public ProcessDefinitionQuery withStartUser() {
        this.withStartUser = true;
        return this;
    }

    public ProcessDefinitionQuery nameLike(String name) {
        this.name = name;
        return this;
    }

    public ProcessDefinitionQuery typeLike(String type) {
        this.type = type;
        return this;
    }

    public ProcessDefinitionQuery descriptionLike(String description) {
        this.description = description;
        return this;
    }

    public boolean isWithStartUser() {
        return withStartUser;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

}
