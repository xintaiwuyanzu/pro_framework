package com.dr.framework.sys.service;

import com.dr.framework.core.organise.service.PassWordEncrypt;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;

/**
 * 默认登录代码验证
 *
 * @author dr
 */
public class DefaultPassWordEncrypt implements PassWordEncrypt {
    private Charset encoding;

    public DefaultPassWordEncrypt(String encoding) {
        this.encoding = Charset.forName(encoding);
    }

    @Override
    public String encryptAddLogin(String password, String salt, String loginType) {
        return encryptValidateLogin(password, salt, loginType);
    }

    @Override
    public String encryptValidateLogin(String password, String salt, String loginType) {
        Assert.isTrue(StringUtils.hasText(password), "密码不能为空！");
        Assert.isTrue(StringUtils.hasText(salt), "加密盐不能为空！");
        //解码密码
        password = decodePassword(password);
        //3、MD5加密
        return DigestUtils.md5DigestAsHex(
                //2、base64编码
                Base64Utils.encode(
                        //1、拼接密码和加密盐
                        (password + salt).getBytes(getEncodingCharset())
                )
        );
    }

    @Override
    public Charset getEncodingCharset() {
        return encoding;
    }
}
