package com.dr.framework.process.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.service.ProcessService;
import com.dr.framework.core.web.annotations.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 暂时的流程管理统一入口
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/process")
public class ProcessController {
    public static final String FORM_PREFIX = "form.";
    public static final String VALUE_PREFIX = "value.";


    ProcessService processService;

    public ProcessController(@Autowired(required = false) ProcessService processService) {
        this.processService = processService;
    }


    /**
     * 查询流程定义分页
     *
     * @return
     */
    @GetMapping("/processPage")
    public ResultEntity processPage(ProcessObject processObject,
                                    @RequestParam(defaultValue = "0") int pageIndex,
                                    @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE + "") int pageSize,
                                    @RequestParam(defaultValue = "true") boolean page) {
        if (page) {
            return ResultEntity.success(processService.processDefinition(processObject, pageIndex, pageSize));
        } else {
            return ResultEntity.success(processService.processDefinition(processObject));
        }
    }

    /**
     * 创建和启动流程
     *
     * @param id
     * @param request
     * @param person
     * @return
     */
    @PostMapping("/insert")
    public ResultEntity insert(String id,
                               HttpServletRequest request,
                               @Current Person person) {
        Assert.notNull(id, "流程【processId】参数不能为空");
        //表单参数
        Map<String, Object> formMap = WebUtils.getParametersStartingWith(request, FORM_PREFIX);
        //环境变量参数
        Map<String, Object> variMap = WebUtils.getParametersStartingWith(request, VALUE_PREFIX);
        return ResultEntity.success(processService.start(id, formMap, variMap, person));
    }

    //更新
    @RequestMapping("/update")
    public ResultEntity update(TaskObject taskObject,
                               HttpServletRequest request,
                               @Current Person person) {
        //表单参数
        Map<String, Object> formMap = WebUtils.getParametersStartingWith(request, FORM_PREFIX);
        //环境变量参数
        Map<String, Object> variMap = WebUtils.getParametersStartingWith(request, VALUE_PREFIX);
        return ResultEntity.success(processService.update(taskObject, formMap, variMap, person));
    }

    //查询
    @RequestMapping("/page")
    public ResultEntity page(@Current Person person,
                             TaskObject taskObject,
                             @RequestParam(defaultValue = "0") int pageIndex,
                             @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE + "") int pageSize,
                             @RequestParam(defaultValue = "true") boolean page) {
        taskObject.setAssignee(person.getId());
        if (page) {
            return ResultEntity.success(processService.getTask(taskObject, pageIndex, pageSize));
        } else {
            return ResultEntity.success(processService.getTask(taskObject));
        }
    }

    @RequestMapping("/history")
    public ResultEntity history(@Current Person person,
                                TaskObject taskObject,
                                @RequestParam(defaultValue = "0") int pageIndex,
                                @RequestParam(defaultValue = Page.DEFAULT_PAGE_SIZE + "") int pageSize,
                                @RequestParam(defaultValue = "true") boolean page) {
        taskObject.setAssignee(person.getId());
        if (page) {
            return ResultEntity.success(processService.getHistoryTask(taskObject, pageIndex, pageSize));
        } else {
            return ResultEntity.success(processService.getHistoryTask(taskObject));
        }
    }

    //删除
    @RequestMapping("/delete")
    public ResultEntity<Boolean> delete(HttpServletRequest request, String id) {
        return null;
    }

    //获取详细的任务信息
    @RequestMapping("/detail")
    public ResultEntity<TaskObject> detail(String id) {
        return ResultEntity.success(processService.taskDetail(id));
    }

    @RequestMapping("/process")
    public ResultListEntity<ProcessObject> detail() {
        return null;
    }

}
