package com.dr.framework.common.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 缓存管理器
 *
 * @author dr
 */
public class CacheManagerWrapper implements CacheManager {
    private CacheManager delegate;
    private final ConcurrentMap<String, StatsCacheWrapper> cacheMap = new ConcurrentHashMap<>(16);

    public CacheManagerWrapper(CacheManager delegate) {
        Assert.notNull(delegate, "CacheManager不能为空");
        this.delegate = delegate;
    }

    public List<CacheStats> listCacheStats(String cacheNameParam) {
        Stream<String> stringStream = getCacheNames().stream();
        if (!StringUtils.isEmpty(cacheNameParam)) {
            stringStream = stringStream.filter(c -> c.startsWith(cacheNameParam));
        }
        return stringStream.map(c -> getCache(c).getStats()).collect(Collectors.toList());
    }

    @Override
    public StatsCacheWrapper getCache(String name) {
        return cacheMap.computeIfAbsent(name, n -> {
            Cache cache = delegate.getCache(n);
            if (cache instanceof StatsCacheWrapper) {
                return (StatsCacheWrapper) cache;
            } else {
                return new StatsCacheWrapper(cache);
            }
        });
    }

    @Override
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
