package com.dr.process.handler;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.process.bo.ProcessObject;
import com.dr.framework.core.process.bo.TaskObject;
import com.dr.framework.core.process.service.ProcessHandler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class TestProcessHandler extends ProcessHandler<Person> {
    @Override
    public boolean canHandle(String processKey) {
        return true;
    }

    @Override
    public String handleStart(Person formObject, ProcessObject processObject, Map<String, Object> variMap, Person person) {
        formObject.setId(UUID.randomUUID().toString());
        return "哈哈哈";
    }

    @Override
    public void handleUpdate(IdEntity formObject, TaskObject taskObject, Map<String, Object> variMap, Person person) {

    }
}
