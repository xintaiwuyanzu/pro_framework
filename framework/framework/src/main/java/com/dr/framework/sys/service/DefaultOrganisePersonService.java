package com.dr.framework.sys.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.common.util.IDNo;
import com.dr.framework.core.organise.entity.*;
import com.dr.framework.core.organise.query.OrganiseQuery;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.system.entity.SubSystem;
import com.dr.framework.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dr.framework.common.entity.StatusEntity.STATUS_COLUMN_KEY;
import static com.dr.framework.core.organise.entity.Organise.DEFAULT_ROOT_ID;

/**
 * 默认组织机构实现类
 *
 * @author dr
 */
@Service
public class DefaultOrganisePersonService implements OrganisePersonService, InitDataService.DataInit, InitializingBean {
    Logger logger = LoggerFactory.getLogger(OrganisePersonService.class);
    @Autowired
    CommonMapper commonMapper;
    @Autowired
    LoginService loginService;
    @Autowired
    DefaultDataBaseService defaultDataBaseService;

    EntityRelation organiseRelation;
    EntityRelation personRelation;
    EntityRelation organiseOrganiseRelation;
    EntityRelation userLoginRelation;
    EntityRelation personOrganiseRelation;
    EntityRelation personGroupRelation;
    EntityRelation personGroupRelationRelation;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Organise> getParentOrganiseList(String organiseId, String... organiseType) {
        Assert.isTrue(!StringUtils.isEmpty(organiseId), "机构编号不能为空！");
        Organise organise = commonMapper.selectById(Organise.class, organiseId);
        Assert.notNull(organise, "未查询到指定的机构信息");
        SqlQuery sqlQuery = SqlQuery.from(organiseRelation)
                .join(
                        organiseRelation.getColumn("id")
                        , organiseOrganiseRelation.getColumn("parent_id")
                )
                .equal(
                        organiseOrganiseRelation.getColumn("organise_id"),
                        organiseId
                ).equal(
                        organiseOrganiseRelation.getColumn("group_id"),
                        organise.getGroupId()
                );
        if (organiseType != null && organiseType.length > 0) {
            sqlQuery.in(organiseRelation.getColumn("organise_type"), organiseType);
        }
        return commonMapper.selectByQuery(sqlQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Organise> getChildrenOrganiseList(String organiseId, String... organiseType) {
        Assert.isTrue(!StringUtils.isEmpty(organiseId), "机构编号不能为空！");
        Organise organise = commonMapper.selectById(Organise.class, organiseId);
        Assert.notNull(organise, "未查询到指定的机构信息");
        SqlQuery sqlQuery = SqlQuery.from(organiseRelation)
                .join(
                        organiseRelation.getColumn("id")
                        , organiseOrganiseRelation.getColumn("organise_id")
                )
                .equal(
                        organiseOrganiseRelation.getColumn("parent_id"),
                        organiseId
                ).equal(
                        organiseOrganiseRelation.getColumn("group_id"),
                        organise.getGroupId()
                );
        if (organiseType != null && organiseType.length > 0) {
            sqlQuery.in(organiseRelation.getColumn("organise_type"), organiseType);
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
                        SqlQuery.from(organiseRelation)
                                .equal(organiseRelation.getColumn("organise_code"), organise.getOrganiseCode())
                                .equal(organiseRelation.getColumn("group_id"), organise.getGroupId())
                ), "已存在指定的机构编码：" + organise.getOrganiseCode());
        CommonService.bindCreateInfo(organise);
        if (StringUtils.isEmpty(organise.getParentId())) {
            organise.setParentId(DEFAULT_ROOT_ID);
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
            OrganiseOrganise organiseOrganise = new OrganiseOrganise();
            organiseOrganise.setOrganiseId(organise.getId());
            organiseOrganise.setParentId(organise.getParentId());
            organiseOrganise.setDefault(true);
            organiseOrganise.setGroupId(organise.getGroupId());
            commonMapper.insert(organiseOrganise);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPerson(Person person, String organiseId, boolean registerLogin, String password) {
        Assert.isTrue(!StringUtils.isEmpty(person.getUserCode()), "人员编码不能为空");
        Assert.isTrue(!StringUtils.isEmpty(organiseId), "机构编码不能为空");
        Assert.isTrue(
                !commonMapper.existsByQuery(
                        SqlQuery.from(personRelation)
                                .equal(personRelation.getColumn("user_code"), person.getUserCode())
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
                logger.info("解析人员身份证信息失败：" + e.getMessage());
            }
        }
        CommonService.bindCreateInfo(person);
        SecurityHolder securityHolder = SecurityHolder.get();
        Organise currentOrganise = securityHolder.currentOrganise();
        if (currentOrganise != null && StringUtils.isEmpty(person.getCreateOrganiseId())) {
            person.setCreateOrganiseId(currentOrganise.getId());
        }

        if (!organiseId.equals(DEFAULT_ROOT_ID)) {
            List<Organise> organises = getParentOrganiseList(organiseId);
            Assert.isTrue(organises.size() > 0, "未查询到指定的机构信息！");
            //保存人员机构关联树信息
            for (Organise organise : organises) {
                PersonOrganise personOrganise = new PersonOrganise();
                personOrganise.setPersonId(person.getId());
                personOrganise.setOrganiseId(organise.getId());
                personOrganise.setDefault(organise.getId().equalsIgnoreCase(organiseId));
                commonMapper.insert(personOrganise);
            }
        }
        PersonOrganise personOrganise = new PersonOrganise();
        personOrganise.setPersonId(person.getId());
        personOrganise.setOrganiseId(DEFAULT_ROOT_ID);
        personOrganise.setDefault(DEFAULT_ROOT_ID.equalsIgnoreCase(organiseId));
        commonMapper.insert(personOrganise);
        commonMapper.insert(person);
        if (registerLogin) {
            loginService.bindLogin(person, password);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGroup(PersonGroup group, String... personIds) {
        if (StringUtils.isEmpty(group.getId())) {
            group.setId(UUID.randomUUID().toString());
        }
        Assert.isTrue(!StringUtils.isEmpty(group.getName()), "用户组名称不能为空！");
        CommonService.bindCreateInfo(group);
        if (StringUtils.isEmpty(group.getStatus())) {
            group.setStatus(StatusEntity.STATUS_ENABLE_STR);
        }
        commonMapper.insert(group);
        addPersonToGroup(group.getId(), personIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PersonGroup> getGroups(String name, String... types) {
        return commonMapper.selectByQuery(buildPersonGroupQuery(name, types));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<PersonGroup> getGroupPage(String name, int start, int end, String... types) {
        return commonMapper.selectPageByQuery(buildPersonGroupQuery(name, types), start, end);
    }

    private SqlQuery buildPersonGroupQuery(String name, String[] types) {
        return SqlQuery.from(personGroupRelation)
                .like(personGroupRelation.getColumn("group_name"), name)
                .in(personRelation.getColumn("group_type"), types);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPersonToGroup(String groupId, String... personIds) {
        Assert.isTrue(!StringUtils.isEmpty(groupId), "分组id不能为空！");
        Assert.isTrue(personIds != null && personIds.length > 0, "人员不能为空！");
        Assert.isTrue(commonMapper.exists(PersonGroup.class, groupId), "不存在指定的人员分组！");
        List<Object> personGroupRelations = commonMapper.selectByQuery(
                SqlQuery.from(personGroupRelationRelation)
                        .equal(personGroupRelationRelation.getColumn("groupId"), groupId)
        );
        List<String> savedPersons = personGroupRelations.stream()
                .map(o -> ((PersonGroupRelation) o).getPersonId())
                .collect(Collectors.toList());
        Arrays.asList(personIds)
                .stream()
                .filter(p -> savedPersons.contains(p))
                .forEach(p -> {
                    PersonGroupRelation personGroupRelation = new PersonGroupRelation();
                    personGroupRelation.setGroupId(groupId);
                    personGroupRelation.setPersonId(p);
                    commonMapper.insert(personGroupRelation);
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Person> groupPerson(String groupId) {
        return commonMapper.selectByQuery(buildGroupPersonQuery(groupId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page<Person> groupPersonPage(String groupId, int start, int end) {
        return commonMapper.selectPageByQuery(buildGroupPersonQuery(groupId), start, end);
    }

    private SqlQuery buildGroupPersonQuery(String groupId) {
        SqlQuery query = SqlQuery.from(personRelation)
                .in(personRelation.getColumn(IdEntity.ID_COLUMN_NAME)
                        , SqlQuery.from(personGroupRelationRelation, false)
                                .column(personGroupRelationRelation.getColumn("personId"))
                                .equal(personGroupRelationRelation.getColumn("groupId"), groupId)
                );
        personQueryJoin(query);
        return query;
    }

    private SqlQuery personQueryJoin(SqlQuery query) {
        return query.join(personRelation.getColumn(IdEntity.ID_COLUMN_NAME), personOrganiseRelation.getColumn("person_id"))
                .join(organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME), personOrganiseRelation.getColumn("organise_id"))
                .equal(personOrganiseRelation.getColumn("is_default"), true)
                .column(
                        organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME).alias("defaultOrganiseId")
                        , organiseRelation.getColumn("organise_name").alias("defaultOrganiseName")
                );
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
            organise.setId(DEFAULT_ROOT_ID);
            organise.setOrganiseName("默认机构");
            organise.setOrganiseCode(DEFAULT_ROOT_ID);
            if (!commonMapper.exists(Organise.class, organise.getId())) {
                addOrganise(organise);
            }
        }
        //添加默认用户
        if (dataBaseService.tableExist(Person.PERSON_TABLE_NAME, Constants.SYS_MODULE_NAME)) {
            Person person = new Person();
            person.setId("admin");
            person.setUserCode("admin");
            person.setUserName("超级管理员");
            if (!commonMapper.exists(Person.class, person.getId())) {
                addPerson(person, DEFAULT_ROOT_ID, true, "1234");
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        organiseRelation = defaultDataBaseService.getTableInfo(Organise.class);
        organiseOrganiseRelation = defaultDataBaseService.getTableInfo(OrganiseOrganise.class);
        personRelation = defaultDataBaseService.getTableInfo(Person.class);
        userLoginRelation = defaultDataBaseService.getTableInfo(UserLogin.class);
        personOrganiseRelation = defaultDataBaseService.getTableInfo(PersonOrganise.class);
        personGroupRelation = defaultDataBaseService.getTableInfo(PersonGroup.class);
        personGroupRelationRelation = defaultDataBaseService.getTableInfo(PersonGroupRelation.class);
    }

    /**
     * OrganiseQuery对象转换成sqlquery对象
     *
     * @param organiseQuery
     * @return
     */
    protected SqlQuery<Organise> organiseQueryToSqlQuery(OrganiseQuery organiseQuery) {
        SqlQuery query = SqlQuery.from(organiseRelation);
        checkBuildInQuery(organiseRelation, query, IdEntity.ID_COLUMN_NAME, organiseQuery.getIds());
        checkBuildInQuery(organiseRelation, query, "organise_type", organiseQuery.getOrganiseType());
        checkBuildInQuery(organiseRelation, query, "parent_id", organiseQuery.getParentIds());
        checkBuildInQuery(organiseRelation, query, STATUS_COLUMN_KEY, organiseQuery.getStatus());
        checkBuildInQuery(organiseRelation, query, "createPerson", organiseQuery.getCreatePersons());
        checkBuildInQuery(organiseRelation, query, "source_ref", organiseQuery.getSourceRef());

        checkBuildNotInQuery(organiseRelation, query, "organise_type", organiseQuery.getOrganiseTypeNotIn());
        checkBuildNotInQuery(organiseRelation, query, "parent_id", organiseQuery.getParentIdNotIn());
        checkBuildNotInQuery(organiseRelation, query, STATUS_COLUMN_KEY, organiseQuery.getStatusNotIn());
        checkBuildNotInQuery(organiseRelation, query, "source_ref", organiseQuery.getSourceRefNotIn());

        checkBuildLikeQuery(organiseRelation, query, "organise_name", organiseQuery.getOrganiseName());
        checkBuildLikeQuery(organiseRelation, query, "organise_type", organiseQuery.getTypeLike());
        checkBuildNotLikeQuery(organiseRelation, query, "organise_type", organiseQuery.getTypeNotLike());

        if (organiseQuery.getCreateDateStart() != null && organiseQuery.getCreateDateStart() > 0) {
            query.greaterThanEqual(personRelation.getColumn("createDate"), organiseQuery.getCreateDateStart());
        }
        if (organiseQuery.getCreateDateEnd() != null && organiseQuery.getCreateDateEnd() >= organiseQuery.getCreateDateStart() && organiseQuery.getCreateDateEnd() > 0) {
            query.lessThanEqual(personRelation.getColumn("createDate"), organiseQuery.getCreateDateEnd());
        }
        if (organiseQuery.getDirectPersonIds() != null && !organiseQuery.getDirectPersonIds().isEmpty()) {
            query.in(organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(personOrganiseRelation, false)
                            .column(personOrganiseRelation.getColumn("organise_id"))
                            .equal(personOrganiseRelation.getColumn("is_default"), true)
                            .in(personOrganiseRelation.getColumn("person_id"), organiseQuery.getDirectPersonIds())
            );
        }
        if (organiseQuery.getPersonIds() != null && !organiseQuery.getPersonIds().isEmpty()) {
            query.in(organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(personOrganiseRelation, false)
                            .column(personOrganiseRelation.getColumn("organise_id"))
                            .in(personOrganiseRelation.getColumn("person_id"), organiseQuery.getPersonIds())
            );
        }
        return query;
    }

    /**
     * personQuery对象转换成sqlquery对象
     *
     * @param personQuery
     * @return
     */
    protected SqlQuery<Person> personQueryToSqlQuery(PersonQuery personQuery) {
        SqlQuery query = SqlQuery.from(personRelation)
                .equal(personRelation.getColumn(IdEntity.ID_COLUMN_NAME), personQuery.getId());
        checkBuildLikeQuery(personRelation, query, "user_name", personQuery.getPersonName());
        checkBuildLikeQuery(personRelation, query, "nick_name", personQuery.getNickName());
        checkBuildLikeQuery(personRelation, query, "email", personQuery.getEmail());
        checkBuildLikeQuery(personRelation, query, "user_name", personQuery.getPersonName());
        checkBuildLikeQuery(personRelation, query, "phone", personQuery.getPhone());
        checkBuildLikeQuery(personRelation, query, "qq", personQuery.getQq());
        checkBuildLikeQuery(personRelation, query, "weiChatId", personQuery.getWeiChatId());
        checkBuildLikeQuery(personRelation, query, "person_type", personQuery.getTypeLike());
        checkBuildNotLikeQuery(personRelation, query, "person_type", personQuery.getTypeNotLike());
        checkBuildInQuery(personRelation, query, "nation", personQuery.getNation());
        checkBuildInQuery(personRelation, query, "person_type", personQuery.getPersonType());
        checkBuildInQuery(personRelation, query, STATUS_COLUMN_KEY, personQuery.getStatus());
        checkBuildInQuery(personRelation, query, "source_ref", personQuery.getSourceRef());
        checkBuildNotInQuery(personRelation, query, "person_type", personQuery.getPersonTypeNotIn());
        checkBuildNotInQuery(personRelation, query, STATUS_COLUMN_KEY, personQuery.getStatusNotIn());
        checkBuildNotInQuery(personRelation, query, "source_ref", personQuery.getSourceRefNotIn());

        if (personQuery.getLoginId() != null && !personQuery.getLoginId().isEmpty()) {
            query.in(personRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(userLoginRelation, false)
                            .column(userLoginRelation.getColumn("person_id"))
                            .in(userLoginRelation.getColumn("login_id"), personQuery.getLoginId())
            );
        }
        if (personQuery.getOrganiseId() != null && !personQuery.getOrganiseId().isEmpty()) {
            query.in(personRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(personOrganiseRelation, false)
                            .column(personOrganiseRelation.getColumn("person_id"))
                            .in(personOrganiseRelation.getColumn("organise_id"), personQuery.getOrganiseId())
            );
        }
        if (personQuery.getDefaultOrganiseId() != null && !personQuery.getDefaultOrganiseId().isEmpty()) {
            query.in(personRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(personOrganiseRelation, false)
                            .column(personOrganiseRelation.getColumn("person_id"))
                            .in(personOrganiseRelation.getColumn("organise_id"), personQuery.getDefaultOrganiseId())
                            .equal(personOrganiseRelation.getColumn("is_default"), true)

            );
        }
        if (personQuery.getBirthDayStart() != null && personQuery.getBirthDayStart() > 0) {
            query.greaterThanEqual(personRelation.getColumn("birthday"), personQuery.getBirthDayStart());
        }
        if (personQuery.getBirthDayEnd() != null && personQuery.getBirthDayEnd() >= personQuery.getBirthDayStart() && personQuery.getBirthDayEnd() > 0) {
            query.lessThanEqual(personRelation.getColumn("birthday"), personQuery.getBirthDayEnd());
        }
        return personQueryJoin(query);
    }

    private void checkBuildInQuery(EntityRelation relation, SqlQuery query, String columnName, List values) {
        if (values != null && !values.isEmpty()) {
            query.in(relation.getColumn(columnName), values);
        }
    }

    private void checkBuildNotInQuery(EntityRelation relation, SqlQuery<Person> query, String columnName, List values) {
        if (values != null && !values.isEmpty()) {
            query.notIn(relation.getColumn(columnName), values);
        }
    }

    private void checkBuildLikeQuery(EntityRelation relation, SqlQuery query, String columnName, String value) {
        if (!StringUtils.isEmpty(value)) {
            query.like(relation.getColumn(columnName), value);
        }
    }

    private void checkBuildNotLikeQuery(EntityRelation relation, SqlQuery query, String columnName, String value) {
        if (!StringUtils.isEmpty(value)) {
            query.notLike(relation.getColumn(columnName), value);
        }
    }
}
