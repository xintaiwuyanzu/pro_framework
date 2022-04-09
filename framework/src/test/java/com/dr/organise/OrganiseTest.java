package com.dr.organise;

import com.dr.Application;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.OrganisePersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class OrganiseTest {
    @Autowired
    LoginService loginService;
    @Autowired
    OrganisePersonService organisePersonService;
    Logger logger = LoggerFactory.getLogger(OrganiseTest.class);

    @Test
    public void testLogin() {
        Person person = loginService.login("admin", "1234");
        Assert.notNull(person, "登录用户为空");
        loginService.addLogin(person.getId(), "test", "test", "test");
        Person person2 = loginService.login("test", "test", "test");
        Assert.isTrue(person.getId() == person2.getId(), "用户id不对");
    }

    @Test
    public void testAddPerson() {
        Person person = new Person();
        person.setUserName("haha");
        person.setUserCode("aaaa");
        organisePersonService.addPerson(person, Organise.DEFAULT_ROOT_ID, true, "aaa");
        Person person1 = loginService.login(person.getUserCode(), "aaa");
        Assert.isTrue(person.getId() == person1.getId(), "用户Id不对");
    }

    @Test
    public void testQuery() {
        testAddPerson();
        List<Person> people = organisePersonService.getOrganiseDefaultPersons(Organise.DEFAULT_ROOT_ID);
        Assert.isTrue(2 == people.size(), "用户数量不对");
    }

    @Test
    public void testQueryOrganise() {
        testAddPerson();
        Organise organise = organisePersonService.getPersonDefaultOrganise("admin");
        Assert.isTrue(organise.getId() == Organise.DEFAULT_ROOT_ID, "机构Id不对");
    }

    @Test
    public void testAddOrganise() {
        Organise organise = new Organise();
        organise.setOrganiseCode("aaaa");
        organisePersonService.addOrganise(organise);
        Organise organise1 = new Organise();
        organise1.setOrganiseCode("bbbbb");
        organise1.setParentId(organise.getId());
        organisePersonService.addOrganise(organise1);
        List<Organise> organises = organisePersonService.getChildrenOrganiseList(Organise.DEFAULT_ROOT_ID);
        Assert.isTrue(2 == organises.size(), "机构数量不对");
        Assert.isTrue(2 == organisePersonService.getParentOrganiseList(organise1.getId()).size(), "机构数量不对");
    }


}
