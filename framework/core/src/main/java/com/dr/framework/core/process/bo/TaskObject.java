package com.dr.framework.core.process.bo;

import java.util.List;

/**
 * @author dr
 */
public class TaskObject<T> extends ProcessObject {

    /**
     * =================================================
     * 上面是流程相关的属性
     * =================================================
     */
    /**
     * 流程实例Id
     */
    private String processInstanceId;
    /**
     * 流程定义的扩展属性
     */
    private List<ProPerty> processProPerties;
    /**
     * 环节定义id
     */
    private String taskDefineKey;
    /**
     * 环节创建人Id
     */
    private String owner;
    /**
     * 环节创建人名称
     */
    private String ownerName;
    /**
     * 环节受理人
     */
    private String assignee;
    /**
     * 环节受理人名称
     */
    private String assigneeName;

    /**
     * =================================================
     * 下面是业务相关的属性
     * =================================================
     */
    /**
     * 表单Id
     */
    private String formKey;

    /**
     * 表单数据
     * TODO
     */
    private T form;


    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }


    public String getTaskDefineKey() {
        return taskDefineKey;
    }

    public void setTaskDefineKey(String taskDefineKey) {
        this.taskDefineKey = taskDefineKey;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }


    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }


    public T getForm() {
        return form;
    }

    public void setForm(T form) {
        this.form = form;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public List<ProPerty> getProcessProPerties() {
        return processProPerties;
    }

    public void setProcessProPerties(List<ProPerty> processProPerties) {
        this.processProPerties = processProPerties;
    }
}
