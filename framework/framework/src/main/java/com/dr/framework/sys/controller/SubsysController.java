package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.sys.entity.SubSystem;
import com.dr.framework.sys.service.InitDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 子系统管理
 */
@RestController
@RequestMapping("/api/subsys")
public class SubsysController extends BaseController<SubSystem> implements InitDataService.DataInit {
    @Override
    public void initData() {
        SubSystem subSystem = new SubSystem();
        subSystem.setId(SubSystem.DEFAULT_SYSTEM_ID);
        subSystem.setSysName("默认系统");
        if (!commonService.exists(subSystem)) {
            commonService.insert(subSystem);
        }
    }
}
