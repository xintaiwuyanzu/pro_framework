package com.dr.framework.sys.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.common.util.IDNo;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.*;
import com.dr.framework.sys.query.OrganiseQuery;
import com.dr.framework.sys.query.PersonQuery;
import com.dr.framework.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.dr.framework.sys.entity.Organise.DEFAULT_ROOT_ID;

/**
 * 默认组织机构实现类
 *
 * @author dr
 */
@Service
public class DefaultSysOrganisePersonService implements SysOrganisePersonService, InitDataService.DataInit {
    @Autowired
    CommonMapper commonMapper;
    @Autowired
    LoginService loginService;

    @Override
    public List<Organise> getParentOrganiseList(String organiseId) {
        Assert.isTrue(!StringUtils.isEmpty(organiseId), "机构编号不能为空！");
        List<Organise> organises = new ArrayList<>();
        while (true) {
            Organise organise = commonMapper.selectOneByQuery(SqlQuery.from(Organise.class).equal(OrganiseInfo.ID, organiseId));
            if (organise == null) {
                break;
            } else {
                organises.add(organise);
                organiseId = organise.getParentId();
                if (StringUtils.isEmpty(organiseId)) {
                    break;
                }
            }
        }
        return organises;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Organise> getOrganiseList(OrganiseQuery organiseQuery) {
        return commonMapper.selectByQuery(organiseQuery.toQuery());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Page<Organise> getOrganisePage(OrganiseQuery organiseQuery, int start, int end) {
        return commonMapper.selectPageByQuery(organiseQuery.toQuery(), start, end);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Person> getPersonList(PersonQuery query) {
        return commonMapper.selectByQuery(query.toQuery());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Page<Person> getPersonPage(PersonQuery query, int start, int end) {
        return commonMapper.selectPageByQuery(query.toQuery(), start, end);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addOrganise(Organise organise) {
        Assert.isTrue(commonMapper.exists(SubSystem.class, organise.getSysId()), "没找到指定的子系统");
        Assert.isTrue(
                !commonMapper.existsByQuery(
                        SqlQuery.from(Organise.class)
                                .equal(OrganiseInfo.ORGANISECODE, organise.getOrganiseCode())
                                .equal(OrganiseInfo.SYSID, organise.getSysId())
                ), "已存在指定的机构编码：" + organise.getOrganiseCode());
        if (StringUtils.isEmpty(organise.getId())) {
            organise.setId(UUID.randomUUID().toString());
        }
        commonMapper.insert(organise);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPerson(Person person, String organiseId, boolean registerLogin, String password) {
        if (StringUtils.isEmpty(person.getSysId())) {
            person.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
        }
        if (!StringUtils.isEmpty(person.getUserCode())) {
            Assert.isTrue(
                    !commonMapper.existsByQuery(
                            SqlQuery.from(Person.class)
                                    .equal(PersonInfo.SYSID, person.getSysId())
                                    .equal(PersonInfo.USERCODE, person.getUserCode())
                    )
                    , "已存在指定的人员编码：" + person.getUserCode());
        }
        //解析身份证信息
        if (!StringUtils.isEmpty(person.getIdNo())) {
            try {
                IDNo idNo = IDNo.from(person.getIdNo());
                person.setBirthday(Long.parseLong(idNo.getBirthday()));
                person.setSex(idNo.isSex() ? 1 : 0);
                person.setIdNo(idNo.getId18());
            } catch (Exception e) {

            }
        }
        person.setUpdateDate(System.currentTimeMillis());
        person.setUpdatePerson(person.getCreatePerson());

        List<Organise> organises = getParentOrganiseList(organiseId);
        Assert.isTrue(organises.size() > 0, "未查询到指定的机构信息！");
        commonMapper.insert(person);
        //保存人员机构关联树信息
        for (Organise organise : organises) {
            PersonOrganise personOrganise = new PersonOrganise();
            personOrganise.setPersonId(person.getId());
            personOrganise.setOrganiseId(organise.getId());
            personOrganise.setDefault(organise.getId().equalsIgnoreCase(organiseId));
            commonMapper.insert(personOrganise);
        }
        if (registerLogin) {
            loginService.bindLogin(person, password);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initData(DataBaseService dataBaseService) {
        //添加默认的子系统信息
        if (dataBaseService.tableExist(SubSystemInfo.TABLE, Constants.SYS_MODULE_NAME)) {
            SubSystem subSystem = new SubSystem();
            subSystem.setId(SubSystem.DEFAULT_SYSTEM_ID);
            subSystem.setSysName("默认系统");
            if (!commonMapper.exists(SubSystem.class, subSystem.getId())) {
                commonMapper.insert(subSystem);
            }
        }
        //添加默认的机构
        if (dataBaseService.tableExist(OrganiseInfo.TABLE, Constants.SYS_MODULE_NAME)) {
            Organise organise = new Organise();
            organise.setId(SubSystem.DEFAULT_SYSTEM_ID + DEFAULT_ROOT_ID);
            organise.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
            organise.setOrganiseName("默认机构");
            if (!commonMapper.exists(Organise.class, organise.getId())) {
                addOrganise(organise);
            }
        }
        //添加默认用户
        if (dataBaseService.tableExist(PersonInfo.TABLE, Constants.SYS_MODULE_NAME)) {
            Person person = new Person();
            person.setId(SubSystem.DEFAULT_SYSTEM_ID + "admin");
            person.setSysId(SubSystem.DEFAULT_SYSTEM_ID);
            person.setUserCode("admin");
            person.setUserName("超级管理员");
            if (!commonMapper.exists(Person.class, person.getId())) {
                addPerson(person, SubSystem.DEFAULT_SYSTEM_ID + DEFAULT_ROOT_ID, true, "1234");
            }
        }
    }

}
