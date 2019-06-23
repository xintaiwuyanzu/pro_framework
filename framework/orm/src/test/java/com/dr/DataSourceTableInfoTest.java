package com.dr;

import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.MultiDataSourceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DataSourceTableInfoTest {
    static Logger logger = LoggerFactory.getLogger(DataSourceTableInfoTest.class);

    public static void main(String[] args) {
        MultiDataSourceProperties multiDataSourceProperties = new MultiDataSourceProperties();
        multiDataSourceProperties.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&nullNamePatternMatchesAll=true&useInformationSchema=true&allowPublicKeyRetrieval=true");
        multiDataSourceProperties.setUsername("root");
        multiDataSourceProperties.setPassword("1234");
       /* multiDataSourceProperties.setUrl("jdbc:oracle:thin:@192.168.200.239:1521:orcl");
        multiDataSourceProperties.setUsername("gentleIlas");
        multiDataSourceProperties.setPassword("gentleIlas");*/

       /* multiDataSourceProperties.setUrl("jdbc:sqlserver://192.168.200.239:1433;database=jygtsglibdb");
        multiDataSourceProperties.setUsername("sa");
        multiDataSourceProperties.setPassword("123456");*/
        multiDataSourceProperties.getDialect();
        multiDataSourceProperties.getDataBaseMetaData().getTables(true)
                .stream()
                .map(Relation::getColumns)
                .flatMap(Collection::stream)
                .filter(c -> JdbcUtils.isNumeric(c.getType()))
                .collect(
                        Collectors.toMap(
                                c -> String.format("name:%s,type:%s", c.getTypeName(), c.getType())
                                , Arrays::asList
                                , (s, t) ->
                                        Arrays.asList(s, t)
                                                .stream()
                                                .flatMap(List::stream)
                                                .collect(Collectors.toList())
                        )
                )
                .forEach((s, columns) -> {
                    logger.info("{},count:{}", s, columns.size());
                    columns.forEach(c -> {
                        logger.info(c.toString());
                    });
                });
    }
}
