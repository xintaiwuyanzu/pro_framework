package com.dr.framework.common.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.query.GenSourceQuery;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liquibase.structure.core.Column;
import liquibase.structure.core.DataType;
import liquibase.structure.core.PrimaryKey;
import liquibase.structure.core.Table;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/codeGen")
@Api(tags = "代码生成模块")
public class CodeGenController {
    @Autowired
    List<MybatisConfigurationBean> mybatisConfigurationBeans;
    Logger logger = LoggerFactory.getLogger(CodeGenController.class);

    /**
     * 读取数据库表名信息
     *
     * @param withDabatase
     * @return
     */
    @ApiOperation(value = "查询所有数据源的数据库树状结构信息")
    @PostMapping("/tableTree")
    public ResultEntity tableTree(boolean withDabatase) {
        List<TreeNode> treeNodes = mybatisConfigurationBeans.stream()
                .map(mybatisConfigurationBean -> mybatisConfigurationBean.tableTree(withDabatase))
                .collect(Collectors.toList());
        return ResultEntity.success(treeNodes);
    }

    /**
     * 读取数据库表结构信息
     *
     * @param dataSource
     * @param tableName
     * @return
     */
    @RequestMapping("/columns")
    public ResultEntity columns(String dataSource, String tableName) {
        Optional<MybatisConfigurationBean> optional = mybatisConfigurationBeans.stream().
                filter(mybatisConfigurationBean1 -> mybatisConfigurationBean1.getDatabaseId().equalsIgnoreCase(dataSource))
                .findFirst();
        if (optional.isPresent()) {
            Table table = optional.get().getTableMap(tableName).get(tableName.toUpperCase());
            return ResultEntity.success(
                    table.getColumns()
                            .stream()
                            .map(column -> {
                                Map<String, Object> attrs = new HashMap<>();
                                attrs.put("defaultValue", column.getDefaultValue());
                                attrs.put("name", column.getName());
                                attrs.put("remarks", column.getRemarks());
                                attrs.put("nullable", column.isNullable());
                                attrs.put("type", column.getType());
                                return attrs;
                            }).collect(Collectors.toList()));
        } else {
            return ResultEntity.error("未找到指定的数据源:" + dataSource);
        }
    }

    /**
     * 根据请求参数生成代码
     *
     * @param query
     * @return
     */
    @RequestMapping("/genSource")
    public ResultEntity genSource(GenSourceQuery query) {
        ResultEntity resultEntity;
        MybatisConfigurationBean mybatisConfigurationBean = null;
        for (MybatisConfigurationBean mybatisConfigurationBean1 : mybatisConfigurationBeans) {
            if (mybatisConfigurationBean1.getDatabaseId().equalsIgnoreCase(query.getDatasource())) {
                mybatisConfigurationBean = mybatisConfigurationBean1;
                break;
            }
        }
        if (mybatisConfigurationBean != null) {
            if (query.getTables() != null) {
                Map<String, Table> tableMap = mybatisConfigurationBean.getTableMap(null);
                List<String> generateMessages = query.getTables().stream()
                        .map(table -> doGen(query, tableMap.get(table.get("tableName").toString().toUpperCase()), table))
                        .collect(Collectors.toList());
                resultEntity = ResultEntity.success("生成成功!", generateMessages);
            } else {
                resultEntity = ResultEntity.error("没有指定要生成的表信息");
            }
        } else {
            resultEntity = ResultEntity.error("未找到指定的数据源：" + query.getDatasource());
        }
        return resultEntity;
    }

    protected String doGen(GenSourceQuery query, Table table, Map<String, Object> tableConfig) {
        //返回消息
        String message;
        String tableName = (String) tableConfig.get("tableName");
        if (table != null) {
            try {
                //准备content参数
                File dir = new File(query.getPath());
                File packageDir = new File(dir, query.getPackageName().replace(".", "/"));
                if (!packageDir.exists()) {
                    packageDir.mkdirs();
                }
                VelocityContext vc = new VelocityContext();
                vc.put("query", query);
                vc.put("tableConfig", tableConfig);
                vc.put("hasDate", false);
                vc.put("columns", table.getColumns().stream().sorted(Comparator.comparingInt(Column::getOrder)).map(column -> mapColumnAttr(vc, table, column)).collect(Collectors.toList()));
                File entityFile = new File(packageDir, tableConfig.get("entityName") + ".java");
                FileWriter fileWriter = new FileWriter(entityFile);
                getTemplate().merge(vc, fileWriter);
                fileWriter.close();
                message = String.format("表%s生成成功！", tableName);
            } catch (Exception e) {
                e.printStackTrace();
                message = String.format("表%s生成失败：%s", tableName, e.getMessage());
            }
        } else {
            message = String.format("没查询到表%s的结构信息", tableName);
        }
        return message;
    }


    VelocityEngine velocityEngine;

    protected Template getTemplate() {
        if (velocityEngine == null) {
            velocityEngine = new VelocityEngine();
            velocityEngine.setProperty("resource.loader", "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        }
        return velocityEngine.getTemplate("/template/codegen/entity.vm");
    }


    protected Map<String, Object> mapColumnAttr(VelocityContext vc, Table table, Column column) {

        Map<String, Object> colConfig = new HashMap<>();
        DataType dataType = column.getType();
        String type;
        ColumnType columnType = null;
        switch (dataType.getTypeName().toLowerCase()) {
            case "varchar":
            case "nvarchar":
            case "text":
                type = "String";
                columnType = ColumnType.VARCHAR;
                break;
            case "ntext":
            case "nchar":
                type = "String";
                columnType = ColumnType.CLOB;
                break;
            case "bit":
                type = "boolean";
                columnType = ColumnType.BOOLEAN;
                break;
            case "smallint":
            case "bigint":
                type = "long";
                columnType = ColumnType.FLOAT;
                break;
            case "decimal":
            case "numeric":
            case "number":
                type = "double";
                columnType = ColumnType.FLOAT;
                break;
            case "datetime":
            case "datetime2":
            case "smalldatetime":
                vc.put("hasDate", true);
                type = "Date";
                columnType = ColumnType.DATE;
                break;
            case "image":
                type = "byte[]";
                columnType = ColumnType.BLOB;
                break;
            default:
                type = dataType.getTypeName();
                break;
        }
        colConfig.put("name", column.getName());
        colConfig.put("type", type);
        colConfig.put("columnType", columnType);
        colConfig.put("jdbctype", dataType.getDataTypeId());
        colConfig.put("order", column.getOrder());
        colConfig.put("getter", getter((String) colConfig.get("type"), column.getName()));
        colConfig.put("setter", setter((String) colConfig.get("type"), column.getName()));
        colConfig.put("comment", column.getRemarks());
        colConfig.put("length", dataType.getColumnSize());
        colConfig.put("scale", dataType.getRadix());
        colConfig.put("primary", "false");
        PrimaryKey primaryKey = table.getPrimaryKey();
        if (primaryKey != null) {
            if (primaryKey.getColumns().stream().anyMatch(column1 -> column1.getName().equalsIgnoreCase(column.getName()))) {
                colConfig.put("primary", "true");
            }
        }
        return colConfig;
    }

    private String getter(String type, String name) {
        String getter = "get";
        if ("boolean".equalsIgnoreCase(type)) {
            getter = "is";
        }
        return getter + firstUplower(name);
    }

    private String setter(String type, String name) {
        String setter = "set";
        return setter + firstUplower(name);
    }

    private String firstUplower(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1, text.length());
    }

}
