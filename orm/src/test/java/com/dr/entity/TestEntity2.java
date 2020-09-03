package com.dr.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.annotations.Column;

public class TestEntity2<A, B> extends BaseStatusEntity<A> {
    @Column
    private B bb;

    public B getBb() {
        return bb;
    }

    public void setBb(B bb) {
        this.bb = bb;
    }
}
