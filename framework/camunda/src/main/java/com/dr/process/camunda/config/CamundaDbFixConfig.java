package com.dr.process.camunda.config;

import com.dr.framework.core.orm.database.DataBaseMetaData;
import com.dr.framework.core.orm.database.dialect.OracleDialect;
import com.dr.process.camunda.manager.CustomHistoricProcessInstanceManager;
import com.dr.process.camunda.manager.CustomTaskManager;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.db.sql.DbSqlSessionFactory;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricProcessInstanceManager;
import org.camunda.bpm.engine.impl.persistence.entity.TaskManager;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.CamundaDatasourceConfiguration;
import org.camunda.bpm.spring.boot.starter.property.CamundaBpmProperties;
import org.camunda.bpm.spring.boot.starter.property.DatabaseProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来解决数据库的问题
 *
 * @author dr
 */
public class CamundaDbFixConfig implements CamundaDatasourceConfiguration {
    private PlatformTransactionManager transactionManager;
    private DataSource dataSource;
    private CamundaBpmProperties properties;
    private DataBaseMetaData dataBaseMetaData;

    public CamundaDbFixConfig(PlatformTransactionManager transactionManager,
                              DataSource dataSource,
                              CamundaBpmProperties properties,
                              DataBaseMetaData dataBaseMetaData) {
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.properties = properties;
        this.dataBaseMetaData = dataBaseMetaData;
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        if (processEngineConfiguration instanceof SpringProcessEngineConfiguration) {
            SpringProcessEngineConfiguration configuration = (SpringProcessEngineConfiguration) processEngineConfiguration;
            configuration.setTransactionManager(transactionManager);
            configuration.setDataSource(dataSource);
            DatabaseProperty database = properties.getDatabase();
            configuration.setDatabaseType(database.getType());
            configuration.setDatabaseSchemaUpdate(database.getSchemaUpdate());
            if (!StringUtils.isEmpty(database.getTablePrefix())) {
                configuration.setDatabaseTablePrefix(database.getTablePrefix());
            }
            if (!StringUtils.isEmpty(database.getSchemaName())) {
            }
            configuration.setDatabaseSchema(dataBaseMetaData.getSchema());
            configuration.setDatabaseTablePrefix(dataBaseMetaData.getSchema() + ".");
            configuration.setJdbcBatchProcessing(database.isJdbcBatchProcessing());
        }
        if (dataBaseMetaData.getDialect() instanceof OracleDialect) {
            processEngineConfiguration.setJdbcBatchProcessing(false);
        }
        //这里的taskManager 改成自己的
        List<SessionFactory> sessionFactories = processEngineConfiguration.getCustomSessionFactories();
        if (sessionFactories == null) {
            sessionFactories = new ArrayList<>();
            processEngineConfiguration.setCustomSessionFactories(sessionFactories);
        }
        sessionFactories.add(new SessionFactory() {
            @Override
            public Class<?> getSessionType() {
                return TaskManager.class;
            }

            @Override
            public Session openSession() {
                return new CustomTaskManager();
            }
        });
        sessionFactories.add(new SessionFactory() {
            @Override
            public Class<?> getSessionType() {
                return HistoricProcessInstanceManager.class;
            }

            @Override
            public Session openSession() {
                return new CustomHistoricProcessInstanceManager();
            }
        });
    }


    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        DbSqlSessionFactory dbSqlSessionFactory = processEngineConfiguration.getDbSqlSessionFactory();
        // dbSqlSessionFactory.setDatabaseSchema(dataBaseMetaData.getSchema());
        Configuration configuration = dbSqlSessionFactory.getSqlSessionFactory()
                .getConfiguration();
        loadXmlMapper(configuration, "Task");
        loadXmlMapper(configuration, "HistoricProcessInstance");
    }

    private void loadXmlMapper(Configuration configuration, String xml) {
        xml = "com/dr/process/camunda/mapping/entity/" + xml + ".xml";
        try {
            new XMLMapperBuilder(
                    new ClassPathResource(xml).getInputStream()
                    , configuration
                    , xml
                    , configuration.getSqlFragments()
            ).parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {

    }
}
