package com.dr.framework.common;

import com.dr.framework.common.entity.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 定义全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultEntity handleEmployeeNotFoundException(HttpServletRequest request, Device device, Exception ex) {
        logger.warn("服务器错误", ex);
        if (device != null && device.isMobile()) {
            return ResultEntity.error("服务器链接超时。");
        }
        return ResultEntity.error("服务器错误：" + ex.getMessage());
    }
}
