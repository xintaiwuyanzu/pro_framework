package com.dr.framework.common.cache;

import java.util.concurrent.atomic.LongAdder;

/**
 * 用来显示缓存状态
 *
 * @author dr
 */
public class CacheStats {
    /**
     * 名称
     */
    private String name;
    /**
     * 请求总数
     */
    private LongAdder requestCount = new LongAdder();
    /**
     * 丢失请求
     */
    private LongAdder missCount = new LongAdder();
    /**
     * 总计请求时间
     */
    private LongAdder totalLoadTime = new LongAdder();

    public CacheStats(String name) {
        this.name = name;
    }

    public void clear() {
        requestCount.reset();
        missCount.reset();
        totalLoadTime.reset();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LongAdder getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(LongAdder requestCount) {
        this.requestCount = requestCount;
    }

    public LongAdder getMissCount() {
        return missCount;
    }

    public void setMissCount(LongAdder missCount) {
        this.missCount = missCount;
    }

    public LongAdder getTotalLoadTime() {
        return totalLoadTime;
    }

    public void setTotalLoadTime(LongAdder totalLoadTime) {
        this.totalLoadTime = totalLoadTime;
    }

    public void addCacheRequestCount(int i) {
        requestCount.add(i);
    }
}
