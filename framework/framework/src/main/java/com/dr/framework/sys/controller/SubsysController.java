package com.dr.framework.sys.controller;

import com.dr.framework.common.controller.BaseController;
import com.dr.framework.sys.entity.SubSystem;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 子系统管理
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/subsys")
public class SubsysController extends BaseController<SubSystem> {
}
