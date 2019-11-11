package com.dr.framework.core.orm.database.tools;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Index;
import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.jdbc.TrueOrFalse;
import com.dr.framework.core.orm.module.EntityRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

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
                readColumnInfo(table, clzz, dialect);
                return table;
            });
        } else {
            logger.warn("类{}没有被@Table注解，不能解析。", clzz);
        }
        return null;
    }

    protected void readColumnInfo(EntityRelation table, Class clazz, Dialect dialect) {
        LinkedList<ClassTypeHolder> linkedList = new LinkedList();
        linkedList.addFirst(new ClassTypeHolder(clazz, clazz));
        readClassList(linkedList);
        List<ColumnSortHolder> columnSortHolders = new ArrayList();
        List<PrimaryKeyHolder> primaryKeyHolders = new ArrayList<>();
        List<IndexColumnHolder> indexColumnHolders = new ArrayList<>();
        if (clazz != null) {
            ReflectionUtils.doWithFields(
                    clazz
                    , field -> parseColumn(field, table, dialect, linkedList, columnSortHolders, primaryKeyHolders, indexColumnHolders)
                    , field -> field.isAnnotationPresent(Column.class)
            );
        }
        Collections.sort(columnSortHolders);
        for (int i = 0; i < columnSortHolders.size(); i++) {
            ColumnSortHolder columnSortHolder = columnSortHolders.get(i);
            columnSortHolder.column.setPosition(i + 1);
            table.addColumn(columnSortHolder.column);
        }
        Collections.sort(primaryKeyHolders);
        for (int i = 0; i < primaryKeyHolders.size(); i++) {
            PrimaryKeyHolder pk = primaryKeyHolders.get(i);
            table.addPrimaryKey(pk.name, pk.column.getName(), i);
        }
        Collections.sort(indexColumnHolders);
        for (int i = 0; i < indexColumnHolders.size(); i++) {
            IndexColumnHolder index = indexColumnHolders.get(i);
            table.addIndex(index.name, index.column.getName(), i, index.acs, index.unique, index.type);
        }
    }

    /**
     * 构建方法继承连表，因为泛型是在父类声明的，但是却是在子类中定义的，所以需要构建一个继承连，一点点找
     *
     * @param linkedList
     */
    private void readClassList(LinkedList<ClassTypeHolder> linkedList) {
        Type superType = linkedList.getFirst().entityClass.getGenericSuperclass();
        if (!superType.equals(Object.class)) {
            if (superType instanceof Class) {
                linkedList.addFirst(new ClassTypeHolder(superType, (Class) superType));
                readClassList(linkedList);
            } else if (superType instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) superType).getRawType();
                if (rawType instanceof Class) {
                    linkedList.addFirst(new ClassTypeHolder(superType, (Class) rawType));
                    readClassList(linkedList);
                }
            }
        }
    }

    private void parseColumn(Field field, Relation table, Dialect dialect, LinkedList<ClassTypeHolder> linkedList, List<ColumnSortHolder> columnSortHolders, List<PrimaryKeyHolder> primaryKeyHolders, List<IndexColumnHolder> indexColumnHolders) {
        Column column = field.getAnnotation(Column.class);

        String cName = column.name();
        if (!StringUtils.hasLength(cName)) {
            cName = field.getName();
        }
        EntityRelation.FieldColumn fieldColumn = new EntityRelation.FieldColumn(table.getName(), cName, field);
        Class fieldDeclareClass = field.getDeclaringClass();
        ClassTypeHolder classTypeHolder = linkedList.stream()
                .filter(c -> c.entityClass.equals(fieldDeclareClass))
                .findFirst()
                .get();
        ColumnSortHolder columnSortHolder = new ColumnSortHolder(fieldColumn, linkedList.indexOf(classTypeHolder), column.order(), classTypeHolder.declareFields.indexOf(field));
        columnSortHolders.add(columnSortHolder);

        fieldColumn.setRemark(column.comment());
        fieldColumn.setSize(column.length());
        if (column.scale() > 0) {
            fieldColumn.setSize(column.scale());
        }
        fieldColumn.setDecimalDigits(column.precision());
        fieldColumn.setNullAble(column.nullable() ? TrueOrFalse.TRUE : TrueOrFalse.FALSE);

        fieldColumn.setTableName(table.getName());

        //推断数据类型
        determineColumnType(column, fieldColumn, field, dialect, linkedList);
        //主键
        if (field.isAnnotationPresent(Id.class)) {
            Id id = field.getAnnotation(Id.class);
            //这里主键加上非空   这里不加上非空可能有主键的修改列非空的bug
            fieldColumn.setNullAble(TrueOrFalse.FALSE);
            String name = id.value();
            if (StringUtils.isEmpty(name)) {
                name = id.name();
            }
            primaryKeyHolders.add(new PrimaryKeyHolder(fieldColumn, columnSortHolder.classLevel, id.order(), columnSortHolder.fieldOrder, name));
            //TODO 主键自增
        }
        //索引
        if (field.isAnnotationPresent(Index.class) && dialect.canIndex(fieldColumn)) {
            Index index = field.getAnnotation(Index.class);
            String indexName = index.value();
            if (StringUtils.isEmpty(indexName)) {
                indexName = index.name();
            }
            TrueOrFalse trueOrFalse;
            switch (index.asc()) {
                case DESC:
                    trueOrFalse = TrueOrFalse.FALSE;
                    break;
                case ASC:
                    trueOrFalse = TrueOrFalse.TRUE;
                    break;
                default:
                    trueOrFalse = TrueOrFalse.UN_KNOWN;
                    break;
            }
            indexColumnHolders.add(new IndexColumnHolder(
                    fieldColumn,
                    columnSortHolder.classLevel,
                    index.order(),
                    columnSortHolder.fieldOrder,
                    indexName,
                    index.unique(),
                    index.type(),
                    trueOrFalse
            ));

        }
    }

    private void determineColumnType(Column column, com.dr.framework.core.orm.jdbc.Column column1, Field field, Dialect dialect, LinkedList<ClassTypeHolder> linkedList) {   // 类型判断
        Class fieldClass = determineFieldType(field.getGenericType(), linkedList);
        boolean classIsBoolean = boolean.class.equals(fieldClass) || Boolean.class.equals(fieldClass);
        if (column.jdbcType() != Types.NULL) {
            column1.setType(column.jdbcType());
            if (Dialect.isNumeric(column.jdbcType())) {
                computeDigital(column, column1, dialect, fieldClass);
            }
            if (classIsBoolean) {
                column1.setSize(1);
            }
        } else {
            ColumnType columnType = column.type();
            if (columnType.equals(ColumnType.AUTO)) {
                if (isNumber(fieldClass)) {
                    columnType = ColumnType.FLOAT;
                } else if (Date.class.isAssignableFrom(fieldClass)) {
                    columnType = ColumnType.DATE;
                } else if (String.class.equals(fieldClass)) {
                    columnType = ColumnType.VARCHAR;
                } else if (classIsBoolean) {
                    columnType = ColumnType.BOOLEAN;
                }
            }
            //长度
            int length = column.length();
            switch (columnType) {
                case INT:
                case FLOAT:
                    computeDigital(column, column1, dialect, fieldClass);
                    break;
                case BOOLEAN:
                    column1.setType(dialect.getColumnType(fieldClass, 0, length));
                    column1.setSize(1);
                    break;
                case VARCHAR:
                    column1.setType(dialect.getColumnType(fieldClass, 0, length));
                    column1.setSize(length);
                    break;
                case DATE:
                    if (Date.class.isAssignableFrom(fieldClass)) {
                        //日期类型的字段
                        column1.setType(dialect.getColumnType(fieldClass, 0, 0));
                    } else if (isNumber(fieldClass)) {
                        column1.setSize(13);
                        column1.setDecimalDigits(0);
                        //数字类型的日期，默认存放13位长的毫秒数据
                        column1.setType(dialect.getColumnType(fieldClass, 0, 13));
                    }
                    break;
                case BLOB:
                    column1.setType(Types.BLOB);
                    column1.setSize(Integer.MIN_VALUE);
                    break;
                case CLOB:
                    column1.setType(Types.CLOB);
                    column1.setSize(Integer.MIN_VALUE);
                    break;
                case OTHER:
                    column1.setType(Types.OTHER);
                    break;
                default:
                    break;
            }
            Assert.isTrue(column1.getType() != 0, String.format("不能判断字段%s的类型或长度", field));
        }
    }

    private void computeDigital(Column column, com.dr.framework.core.orm.jdbc.Column column1, Dialect dialect, Class fieldClass) {
        //长度
        int length = column.length();
        //标度（小数点后几位）
        int scale = column.scale();
        //精度（总共多少位）
        int precision = column.precision();
        //数字类型
        if (precision == 0 && length > 0) {
            precision = length;
        }
        //小数点后最大30位
        if (scale > 30) {
            scale = 30;
        }
        //scale不能大于总长度
        if (scale > precision) {
            precision = precision + scale;
        }
        //十进制数字最长65位
        if (precision > 38) {
            precision = 38;
        }
        if (fieldClass.equals(short.class) || fieldClass.equals(Short.class)) {
            //int类型最大存储10位十进制数据
            precision = precision > 5 ? 5 : precision;
            scale = 0;
        } else if (fieldClass.equals(int.class) || fieldClass.equals(Integer.class)) {
            //int类型最大存储10位十进制数据
            precision = precision > 10 ? 10 : precision;
            scale = 0;
        } else if (fieldClass.equals(long.class) || fieldClass.equals(Long.class)) {
            //int类型最大存储10位十进制数据
            precision = precision > 19 ? 19 : precision;
            scale = 0;
        } else if (fieldClass.equals(double.class) || fieldClass.equals(Double.class) || fieldClass.equals(float.class) || fieldClass.equals(Float.class)) {
            //int类型最大存储10位十进制数据
            precision = precision > 65 ? 65 : precision;
        }
        if (column1.getType() == Types.NULL || column1.getType() == 0) {
            column1.setType(dialect.getColumnType(fieldClass, scale, precision));
        }
        column1.setSize(precision);
        column1.setDecimalDigits(scale);
    }


    private boolean isNumber(Class fieldClass) {
        return Number.class.isAssignableFrom(fieldClass)
                || fieldClass.equals(long.class)
                || fieldClass.equals(float.class)
                || fieldClass.equals(double.class)
                || fieldClass.equals(int.class)
                || fieldClass.equals(short.class);
    }

    /**
     * 根据字段类型，判断字段的class，也能够识别泛型
     *
     * @param fieldType
     * @param linkedList
     * @return
     */
    private Class determineFieldType(Type fieldType, LinkedList<ClassTypeHolder> linkedList) {
        if (fieldType instanceof Class) {
            return (Class) fieldType;
        } else if (fieldType instanceof TypeVariable) {
            //先查找到声明位置
            GenericDeclaration genericDeclaration = ((TypeVariable) fieldType).getGenericDeclaration();
            int index = Arrays.asList(genericDeclaration.getTypeParameters()).indexOf(fieldType);
            ParameterizedType declareType =
                    linkedList.stream()
                            .filter(type -> type.entityClass.equals(genericDeclaration))
                            .findFirst()
                            .map(type -> (ParameterizedType) type.type)
                            .get();
            return determineFieldType(declareType.getActualTypeArguments()[index], linkedList);
        } else {
            return null;
        }
    }

    private class ColumnSortHolder implements Comparable<ColumnSortHolder> {
        EntityRelation.FieldColumn column;
        int classLevel;
        int columnOrder;
        int fieldOrder;

        public ColumnSortHolder(EntityRelation.FieldColumn column, int classLevel, int columnOrder, int fieldOrder) {
            this.column = column;
            this.classLevel = classLevel;
            this.columnOrder = columnOrder;
            this.fieldOrder = fieldOrder;
        }

        @Override
        public int compareTo(ColumnSortHolder o) {
            if (classLevel > o.classLevel) {
                return 1;
            } else if (classLevel == o.classLevel) {
                if (columnOrder > o.columnOrder) {
                    return 1;
                } else if (columnOrder == o.columnOrder) {
                    return fieldOrder - o.fieldOrder;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }
    }

    private class PrimaryKeyHolder extends ColumnSortHolder {
        String name;

        public PrimaryKeyHolder(EntityRelation.FieldColumn column, int classLevel, int columnOrder, int fieldOrder, String name) {
            super(column, classLevel, columnOrder, fieldOrder);
            this.name = name;
        }
    }

    private class IndexColumnHolder extends ColumnSortHolder {
        String name;
        boolean unique;
        int type;
        TrueOrFalse acs;

        public IndexColumnHolder(EntityRelation.FieldColumn column, int classLevel, int columnOrder, int fieldOrder, String name, boolean unique, int type, TrueOrFalse acs) {
            super(column, classLevel, columnOrder, fieldOrder);
            this.name = name;
            this.unique = unique;
            this.type = type;
            this.acs = acs;
        }
    }


    private class ClassTypeHolder {
        Type type;
        Class entityClass;
        List<Field> declareFields;

        public ClassTypeHolder(Type type, Class entityClass) {
            this.type = type;
            this.entityClass = entityClass;
            declareFields = Arrays.asList(entityClass.getDeclaredFields())
                    .stream()
                    .filter(f -> f.isAnnotationPresent(Column.class))
                    .collect(Collectors.toList());
        }
    }

}
