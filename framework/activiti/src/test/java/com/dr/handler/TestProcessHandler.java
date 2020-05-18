package com.dr.handler;

import com.dr.framework.core.organise.entity.Person;
import com.dr.process.bo.ProcessObject;
import com.dr.process.activiti.service.ProcessHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TestProcessHandler extends ProcessHandler<Person> {
    @Override
    public boolean canHandle(String processKey) {
        return true;
    }

    @Override
    public String handleStart(Person formObject, ProcessObject processObject, Map<String, Object> variMap, Person person) {
        return "哈哈哈哈";
    }
}
