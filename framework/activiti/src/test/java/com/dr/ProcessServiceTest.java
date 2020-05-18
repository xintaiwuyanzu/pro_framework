package com.dr;

import com.dr.process.bo.ProcessObject;
import com.dr.process.activiti.service.ProcessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ProcessServiceTest {
    @Autowired
    ProcessService processService;

    @Test
    public void testStart() {
        ProcessObject processObject = new ProcessObject();
        processObject.setKey("myProcess");
        Map map = new HashMap();
        map.put("assignee", "admin");

        processService.start(processObject, null, map, null);
    }

}
