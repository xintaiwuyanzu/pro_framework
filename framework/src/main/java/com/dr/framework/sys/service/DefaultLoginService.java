package com.dr.framework.sys.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.BaseEntity;
import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.entity.UserLogin;
import com.dr.framework.core.organise.service.LoginService;
import com.dr.framework.core.organise.service.PassWordEncrypt;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.dr.framework.common.entity.StatusEntity.STATUS_COLUMN_KEY;
import static com.dr.framework.common.entity.StatusEntity.STATUS_ENABLE;

/**
 * 默认的人员登录service
 *
 * @author dr
 */
@Service
public class DefaultLoginService implements LoginService, InitializingBean {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    DefaultDataBaseService defaultDataBaseService;
    @Lazy
    @Autowired
    PassWordEncrypt passWordEncrypt;

    @Value("${sys.login.retryCount:5}")
    Integer retryCount;
    @Autowired
    LoginTokenHandler loginTokenHandler;


    EntityRelation userLoginRelation;

    /**
     * TODO 记录操作日志
     *
     * @param person
     * @param password
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindLogin(Person person, String password) {
        Assert.isTrue(commonMapper.exists(Person.class, person.getId()), "未查询到指定的人员！");
        String salt = genSalt();
        if (!StringUtils.isEmpty(person.getUserCode())) {
            doAddUserLogin(
                    person,
                    passWordEncrypt.encryptAddLogin(password, salt, LOGIN_TYPE_DEFAULT),
                    salt,
                    person.getUserCode(),
                    LOGIN_TYPE_DEFAULT
            );
        }
        if (!StringUtils.isEmpty(person.getIdNo())) {
            doAddUserLogin(
                    person,
                    passWordEncrypt.encryptAddLogin(password, salt, LOGIN_TYPE_IDNO),
                    salt,
                    person.getIdNo(),
                    LOGIN_TYPE_IDNO
            );
        }
        if (!StringUtils.isEmpty(person.getPhone())) {
            doAddUserLogin(
                    person,
                    passWordEncrypt.encryptAddLogin(password, salt, LOGIN_TYPE_PHONE),
                    salt,
                    person.getPhone(),
                    LOGIN_TYPE_PHONE
            );
        }
        if (!StringUtils.isEmpty(person.getEmail())) {
            doAddUserLogin(
                    person,
                    passWordEncrypt.encryptAddLogin(password, salt, LOGIN_TYPE_EMAIL),
                    salt,
                    person.getEmail(),
                    LOGIN_TYPE_EMAIL
            );
        }
        if (!StringUtils.isEmpty(person.getQq())) {
            doAddUserLogin(
                    person,
                    passWordEncrypt.encryptAddLogin(password, salt, LOGIN_TYPE_QQ),
                    salt,
                    person.getQq(),
                    LOGIN_TYPE_QQ
            );
        }
        if (!StringUtils.isEmpty(person.getWeiChatId())) {
            doAddUserLogin(
                    person,
                    passWordEncrypt.encryptAddLogin(password, salt, LOGIN_TYPE_WX),
                    salt,
                    person.getWeiChatId(),
                    LOGIN_TYPE_WX
            );
        }
    }

    @Transactional(rollbackFor = Exception.class)
    protected void doAddUserLogin(Person person, String password, String salt, String loginId, String loginType) {
        //先检查是否有指定的用户
        UserLogin userLogin = (UserLogin) commonMapper.selectOneByQuery(
                SqlQuery.from(userLoginRelation)
                        .equal(userLoginRelation.getColumn("person_id"), person.getId())
                        .equal(userLoginRelation.getColumn("user_type"), loginType)
                        .equal(userLoginRelation.getColumn("outUser"), person.isOutUser())
        );
        if (userLogin == null) {
            userLogin = new UserLogin();
            userLogin.setId(UUID.randomUUID().toString());
            userLogin.setPersonId(person.getId());
            userLogin.setUserType(loginType);
            userLogin.setCreateDate(System.currentTimeMillis());
            userLogin.setCreatePerson(person.getCreatePerson());
            userLogin.setLoginId(loginId);
            userLogin.setPassword(password);
            userLogin.setSalt(salt);
            userLogin.setUpdateDate(System.currentTimeMillis());
            userLogin.setUpdatePerson(person.getUpdatePerson());
            userLogin.setStatus(StatusEntity.STATUS_ENABLE);
            userLogin.setRetryCount((long) 0);
            userLogin.setLastChangePwdDate(System.currentTimeMillis());
            commonMapper.insert(userLogin);
        } else {
            userLogin.setSalt(salt);
            userLogin.setLoginId(loginId);
            userLogin.setPassword(password);
            userLogin.setLastChangePwdDate(System.currentTimeMillis());
            userLogin.setUpdateDate(System.currentTimeMillis());
            userLogin.setUpdatePerson(person.getUpdatePerson());
            userLogin.setStatus(StatusEntity.STATUS_ENABLE);
            commonMapper.updateIgnoreNullById(userLogin);
        }

    }

    private String genSalt() {
        return RandomStringUtils.randomAlphabetic(6);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addLogin(String personId, String loginType, String loginId, String password) {
        addLogin(personId, loginType, loginId, password, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addLoginWithDefaultPassWord(String personId, String loginType, String loginId) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员Id不能为空！");
        UserLogin userLogin = commonMapper.selectOneByQuery(
                SqlQuery.from(userLoginRelation)
                        .equal(userLoginRelation.getColumn("person_id"), personId)
                        .equal(userLoginRelation.getColumn("user_type"), loginType)
                        .setReturnClass(UserLogin.class)
        );
        Assert.notNull(userLogin, "未查询到默认登陆账户");
        addLogin(personId, loginType, loginId, userLogin.getPassword(), userLogin.getSalt());
    }

    @Transactional(rollbackFor = Exception.class)
    protected long addLogin(String personId, String loginType, String loginId, String password, String salt) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员Id不能为空！");
        Person person = commonMapper.selectById(Person.class, personId);
        Assert.isTrue(person != null, "未查询到指定的人员！");
        Assert.isTrue(!commonMapper.existsByQuery(
                SqlQuery.from(userLoginRelation)
                        .equal(userLoginRelation.getColumn("person_id"), person.getId())
                        .equal(userLoginRelation.getColumn("user_type"), loginType)
                        .equal(userLoginRelation.getColumn("outUser"), person.isOutUser())
        ), "已存在指定类型的登录账户");
        doAddUserLogin(person, password, salt, loginId, loginType);
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    protected long addLogin(String personId, String loginType, String loginId, String password, boolean encryPass) {
        String salt = genSalt();
        if (encryPass) {
            password = passWordEncrypt.encryptAddLogin(password, salt, loginType);
        }
        addLogin(personId, loginType, loginId, password, salt);
        return 0;
    }

    @Override
    public Person login(String loginId, String password, String loginType, String loginSource, boolean outUser) {
        Assert.isTrue(!StringUtils.isEmpty(loginId), "登录账户不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(loginType), "登录类型不能为空！");
        //解密登录账户
        loginId=passWordEncrypt.decodeLoginId(loginId,loginType,loginSource);
        UserLogin userLogin = (UserLogin) commonMapper.selectOneByQuery(
                SqlQuery.from(userLoginRelation)
                        .equal(userLoginRelation.getColumn("login_id"), loginId)
                        .equal(userLoginRelation.getColumn("user_type"), loginType)
                        .equal(userLoginRelation.getColumn("outUser"), outUser)
        );
        Assert.notNull(userLogin, "未查到指定的登录账户！");
        boolean statusEnabld = userLogin.getStatus().equals(StatusEntity.STATUS_ENABLE);

        password = passWordEncrypt.encryptValidateLogin(password, userLogin.getSalt(), loginType);

        boolean success = password.equals(userLogin.getPassword());

        if (!StringUtils.isEmpty(loginSource)) {
            userLogin.setLastLoginIp(loginSource);
        }
        userLogin.setLastLoginDate(System.currentTimeMillis());
        userLogin.setRetryCount(userLogin.getRetryCount() + 1);
        if (success) {
            userLogin.setRetryCount((long) 0);
            if (!statusEnabld && "超出重试次数，请稍后重试".equalsIgnoreCase(userLogin.getFreezeReason())) {
                if (System.currentTimeMillis() - userLogin.getFreezeDate() > 10 * 60 * 60) {
                    userLogin.setStatus(STATUS_ENABLE);
                    userLogin.setFreezeDate((long) 0);
                    userLogin.setFreezeReason("");
                    statusEnabld = true;
                }
            }
        } else {
            //超过5此锁定账户
            if (userLogin.getRetryCount() > retryCount) {
                userLogin.setStatus(StatusEntity.STATUS_DISABLE);
                userLogin.setFreezeDate(System.currentTimeMillis());
                userLogin.setFreezeReason("超出重试次数，请稍后重试");
            }
        }
        commonMapper.updateIgnoreNullById(userLogin);

        Assert.isTrue(success && statusEnabld, "登录失败！");
        return commonMapper.selectById(Person.class, userLogin.getPersonId());
    }

    @Override
    public List<UserLogin> userLogin(String personId) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员Id不能为空");
        SqlQuery<UserLogin> userLoginSqlQuery = SqlQuery.from(userLoginRelation);
        userLoginSqlQuery.equal(userLoginRelation.getColumn("person_id"), personId);
        return commonMapper.selectByQuery(userLoginSqlQuery);
    }

    /**
     * @param person
     * @return
     */
    @Override
    public String auth(Person person) {
        return loginTokenHandler.auth(person);
    }

