package com.dr.framework.core.orm.support.liquibase;

import liquibase.command.CommandResult;
import liquibase.command.core.DiffCommand;
import liquibase.diff.DiffResult;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * 对比代码中的表结构和数据库中的表结构
 */
public class GenerateChangeLogFromCodeCommand extends DiffCommand {
    Logger logger = LoggerFactory.getLogger(GenerateChangeLogFromCodeCommand.class);
    SourceCodeDatabaseSnapshot sourceCodeDatabaseSnapshot;

    @Override
    protected DatabaseSnapshot createReferenceSnapshot() {
        return sourceCodeDatabaseSnapshot;
    }

    @Override
    protected CommandResult run() throws Exception {
        DiffResult diffResult = createDiffResult();
        DiffToChangeLog changeLogWriter = new DiffToChangeLogWithoutDrop(diffResult, new DiffOutputControl());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, "UTF-8")) {
            changeLogWriter.print(ps);
        }
        return new CommandResult(new String(baos.toByteArray(), StandardCharsets.UTF_8));
    }

    public void genToFile(String file) throws DatabaseException, InvalidExampleException, IOException, ParserConfigurationException {
        DiffResult diffResult = createDiffResult();
        DiffToChangeLog changeLogWriter = new DiffToChangeLogWithoutDrop(diffResult, new DiffOutputControl());
        logger.debug("生成diff文件：【{}】", file);
        changeLogWriter.print(file);
    }

    @Override
    public String getName() {
        return "GenerateChangeLogFromCode";
    }

    public void setSourceCodeDatabaseSnapshot(SourceCodeDatabaseSnapshot sourceCodeDatabaseSnapshot) {
        this.sourceCodeDatabaseSnapshot = sourceCodeDatabaseSnapshot;
        setTargetDatabase(sourceCodeDatabaseSnapshot.getTargetDatabase());
        setReferenceDatabase(sourceCodeDatabaseSnapshot.getSourceCodeDatabase());
    }
}
