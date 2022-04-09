package com.dr.framework.common.util;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份证工具类
 * TODO 没有处理港澳台类型的数据
 *
 * @author dr
 */
public class IDNo {
    private static final Map<String, String> cityCodes;
    private static final int[] power = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    static {
        cityCodes = new HashMap();
        cityCodes.put("11", "北京");
        cityCodes.put("12", "天津");
        cityCodes.put("13", "河北");
        cityCodes.put("14", "山西");
        cityCodes.put("15", "内蒙古");
        cityCodes.put("21", "辽宁");
        cityCodes.put("22", "吉林");
        cityCodes.put("23", "黑龙江");
        cityCodes.put("31", "上海");
        cityCodes.put("32", "江苏");
        cityCodes.put("33", "浙江");
        cityCodes.put("34", "安徽");
        cityCodes.put("35", "福建");
        cityCodes.put("36", "江西");
        cityCodes.put("37", "山东");
        cityCodes.put("41", "河南");
        cityCodes.put("42", "湖北");
        cityCodes.put("43", "湖南");
        cityCodes.put("44", "广东");
        cityCodes.put("45", "广西");
        cityCodes.put("46", "海南");
        cityCodes.put("50", "重庆");
        cityCodes.put("51", "四川");
        cityCodes.put("52", "贵州");
        cityCodes.put("53", "云南");
        cityCodes.put("54", "西藏");
        cityCodes.put("61", "陕西");
        cityCodes.put("62", "甘肃");
        cityCodes.put("63", "青海");
        cityCodes.put("64", "宁夏");
        cityCodes.put("65", "新疆");
        cityCodes.put("71", "台湾");
        cityCodes.put("81", "香港");
        cityCodes.put("82", "澳门");
        cityCodes.put("91", "国外");
    }

    /**
     * 6位地址码
     * 2位省
     * 2位市
     * 2位县
     */
    private String areaCode;
    /**
     * 8位生日码
     */
    private String birthday;
    /**
     * 3位顺序码
     * 　表示在同一地址码所标识的区域范围内
     * ，对同年、同月、同日出生的人编定的顺序号
     * ，顺序码的奇数分配给男性，偶数分配给女性。
     */
    private String sn;
    /**
     * 性别
     * 男性true,
     * 女性false
     */
    private boolean sex;
    /**
     * 1位校验码
     * 根据前面十七位数字码，按照ISO 7064:1983.MOD 11-2校验码计算出来的检验码。
     */
    private char validateCode;

    public static boolean validate(String idNo) {
        try {
            from(idNo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static IDNo from(String idNo) {
        Assert.isTrue(StringUtils.hasText(idNo), "身份证号不能为空");
        idNo = idNo.trim();
        boolean is15 = idNo.length() == 15;
        boolean is18 = idNo.length() == 18;
        Assert.isTrue(is15 || is18, "身份证长度不正确");
        IDNo idNo1 = new IDNo();
        if (is15) {
            Assert.isTrue(isNum(idNo), "15位身份证是由纯数字组成的");
            idNo1.areaCode = idNo.substring(0, 6);
            idNo1.birthday = "19" + idNo.substring(6, 12);
            idNo1.sn = idNo.substring(12, 15);
        } else {
            Assert.isTrue(isNum(idNo.substring(0, 17)), "身份证号前17位是由纯数字组成的");
            idNo1.areaCode = idNo.substring(0, 6);
            idNo1.birthday = idNo.substring(6, 14);
            idNo1.sn = idNo.substring(14, 17);
            idNo1.validateCode = idNo.charAt(17);
        }
        idNo1.sex = Integer.parseInt(idNo1.sn.substring(2, 3)) % 2 != 0;
        try {
            Date date = DateUtils.parseDate(idNo1.birthday, "yyyyMMdd");
            Assert.isTrue(new Date().after(date), "出生日期不能小于当前日期");
        } catch (ParseException e) {
            Assert.isTrue(false, "解析出生日期失败");
        }
        Assert.isTrue(cityCodes.containsKey(idNo1.areaCode.substring(0, 2)), "非法所属省份");
        char validateCode = getCheckSum(idNo1.getId18().substring(0, 17));
        if (is15) {
            idNo1.validateCode = validateCode;
        } else {
            Assert.isTrue(idNo1.validateCode == validateCode, "校验码不正确");
        }
        return idNo1;
    }

    private static char getCheckSum(String id17) {
        char[] cArr = id17.toCharArray();
        int[] iCard = converCharToInt(cArr);
        int iSum17 = getPowerSum(iCard);
        return getCheckCode18(iSum17);
    }

    private static char getCheckCode18(int iSum) {
        char sCode;
        switch (iSum % 11) {
            case 10:
                sCode = '2';
                break;
            case 9:
                sCode = '3';
                break;
            case 8:
                sCode = '4';
                break;
            case 7:
                sCode = '5';
                break;
            case 6:
                sCode = '6';
                break;
            case 5:
                sCode = '7';
                break;
            case 4:
                sCode = '8';
                break;
            case 3:
                sCode = '9';
                break;
            case 2:
                sCode = 'x';
                break;
            case 1:
                sCode = '0';
                break;
            case 0:
                sCode = '1';
                break;
            default:
                throw new IllegalArgumentException("计算校验码失败！");
        }
        return sCode;
    }

    private static int[] converCharToInt(char[] cArr) {
        int len = cArr.length;
        int[] iArr = new int[len];
        try {
            for (int i = 0; i < len; i++) {
                iArr[i] = Integer.parseInt(String.valueOf(cArr[i]));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return iArr;
    }

    public static int getPowerSum(int[] iArr) {
        int iSum = 0;
        if (power.length == iArr.length) {
            for (int i = 0; i < iArr.length; i++) {
                for (int j = 0; j < power.length; j++) {
                    if (i == j) {
                        iSum = iSum + iArr[i] * power[j];
                    }
                }
            }
        }
        return iSum;
    }


    private static boolean isNum(String val) {
        return StringUtils.hasText(val) && val.matches("^[0-9]*{1}");
    }

    private IDNo() {
    }

    public String getId18() {
        return getAreaCode() + getBirthday() + getSn() + getValidateCode();
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public char getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(char validateCode) {
        this.validateCode = validateCode;
    }
}
