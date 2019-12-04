package com.dr.process.camunda.service;

import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.OrganisePersonService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.impl.identity.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 扩展service为流程提供表达式支持
 *
 * @author dr
 */
@Service("current")
public class CurrentService {
    private IdentityService identityService;
    private OrganisePersonService organisePersonService;

    public CurrentService(IdentityService identityService, OrganisePersonService organisePersonService) {
        this.identityService = identityService;
        this.organisePersonService = organisePersonService;
    }

    /**
     * 使用表达式${current.personId()}
     * 获取当前登录id
     *
     * @return
     */
    public String personId() {
        Authentication authentication = identityService.getCurrentAuthentication();
        Assert.notNull(authentication, "当前登录人信息为空");
        return authentication.getUserId();
    }

    /**
     * 使用表达式${current.person()}
     * 获取流程当前登录人员详细信息
     *
     * @return
     */
    public Person person() {
        Person person = organisePersonService.getPersonById(personId());
        Assert.notNull(person, "未找到指定的用户");
        return person;
    }

    /**
     * 使用表达式${current.organise()}
     * 获取当前机构
     *
     * @return
     */
    public Organise organise() {
        Organise organise = organisePersonService.getPersonDefaultOrganise(personId());
        Assert.notNull(organise, "未获取到当前机构");
        return organise;
    }

    /**
     * 使用表达式${current.organiseId()}
     * 获取当前机构Id
     *
     * @return
     */
    public String organiseId() {
        return organise().getId();
    }

    /**
     * 使用表达式${current.organisePerson(duty,type)}
     * 获取指定类型的用户
     *
     * @param duty
     * @return
     */
    public List<Person> organisePerson(String duty, String type) {
        return organisePersonService.getPersonList(new PersonQuery.Builder()
                .organiseIdEqual(organiseId())
                .dutyLike(duty).typeLike(type)
                .statusEqual(Person.STATUS_ENABLE_STR)
                .build());
    }

    /**
     * 使用表达式${current.organisePersonIds(duty,type)}
     * 获取指定职责类型的用户
     *
     * @param duty
     * @param type
     * @return
     */
    public List<String> organisePersonIds(String duty, String type) {
        return organisePerson(duty, type)
                .stream()
                .map(p -> p.getId())
                .collect(Collectors.toList());
    }

    /**
     * 使用${current.time()}
     * 获取当前日期
     *
     * @return
     */
    public long time() {
        return System.currentTimeMillis();
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public OrganisePersonService getOrganisePersonService() {
        return organisePersonService;
    }

    public void setOrganisePersonService(OrganisePersonService organisePersonService) {
        this.organisePersonService = organisePersonService;
    }
}
