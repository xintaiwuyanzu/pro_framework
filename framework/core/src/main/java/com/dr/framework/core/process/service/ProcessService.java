package com.dr.framework.core.process.service;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.process.bo.Button;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.bo.TaskObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 流程相关的接口
 *
 * @author dr
 */
public interface ProcessService {
    String OWNER_KEY = "CREATEPERSON";
    String CREATEDATE_KEY = "CREATEDATE";

    /**
     * 启动流程
     *
     * @param processObject
     * @param formMap
     * @param variMap
     * @param person
     * @return
     */
    TaskObject start(ProcessObject processObject, Map<String, Object> formMap, Map<String, Object> variMap, Person person);

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
     * 根据任务id查询当前环节配置的按钮
     *
     * @param taskInstanceId
     * @return
     */
    default List<Button> getTaskButtons(String taskInstanceId) {
        return Collections.emptyList();
    }

    /**
     * 查询流程定义分页
     *
     * @param processObject
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<ProcessObject> processDefion(ProcessObject processObject, int pageIndex, int pageSize);

    /**
     * 查询流程定义列表
     *
     * @param processObject
     * @return
     */
    List<ProcessObject> processDefion(ProcessObject processObject);
}
