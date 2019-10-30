package com.dr.process.service;

import java.util.List;

/**
 * 流程相关的接口
 *
 * @author dr
 */
public interface ProcessService {
    /**
     * 启动流程
     */
    void start();

    /**
     * 往前走
     */
    void step();

    /**
     * 回退
     */
    void back();

    /**
     * 当前待办
     *
     * @return
     */
    List getTask();
}
