package com.dr.entity.ilas;

import com.dr.framework.common.entity.IdEntity;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "SERIES", comment = "期刊书目库", module = "ilasmodul")
public class Series extends MarcTable implements IdEntity {

    @Override
    public String getId() {
        return String.valueOf(getMRC0());
    }

    @Override
    public void setId(String id) {

    }
}
