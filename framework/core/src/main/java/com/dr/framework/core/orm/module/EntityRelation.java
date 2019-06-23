package com.dr.framework.core.orm.module;

import com.dr.framework.core.orm.jdbc.Column;
import com.dr.framework.core.orm.jdbc.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

/**
 * 表示数据库信息是从实体类中解析出来的
 *
 * @author dr
 */
public class EntityRelation extends Relation<EntityRelation.FieldColumn> {
    private Class entityClass;
    static final Logger logger = LoggerFactory.getLogger(" com.dr.framework.core.orm");

    public EntityRelation(boolean isTable) {
        super(isTable);
    }

    public void bind(Relation<Column> relation) {
        if (relation != null) {
            getColumns().forEach(f -> {
                Column column = relation.getColumn(f.getName());
                if (column == null) {
                    logger.warn("实体类【{}】对应的表【{}】的列【{}】在数据库中未创建，请及时更新数据库表结构"
                            , entityClass
                            , getName()
                            , f.getName()
                    );
                } else {
                    f.bind(column);
                }
            });
        }
    }

    public String getPrimaryKeyAlias() {
        return primaryKeyColumns()
                .stream()
                .map(c -> getColumn(c))
                .map(f -> f.field.getName())
                .collect(Collectors.joining());
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    public static class FieldColumn extends Column {
        private Field field;

        public FieldColumn(Field field) {
            this.field = field;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public void bind(Column c) {
            setSize(c.getSize());
            setType(c.getType());
            setPosition(c.getPosition());
            setDecimalDigits(c.getDecimalDigits());
            setTypeName(c.getTypeName());
            setDefaultValue(c.getDefaultValue());
        }

        public String getAlias() {
            return field == null ? "" : field.getName();
        }
    }


}
