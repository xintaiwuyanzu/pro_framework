package com.dr.framework.sys.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.common.util.IDNo;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.OrganiseOrganise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.entity.PersonOrganise;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.SysOrganisePersonService;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.system.entity.SubSystem;
import com.dr.framework.core.organise.query.OrganiseQuery;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.util.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.dr.framework.core.organise.entity.Organise.DEFAULT_ROOT_ID;

/**
 * 默认组织机构实现类
 *
 * @author dr
 */
@Service
public class DefaultSysOrganisePersonService implements SysOrganisePersonService, InitDataService.DataInit, InitializingBean {
    @Autowired
    CommonMapper commonMapper;
    @Autowired
    LoginService loginService;
    @Autowired
    DefaultDataBaseService defaultDataBaseService;
    EntityRelation organiseEntity;
    EntityRelation personEntity;
    EntityRelation organiseOrganiseEntity;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Organise> getParentOrganiseList(String organiseId, String... organiseType) {
        Assert.isTrue(!StringUtils.isEmpty(organiseId), "机构编号不能为空！");
        Organise organise = commonMapper.selectById(Organise.class, organiseId);
        Assert.notNull(organise, "未查询到指定的机构信息");
        SqlQuery<Organise> sqlQuery = SqlQuery.from(Organise.class)
                .join(
                        organiseEntity.getColumn("id")
                        , organiseOrganiseEntity.getColumn("parent_id")
                )
                .equal(
                        organiseOrganiseEntity.getColumn("organise_id"),
                        organiseId
                ).equal(
                        organiseOrganiseEntity.getColumn("group_id"),
                        organise.getGroupId()
                );
        if (organiseType != null && organiseType.length > 0) {
            sqlQuery.in(organiseEntity.getColumn("organise_type"), organiseType);
        }
        return commonMapper.selectByQuery(sqlQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Organise> getChildrenOrganiseList(String organiseId, String... organiseType) {
        Assert.isTrue(!StringUtils.isEmpty(organiseId), "机构编号不能为空！");
        Organise organise = commonMapper.selectById(Organise.class, organiseId);
        Assert.notNull(organise, "未查询到指定的机构信息");
        SqlQuery<Organise> sqlQuery = SqlQuery.from(Organise.class)
                .join(
                        organiseEntity.getColumn("id")
                        , organiseOrganiseEntity.getColumn("organise_id")
                )
                .equal(
                        organiseOrganiseEntity.getColumn("parent_id"),
                        organiseId
                ).equal(
                        organiseOrganiseEntity.getColumn("group_id"),
                        organise.getGroupId()
                );
        if (organiseType != null && organiseType.length > 0) {
            sqlQuery.in(organiseEntity.getColumn("organise_type"), organiseType);
        }
        return commonMapper.selectByQuery(sqlQuery);
    }

    @Override
    public List<Organise> getOrganiseList(OrganiseQuery organiseQuery) {
        return commonMapper.selectByQuery(organiseQueryToSqlQuery(organiseQuery));
    }

    @Override
    public long getOrganiseCount(OrganiseQuery organiseQuery) {
        return commonMapper.countByQuery(organiseQueryToSqlQuery(organiseQuery));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<Organise> getOrganisePage(OrganiseQuery organiseQuery, int start, int end) {
        return commonMapper.selectPageByQuery(organiseQueryToSqlQuery(organiseQuery), start, end);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Person> getPersonList(PersonQuery query) {
        return commonMapper.selectByQuery(personQueryToSqlQuery(query));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Page<Person> getPersonPage(PersonQuery query, int start, int end) {
        return commonMapper.selectPageByQuery(personQueryToSqlQuery(query), start, end);
    }

    @Override
    public long getPersonCount(PersonQuery query) {
        return commonMapper.countByQuery(personQueryToSqlQuery(query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrganise(Organise organise) {
        if (StringUtils.isEmpty(organise.getGroupId())) {
            organise.setGroupId(com.dr.framework.core.util.Constants.DEFAULT);
        }
        Assert.isTrue(
                !commonMapper.existsByQuery(
                        SqlQuery.from(Organise.class)
                                .equal(organiseEntity.getColumn("organise_code"), organise.getOrganiseCode())
                                .equal(organiseEntity.getColumn("group_id"), organise.getGroupId())
                ), "已存在指定的机构编码：" + organise.getOrganiseCode());
        if (StringUtils.isEmpty(organise.getId())) {
            organise.setId(UUID.randomUUID().toString());
        }
        commonMapper.insert(organise);
        //向关联表插入数据
        if (!DEFAULT_ROOT_ID.equals(organise.getId())) {
            List<Organise> parents = getParentOrganiseList(organise.getParentId());
            if (parents != null) {
                for (Organise parent : parents) {
                    OrganiseOrganise organiseOrganise = new OrganiseOrganise();
                    organiseOrganise.setOrganiseId(organise.getId());
                    organiseOrganise.setParentId(parent.getId());
                    organiseOrganise.setDefault(false);
                    organiseOrganise.setGroupId(organise.getGroupId());
                    commonMapper.insert(organiseOrganise);
                }
            }
            String parentId = organise.getParentId();
            if (StringUtils.isEmpty(parentId)) {
                parentId = DEFAULT_ROOT_ID;
            }
            OrganiseOrganise organiseOrganise = new OrganiseOrganise();
            organiseOrganise.setOrganiseId(organise.getId());
            organiseOrganise.setParentId(parentId);
            organiseOrganise.setDefault(true);
            organiseOrganise.setGroupId(organise.getGroupId());
            commonMapper.insert(organiseOrganise);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPerson(Person person, String organiseId, boolean registerLogin, String password) {
        Assert.isTrue(!StringUtils.isEmpty(person.getUserCode()), "人员编码不能为空");
        Assert.isTrue(
                !commonMapper.existsByQuery(
                        SqlQuery.from(Person.class)
                                .equal(personEntity.getColumn("user_code"), person.getUserCode())
                )
                , "已存在指定的人员编码：" + person.getUserCode());
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
        if (dataBaseService.tableExist(SubSystem.SUBSYS_TABLE_NAME, Constants.SYS_MODULE_NAME)) {
            SubSystem subSystem = new SubSystem();
            subSystem.setId(SubSystem.DEFAULT_SYSTEM_ID);
            subSystem.setSysName("默认系统");
            if (!commonMapper.exists(SubSystem.class, subSystem.getId())) {
                commonMapper.insert(subSystem);
            }
        }
        //添加默认的机构
        if (dataBaseService.tableExist(Organise.ORGANISE_TABLE_NAME, Constants.SYS_MODULE_NAME)) {
            Organise organise = new Organise();
            organise.setId(SubSystem.DEFAULT_SYSTEM_ID + DEFAULT_ROOT_ID);
            organise.setOrganiseName("默认机构");
            if (!commonMapper.exists(Organise.class, organise.getId())) {
                addOrganise(organise);
            }
        }
        //添加默认用户
        if (dataBaseService.tableExist(Person.PERSON_TABLE_NAME, Constants.SYS_MODULE_NAME)) {
            Person person = new Person();
            person.setId(SubSystem.DEFAULT_SYSTEM_ID + "admin");
            person.setUserCode("admin");
            person.setUserName("超级管理员");
            if (!commonMapper.exists(Person.class, person.getId())) {
                addPerson(person, SubSystem.DEFAULT_SYSTEM_ID + DEFAULT_ROOT_ID, true, "1234");
            }
        }
    }

    /**
     * OrganiseQuery对象转换成sqlquery对象
     *
     * @param query
     * @return
     */
    protected SqlQuery<Organise> organiseQueryToSqlQuery(OrganiseQuery query) {
        return null;
    }

    /**
     * personQuery对象转换成sqlquery对象
     *
     * @param query
     * @return
     */
    protected SqlQuery<Person> personQueryToSqlQuery(PersonQuery query) {
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        organiseEntity = defaultDataBaseService.getTableInfo(Organise.class);
        organiseOrganiseEntity = defaultDataBaseService.getTableInfo(OrganiseOrganise.class);
        personEntity = defaultDataBaseService.getTableInfo(Person.class);
    }
}