    @Override
    public Person deAuth(String token) {
        return loginTokenHandler.deAuth(token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String personId, String newPassword, String... notIncludeLoginTypes) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员id不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(newPassword), "新密码不能为空！");
        List<Object> userLogins = commonMapper.selectByQuery(
                SqlQuery.from(userLoginRelation)
                        .equal(userLoginRelation.getColumn("person_id"), personId)
        );
        Assert.isTrue(!userLogins.isEmpty(), "未找到指定的登录账户！");
        Set<String> notTypes = new HashSet<>(Arrays.asList(notIncludeLoginTypes));
        for (Object o : userLogins) {
            UserLogin userLogin = (UserLogin) o;
            if (!notTypes.isEmpty() && notTypes.contains(userLogin.getUserType())) {
                //指定登录类型的密码不修改，微信、支付宝的授权登录情况
                continue;
            }
            String salt = genSalt();
            userLogin.setPassword(passWordEncrypt.encryptChangeLogin(newPassword, salt, userLogin.getUserType()));
            userLogin.setSalt(salt);
            userLogin.setLastChangePwdDate(System.currentTimeMillis());
            //TODO 用户锁
            commonMapper.updateIgnoreNullById(userLogin);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeLogin(String personId) {
        changeLoginStatus(personId, StatusEntity.STATUS_DISABLE);
    }

    @Override
    public void unFreezeLogin(String personId) {
        changeLoginStatus(personId, STATUS_ENABLE);
    }

    @Override
    public void changeStatus(String personId, Integer status) {
        changeLoginStatus(personId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeLogin(String loginId) {
        Assert.isTrue(!StringUtils.isEmpty(loginId), "登录账号不能为空");
        return commonMapper.deleteByQuery(SqlQuery.from(userLoginRelation).equal(userLoginRelation.getColumn("login_id"), loginId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removePersonLogin(String personId) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员Id不能为空！");
        return commonMapper.deleteByQuery(SqlQuery.from(userLoginRelation).equal(userLoginRelation.getColumn("person_id"), personId));
    }

    /**
     * 更新登录状态
     *
     * @param personId
     * @param status
     */
    @Transactional(rollbackFor = Exception.class)
    protected void changeLoginStatus(String personId, int status) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员id不能为空！");
        List<Object> userLogins = commonMapper.selectByQuery(
                SqlQuery.from(userLoginRelation)
                        .equal(userLoginRelation.getColumn("person_id"), personId)

        );
        List<String> loginIds = userLogins.stream()
                .map(o -> (UserLogin) o)
                .map(BaseEntity::getId)
                .collect(Collectors.toList());
        //批量更新登录状态
        commonMapper.updateByQuery(SqlQuery.from(userLoginRelation)
                .set(userLoginRelation.getColumn(STATUS_COLUMN_KEY), status)
                .in(userLoginRelation.getColumn("id"), loginIds)
        );
        //TODO 记录操作日志
    }


    @Override
    public void afterPropertiesSet() {
        userLoginRelation = defaultDataBaseService.getTableInfo(UserLogin.class);
    }

}
