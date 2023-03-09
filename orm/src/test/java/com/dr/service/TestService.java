package com.dr.service;

import com.dr.entity.TestEntity;
import com.dr.entity.TestEntity1;
import com.dr.entity.TestEntityInfo;
import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@Service
public class TestService {
    @Autowired(required = false)
    CommonMapper commonMapper;

    @Transactional(rollbackFor = Exception.class)
    public void aaa() {
        commonMapper.count(TestEntity.class);
        commonMapper.count(TestEntity1.class);
        TestEntity testEntity = new TestEntity();
        testEntity.setId(UUID.randomUUID().toString());
        testEntity.setClobCol("aaaaa");
        commonMapper.insert(testEntity);
        commonMapper.selectById(TestEntity.class, "3e9b3aa8-d5dd-4081-ae13-a2ac1c3022f4");
        commonMapper.selectByQuery(SqlQuery.from(TestEntity.class).equal(TestEntityInfo.NAME, TestEntityInfo.NAME1));

    }

}
