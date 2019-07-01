package com.dr.framework.sys.service;

import com.dr.framework.sys.entity.Person;
import com.dr.framework.sys.entity.SubSystem;
import com.dr.framework.sys.entity.UserLogin;

/**
 * 登录接口类
 *
 * @author dr
 */
public interface LoginService {
    /**
     * 默认登录方式，对应人员编码{@link Person#userCode}
     */
    static String LOGIN_TYPE_DEFAULT = "default";
    static String LOGIN_TYPE_IDNO = "idno";
    static String LOGIN_TYPE_PHONE = "phone";
    static String LOGIN_TYPE_EMAIL = "email";
    static String LOGIN_TYPE_QQ = "qq";
    static String LOGIN_TYPE_WX = "wx";

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
     * @param sysId
     * @param personId
     * @param loginType
     * @param loginId
     * @param password
     */
    void addLogin(String sysId, String personId, String loginType, String loginId, String password);

    /**
     * 添加登录账户，使用默认的系统
     *
     * @param personId
     * @param loginType
     * @param loginId
     * @param password
     */
    default void addLogin(String personId, String loginType, String loginId, String password) {
        addLogin(SubSystem.DEFAULT_SYSTEM_ID, personId, loginType, loginId, password);
    }

    /**
     * 登录验证，并返回人员基本信息
     *
     * @param loginId     登录账号
     * @param password    登录密码
     * @param loginType   登录类型
     * @param loginSource 登录ip
     * @param sysId       登录系统
     * @return
     */
    Person login(String loginId, String password, String loginType, String sysId, String loginSource);

    default Person login(String loginId, String password, String loginType, String sysId) {
        return login(loginId, password, loginType, sysId, null);
    }

    default Person login(String loginId, String password, String loginType) {
        return login(loginId, password, loginType, SubSystem.DEFAULT_SYSTEM_ID);
    }

    default Person login(String loginId, String password) {
        return login(loginId, password, LOGIN_TYPE_DEFAULT);
    }

    String auth(Person person);

    Person deAuth(String token);

    default String auth(String loginId, String password, String loginType, String sysId, String loginSource) {
        return auth(login(loginId, password, loginType, sysId, loginSource));
    }

    default String auth(String loginId, String password, String loginType, String sysId) {
        return auth(login(loginId, password, loginType, sysId));
    }

    default String auth(String loginId, String password, String loginType) {
        return auth(login(loginId, password, loginType));
    }

    default String auth(String loginId, String password) {
        return auth(login(loginId, password));
    }

    void changePassword(String personId, String newPassword);

    void freezeLogin(String personId);

    void unFreezeLogin(String personId);

}
