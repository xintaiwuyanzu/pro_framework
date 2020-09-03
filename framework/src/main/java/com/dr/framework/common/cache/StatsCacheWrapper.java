package com.dr.framework.common.cache;

import org.springframework.cache.Cache;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;

/**
 * 带有统计信息的cache
 * TODO 还有时间和失败没统计
 *
 * @author dr
 */
public class StatsCacheWrapper implements Cache {
    private Cache delegate;
    private CacheStats stats;

    public StatsCacheWrapper(Cache delegate) {
        Assert.notNull(delegate, "要代理的cache不能为空！");
        this.delegate = delegate;
        stats = new CacheStats(delegate.getName());
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        getStats().addCacheRequestCount(1);
        return delegate.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        getStats().addCacheRequestCount(1);
        return delegate.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        getStats().addCacheRequestCount(1);
        return delegate.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        delegate.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        delegate.evict(key);
    }

    @Override
    public void clear() {
        stats.clear();
        delegate.clear();
    }

    public CacheStats getStats() {
        return stats;
    }
}
