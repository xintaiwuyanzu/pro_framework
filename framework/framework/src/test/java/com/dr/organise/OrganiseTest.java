package com.dr.organise;

import com.dr.Application;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.OrganisePersonService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
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
        Assert.assertNotNull(person);
        loginService.addLogin(person.getId(), "test", "test", "test");
        Person person2 = loginService.login("test", "test", "test");
        Assert.assertEquals(person.getId(), person2.getId());
    }

    @Test
    public void testAddPerson() {
        Person person = new Person();
        person.setUserName("haha");
        person.setUserCode("aaaa");
        organisePersonService.addPerson(person, Organise.DEFAULT_ROOT_ID, true, "aaa");
        Person person1 = loginService.login(person.getUserCode(), "aaa");
        Assert.assertEquals(person.getId(), person1.getId());
    }

    @Test
    public void testQuery() {
        testAddPerson();
        List<Person> people = organisePersonService.getOrganiseDefaultPersons(Organise.DEFAULT_ROOT_ID);
        Assert.assertEquals(2, people.size());
    }

    @Test
    public void testQueryOrganise() {
        testAddPerson();
        Organise organise = organisePersonService.getPersonDefaultOrganise("admin");
        Assert.assertEquals(organise.getId(), Organise.DEFAULT_ROOT_ID);
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
        Assert.assertEquals(2, organises.size());
        Assert.assertEquals(2, organisePersonService.getParentOrganiseList(organise1.getId()).size());
    }


}
