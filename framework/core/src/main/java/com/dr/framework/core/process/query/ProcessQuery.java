package com.dr.framework.core.process.query;

/**
 * @author dr
 */
public class ProcessQuery extends AbsProcessQuery<ProcessQuery> {
    /**
     * 流程名称
     */
    private String name;
    /**
     * 流程类型
     */
    private String type;
    /**
     * 流程实例描述
     */
    private String description;
    /**
     * 流程实例创建人
     */
    private String createPerson;
    /**
     * 环节实例接收人
     */
    private String taskPerson;

    public ProcessQuery nameLike(String name) {
        this.name = name;
        return this;
    }

    public ProcessQuery typeLike(String type) {
        this.type = type;
        return this;
    }

    public ProcessQuery descriptionLike(String description) {
        this.description = description;
        return this;
    }

    public ProcessQuery createPersonEqual(String createPerson) {
        this.createPerson = createPerson;
        return this;
    }

    public ProcessQuery taskPersonEqual(String taskPerson) {
        this.taskPerson = taskPerson;
        return this;
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

    public String getCreatePerson() {
        return createPerson;
    }

    public String getTaskPerson() {
        return taskPerson;
    }
}
