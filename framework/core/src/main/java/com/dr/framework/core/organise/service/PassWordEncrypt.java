package com.dr.framework.core.organise.service;

/**
 * @author dr
 */
public interface PassWordEncrypt {
    /**
     * 添加用户的时候调用加密方法
     *
     * @param password
     * @param salt
     * @param loginType
     * @return
     */
    String encryptAddLogin(String password, String salt, String loginType);

    /**
     * 登录用户的时候调用加密方法
     *
     * @param password
     * @param salt
     * @param loginType
     * @return
     */
    String encryptValidateLogin(String password, String salt, String loginType);

    /**
     * 更换密码的时候调用加密方法
     *
     * @param password
     * @param salt
     * @param loginType
     * @return
     */
    default String encryptChangeLogin(String password, String salt, String loginType) {
        return encryptAddLogin(password, salt, loginType);
    }

}
