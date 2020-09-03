package com.dr.framework.core.orm.database;

import com.dr.framework.core.orm.database.dialect.OracleDialect;
import com.dr.framework.core.orm.database.tools.DataBaseChangeInfo;
import com.dr.framework.core.orm.jdbc.Column;
import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.jdbc.TrueOrFalse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库方言处理类
 *
 * @author dr
 */
public abstract class Dialect {
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    protected Logger logger = LoggerFactory.getLogger(Dialect.class);
    List<ClassTypeHolder> classTypeHolders = Collections.synchronizedList(new ArrayList<>());

    /**
     * 解析sql注解中的sql语句为适用特定数据库的sql
     *
     * @param sqlSource
     * @return
     */
    public String parseDialectSql(String sqlSource) {
        if (StringUtils.isEmpty(sqlSource)) {
            return sqlSource;
        } else if (sqlSource.startsWith("<") && !sqlSource.startsWith("<script>") && !sqlSource.startsWith("<SCRIPT>")) {
            try {
                DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
                Document document = documentBuilder.parse(new ByteArrayInputStream(String.format("<scripts>%s</scripts>", sqlSource).getBytes()));
                Node defaultNode = null;
                Node dialectNode = null;
                NodeList nodeList = document.getChildNodes().item(0).getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    String nodeName = node.getNodeName();
                    if (nodeName.equalsIgnoreCase("default")) {
                        defaultNode = node;
                    } else if (nodeName.equalsIgnoreCase(getName())) {
                        dialectNode = node;
                    }
                }
                if (dialectNode != null) {
                    return dialectNode.getTextContent();
                }
                if (defaultNode != null) {
                    return defaultNode.getTextContent();
                }
            } catch (Exception e) {
                logger.error(String.format("解析sql语句失败:%s", sqlSource), e);
            }
        }
        return sqlSource;
    }

    /**
     * 解析分页查询语句
     *
     * @param sqlSource
     * @param offset
     * @param limit
     * @return
     */
    public abstract String parseToPageSql(String sqlSource, int offset, int limit);

    /**
     * 获取方言名称
     *
     * @return
     */
    protected abstract String getName();

    public String convertObjectName(String source) {
        return source;
    }

    public static boolean isNumeric(int sqlType) {
        return (/*Types.BIT == sqlType || */Types.BIGINT == sqlType || Types.DECIMAL == sqlType ||
                Types.DOUBLE == sqlType || Types.FLOAT == sqlType || Types.INTEGER == sqlType ||
                Types.NUMERIC == sqlType || Types.REAL == sqlType || Types.SMALLINT == sqlType ||
                Types.TINYINT == sqlType);
    }

    public String[] getTableTypes() {
        return new String[]{"TABLE", "VIEW"};
    }

    /**
     * 解析表基本信息
     *
     * @param resultSet
     * @return
     * @see java.sql.DatabaseMetaData#getTables(String, String, String, String[])
     */
    public List<Relation<Column>> parseTableInfo(ResultSet resultSet) {
        List<Relation<Column>> tables = new ArrayList<>();
        try {
            while (resultSet != null && resultSet.next()) {
                String type = resultSet.getString("TABLE_TYPE");
                Relation<Column> relation = new Relation<>(type.equalsIgnoreCase("TABLE"));
                relation.setName(resultSet.getString("TABLE_NAME"));
                relation.setRemark(resultSet.getString("REMARKS"));
                tables.add(relation);
            }
        } catch (Exception e) {
            logger.error("解析数据库表信息失败", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return tables;
    }

    /**
     * 解析列基本信息数据
     *
     * @param resultSet
     * @return
     * @see java.sql.DatabaseMetaData#getColumns(String, String, String, String)
     */
    public List<Column> parseColumnInfo(ResultSet resultSet) {
        List<Column> columns = new ArrayList<>();
        try {
            //兼容oracle oracle没有自增长列属性，oracle需要单独处理自增长列
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            boolean autoIncrement = false;
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                String columnName = resultSetMetaData.getColumnName(i);
                if (columnName.equals("IS_AUTOINCREMENT")) {
                    autoIncrement = true;
                    break;
                }
            }
            while (resultSet != null && resultSet.next()) {
                Column column = new Column(
                        resultSet.getString("TABLE_NAME")
                        , resultSet.getString("COLUMN_NAME")
                        , resultSet.getString("COLUMN_NAME"));
                column.setType(resultSet.getInt("DATA_TYPE"));
                column.setTypeName(resultSet.getString("TYPE_NAME"));
                column.setRemark(resultSet.getString("REMARKS"));
                column.setSize(resultSet.getInt("COLUMN_SIZE"));
                column.setDecimalDigits(resultSet.getInt("DECIMAL_DIGITS"));
                column.setNullAble(TrueOrFalse.from(resultSet.getString("IS_NULLABLE")));
                column.setPosition(resultSet.getInt("ORDINAL_POSITION"));
                //oracle数据库这两个属性都得专门处理
                if (autoIncrement) {
                    column.setDefaultValue(resultSet.getString("COLUMN_DEF"));
                }
                column.setAutoIncrement(autoIncrement ? TrueOrFalse.from(resultSet.getString("IS_AUTOINCREMENT")) : TrueOrFalse.UN_KNOWN);
                columns.add(column);
            }
        } catch (Exception e) {
            logger.error("解析数据库列信息失败", e);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return columns;
    }

    /**
     * 解析索引信息
     *
     * @param resultSet
     * @param tableMap
     * @return
     * @see java.sql.DatabaseMetaData#getIndexInfo(String, String, String, boolean, boolean)
     */
    public void parseIndexInfo(ResultSet resultSet, Map<String, Relation<Column>> tableMap) {
        try {
            while (resultSet != null && resultSet.next()) {
                int indexType = resultSet.getInt("TYPE");
                //TODO 没弄懂这个索引类型
                if (indexType == DatabaseMetaData.tableIndexStatistic) {
                    continue;
                }
                String tableName = resultSet.getString("TABLE_NAME");
                String indexName = resultSet.getString("INDEX_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                int columnIndex = resultSet.getInt("ORDINAL_POSITION");
                String asc = resultSet.getString("ASC_OR_DESC");
                TrueOrFalse trueOrFalse = "D".equalsIgnoreCase(asc) ? TrueOrFalse.FALSE : TrueOrFalse.TRUE;
              /*  if (indexType == DatabaseMetaData.tableIndexClustered) {
                    //聚簇索引一般是数据库主键，索引直接指向了一条数据的根节点
                    //这里直接认为聚簇索引就是主键了
                    tableMap.get(tableName.toUpperCase()).addPrimaryKey(indexName, columnName, columnIndex);
                } else {

                }*/
                tableMap.get(tableName.toUpperCase())
                        .addIndex(indexName, columnName, columnIndex, trueOrFalse, !resultSet.getBoolean("NON_UNIQUE"), indexType);
            }
        } catch (Exception e) {
            logger.error("解析数据库索引信息失败", e);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析主键信息
     *
     * @param resultSet
     * @param tableMap
     * @see java.sql.DatabaseMetaData#getPrimaryKeys(String, String, String)
     */
    public void parsePrimaryKey(ResultSet resultSet, Map<String, Relation<Column>> tableMap) {
        try {
            while (resultSet != null && resultSet.next()) {
                String pkName = resultSet.getString("PK_NAME");
                String tableName = resultSet.getString("TABLE_NAME");
                String columnName = resultSet.getString("COLUMN_NAME");
                int columnIndex = resultSet.getInt("KEY_SEQ");
                tableMap.get(tableName.toUpperCase())
                        .addPrimaryKey(pkName, columnName, columnIndex);
            }
        } catch (Exception e) {
            logger.error("解析数据库主键信息失败", e);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO
     * 获取删除数据库指定表的语句
     *
     * @param relation
     * @return
     */
    public List<DataBaseChangeInfo> getDropTableInfo(Relation relation) {
        //TODO 删除索引
        //TODO 删除序列
        //删除表定义
        DataBaseChangeInfo dropTableInfo = new DataBaseChangeInfo(
                String.format("drop table %s", relation.getName()),
                String.format("删除表【%s】", relation.getName())
        );
        return Arrays.asList(dropTableInfo);
    }

    /**
     * 根据表定义生成数据库ddl操作sql
     *
     * @param relation
     * @return
     */
    public List<DataBaseChangeInfo> getCreateTableInfo(Relation<? extends Column> relation) {
        List<DataBaseChangeInfo> sqls = new ArrayList<>();
        if (relation.isTable()) {
            if (relation.getColumns().isEmpty()) {
                logger.warn("表【{}】没有列，忽略更新", relation.getName());
                return sqls;
            }
            //建表sql
            StringBuilder createTable = new StringBuilder(getCreateTableString())
                    .append(' ')
                    .append(convertObjectName(relation.getName()))
                    .append(" (");
            //列
            String columnsSql = relation.getColumns()
                    .stream()
                    .map(c -> {
                        StringBuffer stringBuffer = new StringBuffer(convertObjectName(c.getName()))
                                .append(' ')
                                .append(getColumnType(c));
                        appendColumnBaseInfo(stringBuffer, c);
                        return stringBuffer.toString();
                    }).collect(Collectors.joining(","));
            createTable.append(columnsSql);
            //主键
            String primaryKeySql = relation.primaryKeySql();
            if (!StringUtils.isEmpty(primaryKeySql)) {
                createTable.append(',').append(primaryKeySql);
            }
            createTable.append(')');
            //表注释
            String remark = relation.getRemark();
            if (!StringUtils.isEmpty(remark)) {
                createTable.append(' ').append(getTableRemark(remark));
            }
            sqls.add(new DataBaseChangeInfo(createTable.toString(), "新建表：" + relation.getName()));
            //注释sql
            if (supportCommentOn()) {
                if (!StringUtils.isEmpty(remark)) {
                    String commentTableSql = "comment on table " + convertObjectName(relation.getName()) + " is '" + remark + "'";
                    sqls.add(new DataBaseChangeInfo(commentTableSql, String.format("表：【%s】，添加注释【%s】", relation.getName(), remark)));
                }
                relation.getColumns()
                        .stream()
                        .filter(c -> !StringUtils.isEmpty(c.getRemark()))
                        .forEach(c ->
                                sqls.add(new DataBaseChangeInfo(
                                        String.format("comment on column %s.%s is '%s'", convertObjectName(relation.getName()), c.getName(), c.getRemark()),
                                        String.format("表：【%s】，列：%s添加注释【%s】", relation.getName(), c.getName(), c.getRemark())
                                ))
                        );
            }
            //索引sql
            relation.indexSql()
                    .forEach((k, v) -> sqls.add(new DataBaseChangeInfo(v, String.format("表：【%s】，添加索引【%s】", relation.getName(), k))));
        } else {
            //视图的处理
            if (!StringUtils.isEmpty(relation.getCreateSql())) {
                //TODO 校验sql语句格式是否正确
                sqls.add(new DataBaseChangeInfo(relation.getCreateSql(), String.format("新建视图：【%s】", relation.getName())));
            }
        }
        return sqls;
    }

    /**
     * 有的数据库类型不支持索引
     *
     * @param column
     * @return
     */
    public boolean canIndex(Column column) {
        int columnType = column.getType();
        if (columnType == Types.LONGNVARCHAR || columnType == Types.LONGVARCHAR || columnType == Types.CLOB || columnType == Types.NCLOB) {
            logger.error("表【{}】列【{}】类型不支持建立索引", column.getTableName(), column.getName());
            return false;
        }
        return true;
    }

    public boolean supportCommentOn() {
        return false;
    }


    protected String getTableRemark(String remark) {
        return "";
    }

    protected String getColumnRemark(String remark) {
        return "";
    }

    protected String getNullColumnString() {
        return "";
    }

    /**
     * 获取列类型string
     *
     * @param column
     * @return
     */
    protected String getColumnType(Column column) {
        for (ClassTypeHolder classTypeHolder : classTypeHolders) {
            String name = classTypeHolder.getTypeName(column.getType(), column.getDecimalDigits(), column.getSize());
            if (!StringUtils.isEmpty(name)) {
                return name;
            }
        }
        return null;
    }

    /**
     * 根据java字段类型和长度精度获取sql的类型
     *
     * @param clazz
     * @param scale
     * @param precision
     * @return
     */
    public int getColumnType(Class clazz, int scale, int precision) {
        for (ClassTypeHolder classTypeHolder : classTypeHolders) {
            int type = classTypeHolder.getType(clazz, scale, precision);
            if (type != 0) {
                return type;
            }
        }
        return 0;
    }

    protected ClassTypeHolder registerClass(Class... classes) {
        return registerClass(0, classes);
    }

    protected ClassTypeHolder registerClass(int order, Class... classes) {
        ClassTypeHolder classTypeHolder = new ClassTypeHolder(order);
        classTypeHolder.registerClasses(classes);
        classTypeHolders.add(classTypeHolder);
        classTypeHolders.sort(Comparator.comparingInt(ClassTypeHolder::getOrder));
        return classTypeHolder;
    }


    protected String getCreateTableString() {
        return "create table ";
    }

    /**
     * 获取添加列信息和sql
     *
     * @param column
     * @return
     */
    public Collection<? extends DataBaseChangeInfo> getAddColumnInfo(Column column) {
        List<DataBaseChangeInfo> dataBaseChangeInfos = new ArrayList<>();
        //TODO  列位置
        StringBuffer stringBuffer = new StringBuffer(getAlterTableString(column.getTableName()))
                .append(' ')
                //添加列指令
                .append(getAddColumnString())
                .append(' ')
                //列名称
                .append(column.getName())
                .append(' ')
                //列类型
                .append(getColumnType(column));
        appendColumnBaseInfo(stringBuffer, column);
        dataBaseChangeInfos.add(
                new DataBaseChangeInfo(stringBuffer.toString()
                        , String.format(
                        "添加列【%s.%s】：【%s】"
                        , column.getTableName(), column.getName()
                        , column.getRemark()
                )
                )
        );
        return dataBaseChangeInfos;
    }

    /**
     * 绑定创建或者添加列的基本信息
     *
     * @param stringBuffer
     * @param column
     */
    protected void appendColumnBaseInfo(StringBuffer stringBuffer, Column column) {
        //默认值
        String defaultValue = column.getDefaultValue();
        if (!StringUtils.isEmpty(defaultValue)) {
            stringBuffer.append(" default ").append(defaultValue);
        }
        //是否为空
        switch (column.getNullAble()) {
            case TRUE:
                stringBuffer.append(getNullColumnString());
                break;
            case FALSE:
                stringBuffer.append(" not null");
                break;
            default:
                break;
        }
        //TODO Check校验约束,业务逻辑校验应该在代码里面完成，而不是依赖数据库
        //列注释，这里注释不一定能够生效，有可能得有新的sql语句专门用于添加注释
        stringBuffer.append(getColumnRemark(column.getRemark()));
    }

    /**
     * 添加列详细指令
     *
     * @return
     */
    protected String getAddColumnString() {
        return "add column";
    }

    protected String getAlterTableString(String tableName) {
        final StringBuilder sb = new StringBuilder("alter table ");
        if (supportsIfExistsAfterAlterTable()) {
            sb.append("if exists ");
        }
        sb.append(convertObjectName(tableName));
        return sb.toString();
    }

    private boolean supportsIfExistsAfterAlterTable() {
        return false;
    }

    /**
     * 获取更新表sql
     *
     * @param relation
     * @param jdbcTable
     * @return
     */
    public List<DataBaseChangeInfo> getUpdateTableInfo(Relation<? extends Column> relation, Relation<? extends Column> jdbcTable) {
        if (jdbcTable == null) {
            return getCreateTableInfo(relation);
        }
        List<DataBaseChangeInfo> sqls = new ArrayList<>();

        if (relation.isTable()) {
            if (relation.getColumns().isEmpty()) {
                logger.warn("表【{}】没有列，忽略更新", relation.getName());
                return sqls;
            }
            //修改表注释
            if (!StringUtils.isEmpty(relation.getRemark())) {
                if (!relation.getRemark().equals(jdbcTable.getRemark()) && supportCommentOn()) {
                    //TODO oracle 默认配置获取不到注释信息
                    if (!(this instanceof OracleDialect)) {
                        String commentTableSql = "comment on table " + convertObjectName(relation.getName()) + " is '" + relation.getRemark() + "'";
                        sqls.add(new DataBaseChangeInfo(commentTableSql, String.format("表：【%s】，添加注释【%s】", relation.getName(), relation.getRemark())));
                    }
                }
            }
            //遍历列
            relation.getColumns().forEach(c -> {
                Column jdbcColumn = jdbcTable.getColumn(c.getName());
                if (jdbcColumn == null) {
                    //列不存在则创建
                    sqls.addAll(getAddColumnInfo(c));
                } else {
                    //存在则对比类型
                    sqls.addAll(getChangeColumnInfo(c, jdbcColumn));
                }
            });
            //主键
            String newPkColumns = String.join(",", relation.primaryKeyColumns());
            String oldPkColumns = String.join(",", jdbcTable.primaryKeyColumns());
            if (!oldPkColumns.equalsIgnoreCase(newPkColumns)) {
                //主键定义不同
                String oldPkName = jdbcTable.getPrimaryKeyName();
                if (!StringUtils.isEmpty(oldPkName) || !StringUtils.isEmpty(oldPkColumns)) {
                    sqls.add(new DataBaseChangeInfo(getDropPrimaryKeySql(jdbcTable),
                            String.format("表：【%s】，删除主键【%s】", relation.getName(), oldPkName)
                    ));
                }
                //添加主键定义
                sqls.add(new DataBaseChangeInfo(
                        String.format("%s add %s", getAlterTableString(relation.getName()), relation.primaryKeySql()),
                        String.format("表：【%s】，添加主键【%s】", relation.getName(), newPkColumns)
                ));
            }
            //索引
            Map<String, String> oldIndexs = jdbcTable.indexColumns();
            Map<String, String> newIndexs = relation.indexColumns();
            Map<String, String> indexSqls = relation.indexSql();
            newIndexs.forEach((k, v) -> {
                if (!oldIndexs.containsValue(v)) {
                    if (oldIndexs.containsKey(k)) {
                        sqls.add(new DataBaseChangeInfo(
                                getDropIndexSql(relation, k),
                                String.format("表：【%s】，删除索引【%s】", relation.getName(), k)
                        ));
                    } else {
                        logger.warn("表【{}】添加新的索引【{}】，请使用数据库工具及时删除无用索引！", relation.getName(), k);
                    }
                    sqls.add(new DataBaseChangeInfo(indexSqls.get(k), String.format("表：【%s】，添加索引【%s】", relation.getName(), k)));
                }
            });
        } else {
            logger.warn("视图【{}】已经存在，忽略创建，如果有修改视图创建语句，请使用数据库工具更新。", relation.getName());
        }
        return sqls;
    }

    protected String getDropIndexSql(Relation relation, String indexName) {
        return String.format("drop index %s.%s", convertObjectName(indexName), convertObjectName(relation.getName()));
    }

    protected String getDropPrimaryKeySql(Relation jdbcTable) {
        return String.format("%s drop primary key", getAlterTableString(jdbcTable.getName()));
    }

    /**
     * 获取更改列sql
     *
     * @param newColumn 新列定义
     * @param oldColumn 旧列定义
     * @return
     */
    protected Collection<? extends DataBaseChangeInfo> getChangeColumnInfo(Column newColumn, Column oldColumn) {
        List<DataBaseChangeInfo> sqls = new ArrayList<>();
        //先检查类型变化
        if (typeChanged(newColumn, oldColumn)) {
            if (canChange(newColumn, oldColumn)) {
                sqls.add(getModifyColumnChange(newColumn, "表【%s】列【%s】修改数据类型为【%s】"));
            } else {
                //类型不兼容的话就rename
                String renameColumnName = getRenameColumnName(oldColumn, 1);
                String newColumnType = getColumnType(newColumn);
                logger.warn("由于数据类型不兼容，表【{}】，列【{} {}】重命名为【{}】，原列名【{}】的新定义为【{}】，\n请使用数据库工具完成列数据迁移！"
                        , newColumn.getTableName()
                        , newColumn.getName()
                        , getColumnType(oldColumn)
                        , renameColumnName
                        , newColumn.getName()
                        , newColumnType);
                //rename 列
                sqls.add(new DataBaseChangeInfo(
                        getRenameColumnSql(newColumn, renameColumnName),
                        String.format("表【%s】列【%s】重命名为【%s】", oldColumn.getRelation().getName(), oldColumn.getName(), renameColumnName)
                ));
                //添加列
                sqls.addAll(getAddColumnInfo(newColumn));
                //TODO 联合主键和索引的问题
            }
        } else if (!newColumn.getNullAble().equals(oldColumn.getNullAble())) {
            //非空
            sqls.add(getModifyColumnChange(newColumn, "表【%s】列【%s】修改非空约束为：" + newColumn.getNullAble()));
        }
        //单独处理注释
        if (supportCommentOn()) {
            //不支持注释的就不管了，修改注释数据库影响很大
            if (!StringUtils.isEmpty(newColumn.getRemark()) && !newColumn.getRemark().equalsIgnoreCase(oldColumn.getRemark())) {
                //TODO oracle 默认不获取备注信息
                if (!(this instanceof OracleDialect)) {
                    sqls.add(new DataBaseChangeInfo(
                            String.format("comment on column %s.%s is '%s'", convertObjectName(oldColumn.getRelation().getName()), oldColumn.getName(), newColumn.getRemark()),
                            String.format("表【%s】列【%s】修改注释为【%s】", oldColumn.getRelation().getName(), oldColumn.getName(), newColumn.getRemark())
                    ));
                }
            }
        }
        return sqls;
    }

    private DataBaseChangeInfo
    getModifyColumnChange(Column newColumn, String format) {
        //类型兼容就修改表定义为新表定义
        StringBuffer sb = new StringBuffer(getAlterTableString(newColumn.getTableName()))
                .append(' ')
                //更改列关键字
                .append(getModifyColumnString())
                .append(' ')
                //列名
                .append(convertObjectName(newColumn.getName()))
                .append(' ');
        String newColumnType = getColumnType(newColumn);
        //列类型
        sb.append(newColumnType)
                .append(' ');
        //列默认信息
        appendColumnBaseInfo(sb, newColumn);
        return new DataBaseChangeInfo(sb.toString(),
                String.format(format, newColumn.getRelation().getName(), newColumn.getName(), newColumnType)
        );
    }

    /**
     * 新列定义能不能执行
     *
     * @param newColumn
     * @param oldColumn
     * @return
     */
    protected boolean canChange(Column newColumn, Column oldColumn) {
        if (newColumn.getType() == oldColumn.getType()) {
            return true;
        }
        int newType = newColumn.getType();
        int oldType = oldColumn.getType();
        //数据类型
        if (isNumeric(newType)) {
            if (isNumeric(oldType)) {
                return newColumn.getSize() >= oldColumn.getSize() && newColumn.getDecimalDigits() >= oldColumn.getDecimalDigits();
            } else {
                return false;
            }
        }
        //字符串类型
        else if (newType == Types.CHAR
                || newType == Types.NCHAR
                || newType == Types.VARCHAR
                || newType == Types.NVARCHAR
                || newType == Types.LONGVARCHAR
                || newType == Types.LONGNVARCHAR
                || newType == Types.CLOB
                || newType == Types.NCLOB
        ) {
            if (oldType == Types.CHAR
                    || oldType == Types.NCHAR
                    || oldType == Types.VARCHAR
                    || oldType == Types.NVARCHAR
                    || oldType == Types.LONGVARCHAR
                    || oldType == Types.LONGNVARCHAR
                    || oldType == Types.CLOB
                    || oldType == Types.NCLOB) {
                return newColumn.getSize() >= oldColumn.getSize();
            } else {
                return false;
            }
        }
        //日期类型
        else if (newType == Types.TIMESTAMP || newType == Types.TIMESTAMP_WITH_TIMEZONE) {
            return oldType == Types.DATE
                    || oldType == Types.TIME
                    || oldType == Types.TIMESTAMP
                    || oldType == Types.TIME_WITH_TIMEZONE
                    || oldType == Types.TIMESTAMP_WITH_TIMEZONE;
        }
        //boolean 类型
        else if (newType == Types.BIT || newType == Types.BOOLEAN) {
            return oldType == Types.BIT || oldType == Types.BOOLEAN;
        } else if (newType == Types.BLOB || newType == Types.BINARY || newType == Types.VARBINARY || newType == Types.LONGVARBINARY) {
            return oldType == Types.BLOB || oldType == Types.BINARY || oldType == Types.VARBINARY || oldType == Types.LONGVARBINARY;
        }
        return false;
    }

    /**
     * 判断数据类型是否修改了
     *
     * @param newColumn
     * @param oldColumn
     * @return
     */
    protected boolean typeChanged(Column newColumn, Column oldColumn) {
        int newType = newColumn.getType();
        int oldType = oldColumn.getType();
        boolean typeChanged = newType != oldType;
        boolean sizeChanged = newColumn.getSize() > oldColumn.getSize();
        if (typeChanged) {
            //数据类型
            if (isNumeric(newType) && isNumeric(oldType)) {
                return !getColumnType(newColumn).equalsIgnoreCase(getColumnType(oldColumn));
            }
            //字符串类型
            else if (
                    (newType == Types.CHAR
                            || newType == Types.NCHAR
                            || newType == Types.VARCHAR
                            || newType == Types.NVARCHAR
                            || newType == Types.LONGVARCHAR
                            || newType == Types.LONGNVARCHAR
                            || newType == Types.CLOB
                            || newType == Types.NCLOB)
                            && (oldType == Types.CHAR
                            || oldType == Types.NCHAR
                            || oldType == Types.VARCHAR
                            || oldType == Types.NVARCHAR
                            || oldType == Types.LONGVARCHAR
                            || oldType == Types.LONGNVARCHAR
                            || oldType == Types.CLOB
                            || oldType == Types.NCLOB)
            ) {
                return sizeChanged;
            }
            //日期类型
            else if ((oldType == Types.TIMESTAMP || oldType == Types.TIMESTAMP_WITH_TIMEZONE) && (
                    newType == Types.DATE
                            || newType == Types.TIME
                            || newType == Types.TIMESTAMP
                            || newType == Types.TIME_WITH_TIMEZONE
                            || newType == Types.TIMESTAMP_WITH_TIMEZONE
            )) {
                return false;
            }
            //blob
            else if ((oldType == Types.BLOB || oldType == Types.BINARY || oldType == Types.VARBINARY || oldType == Types.LONGVARBINARY) && (
                    newType == Types.BLOB || newType == Types.BINARY || newType == Types.VARBINARY || newType == Types.LONGVARBINARY
            )) {
                return false;
            }
            //TODO 其他类型，脑子转不过来了
            String oldColumnType = getColumnType(oldColumn);
            String newColumnType = getColumnType(newColumn);
            return !newColumnType.equalsIgnoreCase(oldColumnType);
        } else if (sizeChanged) {
            return true;
        } else if (isNumeric(newType)) {
            return newColumn.getDecimalDigits() > oldColumn.getDecimalDigits();
        } else {
            return false;
        }
    }

    protected String getRenameColumnSql(Column newColumn, String renameColumnName) {
        return String.format("%s rename column %s to %s"
                , getAlterTableString(newColumn.getTableName())
                , convertObjectName(newColumn.getName())
                , convertObjectName(renameColumnName));
    }

    private String getRenameColumnName(Column oldColumn, int i) {
        String renameName = oldColumn.getName() + i;
        if (oldColumn.getRelation().getColumn(renameName) == null) {
            return renameName;
        } else {
            return getRenameColumnName(oldColumn, i + 1);
        }
    }

    protected String getModifyColumnString() {
        return "alter column";
    }

    public boolean supportPrimaryKeyWithOutTable() {
        return false;
    }

    public boolean supportIndexInfoWithOutTable() {
        return false;
    }

}
