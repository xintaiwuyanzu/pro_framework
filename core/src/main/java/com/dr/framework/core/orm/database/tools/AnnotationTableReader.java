package com.dr.framework.core.orm.database.tools;

import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.module.EntityRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

/**
 * 用来从tableClass读取配置信息，转换成Table实体类
 *
 * @author dr
 */
public class AnnotationTableReader {
    Logger logger = LoggerFactory.getLogger(AnnotationTableReader.class);
    private Map<Class, EntityRelation> classRelationMap = Collections.synchronizedMap(new Hashtable<>());

    /**
     * 从类中读取表机构信息
     *
     * @param clzz
     * @return
     */
    public EntityRelation readerTableInfo(Class clzz, Dialect dialect) {
        if (clzz.isAnnotationPresent(com.dr.framework.core.orm.annotations.Table.class)) {
            return classRelationMap.computeIfAbsent(clzz, c -> {
                com.dr.framework.core.orm.annotations.Table tableAnn = (com.dr.framework.core.orm.annotations.Table) clzz.getAnnotation(com.dr.framework.core.orm.annotations.Table.class);
                EntityRelation table = new EntityRelation(tableAnn.isTable());
                table.setCreateSql(tableAnn.createSql());
                table.setEntityClass(clzz);
                table.setModule(tableAnn.module());
                String tableName = tableAnn.name();
                if (!StringUtils.hasLength(tableName)) {
                    tableName = clzz.getSimpleName();
                }
                table.setName(tableName);
                table.setRemark(tableAnn.comment());
                AnnotationTableReaderUtil.readColumnInfo(table, clzz, dialect);
                return table;
            });
        } else {
            logger.warn("类{}没有被@Table注解，不能解析。", clzz);
        }
        return null;
    }


}
