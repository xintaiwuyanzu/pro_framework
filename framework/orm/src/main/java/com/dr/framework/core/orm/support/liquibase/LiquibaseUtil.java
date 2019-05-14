package com.dr.framework.core.orm.support.liquibase;

import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import liquibase.Liquibase;
import liquibase.command.CommandResult;
import liquibase.database.Database;
import liquibase.diff.compare.CompareControl;
import liquibase.exception.DatabaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.snapshot.InvalidExampleException;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据库操作工具
 */
public class LiquibaseUtil {

    static Logger logger = LoggerFactory.getLogger(LiquibaseUtil.class);
    private MybatisConfigurationBean mybatisConfigurationBean;

    public LiquibaseUtil(MybatisConfigurationBean mybatisConfigurationBean) {
        this.mybatisConfigurationBean = mybatisConfigurationBean;
    }

    public void validate() {
        Database database = null;
        try {
            database = mybatisConfigurationBean.createDataBase();
            GenerateChangeLogFromCodeCommand command = createCommand(database);
            CommandResult result = command.run();
            logger.info("数据库{}对比结果为：\n{}", mybatisConfigurationBean.getDatabaseId(), result.message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update() {
        Database database = null;
        try {
            database = mybatisConfigurationBean.createDataBase();
            GenerateChangeLogFromCodeCommand command = createCommand(database);
            File file = File.createTempFile("changelog-", ".xml");
            command.genToFile(file.getAbsolutePath());
            Liquibase liquibase = new Liquibase(file.getAbsolutePath(), new FileSystemResourceAccessor(file.getParent()), database);
            liquibase.update("");
        } catch (Exception e) {
            logger.warn("数据库" + mybatisConfigurationBean.getDatabaseId() + "表结构更新失败！", e);
        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (DatabaseException e) {
                    logger.warn("关闭数据库失败", e);
                }
            }
        }
    }

    private GenerateChangeLogFromCodeCommand createCommand(Database database) throws DatabaseException, InvalidExampleException {
        SourceCodeDatabaseSnapshot sourceCodeDatabaseSnapshot = new SourceCodeDatabaseSnapshot(database);
        for (Class entityClass : mybatisConfigurationBean.getEntityClass()) {
            sourceCodeDatabaseSnapshot.getAllFoundCollection().getTableInfo(entityClass);
        }
        GenerateChangeLogFromCodeCommand generateChangeLogFromCodeCommand = new GenerateChangeLogFromCodeCommand();
        generateChangeLogFromCodeCommand.setSourceCodeDatabaseSnapshot(sourceCodeDatabaseSnapshot);
        Set<Class<? extends DatabaseObject>> compareTypes = new HashSet<>();
        compareTypes.add(Table.class);
        compareTypes.add(View.class);
        compareTypes.add(Column.class);
        compareTypes.add(PrimaryKey.class);
        compareTypes.add(UniqueConstraint.class);
        CompareControl compareControl = new CompareControl(compareTypes);
        generateChangeLogFromCodeCommand.setCompareControl(compareControl);
        return generateChangeLogFromCodeCommand;
    }
}
