package com.dr;

import com.dr.framework.core.orm.jdbc.Column;
import com.dr.framework.core.orm.jdbc.TrueOrFalse;

import java.sql.*;

public class JdbcTest {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.200.239:1521:orcl",
                "test1",
                "test1");
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getColumns(null, null, "ACT_RU_EXECUTION", null);
        try {
            while (resultSet != null && resultSet.next()) {
                String schema = resultSet.getString("TABLE_SCHEM");
                String cat = resultSet.getString("TABLE_CAT");

                Column column = new Column(
                        resultSet.getString("TABLE_NAME")
                        , resultSet.getString("COLUMN_NAME")
                        , resultSet.getString("COLUMN_NAME"));
                column.setType(resultSet.getInt("DATA_TYPE"));
                column.setTypeName(resultSet.getString("TYPE_NAME"));
                column.setRemark(resultSet.getString("REMARKS"));
                column.setSize(resultSet.getInt("COLUMN_SIZE"));
                column.setDecimalDigits(resultSet.getInt("DECIMAL_DIGITS"));
                column.setNullAble(TrueOrFalse.from(resultSet.getString("IS_NULLABLE")));
                column.setPosition(resultSet.getInt("ORDINAL_POSITION"));
                //oracle数据库这两个属性都得专门处理
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        connection.close();
    }
}
