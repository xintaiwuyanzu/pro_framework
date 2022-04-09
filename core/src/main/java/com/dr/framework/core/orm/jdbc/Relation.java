package com.dr.framework.core.orm.jdbc;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class Relation<C extends Column> {
    static final String DEFAULT_AUTO_NAME = "$AUTONAME";
    private String name;
    private String module;
    private String remark;
    /* 下面的属性用来判断视图*/
    private boolean isTable;
    private String createSql;
    /**
     * 列
     */
    private Map<String, C> columnMap = new HashMap<>();
    /**
     * 主键，单表只能有一个主键，但是主键可以是多列联合的
     */
    private ColumnsHolder<ColumnHolder> primaryKeys;
    /**
     * 索引，可以有多列联合索引
     */
    private Map<String, ColumnsHolder<IndexHolder>> indexMap = new HashMap<>();

    public Relation(boolean isTable) {
        this.isTable = isTable;
    }

    /**
     * 添加列
     *
     * @param column
     * @return
     */
    public Relation addColumn(C column) {
        Assert.isTrue(!columnMap.containsKey(column.getName().toUpperCase()), "不能存在重复列");
        column.setRelation(this);
        column.setTableName(getName());
        columnMap.put(column.getName().toUpperCase(), column);
        return this;
    }

    /**
     * 添加主键
     *
     * @param name
     * @param columnName
     * @param columnPosition
     * @return
     */
    public Relation addPrimaryKey(String name, String columnName, int columnPosition) {
        if (primaryKeys != null) {
            Assert.isTrue(name.equalsIgnoreCase(primaryKeys.name), "不能设置多个名称不同的主键");
        } else {
            primaryKeys = new ColumnsHolder<>(name);
        }
        //有可能重复添加
        for (ColumnHolder columnHolder : primaryKeys.columns) {
            if (columnHolder.columnName.equalsIgnoreCase(columnName) && columnHolder.columnIndex == columnPosition) {
                return this;
            }
        }
        primaryKeys.columns.add(new ColumnHolder(columnName, columnPosition));
        return this;
    }

    public Relation addIndex(String name, String columnName, int columnPosition, TrueOrFalse asc, boolean unique, int type) {
        if (!StringUtils.hasText(name)) {
            name = DEFAULT_AUTO_NAME;
        } else if (primaryKeys != null) {
            //索引是主键，则添加主键定义
            if (name.equalsIgnoreCase(primaryKeys.name)) {
                return addPrimaryKey(name, columnName, columnPosition);
            }
        }
        indexMap.computeIfAbsent(name, ColumnsHolder::new)
                .columns.add(new IndexHolder(columnName, columnPosition, asc, unique, type));
        return this;
    }

    public String primaryKeySql() {
        if (primaryKeys != null && !primaryKeys.columns.isEmpty()) {
            String format = "primary key ( %2$s )";
            String pkName = primaryKeys.name;
            if (!StringUtils.hasText(pkName)) {
                if (primaryKeys.columns.size() > 1) {
                    pkName = "PK_" + getName();
                    format = "constraint   %1$s  primary key ( %2$s )";
                }
            } else {
                format = "constraint   %1$s  primary key ( %2$s )";
            }
            return String.format(format, pkName, primaryKeys.columns.stream().sorted().map(c -> c.columnName).collect(Collectors.joining(",")));
        }
        return null;
    }

    public String getPrimaryKeyName() {
        return primaryKeys == null ? null : primaryKeys.name;
    }

    public String getPrimaryKeyAlias() {
        return String.join("", primaryKeyColumns());
    }

    public List<String> primaryKeyColumns() {
        return primaryKeys == null ?
                Collections.EMPTY_LIST :
                primaryKeys.columns
                        .stream()
                        .sorted()
                        .map(c -> c.columnName)
                        .collect(Collectors.toList());
    }


    public Map<String, String> indexSql() {
        Map<String, String> map = new HashMap<>();
        for (String key : indexMap.keySet()) {
            ColumnsHolder<IndexHolder> indexHolders = indexMap.get(key);
            if (key.equalsIgnoreCase(DEFAULT_AUTO_NAME)) {
                //没有声明索引名称，则表示每列单独建立索引
                indexHolders.columns
                        .forEach(c -> {
                            String indexName = "Idx_" + c.columnName;
                            map.put(indexName
                                    , buildCreateIndexSql(c.unique, indexName, appendIndex(c)));
                        });
            } else {
                //声明了索引名称表示联合索引
                boolean unique = indexHolders.columns.stream().anyMatch(i -> i.unique);
                map.put(
                        indexHolders.name
                        , buildCreateIndexSql(
                                unique
                                , indexHolders.name
                                , indexHolders.columns.stream().sorted().map(this::appendIndex).collect(Collectors.joining(","))
                        ));
            }
        }
        return map;
    }

    /**
     * @return key 索引名称，value，索引组成
     */
    public Map<String, String> indexColumns() {
        Map<String, String> map = new HashMap<>();
        for (String key : indexMap.keySet()) {
            ColumnsHolder<IndexHolder> indexHolders = indexMap.get(key);
            if (key.equalsIgnoreCase(DEFAULT_AUTO_NAME)) {
                //没有声明索引名称，则表示每列单独建立索引
                indexHolders.columns
                        .forEach(c -> {
                            String indexName = "Idx_" + c.columnName;
                            map.put(indexName
                                    , String.format("%s_%s_%s", c.columnName, c.acs, c.unique).toUpperCase());
                        });
            } else {
                //声明了索引名称表示联合索引
                boolean unique = indexHolders.columns.stream().anyMatch(i -> i.unique);
                map.put(
                        indexHolders.name
                        , String.format("%s_%s"
                                , indexHolders.columns.stream().sorted()
                                        .map(c -> c.columnName + "_" + c.acs)
                                        .collect(Collectors.joining("_"))
                                , unique
                        ).toUpperCase()
                );
            }
        }
        return map;
    }


    private String buildCreateIndexSql(boolean unique, String indexName, String indexColumn) {
        StringBuilder stringBuffer = new StringBuilder("create ");
        if (unique) {
            stringBuffer.append("unique ");
        }
        stringBuffer.append("index ")
                .append(indexName)
                .append(" on ")
                .append(name)
                .append('(')
                .append(indexColumn)
                .append(')');
        return stringBuffer.toString();
    }

    private String appendIndex(IndexHolder indexHolder) {
        StringBuilder sb = new StringBuilder(indexHolder.columnName);
        switch (indexHolder.acs) {
            case TRUE:
                sb.append(" asc");
                break;
            case FALSE:
                sb.append(" desc");
                break;
            default:
                break;
        }
        return sb.toString();
    }


    public Collection<C> getColumns() {
        return columnMap.values()
                .stream()
                .sorted(Comparator.comparing(Column::getPosition))
                .peek(c -> {
                    if (primaryKeys != null) {
                        //如果列是联合主键，则列不能为空
                        for (ColumnHolder columnHolder : primaryKeys.columns) {
                            if (columnHolder.columnName.equalsIgnoreCase(c.getName())) {
                                c.setNullAble(TrueOrFalse.FALSE);
                                break;
                            }
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    public C getColumn(String name) {
        return columnMap.get(name.toUpperCase());
    }

    public boolean isTable() {
        return isTable;
    }

    public void setTable(boolean table) {
        isTable = table;
    }

    public String getCreateSql() {
        return createSql;
    }

    public void setCreateSql(String createSql) {
        this.createSql = createSql;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * ============================================================================
     * TODO jdbc获取不到数据库约束，默认所有的唯一约束通过唯一索引实现
     *
     * ============================================================================
     */
    /**
     * 唯一键，可以有多列联合唯一
     */
    private Map<String, ColumnsHolder<ColumnHolder>> uniqueKeys = new HashMap<>();

    Relation addUniqueKeys(String columnName, int columnPosition) {
        return addUniqueKeys(DEFAULT_AUTO_NAME, columnName, columnPosition);
    }

    /**
     * 添加唯一约束
     *
     * @param name
     * @param columnName
     * @param columnPosition
     * @return
     */
    Relation addUniqueKeys(String name, String columnName, int columnPosition) {
        if (!StringUtils.hasText(name)) {
            name = DEFAULT_AUTO_NAME;
        }
        uniqueKeys.computeIfAbsent(name, ColumnsHolder::new)
                .columns.add(new ColumnHolder(columnName, columnPosition));
        return this;
    }

    private static class ColumnsHolder<C extends ColumnHolder> {
        String name;
        Collection<C> columns = new ArrayList<>();

        public ColumnsHolder(String name) {
            this.name = name;
        }
    }

    private static class ColumnHolder implements Comparable<ColumnHolder> {
        String columnName;
        int columnIndex;

        ColumnHolder(String columnName, int columnIndex) {
            this.columnName = columnName;
            this.columnIndex = columnIndex;
        }

        @Override
        public int compareTo(ColumnHolder o) {
            return columnIndex - o.columnIndex;
        }
    }

    private static class IndexHolder extends ColumnHolder {
        TrueOrFalse acs;
        boolean unique;
        int type;

        public IndexHolder(String columnName, int columnIndex, TrueOrFalse acs, boolean unique, int type) {
            super(columnName, columnIndex);
            this.acs = acs;
            this.unique = unique;
            this.type = type;
        }
    }

}
