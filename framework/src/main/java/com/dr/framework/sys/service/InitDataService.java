package com.dr.framework.sys.service;

import com.dr.framework.autoconfig.CommonConfig;
import com.dr.framework.common.service.DataBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * 用来初始化数据库
 *
 * @author dr
 */
@Service
public class InitDataService implements InitializingBean {
    Logger logger = LoggerFactory.getLogger(InitializingBean.class);
    @Autowired
    List<DataInit> dataInitList;
    @Autowired
    DataBaseService dataBaseService;
    @Autowired
    CommonConfig commonConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void afterPropertiesSet() {
        if (dataInitList != null && commonConfig.isAutoInitData()) {
            dataInitList.stream()
                    .sorted(Comparator.comparingInt(DataInit::order))
                    .forEach(dataInit -> {
                        String name = dataInit.name();
                        if (StringUtils.isEmpty(name)) {
                            name = dataInit.getClass().getSimpleName();
                        }
                        try {
                            logger.info("正在执行数据初始化:{}", name);
                            long start = System.currentTimeMillis();
                            dataInit.initData(dataBaseService);
                            logger.info("数据初始化完成:{}秒，{}", (System.currentTimeMillis() - start) / 1000, name);
                        } catch (Exception e) {
                            logger.error("执行{}数据初始化错误", name, e);
                        }
                    });
        }
    }

    public interface DataInit {
        /**
         * 获取初始化模块的名称
         *
         * @return
         */
        default String name() {
            return "";
        }

        /**
         * 初始化数据
         *
         * @param dataBaseService
         */
        void initData(DataBaseService dataBaseService);

        /**
         * 数据初始化顺序
         *
         * @return
         */
        default int order() {
            return 0;
        }
    }

}
