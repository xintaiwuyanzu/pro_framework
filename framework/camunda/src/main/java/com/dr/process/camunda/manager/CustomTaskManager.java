package com.dr.process.camunda.manager;

import com.dr.process.camunda.query.CustomTaskQuery;
import org.camunda.bpm.engine.impl.TaskQueryImpl;
import org.camunda.bpm.engine.impl.persistence.entity.TaskManager;
import org.camunda.bpm.engine.task.Task;

import java.util.List;

/**
 * @author 添加自定义的参数实现
 */
public class CustomTaskManager extends TaskManager {
    @Override
    public List<Task> findTasksByQueryCriteria(TaskQueryImpl taskQuery) {
        if (taskQuery instanceof CustomTaskQuery) {
            configureQuery(taskQuery);
            return getDbEntityManager().selectList("selectTaskByQueryCriteriaCustom", taskQuery);
        }
        return super.findTasksByQueryCriteria(taskQuery);
    }

    @Override
    public long findTaskCountByQueryCriteria(TaskQueryImpl taskQuery) {
        if (taskQuery instanceof CustomTaskQuery) {
            configureQuery(taskQuery);
            return (Long) getDbEntityManager().selectOne("selectTaskCountByQueryCriteriaCustom", taskQuery);
        }
        return super.findTaskCountByQueryCriteria(taskQuery);
    }
}
