package com.dr;

import com.dr.entity.TestEntity;
import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.page.Page;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.liquibase.enabled=false", classes = TestApplication.class)
public class MybatisTest {
    Logger logger = LoggerFactory.getLogger(MybatisTest.class);
    @Autowired
    CommonMapper testMapper;

    @Test
    public void testfanxing() {
        Assert.assertNotNull(testMapper);
        logger.info(ToStringBuilder.reflectionToString(testMapper));
    }

    @Test
    public void testQuery() {
        Page<TestEntity> testEntityPage = testMapper.selectPageByQuery(SqlQuery.from(TestEntity.class, true), 10, 30);
        logger.info(ToStringBuilder.reflectionToString(testEntityPage));
        logger.info(testEntityPage.getData().size() + "");
    }

    @Test
    public void afterPropertiesSet() {
        TestEntity testEntity = new TestEntity();
        testEntity.setBlobCol("BlobCo111111");
        testEntity.setClobCol("ClobCol");
        testEntity.setDateCol(2222222);
        testEntity.setDoubleCol(0.000);
        testEntity.setFloatCol(1.111f);
        testEntity.setIntCol(11111);
        testEntity.setName("name");
        testEntity.setName1("name1");
        testEntity.setId(UUID.randomUUID().toString());
        testMapper.insert(testEntity);
        TestEntity testEntity1 = testMapper.selectById(TestEntity.class, testEntity.getId());
        System.out.println(testEntity1.getBlobCol());
        testMapper.selectBatchIds(TestEntity.class, "7c06a865-170a-447b-a567-34776cb4086c", "8a3e6a21-7626-4991-b828-514121ac0250", "bafd0e72-e884-4295-a493-40bed6d8611e", "ed0b1580-33b0-42cd-8989-2a601c8307d7");

        TestEntity testEntity2 = new TestEntity();
        testEntity2.setId("3fa97a08-0511-40e1-b301-b23b056ebd02");
        testEntity2.setName1("tattatattatatattatat");
        testEntity2.setClobCol("");
        testMapper.updateById(testEntity2);
    }
}
