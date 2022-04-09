package com.dr.framework.core.orm.database.dialect;

import org.springframework.util.StringUtils;

/**
 * mysql数据库8及以上
 * <p>
 * TODO 详细的还没仔细看
 *
 * @author dr
 */
public class Mysql8Dialect extends Mysql57Dialect {

    @Override
    protected String getTableRemark(String remark) {
        StringBuilder sb = new StringBuilder(" default charset=utf8mb4 ");
        if (StringUtils.hasText(remark)) {
            sb.append(" comment='")
                    .append(remark)
                    .append("' ");
        }
        return sb.toString();
    }
}
