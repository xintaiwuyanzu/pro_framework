package com.dr.framework.core.orm.module;

import com.dr.framework.core.orm.jdbc.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * 表示某一个业务模块
 *
 * @author dr
 */
public class Module {
    Logger logger = LoggerFactory.getLogger(Module.class);
    /**
     * 数据名称
     */
    private String dbName;
    /**
     * 模块名称
     */
    private String name;
    /**
     * 从实体类中读取的表结构信息
     */
    private Map<String, EntityRelation> entityTables = new HashMap<>();
    /**
     * 运行时配置的表结构信息
     * 没有对应的实体类
     */
    private Map<String, ConfigedRelation> configedRelationMap = new HashMap<>();

    public Module(String dbName, String name) {
        this.dbName = dbName;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Module addRelation(@Nonnull Relation relation) {
        if (relation instanceof EntityRelation) {
            entityTables.putIfAbsent(relation.getName().toUpperCase(), (EntityRelation) relation);
        } else if (relation instanceof ConfigedRelation) {
            configedRelationMap.putIfAbsent(relation.getName().toUpperCase(), (ConfigedRelation) relation);
        } else {
            logger.warn("模块【{}】不管理该类型的表结构定义【{}】", getName(), relation.getClass());
        }
        return this;
    }

    public Relation getTable(@Nonnull String tableName) {
        tableName = tableName.toUpperCase();
        Relation relation = entityTables.get(tableName);
        return relation == null ? configedRelationMap.get(tableName) : relation;
    }

    public EntityRelation getTable(Class entityCLass) {
        for (EntityRelation entityRelation : entityTables.values()) {
            if (entityCLass == entityRelation.getEntityClass()) {
                return entityRelation;
            }
        }

        return null;
    }
}
