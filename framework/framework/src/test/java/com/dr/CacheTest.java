package com.dr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheTest {
    Logger logger = LoggerFactory.getLogger(CacheTest.class);

    @Cacheable(value = "aaa")
    public String aaa() {
        logger.warn("method is invoke");
        return "aaaa";
    }

}
