package com.dr.framework.core.process.service;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.bo.TaskObject;

import java.util.List;
import java.util.Map;

/**
 * 流程相关的接口
 *
 * @author dr
 */
public interface ProcessService {
    String CREATE_KEY = "CREATE_PERSON";
    String CREATE_NAME_KEY = "CREATE_PERSON_NAME";
    String CREATE_DATE_KEY = "CREATE_DATE";


    String OWNER_KEY = "OWNER_PERSON";
    String OWNER_NAME_KEY = "OWNER_PERSON_NAME";


    String ASSIGNEE_KEY = "ASSIGNEE";
    String ASSIGNEE_NAME_KEY = "ASSIGNEE_NAME";

    String FORM_URL_KEY = "formUrl";

    /**
     * 启动流程
     *
     * @param processId
     * @param formMap
     * @param variMap
     * @param person
     * @return
     */
    TaskObject start(String processId, Map<String, Object> formMap, Map<String, Object> variMap, Person person);


    /**
     * 更新业务数据和流程信息
     *
     * @param taskObject
     * @param formMap
     * @param variMap
     * @param person
     * @return
     */
    TaskObject update(TaskObject taskObject, Map<String, Object> formMap, Map<String, Object> variMap, Person person);

    /**
     * 当前待办
     *
     * @return
     */
    Page<TaskObject> getTask(TaskObject query, int pageIndex, int pageSize);

    /**
     * 获取历史任务
     *
     * @param query
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<TaskObject> getHistoryTask(TaskObject query, int pageIndex, int pageSize);

    /**
     * 当前待办列表
     *
     * @param query
     * @return
     */
    List<TaskObject> getTask(TaskObject query);

    /**
     * 获取历史任务
     *
     * @param query
     * @return
     */
    List<TaskObject> getHistoryTask(TaskObject query);

    /**
     * 查询流程定义分页
     *
     * @param processObject
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<ProcessObject> processDefinition(ProcessObject processObject, int pageIndex, int pageSize);

    /**
     * 查询流程定义列表
     *
     * @param processObject
     * @return
     */
    List<ProcessObject> processDefinition(ProcessObject processObject);

    /**
     * 获取任务详细信息
     *
     * @param id
     * @return
     */
    TaskObject taskDetail(String id);
}
