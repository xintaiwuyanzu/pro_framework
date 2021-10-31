package com.dr.framework.core.organise.service;

import org.springframework.util.Base64Utils;

import java.nio.charset.Charset;

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

    /**
     * 可逆加密密码
     *
     * @param passWord
     * @return
     */
    default String encodePassword(String passWord) {
        return Base64Utils.encodeToString(passWord.getBytes(getEncodingCharset()));
    }

    /**
     * 可逆解密密码
     *
     * @param passWord
     * @return
     */
    default String decodePassword(String passWord) {
        return new String(Base64Utils.decodeFromString(passWord), getEncodingCharset());
    }

    /**
     * 获取字符串编码
     *
     * @return
     */
    Charset getEncodingCharset();
}
