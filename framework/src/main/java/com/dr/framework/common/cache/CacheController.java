package com.dr.framework.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

/**
 * 用来管理cache的web接口
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/cache")
public class CacheController {
    @Autowired(required = false)
    CacheManager cacheManager;

    /**
     * 列出缓存
     *
     * @return
     */
    @GetMapping("/list")
    public List<CacheStats> cacheStats() {
        Assert.isTrue(cacheManager instanceof CacheManagerWrapper, "使用了自定义的cacheManager，不能调用此方法");
        return ((CacheManagerWrapper) cacheManager).listCacheStats(null);
    }

    /**
     * 清空缓存
     *
     * @param cacheName 缓存名称，带有指定前缀的都会被清空 ，如果为空则删除所有缓存
     */
    @GetMapping("/clear")
    public void clearCache(String cacheName) {
        Stream<String> cac = cacheManager.getCacheNames().stream();
        if (StringUtils.hasText(cacheName)) {
            cac = cac.filter(c -> c.startsWith(cacheName));
        }
        cac.map(c -> cacheManager.getCache(c))
                .forEach(Cache::clear);
    }

}
