package com.dr.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "TestEntity3", module = "test")
public class TestEntity3 {
    @Column
    @Id
    int bb;
    @Column
    String aa;
}
