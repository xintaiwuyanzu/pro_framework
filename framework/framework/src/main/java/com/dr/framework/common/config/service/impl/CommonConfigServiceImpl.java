package com.dr.framework.common.config.service.impl;

import com.dr.framework.common.config.entity.CommonConfig;
import com.dr.framework.common.config.service.CommonConfigBeanFactory;
import com.dr.framework.common.config.service.CommonConfigService;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.common.service.DefaultBaseService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.util.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 配置类操作接口
 *
 * @author dr
 */
@Service
public class CommonConfigServiceImpl extends DefaultBaseService<CommonConfig> implements CommonConfigService, InitializingBean {
    @Autowired
    CommonConfigBeanFactory commonConfigBeanFactory;

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public <T> List<T> findListByQuery(SqlQuery query, Class<T> clazz) {
        List<CommonConfig> commonConfigs = commonMapper.selectByQuery(query);
        return commonConfigBeanFactory.newConfigBean(commonConfigs, clazz);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> List<CommonConfig> saveConfigs(T configBean, String refId, String... codes) {
        List<CommonConfig> commonConfigs = commonConfigBeanFactory.newConfigs(configBean);
        List<String> codeList = Arrays.asList(codes);

        for (CommonConfig commonConfig : commonConfigs) {
            if (!codeList.isEmpty() && !codeList.contains(commonConfig.getCode())) {
                continue;
            }
            if (StringUtils.isEmpty(commonConfig.getRefId())) {
                Assert.isTrue(!StringUtils.isEmpty(refId), "业务外键不能为空！");
                commonConfig.setRefId(refId);
                CommonService service = getCommonService();
                if (service.exists(commonConfig)) {
                    service.update(commonConfig);
                } else {
                    service.insert(commonConfig);
                }
            }
        }
        return commonConfigs;
    }

    @Override
    public void afterPropertiesSet() {
        if (defaultDataBaseService.tableExist(TableName, Constants.COMMON_MODULE_NAME)) {
            entityRelation = defaultDataBaseService.getTableInfo(getEntityClass());
        }
    }
}
