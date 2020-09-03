package com.dr;

import com.dr.framework.common.cache.CacheController;
import com.dr.framework.common.cache.CacheStats;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CacheTestCase {
    @Autowired
    CacheManager cacheManager;
    @Autowired
    CacheTest cacheTest;
    @Autowired
    CacheController cacheController;

    @Test
    public void test() {
        cacheTest.aaa();
        cacheTest.aaa();
        cacheTest.aaa();
        List<CacheStats> stats = cacheController.cacheStats();
    }

}
