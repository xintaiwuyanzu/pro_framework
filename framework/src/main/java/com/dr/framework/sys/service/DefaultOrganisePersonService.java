package com.dr.framework.sys.service;

import com.dr.framework.autoconfig.CommonConfig;
import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.page.Page;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.common.util.IDNo;
import com.dr.framework.core.event.BaseCRUDEvent;
import com.dr.framework.core.organise.entity.Organise;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.entity.PersonGroup;
import com.dr.framework.core.organise.entity.UserLogin;
import com.dr.framework.core.organise.query.OrganiseQuery;
import com.dr.framework.core.organise.query.PersonQuery;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.OrganisePersonService;
import com.dr.framework.core.organise.service.PassWordEncrypt;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.Column;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.SecurityHolder;
import com.dr.framework.core.security.entity.SubSystem;
import com.dr.framework.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.dr.framework.common.entity.IdEntity.ID_COLUMN_NAME;
import static com.dr.framework.common.entity.OrderEntity.ORDER_COLUMN_NAME;
import static com.dr.framework.common.entity.StatusEntity.STATUS_COLUMN_KEY;
import static com.dr.framework.core.organise.entity.Organise.DEFAULT_ROOT_ID;

/**
 * 默认组织机构实现类
 *
 * @author dr
 */
@Service
public class DefaultOrganisePersonService
        implements RelationHelper, OrganisePersonService, InitDataService.DataInit, InitializingBean {
    Logger logger = LoggerFactory.getLogger(OrganisePersonService.class);
    private CommonMapper commonMapper;
    private LoginService loginService;
    private DefaultDataBaseService defaultDataBaseService;
    private ApplicationEventPublisher applicationEventPublisher;
    private CommonConfig commonConfig;
    @Autowired
    @Lazy
    private PassWordEncrypt passWordEncrypt;
    EntityRelation organiseRelation;
    EntityRelation personRelation;
    EntityRelation personGroupRelation;
    EntityRelation userLoginRelation;


    public DefaultOrganisePersonService(CommonMapper commonMapper,
                                        LoginService loginService,
                                        DefaultDataBaseService defaultDataBaseService,
                                        ApplicationEventPublisher applicationEventPublisher,
                                        CommonConfig commonConfig) {
        this.commonMapper = commonMapper;
        this.loginService = loginService;
        this.defaultDataBaseService = defaultDataBaseService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.commonConfig = commonConfig;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Organise> getParentOrganiseList(String organiseId, String... organiseType) {
        Assert.isTrue(StringUtils.hasText(organiseId), "机构编号不能为空！");
        Organise organise = commonMapper.selectById(Organise.class, organiseId);
        Assert.notNull(organise, "未查询到指定的机构信息");
        SqlQuery<Organise> sqlQuery = SqlQuery.from(organiseRelation)
                .join(organiseRelation.getColumn("id"), EntityOrganiseOrganiseInfo.PARENTID)
                .equal(EntityOrganiseOrganiseInfo.ORGANISEID, organiseId)
                .equal(EntityOrganiseOrganiseInfo.GROUPID, organise.getGroupId())
                .setReturnClass(Organise.class);
        if (organiseType != null && organiseType.length > 0) {
            sqlQuery.in(organiseRelation.getColumn("organise_type"), organiseType);
        }
        return commonMapper.selectByQuery(sqlQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Organise> getChildrenOrganiseList(String organiseId, String... organiseType) {
        Assert.isTrue(StringUtils.hasText(organiseId), "机构编号不能为空！");
        Organise organise = commonMapper.selectById(Organise.class, organiseId);
        Assert.notNull(organise, "未查询到指定的机构信息");
        SqlQuery<Organise> sqlQuery = SqlQuery.from(organiseRelation)
                .join(organiseRelation.getColumn("id"), EntityOrganiseOrganiseInfo.ORGANISEID)
                .equal(EntityOrganiseOrganiseInfo.PARENTID, organiseId)
                .equal(EntityOrganiseOrganiseInfo.GROUPID, organise.getGroupId())
                .setReturnClass(Organise.class);
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
        if (!StringUtils.hasText(organise.getGroupId())) {
            organise.setGroupId(com.dr.framework.core.util.Constants.DEFAULT);
        }
        Assert.isTrue(
                !commonMapper.existsByQuery(
                        SqlQuery.from(organiseRelation)
                                .equal(organiseRelation.getColumn("organise_code"), organise.getOrganiseCode())
                                .equal(organiseRelation.getColumn("group_id"), organise.getGroupId())
                ), "已存在指定的机构编码：" + organise.getOrganiseCode());
        CommonService.bindCreateInfo(organise);
        if (!StringUtils.hasText(organise.getParentId()) && !DEFAULT_ROOT_ID.equalsIgnoreCase(organise.getId())) {
            organise.setParentId(DEFAULT_ROOT_ID);
        }
        if (!StringUtils.hasText(organise.getStatus())) {
            organise.setStatus(StatusEntity.STATUS_ENABLE_STR);
        }
        commonMapper.insert(organise);
        addOrganiseRelation(organise);
        applicationEventPublisher.publishEvent(new BaseCRUDEvent<>(organise, BaseCRUDEvent.EventType.CREATE));
    }

    @Transactional(rollbackFor = Exception.class)
    protected void addOrganiseRelation(Organise organise) {
        //向关联表插入数据
        if (!DEFAULT_ROOT_ID.equals(organise.getId())) {
            List<Organise> parents = getParentOrganiseList(organise.getParentId());
            if (parents != null) {
                for (Organise parent : parents) {
                    EntityOrganiseOrganise organiseOrganise = new EntityOrganiseOrganise();
                    organiseOrganise.setOrganiseId(organise.getId());
                    organiseOrganise.setParentId(parent.getId());
                    organiseOrganise.setDefault(false);
                    organiseOrganise.setGroupId(organise.getGroupId());
                    commonMapper.insert(organiseOrganise);
                }
            }
            EntityOrganiseOrganise organiseOrganise = new EntityOrganiseOrganise();
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
        Assert.isTrue(StringUtils.hasText(person.getUserCode()), "人员编码不能为空");
        Assert.isTrue(StringUtils.hasText(organiseId), "机构编码不能为空");
        Assert.isTrue(
                !commonMapper.existsByQuery(
                        SqlQuery.from(personRelation)
                                .equal(personRelation.getColumn("user_code"), person.getUserCode())
                )
                , "已存在指定的人员编码：" + person.getUserCode());
        //解析身份证信息
        if (StringUtils.hasText(person.getIdNo())) {
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
        if (currentOrganise != null && !StringUtils.hasText(person.getCreateOrganiseId())) {
            person.setCreateOrganiseId(currentOrganise.getId());
        }
        addPersonOrganise(person.getId(), organiseId);
        commonMapper.insert(person);
        //发布创建人员消息
        applicationEventPublisher.publishEvent(new BaseCRUDEvent(person, BaseCRUDEvent.EventType.CREATE));
        if (registerLogin) {
            loginService.bindLogin(person, password);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    protected void addPersonOrganise(String personId, String organiseId) {
        if (!organiseId.equals(DEFAULT_ROOT_ID)) {
            List<Organise> organises = getParentOrganiseList(organiseId);
            Assert.isTrue(!organises.isEmpty(), "未查询到指定的机构信息！");
            //保存人员机构关联树信息
            for (Organise organise : organises) {
                EntityPersonOrganise personOrganise = new EntityPersonOrganise();
                personOrganise.setPersonId(personId);
                personOrganise.setOrganiseId(organise.getId());
                personOrganise.setDefault(organise.getId().equalsIgnoreCase(organiseId));
                commonMapper.insert(personOrganise);
            }
            EntityPersonOrganise personOrganise = new EntityPersonOrganise();
            personOrganise.setPersonId(personId);
            personOrganise.setOrganiseId(organiseId);
            personOrganise.setDefault(true);
            commonMapper.insert(personOrganise);
        } else {
            EntityPersonOrganise personOrganise = new EntityPersonOrganise();
            personOrganise.setPersonId(personId);
            personOrganise.setOrganiseId(DEFAULT_ROOT_ID);
            personOrganise.setDefault(true);
            commonMapper.insert(personOrganise);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGroup(PersonGroup group, String... personIds) {
        if (!StringUtils.hasText(group.getId())) {
            group.setId(UUID.randomUUID().toString());
        }
        Assert.isTrue(StringUtils.hasText(group.getName()), "用户组名称不能为空！");
        CommonService.bindCreateInfo(group);
        if (!StringUtils.hasText(group.getStatus())) {
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
        Assert.isTrue(StringUtils.hasText(groupId), "分组id不能为空！");
        Assert.isTrue(personIds != null && personIds.length > 0, "人员不能为空！");
        Assert.isTrue(commonMapper.exists(PersonGroup.class, groupId), "不存在指定的人员分组！");
        List<String> savedPersons = commonMapper.selectByQuery(
                        SqlQuery.from(EntityPersonGroupRelation.class)
                                .equal(EntityPersonGroupRelationInfo.GROUPID, groupId)
                ).stream()
                .map(EntityPersonGroupRelation::getPersonId)
                .collect(Collectors.toList());
        Arrays.stream(personIds)
                .filter(savedPersons::contains)
                .forEach(p -> {
                    EntityPersonGroupRelation personGroupRelation = new EntityPersonGroupRelation();
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

    /**
     * 机构更新只更新基本信息和关联信息
     * 机构编号不能更新
     *
     * @param organise
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateOrganise(Organise organise) {
        //先查出来原来的机构信息
        Organise old = getOrganise(new OrganiseQuery.Builder().idEqual(organise.getId()).build());
        Assert.notNull(old, "未查询到指定的机构");
        if (StringUtils.hasText(organise.getOrganiseCode())) {
            Assert.isTrue(old.getOrganiseCode().equals(organise.getOrganiseCode()), "机构代码不能更改");
        }
        //如果机构parentid改了，更新机构关联关系
        if (StringUtils.hasText(organise.getParentId()) && !old.getParentId().equals(organise.getParentId())) {
            Assert.notNull(getOrganise(new OrganiseQuery.Builder().idEqual(organise.getId()).build()), "未查询到指定的父机构");
            //1、先删除所有的之前的机构关联关系
            commonMapper.deleteByQuery(SqlQuery.from(EntityOrganiseOrganise.class)
                    .equal(EntityOrganiseOrganiseInfo.ORGANISEID, old.getId())
                    .equal(EntityOrganiseOrganiseInfo.GROUPID, old.getGroupId())
            );
            //2、在添加新的机构关联关系
            addOrganiseRelation(organise);
            //更新机构人员关联关系，这个更改数据很多！！ TODO 这里有很大优化空间
            //1、先查出来该机构所有的人员
            List<Person> people = getPersonList(new PersonQuery.Builder().
                    organiseIdEqual(organise.getId()).
                    build());
            if (people != null && !people.isEmpty()) {
                for (Person person : people) {
                    //2、逐条删除人员机构关联
                    commonMapper.deleteByQuery(
                            SqlQuery.from(EntityPersonOrganise.class)
                                    .equal(EntityPersonOrganiseInfo.PERSONID, person.getIdNo())
                    );
                    //3、添加新的人员机构关联
                    addPersonOrganise(person.getId(), organise.getId());
                }
            }
        }
        //更新机构基本信息
        SqlQuery organiseUpdate = SqlQuery.from(organiseRelation)
                .set(organiseRelation.getColumn("organise_old_name"), organise.getOrganiseOldName())
                .set(organiseRelation.getColumn("organise_name"), organise.getOrganiseName())
                .set(organiseRelation.getColumn("organise_type"), organise.getOrganiseType())
                .set(organiseRelation.getColumn("phone"), organise.getPhone())
                .set(organiseRelation.getColumn("mobile"), organise.getMobile())
                .set(organiseRelation.getColumn("concat_name"), organise.getConcatName())
                .set(organiseRelation.getColumn("address"), organise.getAddress())
                .set(organiseRelation.getColumn("summary"), organise.getSummary())
                .set(organiseRelation.getColumn("latitude"), organise.getLatitude())
                .set(organiseRelation.getColumn("longitude"), organise.getLongitude())
                .set(organiseRelation.getColumn("coordinate_type"), organise.getCoordinateType())
                .set(organiseRelation.getColumn("group_id"), organise.getGroupId())
                .set(organiseRelation.getColumn(ORDER_COLUMN_NAME), organise.getOrder())
                .set(organiseRelation.getColumn(STATUS_COLUMN_KEY), organise.getStatus())
                .equal(organiseRelation.getColumn(ID_COLUMN_NAME), old.getId());
        commonMapper.updateIgnoreNullByQuery(organiseUpdate);

        //TODO 发布更新消息
        Organise nOrganise = getOrganise(new OrganiseQuery.Builder().idEqual(organise.getId()).build());
        applicationEventPublisher.publishEvent(new BaseCRUDEvent(nOrganise, old, BaseCRUDEvent.EventType.UPDATE));
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteOrganise(String organiseId) {
        Assert.isTrue(StringUtils.hasText(organiseId), "机构id不能为空");
        Organise old = getOrganise(new OrganiseQuery.Builder().idEqual(organiseId).build());
        Assert.notNull(old, "未查询到指定的机构");
        List<Organise> child = getChildrenOrganiseList(organiseId);
        List<String> organiseIds = Collections.singletonList(organiseId);
        if (child != null && !child.isEmpty()) {
            organiseIds.addAll(child.stream().map(BaseEntity::getId).collect(Collectors.toList()));
        }
        //删除机构本身和子机构
        commonMapper.deleteByQuery(SqlQuery.from(organiseRelation)
                .in(organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME), organiseIds));
        //删除机构关联
        commonMapper.deleteByQuery(SqlQuery.from(EntityOrganiseOrganise.class)
                .in(EntityOrganiseOrganiseInfo.ORGANISEID, organiseIds));
        //删除人员
        List<Person> people = getPersonList(new PersonQuery.Builder()
                .organiseIdEqual(organiseIds)
                .build()
        );
        if (people != null && !people.isEmpty()) {
            for (Person person : people) {
                deletePerson(person.getId());
            }
        }
        //TODO 删除虚拟组织关联

        applicationEventPublisher.publishEvent(new BaseCRUDEvent(old, BaseCRUDEvent.EventType.DELETE));
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updatePerson(Person person) {
        return updatePerson(person, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updatePerson(Person person, boolean updateLogin) {
        //TODO 这里只是更新基本信息
        Person old = getPerson(new PersonQuery.Builder().idEqual(person.getId()).build());
        Assert.notNull(old, "未查询到指定的用户");
        if (StringUtils.hasText(person.getUserCode())) {
            Assert.isTrue(old.getUserCode().equals(person.getUserCode()), "用户编号不能更改");
        }
        SqlQuery sqlQuery = SqlQuery.from(personRelation)
                .set(personRelation.getColumn("user_name"), person.getUserName())
                .set(personRelation.getColumn("nick_name"), person.getNickName())
                .set(personRelation.getColumn("remark"), person.getRemark())
                .set(personRelation.getColumn("person_type"), person.getPersonType())
                .set(personRelation.getColumn("address"), person.getAddress())
                .set(personRelation.getColumn("duty"), person.getDuty())
                .set(personRelation.getColumn("email"), person.getEmail())
                .set(personRelation.getColumn("mobile"), person.getMobile())
                .set(personRelation.getColumn("phone"), person.getPhone())
                .set(personRelation.getColumn("nation"), person.getNation())
                .set(personRelation.getColumn("weiChatId"), person.getWeiChatId())
                .set(personRelation.getColumn("qq"), person.getQq())
                .set(personRelation.getColumn("address"), person.getAddressId())
                .set(personRelation.getColumn("status_info"), person.getStatus())
                .set(personRelation.getColumn("order_info"), person.getOrder())
                .set(personRelation.getColumn("avatar_file_id"), person.getAvatarFileId())
                .set(personRelation.getColumn(ORDER_COLUMN_NAME), person.getOrder())
                .equal(personRelation.getColumn(ID_COLUMN_NAME), old.getId());

        if (!ObjectUtils.isEmpty(person.getUpdateDate())) {
            sqlQuery.set(personRelation.getColumn("updateDate"), person.getUpdateDate());
        }
        if (StringUtils.hasText(person.getUpdatePerson())) {
            sqlQuery.set(personRelation.getColumn("updatePerson"), person.getUpdatePerson());
        }
        commonMapper.updateByQuery(sqlQuery);
        //查询新的人员信息
        Person nPerson = getPerson(new PersonQuery.Builder().idEqual(person.getId()).build());

        //更新登录账户
        if (updateLogin) {
            List<UserLogin> userLogins = loginService.userLogin(old.getId());
            Map<String, String> oldLoginTypeValues = loginTypeValues(old);
            Map<String, String> newLoginTypeValues = loginTypeValues(nPerson);
            Map<String, UserLogin> userLoginMap = userLogins.stream().collect(Collectors.toMap(UserLogin::getUserType, u -> u));

            //TODO 这里的逻辑还不够严谨
            newLoginTypeValues.entrySet()
                    .stream()
                    .forEach(e -> {
                        String loginType = e.getKey();
                        String loginId = e.getValue();
                        String oldLoginId = oldLoginTypeValues.get(loginType);

                        if (!StringUtils.hasText(loginId)) {
                            //如果新的loginId为空，删除指定的登录账户
                            UserLogin userLogin = userLoginMap.get(loginType);
                            if (userLogin != null) {
                                loginService.removeLogin(userLogin.getLoginId());
                            }
                        } else {
                            if (!StringUtils.hasText(oldLoginId)) {
                                //如果老的login为空，则添加默认的登录账户
                                loginService.addLoginWithDefaultPassWord(person.getId(), loginType, loginId);
                            } else {
                                if (!loginId.equals(oldLoginId)) {
                                    //如果老的loginId与新的loginId不同，则更新loginId
                                    UserLogin userLogin = userLoginMap.get(loginType);
                                    if (userLogin != null) {
                                        userLogin.setLoginId(loginId);
                                        Assert.isTrue(!commonMapper.existsByQuery(
                                                SqlQuery.from(userLoginRelation)
                                                        .equal(userLoginRelation.getColumn("person_id"), person.getId())
                                                        .equal(userLoginRelation.getColumn("user_type"), loginType)
                                                        .equal(userLoginRelation.getColumn("outUser"), person.isOutUser())
                                        ), "已存在指定类型的登录账户");
                                        userLogin.setUpdateDate(System.currentTimeMillis());
                                        Person currentPerson = SecurityHolder.get().currentPerson();
                                        if (currentPerson != null) {
                                            userLogin.setUpdatePerson(currentPerson.getId());
                                        }
                                        commonMapper.updateById(userLogin);
                                    }
                                }
                            }
                        }
                    });
        }
        applicationEventPublisher.publishEvent(new BaseCRUDEvent(nPerson, old, BaseCRUDEvent.EventType.UPDATE));
        return 0;
    }

    private Map<String, String> loginTypeValues(Person person) {
        Map<String, String> map = new HashMap<>(6);
        if (person != null) {
            map.put(LoginService.LOGIN_TYPE_DEFAULT, person.getUserCode());
            map.put(LoginService.LOGIN_TYPE_IDNO, person.getIdNo());
            map.put(LoginService.LOGIN_TYPE_PHONE, person.getPhone());
            map.put(LoginService.LOGIN_TYPE_EMAIL, person.getEmail());
            map.put(LoginService.LOGIN_TYPE_QQ, person.getQq());
            map.put(LoginService.LOGIN_TYPE_WX, person.getWeiChatId());
        }
        return map;
    }

    @Override
    public long changePersonUserCode(String personId, String userCode) {
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long changePersonOrganise(String personId, String organiseId) {
        Assert.isTrue(StringUtils.hasText(personId), "用户id不能为空");
        Assert.isTrue(StringUtils.hasText(organiseId), "机构id不能为空");
        Person person = getPersonById(personId);
        Assert.notNull(person, "未查询到指定用户");
        Assert.isTrue(!organiseId.equals(person.getDefaultOrganiseId()), "新机构与原有机构不能相同");
        //删除机构人员关联
        commonMapper.deleteByQuery(SqlQuery.from(EntityPersonOrganise.class).equal(EntityPersonOrganiseInfo.PERSONID, personId));
        //添加到新的机构
        addPersonOrganise(personId, organiseId);
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deletePerson(String personId) {
        Assert.isTrue(StringUtils.hasText(personId), "人员id不能为空");
        Person old = getPersonById(personId);
        //删除人员本身
        commonMapper.deleteByQuery(SqlQuery.from(personRelation).equal(personRelation.getColumn(IdEntity.ID_COLUMN_NAME), personId));
        //删除登录用户
        loginService.removePersonLogin(personId);
        //删除人员机构关联
        commonMapper.deleteByQuery(SqlQuery.from(EntityPersonOrganise.class)
                .equal(EntityPersonOrganiseInfo.PERSONID, personId)
        );
        //  删除人员所属分组
        commonMapper.deleteByQuery(SqlQuery.from(EntityPersonGroupRelation.class)
                .equal(EntityPersonGroupRelationInfo.PERSONID, personId)
        );
        applicationEventPublisher.publishEvent(new BaseCRUDEvent(old, BaseCRUDEvent.EventType.DELETE));
        return 0;
    }

    private SqlQuery<Person> buildGroupPersonQuery(String groupId) {
        SqlQuery<Person> query = SqlQuery.from(personRelation)
                .in(personRelation.getColumn(IdEntity.ID_COLUMN_NAME)
                        , SqlQuery.from(EntityPersonGroupRelation.class, false)
                                .column(EntityPersonGroupRelationInfo.PERSONID)
                                .equal(EntityPersonGroupRelationInfo.GROUPID, groupId)
                ).setReturnClass(Person.class);
        personQueryJoin(query);
        return query;
    }

    private SqlQuery personQueryJoin(SqlQuery query) {
        return query.join(personRelation.getColumn(IdEntity.ID_COLUMN_NAME), EntityPersonOrganiseInfo.PERSONID)
                .join(organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME), EntityPersonOrganiseInfo.ORGANISEID)
                .equal(EntityPersonOrganiseInfo.ISDEFAULT, true)
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
            subSystem.setOrder(0);
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
            organise.setOrder(0);
            organise.setOrganiseCode(DEFAULT_ROOT_ID);
            if (!commonMapper.exists(Organise.class, organise.getId())) {
                addOrganise(organise);
            }
        }
        //添加默认用户
        if (dataBaseService.tableExist(Person.PERSON_TABLE_NAME, Constants.SYS_MODULE_NAME)) {
            Person person = new Person();
            person.setId("admin");
            person.setOrder(0);
            person.setUserCode("admin");
            person.setUserName("超级管理员");
            if (!commonMapper.exists(Person.class, person.getId())) {
                addPerson(person,
                        DEFAULT_ROOT_ID,
                        true,
                        passWordEncrypt.encodePassword(commonConfig.getDefaultPassWord())
                );
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        organiseRelation = defaultDataBaseService.getTableInfo(Organise.class);
        personRelation = defaultDataBaseService.getTableInfo(Person.class);
        userLoginRelation = defaultDataBaseService.getTableInfo(UserLogin.class);
        personGroupRelation = defaultDataBaseService.getTableInfo(PersonGroup.class);
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
        checkBuildLikeQuery(organiseRelation, query, "group_id", organiseQuery.getGroupId());
        checkBuildNotLikeQuery(organiseRelation, query, "organise_type", organiseQuery.getTypeNotLike());

        if (organiseQuery.getCreateDateStart() != null && organiseQuery.getCreateDateStart() > 0) {
            query.greaterThanEqual(personRelation.getColumn("createDate"), organiseQuery.getCreateDateStart());
        }
        if (organiseQuery.getCreateDateEnd() != null && organiseQuery.getCreateDateEnd() >= organiseQuery.getCreateDateStart() && organiseQuery.getCreateDateEnd() > 0) {
            query.lessThanEqual(personRelation.getColumn("createDate"), organiseQuery.getCreateDateEnd());
        }
        if (organiseQuery.getDirectPersonIds() != null && !organiseQuery.getDirectPersonIds().isEmpty()) {
            query.in(organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(EntityPersonOrganise.class, false)
                            .column(EntityPersonOrganiseInfo.ORGANISEID)
                            .equal(EntityPersonOrganiseInfo.ISDEFAULT, true)
                            .in(EntityPersonOrganiseInfo.PERSONID, organiseQuery.getDirectPersonIds())
            );
        }
        if (organiseQuery.getPersonIds() != null && !organiseQuery.getPersonIds().isEmpty()) {
            query.in(organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(EntityPersonOrganise.class, false)
                            .column(EntityPersonOrganiseInfo.ORGANISEID)
                            .in(EntityPersonOrganiseInfo.PERSONID, organiseQuery.getPersonIds())
            );
        }

        if (organiseQuery.getTreeParentId() != null && !organiseQuery.getTreeParentId().isEmpty()) {
            query.in(organiseRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(EntityOrganiseOrganise.class, false)
                            .column(EntityOrganiseOrganiseInfo.ORGANISEID)
                            .in(EntityOrganiseOrganiseInfo.PARENTID, organiseQuery.getTreeParentId())
            );
        }
        if (StringUtils.hasText(organiseQuery.getCodeEqual())) {
            query.equal(organiseRelation.getColumn("organise_code"), organiseQuery.getCodeEqual());
        }
        query.orderBy(organiseRelation.getColumn(ORDER_COLUMN_NAME));
        return query;
    }

    /**
     * personQuery对象转换成sqlquery对象
     *
     * @param personQuery
     * @return
     */
    protected SqlQuery<Person> personQueryToSqlQuery(PersonQuery personQuery) {
        SqlQuery query = SqlQuery.from(personRelation);
        if (StringUtils.hasText(personQuery.getCreatePerson())) {
            query.equal(personRelation.getColumn("createPerson"), personQuery.getCreatePerson());
        }
        checkBuildInQuery(personRelation, query, IdEntity.ID_COLUMN_NAME, personQuery.getIds());
        checkBuildLikeQuery(personRelation, query, "id_no", personQuery.getIdNo());
        checkBuildLikeQuery(personRelation, query, "user_name", personQuery.getPersonName());
        checkBuildLikeQuery(personRelation, query, "nick_name", personQuery.getNickName());
        checkBuildLikeQuery(personRelation, query, "email", personQuery.getEmail());
        checkBuildLikeQuery(personRelation, query, "user_name", personQuery.getPersonName());
        checkBuildLikeQuery(personRelation, query, "phone", personQuery.getPhone());
        checkBuildLikeQuery(personRelation, query, "qq", personQuery.getQq());
        checkBuildLikeQuery(personRelation, query, "weiChatId", personQuery.getWeiChatId());
        checkBuildLikeQuery(personRelation, query, "person_type", personQuery.getTypeLike());
        checkBuildLikeQuery(personRelation, query, "duty", personQuery.getDuty());
        checkBuildLikeQuery(personRelation, query, "user_code", personQuery.getUserCode());
        checkBuildNotLikeQuery(personRelation, query, "person_type", personQuery.getTypeNotLike());
        checkBuildInQuery(personRelation, query, "nation", personQuery.getNation());
        checkBuildInQuery(personRelation, query, "person_type", personQuery.getPersonType());
        checkBuildInQuery(personRelation, query, STATUS_COLUMN_KEY, personQuery.getStatus());
        checkBuildInQuery(personRelation, query, "source_ref", personQuery.getSourceRef());
        checkBuildNotInQuery(personRelation, query, "person_type", personQuery.getPersonTypeNotIn());
        checkBuildNotInQuery(personRelation, query, STATUS_COLUMN_KEY, personQuery.getStatusNotIn());
        checkBuildNotInQuery(personRelation, query, "source_ref", personQuery.getSourceRefNotIn());
        if (personQuery.getDutyNull() != null) {
            Column duty = personRelation.getColumn("duty");
            if (personQuery.getDutyNull()) {
                query.isNull(duty);
            } else {
                query.isNotNull(duty);
            }
        }
        if (personQuery.getLoginId() != null && !personQuery.getLoginId().isEmpty()) {
            query.in(personRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(userLoginRelation, false)
                            .column(userLoginRelation.getColumn("person_id"))
                            .in(userLoginRelation.getColumn("login_id"), personQuery.getLoginId())
            );
        }
        if (personQuery.getOrganiseId() != null && !personQuery.getOrganiseId().isEmpty()) {
            query.in(personRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(EntityPersonOrganise.class, false)
                            .column(EntityPersonOrganiseInfo.PERSONID)
                            .in(EntityPersonOrganiseInfo.ORGANISEID, personQuery.getOrganiseId())
            );
        }
        if (personQuery.getDefaultOrganiseId() != null && !personQuery.getDefaultOrganiseId().isEmpty()) {
            query.in(personRelation.getColumn(IdEntity.ID_COLUMN_NAME),
                    SqlQuery.from(EntityPersonOrganise.class, false)
                            .column(EntityPersonOrganiseInfo.PERSONID)
                            .in(EntityPersonOrganiseInfo.ORGANISEID, personQuery.getDefaultOrganiseId())
                            .equal(EntityPersonOrganiseInfo.ISDEFAULT, true)

            );
        }
        if (personQuery.getBirthDayStart() != null && personQuery.getBirthDayStart() > 0) {
            query.greaterThanEqual(personRelation.getColumn("birthday"), personQuery.getBirthDayStart());
        }
        if (personQuery.getBirthDayEnd() != null && personQuery.getBirthDayEnd() >= personQuery.getBirthDayStart() && personQuery.getBirthDayEnd() > 0) {
            query.lessThanEqual(personRelation.getColumn("birthday"), personQuery.getBirthDayEnd());
        }
        if (personQuery.getCreateDayStart() != null && personQuery.getCreateDayStart() > 0) {
            query.greaterThanEqual(personRelation.getColumn("createDate"), personQuery.getCreateDayStart());
        }
        if (personQuery.getCreateDayStart() != null && personQuery.getCreateDayEnd() >= personQuery.getCreateDayStart() && personQuery.getCreateDayEnd() > 0) {
            query.lessThanEqual(personRelation.getColumn("createDate"), personQuery.getCreateDayEnd());
        }
        query.orderBy(personRelation.getColumn(ORDER_COLUMN_NAME))
                .equal(EntityPersonOrganiseInfo.ISDEFAULT, true);
        return personQueryJoin(query);
    }


}
