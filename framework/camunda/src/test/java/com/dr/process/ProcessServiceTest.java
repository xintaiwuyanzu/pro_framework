package com.dr.process;

import com.dr.framework.common.page.Page;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.process.bo.ProcessDefinition;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.query.ProcessDefinitionQuery;
import com.dr.framework.core.process.query.ProcessQuery;
import com.dr.framework.core.process.query.TaskQuery;
import com.dr.framework.core.process.service.ProcessService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = CamundaApplication.class)
@RunWith(SpringRunner.class)
public class ProcessServiceTest {
    @Autowired
    ProcessService processService;
    @Autowired
    OrganisePersonService organisePersonService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    IdentityService identityService;
    ProcessDefinition processDefinition;
    Person person;

    @Autowired
    RuntimeService runtimeService;

    @Before
    public void initDefine() {
        person = organisePersonService.getPersonById("admin");
        identityService.setAuthenticatedUserId(person.getId());
        processDefinition = processService.getProcessDefinitionByKey("sp_leave");
    }

    @Test
    public void testProcessDefinition() {
        List<ProcessDefinition> definitions = processService.processDefinitionList(new ProcessDefinitionQuery().withProperty());
        Page<ProcessDefinition> definitionPage = processService.processDefinitionPage(new ProcessDefinitionQuery().withProperty(), 0, 10);
    }


    @Test
    public void testStart() {

        Map map = new HashMap();
        map.put("assignee", "admin1");
        map.put("title", "hahha");
        map.put("formId", "aaaa");
        ProcessObject processObject = processService.start(processDefinition.getId(), map, person);

        TaskQuery query = new TaskQuery()
                .processDefinitionKeyLike("%aaa%")
                .taskKeyNotLike("bbb")
                .withProperty().withVariables();

        List<TaskObject> taskObjects = processService.taskList(query);


        map.put("title", "bbbbb");
        //  processService.complete(taskObjects.get(0).getId(), map);
        //  taskObjects = processService.taskList(query);

        //   processService.taskHistoryList(new TaskQuery().withVariables());
        //  List<ProcessObject> objects = processService.processObjectList(null);
    }


    @Test
    public void testStartUser() {

    }

    @Test
    public void testJump() {
        TaskObject taskObject = processService.taskList(new TaskQuery()).get(0);
        processService.jump(taskObject.getId(), "Task_0oihs3n", "admin");
    }

    @Test
    public void testEnd() {
        ProcessDefinition processObject = processService.getProcessDefinitionByKey("sp_leave");
        Map<String, Object> map = new HashMap<>();
        map.put("title", "aaa");
        map.put("assignee", "aaa");
        map.put("formId", "aaa");
        ProcessObject object = processService.start(processObject.getId(), map, person);
        List<TaskObject> objects = processService.taskList(new TaskQuery().processInstanceIdEqual(object.getId()));

        processService.endProcess(objects.get(0).getId(), null);

    }


    @Test
    public void testProcess() {
        List<ProcessObject> pr = processService.processObjectList(new ProcessQuery());
        List<ProcessObject> processObjectHistoryList = processService.processObjectHistoryList(new ProcessQuery());

    }

}
