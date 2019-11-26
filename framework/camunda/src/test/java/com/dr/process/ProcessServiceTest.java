package com.dr.process;

import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.service.ProcessService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
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
    ProcessDefinition processDefinition;

    @Before
    public void initDefine() {
        processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("leave")
                .singleResult();
    }


    @Test
    public void testStart() {
        Map map = new HashMap();
        map.put("assignee", "admin1");
        Person person = organisePersonService.getPersonById("admin");
        processService.start(processDefinition.getId(), null, map, person);
        List<TaskObject> taskObjects = processService.getTask(null);
    }

    @Test
    public void testStartUser() {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("leave").singleResult();
    }

}
