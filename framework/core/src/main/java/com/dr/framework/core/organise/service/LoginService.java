package com.dr.framework.core.organise.service;

import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.organise.entity.UserLogin;
import com.dr.framework.core.util.Constants;

import java.util.List;

/**
 * 登录接口类
 *
 * @author dr
 */
public interface LoginService {
    /**
     * 默认登录方式，对应人员编码{@link Person#userCode}
     */
    String LOGIN_TYPE_DEFAULT = "default";
    String LOGIN_TYPE_IDNO = "idno";
    String LOGIN_TYPE_PHONE = "phone";
    String LOGIN_TYPE_EMAIL = "email";
    String LOGIN_TYPE_QQ = "qq";
    String LOGIN_TYPE_WX = "wx";

    /**
     * 根据用户创建登录信息
     * <p>
     * 一个人员可以绑定多条登录信息
     * <p>
     * 默认绑定
     * 人员编码{@link Person#userCode}
     * 身份证{@link Person#idNo}
     * 电话{@link Person#phone}
     * 邮箱{@link Person#email}
     * QQ{@link Person#qq}
     * WX{@link Person#weiChatId}
     * <p>
     * 自定义登录方式请使用
     *
     * @param person
     * @param password
     * @see #bingLogin(UserLogin)
     */
    void bindLogin(Person person, String password);

    /**
     * 绑定自定义登录账户
     *
     * @param personId
     * @param loginType
     * @param loginId
     * @param password
     */
    void addLogin(String personId, String loginType, String loginId, String password);

    /**
     * 根据历史登录账户的登录密码创建登录用户
     *  TODO 这个需求不大合理
     * @param personId
     * @param loginType
     * @param loginId
     */
    //void addLoginWithExistPassWord(String personId, String loginType, String loginId);

    /**
     * 登录验证，并返回人员基本信息
     *
     * @param loginId     登录账号
     * @param password    登录密码
     * @param loginType   登录类型
     * @param loginSource 登录ip
     * @param outUser     是否外部用户
     * @return
     */
    Person login(String loginId, String password, String loginType, String loginSource, boolean outUser);

    default Person login(String loginId, String password, String loginType, String loginSource) {
        return login(loginId, password, loginType, loginSource, false);
    }

    default Person login(String loginId, String password, String loginType) {
        return login(loginId, password, loginType, Constants.DEFAULT);
    }

    default Person login(String loginId, String password) {
        return login(loginId, password, LOGIN_TYPE_DEFAULT);
    }

    /**
     * 查询用户所有的登录账户
     *
     * @param personId
     * @return
     */
    List<UserLogin> userLogin(String personId);

    /**
     * 将person转换成token码
     *
     * @param person
     * @return
     */
    String auth(Person person);

    /**
     * 将token码转换成person对象
     *
     * @param token
     * @return
     */
    Person deAuth(String token);

    default String auth(String loginId, String password, String loginType, String loginSource) {
        return auth(login(loginId, password, loginType, loginSource));
    }

    default String auth(String loginId, String password, String loginType) {
        return auth(login(loginId, password, loginType));
    }

    default String auth(String loginId, String password) {
        return auth(login(loginId, password));
    }

    /**
     * 更新密码
     *
     * @param personId             用户id
     * @param newPassword          新密码
     * @param notIncludeLoginTypes 指定登录类型不更改密码
     */
    void changePassword(String personId, String newPassword, String... notIncludeLoginTypes);

    void freezeLogin(String personId);

    void unFreezeLogin(String personId);

    /**
     * 直接修改登录账户的状态
     *
     * @param personId
     * @param status
     */
    void changeStatus(String personId, Integer status);

    /**
     * 删除指定的登录账户
     *
     * @param loginId
     * @return
     */
    long removeLogin(String loginId);

    /**
     * 删除指定人员所有的登录账户
     *
     * @param personId
     * @return
     */
    long removePersonLogin(String personId);

}
