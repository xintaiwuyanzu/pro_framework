package com.dr.framework.common.service;

import com.dr.framework.core.orm.database.DataBaseMetaData;
import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.database.tools.DataBaseChangeInfo;
import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.module.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认的数据库管理工具
 *
 * @author dr
 */
@Service
public class DefaultDataBaseService implements DataBaseService {
    Logger logger = LoggerFactory.getLogger(DefaultDataBaseService.class);
    protected Map<String, DataBaseMetaData> databaseMetaMap = Collections.synchronizedMap(new HashMap<>());
    protected Map<String, Module> moduleMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public List<DataBaseMetaData> getAllDatabases() {
        return databaseMetaMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Module> getModules(@Nonnull String dbName) {
        return moduleMap.values().stream().filter(module -> module.getDbName().equalsIgnoreCase(dbName)).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public DataBaseMetaData getDataBaseMetaData(@Nonnull String dbName) {
        return databaseMetaMap.get(dbName.toUpperCase());
    }

    @Nullable
    @Override
    public DataBaseMetaData getDataBaseMetaDataByModuleName(@Nonnull String moduleName) {
        Module module = moduleMap.get(moduleName.toUpperCase());
        if (module != null) {
            return getDataBaseMetaData(module.getDbName());
        }
        return null;
    }

    @Override
    public Connection getSelfManagedConnection(@Nonnull String moduleName) throws SQLException {
        DataBaseMetaData dataBaseMetaData = getDataBaseMetaDataByModuleName(moduleName);
        if (dataBaseMetaData != null) {
            return dataBaseMetaData.openSelfManagedConnection();
        }
        return null;
    }

    @Override
    public boolean tableExist(@Nonnull String tableName, @Nonnull String moduleName) {
        try {
            return getTableInfo(tableName, moduleName) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Nullable
    @Override
    public Relation getTableInfo(@Nonnull String tableName, @Nonnull String moduleName) {
        DataBaseMetaData metaData = getDataBaseMetaDataByModuleName(moduleName);
        Assert.notNull(metaData, "未找到指定模块所属的数据库配置");
        return metaData.getTable(tableName);
    }

    public EntityRelation getTableInfo(Class entityCLass) {
        for (Module module : moduleMap.values()) {
            EntityRelation entityRelation = module.getTable(entityCLass);
            if (entityRelation != null) {
                return entityRelation;
            }
        }
        return null;
    }

    public EntityRelation getTableInfo(String className) {
        for (Module module : moduleMap.values()) {
            EntityRelation entityRelation = module.getTableByClassName(className);
            if (entityRelation != null) {
                return entityRelation;
            }
        }
        return null;
    }


    @Override
    public Collection<DataBaseChangeInfo> validateTable(@Nonnull Relation relation) {
        DataBaseMetaData dataBaseMetaData = getDataBaseMetaDataByModuleName(relation.getModule());
        if (dataBaseMetaData != null) {
            Dialect dialect = dataBaseMetaData.getDialect();
            Relation jdbcTable = getTableInfo(relation.getName(), relation.getModule());
            return dialect.getUpdateTableInfo(relation, jdbcTable);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void updateTable(@Nonnull Relation relation) {
        Collection<DataBaseChangeInfo> changeInfos = validateTable(relation);
        doExecTableDDl(relation, changeInfos);
    }

    private void doExecTableDDl(Relation relation, Collection<DataBaseChangeInfo> changeInfos) {
        if (changeInfos != null && !changeInfos.isEmpty()) {
            try (Connection connection = getSelfManagedConnection(relation.getModule())) {
                connection.setAutoCommit(false);
                Statement statement = connection.createStatement();
                for (DataBaseChangeInfo s : changeInfos) {
                    logger.info(s.getMessage());
                    logger.info(s.getSql());
                    if (statement != null) {
                        try {
                            statement.execute(s.getSql());
                            try {
                                SQLWarning sqlWarning = statement.getWarnings();
                                while (sqlWarning != null) {
                                    logger.warn("执行更新ddlsql警告\n Code:{},SQLState:{},Message:{}", sqlWarning.getErrorCode(), sqlWarning.getSQLState(), sqlWarning.getMessage());
                                    sqlWarning = sqlWarning.getNextWarning();
                                }
                                statement.clearWarnings();
                            } catch (Exception e) {
                                logger.warn("清理ddl异常失败", e);
                            }
                        } catch (SQLException e) {
                            logger.error("执行数据库更新操作失败", e);
                        }
                    }
                }
                connection.commit();
                //执行完ddl操作后，数据库表结构有可能已经修改了，所以要删除本地的数据库表结构缓存
                getDataBaseMetaDataByModuleName(relation.getModule()).clearTable(relation.getName());
            } catch (SQLException e) {
                logger.error("执行数据库更新操作失败！", e);
            }
        }
    }


    @Override
    public void dropTable(@Nonnull String tableName, @Nonnull String moduleName) {
        Module module = moduleMap.get(moduleName.toUpperCase());
        Assert.notNull(module, "未找到指定的模块声明：" + moduleName);
        Relation relation = module.getTable(tableName);
        if (relation instanceof EntityRelation) {
            logger.warn("指定模块【{}】的表定义【{}】有相应的java类【{}】与其映射，不能直接删除"
                    , moduleName
                    , tableName
                    , ((EntityRelation) relation).getEntityClass()
            );
            return;
        } else if (relation == null) {
            relation = getTableInfo(tableName, moduleName);
        }
        if (relation != null) {
            DataBaseMetaData dataBaseMetaData = getDataBaseMetaDataByModuleName(moduleName);
            Assert.notNull(module, "未找到指定的模块所属数据源" + moduleName);
            List<DataBaseChangeInfo> dropInfos = dataBaseMetaData.getDialect().getDropTableInfo(relation);
            doExecTableDDl(relation, dropInfos);
        } else {
            logger.warn("指定模块【{}】的表定义【{}】在数据库中没有创建，不执行任何操作"
                    , moduleName
                    , tableName
            );
        }
    }

    public DefaultDataBaseService addDb(DataBaseMetaData dataBaseMetaData) {
        databaseMetaMap.put(dataBaseMetaData.getName().toUpperCase(), dataBaseMetaData);
        return this;
    }

    public DefaultDataBaseService addModule(String moduleName, String dbName) {
        moduleName = moduleName.toUpperCase();
        Module module = moduleMap.get(moduleName);
        if (module != null) {
            Assert.isTrue(module.getDbName().equalsIgnoreCase(dbName), "一个模块不能同时被两个数据源管理：" + moduleName);
        } else {
            module = new Module(dbName, moduleName);
            moduleMap.put(moduleName, module);
        }
        return this;
    }

    public DefaultDataBaseService addEntityRelation(EntityRelation entityRelation, String dbName) {
        String moduleName = entityRelation.getModule();
        addModule(moduleName, dbName);
        moduleMap.get(entityRelation.getModule().toUpperCase())
                .addRelation(entityRelation);
        return this;
    }

}
