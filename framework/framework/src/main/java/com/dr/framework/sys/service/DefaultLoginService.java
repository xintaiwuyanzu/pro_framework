package com.dr.framework.sys.service;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.Person;
import com.dr.framework.sys.entity.SubSystem;
import com.dr.framework.sys.entity.UserLogin;
import com.dr.framework.sys.entity.UserLoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * 默认的人员登录service
 *
 * @author dr
 */
@Service
public class DefaultLoginService implements LoginService {

    @Autowired
    CommonMapper commonMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindLogin(Person person, String password) {
        Assert.isTrue(commonMapper.exists(Person.class, person.getId()), "未查询到指定的人员！");
        Assert.isTrue(!StringUtils.isEmpty(person.getSysId()), "子系统编码不能为空！");
        Assert.isTrue(commonMapper.exists(SubSystem.class, person.getSysId()), "未找到指定的子系统！");
        String salt = genSalt();
        password = encryptPassword(password, salt);
        if (!StringUtils.isEmpty(person.getUserCode())) {
            doAddUserLogin(person, password, salt, person.getUserCode(), LOGIN_TYPE_DEFAULT);
        }
        if (!StringUtils.isEmpty(person.getIdNo())) {
            doAddUserLogin(person, password, salt, person.getIdNo(), LOGIN_TYPE_IDNO);
        }
        if (!StringUtils.isEmpty(person.getPhone())) {
            doAddUserLogin(person, password, salt, person.getPhone(), LOGIN_TYPE_PHONE);
        }
        if (!StringUtils.isEmpty(person.getEmail())) {
            doAddUserLogin(person, password, salt, person.getEmail(), LOGIN_TYPE_EMAIL);
        }
        if (!StringUtils.isEmpty(person.getQq())) {
            doAddUserLogin(person, password, salt, person.getQq(), LOGIN_TYPE_QQ);
        }
        if (!StringUtils.isEmpty(person.getWeiChatId())) {
            doAddUserLogin(person, password, salt, person.getWeiChatId(), LOGIN_TYPE_WX);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    protected void doAddUserLogin(Person person, String password, String salt, String loginId, String loginType) {
        //先检查是否有指定的用户
        UserLogin userLogin = commonMapper.selectOneByQuery(
                SqlQuery.from(UserLogin.class)
                        .equal(UserLoginInfo.PERSONID, person.getId())
                        .equal(UserLoginInfo.USERTYPE, loginType)
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
            userLogin.setSysId(person.getSysId());
            userLogin.setStatus(StatusEntity.STATUS_ENABLE);
            userLogin.setLastChangePwdDate(System.currentTimeMillis());
            commonMapper.insert(userLogin);
        } else {
            userLogin.setSalt(salt);
            userLogin.setLoginId(loginId);
            userLogin.setPassword(password);
            userLogin.setLastChangePwdDate(System.currentTimeMillis());
            userLogin.setUpdateDate(System.currentTimeMillis());
            userLogin.setUpdatePerson(person.getUpdatePerson());
            userLogin.setSysId(person.getSysId());
            userLogin.setStatus(StatusEntity.STATUS_ENABLE);
            commonMapper.updateIgnoreNullById(userLogin);
        }

    }

    private String genSalt() {
        return "dr";
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addLogin(String sysId, String personId, String loginType, String loginId, String password) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员Id不能为空！");
        Person person = commonMapper.selectById(Person.class, personId);
        Assert.isTrue(person != null, "未查询到指定的人员！");
        Assert.isTrue(!StringUtils.isEmpty(sysId), "子系统编码不能为空！");
        Assert.isTrue(commonMapper.exists(SubSystem.class, sysId), "未找到指定的子系统！");
        Assert.isTrue(!commonMapper.existsByQuery(
                SqlQuery.from(UserLogin.class)
                        .equal(UserLoginInfo.PERSONID, personId)
                        .equal(UserLoginInfo.USERTYPE, loginType)
        ), "已存在指定类型的登录账户");
        String salt = genSalt();
        password = encryptPassword(password, salt);
        doAddUserLogin(person, password, salt, loginId, loginType);
    }

    /**
     * TODO
     * 加密密码
     *
     * @param password
     * @param salt     加密盐
     * @return
     */
    private String encryptPassword(String password, String salt) {
        Assert.isTrue(!StringUtils.isEmpty(password), "密码不能为空！");
        return password;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person login(String loginId, String password, String loginType, String sysId, String loginSource) {
        Assert.isTrue(!StringUtils.isEmpty(loginId), "登录账户不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(loginType), "登录类型不能为空！");
        UserLogin userLogin = commonMapper.selectOneByQuery(
                SqlQuery.from(UserLogin.class)
                        .equal(UserLoginInfo.LOGINID, loginId)
                        .equal(UserLoginInfo.SYSID, sysId)
                        .equal(UserLoginInfo.USERTYPE, loginType)
        );
        Assert.notNull(userLogin, "未查到指定的登录账户！");
        password = encryptPassword(password, userLogin.getSalt());
        Assert.isTrue(password.equals(userLogin.getPassword()), "密码错误！");
        Assert.isTrue(userLogin.getStatus().equals(StatusEntity.STATUS_ENABLE), "账户已禁用！");
        if (!StringUtils.isEmpty(loginSource)) {
            userLogin.setLastLoginIp(loginSource);
        }
        userLogin.setLastLoginDate(System.currentTimeMillis());
        commonMapper.updateIgnoreNullById(userLogin);

        return commonMapper.selectById(Person.class, userLogin.getPersonId());
    }

    /**
     * oauth 返回数据，正常应该带着用户id，名称，编码之类的信息
     * TODO 这里暂时直接返回用户id
     *
     * @param person
     * @return
     */
    @Override
    public String auth(Person person) {
        return person.getId();
    }

    @Override
    public Person deAuth(String token) {
        Assert.isTrue(!StringUtils.isEmpty(token), "token不能为空！");
        return commonMapper.selectById(Person.class, token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String personId, String newPassword) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员id不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(newPassword), "新密码不能为空！");
        String salt = genSalt();
        newPassword = encryptPassword(newPassword, salt);
        List<UserLogin> userLogins = commonMapper.selectByQuery(SqlQuery.from(UserLogin.class).equal(UserLoginInfo.PERSONID, personId));
        for (UserLogin userLogin : userLogins) {
            userLogin.setPassword(newPassword);
            userLogin.setSalt(salt);
            userLogin.setLastChangePwdDate(System.currentTimeMillis());
            commonMapper.updateIgnoreNullById(userLogin);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeLogin(String personId) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员id不能为空！");
        List<UserLogin> userLogins = commonMapper.selectByQuery(SqlQuery.from(UserLogin.class).equal(UserLoginInfo.PERSONID, personId));
        for (UserLogin userLogin : userLogins) {
            userLogin.setStatus(StatusEntity.STATUS_DISABLE);
            commonMapper.updateIgnoreNullById(userLogin);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unFreezeLogin(String personId) {
        Assert.isTrue(!StringUtils.isEmpty(personId), "人员id不能为空！");
        List<UserLogin> userLogins = commonMapper.selectByQuery(SqlQuery.from(UserLogin.class).equal(UserLoginInfo.PERSONID, personId));
        for (UserLogin userLogin : userLogins) {
            userLogin.setStatus(StatusEntity.STATUS_ENABLE);
            commonMapper.updateIgnoreNullById(userLogin);
        }
    }
}
