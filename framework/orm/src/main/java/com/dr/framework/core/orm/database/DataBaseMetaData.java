package com.dr.framework.core.orm.database;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 数据库链接信息,缓存数据库链接信息
 *
 * @author dr
 * @see java.sql.DatabaseMetaData
 */
public class DataBaseMetaData {
    static final int NO_VERSION = Integer.MIN_VALUE;
    /**
     * 数据库产品名称
     */
    private String databaseProductName;
    /**
     * 数据库主版本号
     */
    private int databaseMajorVersion;
    /**
     * 数据库次版本号
     */
    private int databaseMinorVersion;
    /**
     * 数据库链接驱动名称
     */
    private String driverName;
    /**
     * 数据库驱动主版本号
     */
    private int driverMajorVersion;
    /**
     * 数据库次版本号
     */
    private int driverMinorVersion;

    public static DataBaseMetaData from(DatabaseMetaData source) throws SQLException {
        DataBaseMetaData target = new DataBaseMetaData();
        target.setDatabaseProductName(source.getDatabaseProductName());
        target.setDriverName(source.getDriverName());
        target.setDatabaseMajorVersion(versionOfNoVersion(source.getDatabaseMajorVersion()));
        target.setDatabaseMinorVersion(versionOfNoVersion(source.getDatabaseMinorVersion()));
        target.setDriverMajorVersion(versionOfNoVersion(source.getDriverMajorVersion()));
        target.setDriverMinorVersion(versionOfNoVersion(source.getDriverMinorVersion()));
        return target;
    }

    private static int versionOfNoVersion(int version) {
        return version < 0 ? NO_VERSION : version;
    }

    public DataBaseMetaData() {
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public void setDatabaseProductName(String databaseProductName) {
        this.databaseProductName = databaseProductName;
    }

    public int getDatabaseMajorVersion() {
        return databaseMajorVersion;
    }

    public void setDatabaseMajorVersion(int databaseMajorVersion) {
        this.databaseMajorVersion = databaseMajorVersion;
    }

    public int getDatabaseMinorVersion() {
        return databaseMinorVersion;
    }

    public void setDatabaseMinorVersion(int databaseMinorVersion) {
        this.databaseMinorVersion = databaseMinorVersion;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getDriverMajorVersion() {
        return driverMajorVersion;
    }

    public void setDriverMajorVersion(int driverMajorVersion) {
        this.driverMajorVersion = driverMajorVersion;
    }

    public int getDriverMinorVersion() {
        return driverMinorVersion;
    }

    public void setDriverMinorVersion(int driverMinorVersion) {
        this.driverMinorVersion = driverMinorVersion;
    }
}
