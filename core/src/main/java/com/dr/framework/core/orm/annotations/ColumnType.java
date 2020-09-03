package com.dr.framework.core.orm.annotations;

/**
 * 数据库字段类型
 *
 * @author dr
 */
public enum ColumnType {
    /**
     * 程序自动处理
     */
    AUTO,
    /**
     * 对应java基本类型String长度范围在0-4000
     * 其中长度是指字节长度而不是字符长度，
     * 4000是因为老版本oracle字符类型最长为4000，为保证数据库兼容性，故设计最长为4000
     * 长度超过4000，程序自动转换为clob类型
     */
    VARCHAR,
    /**
     * 允许从 -2,147,483,648 到 2,147,483,647 的所有数字。对应Java的int 和long类型数据
     */
    INT,
    /**
     * 带有浮动小数点的小数字。在括号中规定最大位数。在 d 参数中规定小数点右侧的最大位数。
     * 对应java的double、BigDecimal类型
     */
    FLOAT,
    /**
     * 日期类型：
     * 注意：数据库支持date(日期) datetime(日期和时间) time(时间) timestamp(时间戳)四种类型，
     * 这里为了数据库兼容性，所有日期数据都将以long类型存储日期时间戳
     */
    DATE,
    /**
     * 布尔类型，正常业务数据库字段使用此类型并不常见，这里只做简单处理，对应java的boolean类型
     */
    BOOLEAN,
    /**
     * 字符大字段，一般不用担心长度的问题，oracle最大能存储4g的文件。
     * 但是使用过程中要注意如果时很大的文件，请结合实际情况将数据存放在文件中，数据库只保存关联信息
     */
    CLOB,
    /**
     * 字节大字段
     */
    BLOB,
    /**
     * 其他不支持的类型，用来报错提示
     */
    OTHER


}
