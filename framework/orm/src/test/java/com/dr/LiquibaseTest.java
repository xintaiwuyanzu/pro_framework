package com.dr;

import com.dr.framework.core.orm.support.liquibase.GenerateChangeLogFromCodeCommand;
import com.dr.framework.core.orm.support.liquibase.SourceCodeDatabaseSnapshot;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.diff.compare.CompareControl;
import liquibase.resource.ClassLoaderResourceAccessor;

public class LiquibaseTest {
    static String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&nullNamePatternMatchesAll=true&useInformationSchema=true";

    public static void main(String[] args) throws Exception {
        Database database = DatabaseFactory.getInstance().openDatabase(url, "root", "1234", null, new ClassLoaderResourceAccessor());
        SourceCodeDatabaseSnapshot sourceCodeDatabaseSnapshot = new SourceCodeDatabaseSnapshot(database);
        GenerateChangeLogFromCodeCommand changeLogFromCodeCommand = new GenerateChangeLogFromCodeCommand();
        changeLogFromCodeCommand.setCompareControl(new CompareControl());
        changeLogFromCodeCommand.setSourceCodeDatabaseSnapshot(sourceCodeDatabaseSnapshot);
        changeLogFromCodeCommand.genToFile("./orm/target/11.xml");
    }
}
