package com.dr.framework.core.orm.database.dialect;

import java.sql.Blob;
import java.sql.Types;

/**
 * 达梦8数据库方言
 *
 * @author dr
 */
public class Dm8Dialect extends Oracle9iDialect {
    public Dm8Dialect() {
        super();
        //blob
        registerClass(-1, Blob.class, Byte[].class, byte[].class)
                .registerType(Types.BLOB, "blob")
                .registerType(Types.BINARY, "BINARY($l)")
                .registerType(Types.VARBINARY, "VARBINARY($l)")
                .registerType(Types.LONGVARBINARY, "LONGVARBINARY");
    }
}
