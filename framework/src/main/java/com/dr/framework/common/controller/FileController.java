package com.dr.framework.common.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件处理服务器地址
 */
@RestController
@RequestMapping("${common.api-path:/api}/files")
public class FileController {
    @RequestMapping("/images/{id}")
    public void images(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
    }
}
