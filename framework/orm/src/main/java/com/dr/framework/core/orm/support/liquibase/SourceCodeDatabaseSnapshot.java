package com.dr.framework.core.orm.support.liquibase;

import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.SnapshotIdService;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Catalog;
import liquibase.structure.core.Schema;
import liquibase.structure.core.Table;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * 从源码当中读取数据库配置信息
 *
 * @author dr
 */
public class SourceCodeDatabaseSnapshot extends DatabaseSnapshot {
    private SourceCodeDatabaseObjectCollection allFound;

    private Database proxySourceCodeDatabase;
    private Database proxyTargetDatabase;

    public SourceCodeDatabaseSnapshot(Database targetDatabase, String... pkgs) throws DatabaseException, InvalidExampleException {
        this(targetDatabase, new SourceCodeDatabaseObjectCollection(targetDatabase, pkgs));
    }

    public SourceCodeDatabaseSnapshot(Database targetDatabase, SourceCodeDatabaseObjectCollection allFound) throws DatabaseException, InvalidExampleException {
        super(null, targetDatabase);
        this.allFound = allFound;
        init(targetDatabase);
    }


    private void init(Database targetDatabase) {
        if (targetDatabase.supportsCatalogs()) {
            Catalog catalog = new Catalog(targetDatabase.getDefaultCatalogName());
            catalog.setSnapshotId(SnapshotIdService.getInstance().generateId());
            allFound.add(catalog);
        }
        if (targetDatabase.supportsSchemas()) {
            Schema schema = new Schema(targetDatabase.getDefaultCatalogName(), targetDatabase.getDefaultSchemaName());
            schema.setSnapshotId(SnapshotIdService.getInstance().generateId());
            allFound.add(schema);
        }
        this.proxySourceCodeDatabase = (Database) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Database.class}, (Object proxy, Method method, Object[] args) -> method.invoke(targetDatabase, args));
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetDatabase.getClass());
        enhancer.setCallback(new DataBaseMethodInterceptor(this));
        this.proxyTargetDatabase = (Database) enhancer.create();
        this.proxyTargetDatabase.setConnection(targetDatabase.getConnection());
    }

    public static class DataBaseMethodInterceptor implements MethodInterceptor {

        SourceCodeDatabaseSnapshot sourceCodeDatabaseSnapshot;

        public DataBaseMethodInterceptor(SourceCodeDatabaseSnapshot sourceCodeDatabaseSnapshot) {
            this.sourceCodeDatabaseSnapshot = sourceCodeDatabaseSnapshot;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            Object result = methodProxy.invokeSuper(proxy, objects);
            if ("isSystemObject".equalsIgnoreCase(method.getName())) {
                if (Boolean.TRUE.equals(result)) {
                    return true;
                }
                DatabaseObject databaseObject = (DatabaseObject) objects[0];
                if (databaseObject instanceof Table) {
                    for (Table table : sourceCodeDatabaseSnapshot.allFound.getTables()) {
                        if (table.getName().equalsIgnoreCase(databaseObject.getName())) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return result;
        }
    }

    @Override
    public <D extends DatabaseObject> D get(D example) {
        return allFound.get(example, getSchemaComparisons());
    }

    @Override
    public <D extends DatabaseObject> Set<D> get(Class<D> type) {
        return allFound.get(type);
    }

    public SourceCodeDatabaseObjectCollection getAllFoundCollection() {
        return allFound;
    }

    public Database getSourceCodeDatabase() {
        return proxySourceCodeDatabase;
    }

    public Database getTargetDatabase() {
        return proxyTargetDatabase;
    }

    public void setProxyTargetDatabase(Database proxyTargetDatabase) {
        this.proxyTargetDatabase = proxyTargetDatabase;
    }
}
