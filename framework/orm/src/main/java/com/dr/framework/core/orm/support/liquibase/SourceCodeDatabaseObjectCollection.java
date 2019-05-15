package com.dr.framework.core.orm.support.liquibase;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Id;
import liquibase.database.Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.snapshot.SnapshotIdService;
import liquibase.structure.DatabaseObjectCollection;
import liquibase.structure.core.DataType;
import liquibase.structure.core.PrimaryKey;
import liquibase.structure.core.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dr
 */
public class SourceCodeDatabaseObjectCollection extends DatabaseObjectCollection {
    Logger logger = LoggerFactory.getLogger(SourceCodeDatabaseObjectCollection.class);
    private static final String RESOURCE_PATTERN = "/**/*.class";
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
    private Database database;
    private Map<MetadataReader, Table> metaTables = new ConcurrentHashMap<>();
    SnapshotIdService snapshotIdService;

    public SourceCodeDatabaseObjectCollection(Database database, String... packages) {
        super(database);
        this.database = database;
        if (packages != null) {
            scan(packages);
        }
        snapshotIdService = SnapshotIdService.getInstance();
    }

    public Collection<Table> getTables() {
        return metaTables.values();
    }

    public Table getTableInfo(Class modelClass) {
        try {
            MetadataReader reader = readerFactory.getMetadataReader(modelClass.getName());
            if (metaTables.containsKey(reader)) {
                return metaTables.get(reader);
            }
            parseClass(reader);
            return metaTables.get(reader);
        } catch (IOException e) {
            logger.error(String.format("读取表[%s]信息失败", modelClass.getName()), e);
        }
        return null;
    }

    /**
     * 扫描所有的包并且缓存信息
     *
     * @param pkgs 要扫描的包
     */
    public void scan(String... pkgs) {
        for (String pkg : pkgs) {
            String pat = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
            try {
                Resource[] resources = resourcePatternResolver.getResources(pat);
                for (Resource resource : resources) {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    if (reader.getAnnotationMetadata().hasAnnotation(com.dr.framework.core.orm.annotations.Table.class.getName())) {
                        parseClass(reader);
                    }
                }
            } catch (IOException e) {
                logger.error("扫描实体类失败", e);
            }
        }
    }

