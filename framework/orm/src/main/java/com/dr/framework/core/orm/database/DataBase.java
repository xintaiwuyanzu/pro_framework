package com.dr.framework.core.orm.database;

import com.dr.framework.core.orm.database.dialect.MysqlDialect;
import com.dr.framework.core.orm.database.dialect.OracleDialect;
import com.dr.framework.core.orm.database.dialect.SQLServer2008Dialect;
import com.dr.framework.core.orm.database.dialect.SQLServer2012Dialect;

/**
 * 数据库类型枚举
 *
 * @author dr
 */
public enum DataBase {
    /**
     * mysql数据库
     */
    MY_SQL {
        @Override
        public Class<? extends Dialect> lastestDialect() {
            return MysqlDialect.class;
        }

        @Override
        public Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData) {
            if (match(dataBaseMetaData.getDatabaseProductName())) {
                switch (dataBaseMetaData.getDatabaseMajorVersion()) {


                    default:
                        return lastestDialect();
                }
            }
            return null;
        }

        @Override
        public boolean match(String productionName) {
            return "MySQL".equalsIgnoreCase(productionName);
        }
    },
    /**
     * oracle数据库
     */
    ORACLE {
        @Override
        public Class<? extends Dialect> lastestDialect() {
            return OracleDialect.class;
        }

        @Override
        public Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData) {
            return lastestDialect();
        }

        @Override
        public boolean match(String productionName) {
            return "Oracle".equalsIgnoreCase(productionName);
        }
    },
    /**
     * sqlserver数据库
     */
    SQLSERVER {
        @Override
        public Class<? extends Dialect> lastestDialect() {
            return SQLServer2012Dialect.class;
        }

        @Override
        public Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData) {
            if (match(dataBaseMetaData.getDatabaseProductName())) {
                switch (dataBaseMetaData.getDatabaseMajorVersion()) {
                    default:
                        if (dataBaseMetaData.getDatabaseMajorVersion() <= 10) {
                            return SQLServer2008Dialect.class;
                        }
                        return lastestDialect();
                }
            }
            return null;
        }

        @Override
        public boolean match(String productionName) {
            return "Microsoft SQL Server".equalsIgnoreCase(productionName);
        }
    };

    /**
     * 获取最新的方言处理类
     *
     * @return
     */
    public abstract Class<? extends Dialect> lastestDialect();

    /**
     * 获取具体版本的方言处理类
     *
     * @param dataBaseMetaData
     * @return
     */
    public abstract Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData);

    /**
     * 名称是否相同
     *
     * @param productionName
     * @return
     */
    public abstract boolean match(String productionName);

}
