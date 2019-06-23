package com.dr.entity.ilas;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "CATALOG", comment = "采编书目信息", module = "ilasmodul")
public class CataLog extends MarcTable implements IdEntity {
    @Override
    public String getId() {
        return String.valueOf(getMRC0());
    }

    @Override
    public void setId(String id) {

    }
}
