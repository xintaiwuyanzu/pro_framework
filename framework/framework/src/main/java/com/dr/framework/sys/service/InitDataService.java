package com.dr.framework.sys.service;

import com.dr.framework.common.service.CommonService;
import com.dr.framework.sys.entity.UserLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用来初始化数据库
 */
@Service
public class InitDataService implements InitializingBean {
    Logger logger = LoggerFactory.getLogger(InitializingBean.class);
    @Autowired
    List<DataInit> dataInitList;

    @Override
    public void afterPropertiesSet() {
        if (dataInitList != null) {
            for (DataInit dataInit : dataInitList) {
                try {
                    dataInit.initData();
                } catch (Exception e) {
                    String name = dataInit.name();
                    if (StringUtils.isEmpty(name)) {
                        name = dataInit.getClass().getSimpleName();
                    }
                    logger.error("执行{}数据初始化错误", name, e);
                }
            }
        }
    }

    public interface DataInit {
        public default String name() {
            return "";
        }

        /**
         * 初始化数据
         */
        public void initData();
    }

}
