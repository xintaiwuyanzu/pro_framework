package com.dr.framework.sys.service;

import com.dr.framework.core.organise.entity.Person;

/**
 * 登录token转换工具类
 *
 * @author dr
 */
public interface LoginTokenHandler {
    /**
     * oauth 返回数据，正常应该带着用户id，名称，编码之类的信息
     * TODO 这里暂时直接返回用户id 计划使用rsa非对称加密解密
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
}
