package com.dr.framework.core.process.query;

/**
 * 用来查询流程任务
 *
 * @author dr
 */
public class TaskQuery extends AbsProcessQuery<TaskQuery> {
    /**
     * 流程创建人
     */
    private String createPerson;
    /**
     * 环节创建人
     */
    private String owner;
    /**
     * 环节受理人
     */
    private String assignee;

    /**
     * 环节定义Id
     */
    private String taskKeyLike;
    private String taskKeyNotLike;

    /**
     * 环节名称
     */
    private String title;
    /**
     * 环节描述
     */
    private String description;

    /**
     * 流程实例Id
     */
    private String processInstanceId;
    private String processDefinitionKey;
    /**
     * 是否查询环节扩展属性
     */
    private boolean withVariables;

    /**
     * 是否查询流程扩展属性
     */
    private boolean withProcessProperty;

    public TaskQuery processInstanceIdEqual(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public TaskQuery createPersonEqual(String createPerson) {
        this.createPerson = createPerson;
        return this;
    }

    public TaskQuery ownerEqual(String owner) {
        this.owner = owner;
        return this;
    }

    public TaskQuery assigneeEqual(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public TaskQuery taskKeyLike(String taskKeyLike) {
        this.taskKeyLike = taskKeyLike;
        return this;
    }

    public TaskQuery taskKeyNotLike(String taskKeyNotLike) {
        this.taskKeyNotLike = taskKeyNotLike;
        return this;
    }

    public TaskQuery withVariables() {
        this.withVariables = true;
        return this;
    }

    public TaskQuery titleLike(String title) {
        this.title = title;
        return this;
    }

    public TaskQuery descriptionLike(String description) {
        this.description = description;
        return this;
    }

    public TaskQuery withProcessProperty(boolean withProcessProperty) {
        this.withProcessProperty = withProcessProperty;
        return this;
    }

    public TaskQuery processDefinitionKeyLike(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public String getAssignee() {
        return assignee;
    }


    public String getTaskKeyLike() {
        return taskKeyLike;
    }

    public String getTaskKeyNotLike() {
        return taskKeyNotLike;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isWithVariables() {
        return withVariables;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public boolean isWithProcessProperty() {
        return withProcessProperty;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }
}
