package com.dr.framework.core.orm.database;

import com.dr.framework.core.orm.database.dialect.*;

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
            return Mysql8Dialect.class;
        }

        @Override
        public Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData) {
            if (match(dataBaseMetaData.getDatabaseProductName())) {
                if (dataBaseMetaData.getDatabaseMajorVersion() < 5) {
                    return MysqlDialect.class;
                } else {
                    if (dataBaseMetaData.getDatabaseMajorVersion() >= 8) {
                        return Mysql8Dialect.class;
                    }
                    if (dataBaseMetaData.getDatabaseMajorVersion() >= 5) {
                        return Mysql57Dialect.class;
                    }
                }
                return lastestDialect();
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
            return Oracle9iDialect.class;
        }

        @Override
        public Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData) {
            if (match(dataBaseMetaData.getDatabaseProductName())) {
                if (dataBaseMetaData.getDatabaseMajorVersion() <= 8) {
                    return Oracle8iDialect.class;
                } else {
                    return Oracle9iDialect.class;
                }
            }
            return null;
        }

        @Override
        public boolean match(String productionName) {
            return "Oracle".equalsIgnoreCase(productionName);
        }
    },
    /**
     * oracle数据库
     */
    DM {
        @Override
        public Class<? extends Dialect> lastestDialect() {
            return Dm8Dialect.class;
        }

        @Override
        public Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData) {
            return Dm8Dialect.class;
        }

        @Override
        public boolean match(String productionName) {
            return "DM DBMS".equalsIgnoreCase(productionName);
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
                    case 9:
                        return SQLServer2005Dialect.class;
                    case 10:
                        return SQLServer2008Dialect.class;
                    case 11:
                    case 12:
                    case 13:
                        return SQLServer2012Dialect.class;
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
    },
    H2 {
        @Override
        public Class<? extends Dialect> lastestDialect() {
            return H2Dialect.class;
        }

        @Override
        public Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData) {
            return lastestDialect();
        }

        @Override
        public boolean match(String productionName) {
            return "H2".equals(productionName);
        }
    },
    POSTGRESQL {
        @Override
        public Class<? extends Dialect> lastestDialect() {
            return PostgreSQLDialect.class;
        }

        @Override
        public Class<? extends Dialect> getDialectClass(DataBaseMetaData dataBaseMetaData) {
            return lastestDialect();
        }

        @Override
        public boolean match(String productionName) {
            return "PostgreSQL".equals(productionName);
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
