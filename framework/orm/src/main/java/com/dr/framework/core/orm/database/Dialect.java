package com.dr.framework.core.orm.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * 数据库方言处理类
 *
 * @author dr
 */
public abstract class Dialect {
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    Logger logger = LoggerFactory.getLogger(Dialect.class);

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
}
