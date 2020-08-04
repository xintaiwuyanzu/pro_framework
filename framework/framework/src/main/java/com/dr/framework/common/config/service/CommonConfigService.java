package com.dr.framework.common.config.service;

import com.dr.framework.common.config.entity.CommonConfig;
import com.dr.framework.common.config.entity.CommonConfigInfo;
import com.dr.framework.common.entity.StatusEntity;
import com.dr.framework.common.service.BaseService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.util.Constants;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author dr
 */
public interface CommonConfigService extends BaseService<CommonConfig> {
    String TableName = Constants.COMMON_TABLE_PREFIX + "CONFIG";

    /**
     * 查询一条数据
     *
     * @param refId
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> T findOneByRefId(String refId, Class<T> clazz) {
        List<T> list = findListByRefId(refId, clazz);
        Assert.isTrue(list.size() < 2, "查询到多条数据");
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询多条数据
     *
     * @param id
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> List<T> findListByRefId(String id, Class<T> clazz) {
        return findListByQuery(
                SqlQuery.from(CommonConfig.class)
                        .equal(CommonConfigInfo.REFID, id)
                        .equal(CommonConfigInfo.STATUS, StatusEntity.STATUS_ENABLE_STR)
                , clazz
        );
    }

    /**
     * 根据条件查询数据，并转换成指定类型的结果
     *
     * @param query
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> T findOneByQuery(SqlQuery query, Class<T> clazz) {
        List<T> list = findListByQuery(query, clazz);
        Assert.isTrue(list.size() < 2, "查询到多条数据");
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 根据条件查询数据，并转换成指定类型的结果
     *
     * @param query
     * @param clazz
     * @param <T>
     * @return
     */
    <T> List<T> findListByQuery(SqlQuery query, Class<T> clazz);

    /**
     * 明确转换会返回一条config对象
     *
     * @param configBean
     * @param refId
     * @param codes
     * @param <T>
     * @return
     */
    default <T> CommonConfig saveConfig(T configBean, String refId, String... codes) {
        List<CommonConfig> commonConfigs = saveConfigs(configBean, refId);
        Assert.isTrue(commonConfigs.size() < 2, "检测到多条数据！");
        return commonConfigs.isEmpty() ? null : commonConfigs.get(0);
    }

    /**
     * 传入带有注解的配置类，转换并且保存数据
     *
     * @param configBean
     * @param refId      业务外键
     * @param codes      需要更新的指定的code
     * @param <T>
     * @return 返回转换后并且保存到数据库的多条配置数据
     */
    <T> List<CommonConfig> saveConfigs(T configBean, String refId, String... codes);

    /**
     * 修改指定数据的状态
     *
     * @param id
     * @param status
     */
    void changeStatus(String id, String status);
}
