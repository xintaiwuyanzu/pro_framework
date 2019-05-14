package com.dr.framework.common.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 文件处理服务器地址
 */
@Controller
@RequestMapping(FileController.FILE_URL)
public class FileController {
    public static final String FILE_URL = "/api/files";

    @RequestMapping("/images/{id}")
    public void images(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws IOException {
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        //FileUtils.copyFile(new File("C:\\Users\\dr\\Desktop\\bd3eb13533fa828bafc235bcf71f4134960a5a8f.jpg"), response.getOutputStream());
    }
}
