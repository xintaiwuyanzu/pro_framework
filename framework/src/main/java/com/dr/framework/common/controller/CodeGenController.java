package com.dr.framework.common.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.entity.ResultListEntity;
import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.query.GenSourceQuery;
import com.dr.framework.core.orm.jdbc.Column;
import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
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

/**
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/codeGen")
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
    @PostMapping("/tableTree")
    public ResultListEntity<TreeNode> tableTree(boolean withDabatase) {
        List<TreeNode> treeNodes = mybatisConfigurationBeans.stream()
                .map(mybatisConfigurationBean -> mybatisConfigurationBean.tableTree(withDabatase))
                .collect(Collectors.toList());
        return ResultListEntity.success(treeNodes);
    }

    /**
     * 读取数据库表结构信息
     *
     * @param dataSource
     * @param tableName
     * @return
     */
    @PostMapping("/columns")
    public ResultListEntity<Map<String, Object>> columns(String dataSource, String tableName) {
        Optional<MybatisConfigurationBean> optional;
        if (mybatisConfigurationBeans.size() == 1) {
            optional = Optional.of(mybatisConfigurationBeans.get(0));
        } else {
            optional = mybatisConfigurationBeans.stream().
                    filter(mybatisConfigurationBean1 -> mybatisConfigurationBean1.getDatabaseId().equalsIgnoreCase(dataSource))
                    .findFirst();
        }
        if (optional.isPresent()) {
            Relation<Column> table = optional.get().getTableMap().get(tableName.toUpperCase());
            return ResultListEntity.success(
                    table.getColumns()
                            .stream()
                            .map(column -> {
                                Map<String, Object> attrs = new HashMap<>();
                                attrs.put("defaultValue", column.getDefaultValue());
                                attrs.put("name", column.getName());
                                attrs.put("remarks", column.getRemark());
                                attrs.put("nullable", column.getNullAble());
                                attrs.put("type", column.getType());
                                return attrs;
                            }).collect(Collectors.toList()));
        } else {
            return ResultListEntity.error("未找到指定的数据源:" + dataSource);
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
                Map<String, Relation<Column>> tableMap = mybatisConfigurationBean.getTableMap();
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

    protected String doGen(GenSourceQuery query, Relation<Column> table, Map<String, Object> tableConfig) {
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
                vc.put("columns", table.getColumns().stream().sorted(Comparator.comparingInt(Column::getPosition)).map(column -> mapColumnAttr(vc, table, column)).collect(Collectors.toList()));
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

    /**
     * TODO 代码生成用别的方式实现
     *
     * @param vc
     * @param table
     * @param column
     * @return
     */
    protected Map<String, Object> mapColumnAttr(VelocityContext vc, Relation table, Column column) {
        return null;
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
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

}