    private void parseClass(MetadataReader reader) {
        if (metaTables.containsKey(reader)) {
            return;
        }
        logger.debug("parsing entity:【{}】", reader.getClassMetadata().getClassName());
        Map<String, Object> annotationAttributes = reader.getAnnotationMetadata().getAnnotationAttributes(com.dr.framework.core.orm.annotations.Table.class.getName());
        liquibase.structure.core.Table table = new liquibase.structure.core.Table();
        table.setSchema(database.getDefaultCatalogName(), database.getDefaultSchemaName());
        table.setName((String) annotationAttributes.get("name"));
        table.setSnapshotId(snapshotIdService.generateId());
        String comment = (String) annotationAttributes.get("comment");
        if (!StringUtils.isEmpty(comment)) {
            table.setRemarks((String) annotationAttributes.get("comment"));
        }
        add(table);
        metaTables.put(reader, table);
        ClassLoader loader = getClass().getClassLoader();
        String clssName = reader.getClassMetadata().getClassName();
        try {
            Class clazz = loader.loadClass(clssName);
            List<liquibase.structure.core.Column> columnList = new ArrayList<>();
            ReflectionUtils.doWithFields(clazz, field -> {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    liquibase.structure.core.Column column1 = parseColumn(clazz, column, field, table);
                    column1.setAttribute("field", field.getName());
                    Id id = field.getAnnotation(Id.class);
                    if (id != null) {
                        PrimaryKey primaryKey = new PrimaryKey();
                        primaryKey.setTable(table);
                        primaryKey.setName(database.generatePrimaryKeyName(table.getName()));
                        primaryKey.setSnapshotId(snapshotIdService.generateId());
                        primaryKey.addColumn(0, column1);
                        add(primaryKey);

                        column1.setNullable(false);
                        table.setPrimaryKey(primaryKey);
                    }
                    columnList.add(column1);
                }
            });
            Collections.sort(columnList, ((o1, o2) -> o2.getOrder() - o1.getOrder()));
            columnList.stream().forEach(this::add);
        } catch (ClassNotFoundException e) {
            logger.error(String.format("读取表[%s]信息失败", clssName), e);
        }
    }

    private liquibase.structure.core.Column parseColumn(Class clazz, Column column, Field field, Table table) {
        String colName = column.name();
        if (StringUtils.isEmpty(colName)) {
            colName = field.getName();
        }
        liquibase.structure.core.Column columnObj = new liquibase.structure.core.Column(colName);
        columnObj.setRelation(table);
        if (!StringUtils.isEmpty(column.comment())) {
            columnObj.setRemarks(column.comment());
        }
        columnObj.setType(determanDataType(clazz, column, field));
        columnObj.setOrder(Integer.MIN_VALUE - column.order());
        columnObj.setSnapshotId(snapshotIdService.generateId());
        columnObj.setNullable(column.nullable());

        table.addColumn(columnObj);
        return columnObj;
    }

    private DataType determanDataType(Class clazz, Column column, Field field) {
        ColumnType columnType = column.type();
        Type typeVariables = clazz.getGenericSuperclass();
        Type fieldGenericType = field.getGenericType();
        if (fieldGenericType instanceof TypeVariable && typeVariables instanceof ParameterizedType) {
            fieldGenericType = ((ParameterizedType) typeVariables).getActualTypeArguments()[0];
        }
        if (columnType.equals(ColumnType.AUTO)) {
            if (fieldGenericType.equals(String.class)) {
                columnType = ColumnType.VARCHAR;
            } else if (fieldGenericType.equals(int.class) || fieldGenericType.equals(Integer.class) || fieldGenericType.equals(long.class) || fieldGenericType.equals(Long.class)) {
                columnType = ColumnType.INT;
            } else if (fieldGenericType.equals(boolean.class) || fieldGenericType.equals(Boolean.class)) {
                columnType = ColumnType.BOOLEAN;
            } else if (fieldGenericType.equals(double.class) || fieldGenericType.equals(Double.class) || fieldGenericType.equals(float.class) || fieldGenericType.equals(Float.class)) {
                columnType = ColumnType.FLOAT;
            }
        }
        DataType dataType = new DataType("");
        switch (columnType) {
            case INT:
            case DATE:
            case FLOAT:
                int length = column.precision();
                int scale = column.scale();
                if (length == 0) {
                    length = column.length();
                    if (length < scale) {
                        length = length + scale;
                    }
                }
                if (length > 38) {
                    length = 38;
                }
                dataType.setColumnSizeUnit(DataType.ColumnSizeUnit.BYTE);
                dataType.setDataTypeId(Types.NUMERIC);
                dataType.setTypeName("numeric");
                if (length < scale) {
                    logger.error("{}数据长度{}不能小于精度{}", field, length, scale);
                }
                dataType.setColumnSize(length);
                if (scale > 0) {
                    if (database instanceof OracleDatabase) {
                        dataType.setTypeName("NUMBER");
                    }
                    dataType.setDecimalDigits(scale);
                }
                break;
            case BLOB:
                dataType.setTypeName("blob");
                break;
            case CLOB:
                dataType.setTypeName("clob");
                break;
            case BOOLEAN:
                if (database instanceof OracleDatabase) {
                    dataType.setTypeName("NUMBER");
                    dataType.setColumnSizeUnit(DataType.ColumnSizeUnit.BYTE);
                    dataType.setColumnSize(1);
                    dataType.setDecimalDigits(0);
                    dataType.setDataTypeId(Types.DECIMAL);
                } else if (database instanceof MSSQLDatabase || database instanceof MySQLDatabase) {
                    dataType.setTypeName("bit");
                    dataType.setColumnSizeUnit(DataType.ColumnSizeUnit.BYTE);
                    dataType.setDataTypeId(Types.BIT);
                }
                break;
            case VARCHAR:
                dataType.setTypeName("varchar");
                if (column.length() > 0) {
                    dataType.setColumnSize(column.length());
                } else {
                    dataType.setColumnSize(255);
                }
                dataType.setDataTypeId(Types.VARCHAR);
                dataType.setColumnSizeUnit(DataType.ColumnSizeUnit.BYTE);
                break;
            default:
                break;
        }
        return dataType;
    }
}
