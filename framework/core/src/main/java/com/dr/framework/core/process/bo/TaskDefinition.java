package com.dr.framework.core.process.bo;

import com.dr.framework.core.organise.entity.Person;

import java.util.List;

/**
 * @author 环节定义信息
 */
public class TaskDefinition extends AbstractProcessObject {
    /**
     * 启动人员
     */
    private List<Person> startUser;
    /**
     * 定义的流程属性
     */
    private List<ProPerty> processProPerties;

    public List<Person> getStartUser() {
        return startUser;
    }

    public void setStartUser(List<Person> startUser) {
        this.startUser = startUser;
    }

    public List<ProPerty> getProcessProPerties() {
        return processProPerties;
    }

    public void setProcessProPerties(List<ProPerty> processProPerties) {
        this.processProPerties = processProPerties;
    }
}
