package com.dr.framework.common.service;

import com.dr.framework.core.orm.database.DataBaseMetaData;
import com.dr.framework.core.orm.database.tools.DataBaseChangeInfo;
import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.module.Module;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 数据库ddl操作相关的接口
 * <p>
 * <p>
 * 数据库数据备份操作相关的接口
 *
 * @author dr
 */
public interface DataBaseService {
    /**
     * 默认的数据库连接，一个项目可能有多个数据库连接，
     * 奔雷所有的操作都是指定特定的数据库的
     */
    String DEFAULT_DATABASE = "default";

    /**
     * 获取当前环境所有的数据库连接信息
     *
     * @return
     */
    List<DataBaseMetaData> getAllDatabases();

    List<Module> getModules(@Nonnull String dbName);

    DataBaseMetaData getDataBaseMetaData(@Nonnull String dbName);

    /**
     * 根据模块名称获取
     *
     * @param moduleName
     * @return
     */
    @Nullable
    DataBaseMetaData getDataBaseMetaDataByModuleName(@Nonnull String moduleName);

    @Nullable
    Connection getSelfManagedConnection(@Nonnull String moduleName) throws SQLException;

    /**
     * 判断表是否存在
     *
     * @param tableName
     * @param moduleName
     * @return
     */
    boolean tableExist(@Nonnull String tableName, @Nonnull String moduleName);

    /**
     * 读取指定表信息
     *
     * @param tableName
     * @param moduleName
     * @return
     */
    @Nullable
    Relation getTableInfo(@Nonnull String tableName, @Nonnull String moduleName);

    /**
     * 校验表定义，获取更新信息
     *
     * @param relation
     * @return
     */
    Collection<DataBaseChangeInfo> validateTable(@Nonnull Relation relation);

    /**
     * 创建或者更新表结构
     *
     * @param relation
     */
    void updateTable(@Nonnull Relation relation);

    /**
     * 删除数据库表定义
     *
     * @param tableName
     * @param module
     */
    void dropTable(@Nonnull String tableName, @Nonnull String module);

    /**
     * TODO  下面是数据迁移的功能
     * 初始化数据库
     * <p>
     * 初始化数据库数据
     * <p>
     * 按照指定条件备份数据库
     * <p>
     * 导入备份的数据
     */
    /**
     * 别名加转义符，防止数据库大小写敏感转义
     *
     * @param relation
     * @return
     */
    default String getColumFix(Relation relation) {
        return getColumFix(relation.getModule());
    }

    /**
     * 别名加转义符，防止数据库大小写敏感转义
     *
     * @param module
     * @return
     */
    String getColumFix(String module);
}
