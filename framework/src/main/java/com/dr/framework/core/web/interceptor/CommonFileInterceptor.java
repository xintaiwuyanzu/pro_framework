package com.dr.framework.core.web.interceptor;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.sys.entity.SysDict;
import com.dr.framework.sys.entity.SysDictInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 用来拦截文件上传
 * <p>
 * 配置信息在{@link  SysDict}中配置
 * 根据  “upload.+访问路径” 匹配{@link SysDict#getKey()}
 * "upload.+访问路径+.accept"为该访问路径白名单
 * "upload.+访问路径+.blacklist"为该访问路径黑名单
 * {@link  SysDict#getValue()}为媒体类型匹配
 * {@link  SysDict#getDescription()}为文件后缀匹配
 *
 * @author dr
 */
@Component
public class CommonFileInterceptor implements HandlerInterceptor {
    public static final int ORDER = 2;
    @Lazy
    @Autowired
    CommonMapper commonMapper;
    @Lazy
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request instanceof MultipartHttpServletRequest) {
            //只拦截文件上传的请求
            String url = request.getRequestURI();
            String dictKey = "upload." + url;
            List<SysDict> dicts = commonMapper.selectByQuery(SqlQuery.from(SysDict.class).startingWith(SysDictInfo.KEYINFO, dictKey).equal(SysDictInfo.STATUS, 1));
            if (dicts != null) {
                return checkFileType(dictKey, dicts, (MultipartHttpServletRequest) request, response);
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


    private boolean checkFileType(String dictKey, List<SysDict> dict, MultipartHttpServletRequest request, HttpServletResponse response) {
        //接受的媒体类型
        List<MediaType> acceptTypes = new ArrayList<>();
        //接受的文件后缀
        Set<String> acceptSuffix = new HashSet<>();
        //不接受的媒体类型
        List<MediaType> blackTypes = new ArrayList<>();
        //不接受的文件后缀
        Set<String> blackSuffix = new HashSet<>();

        dict.stream().filter(d -> d.getKey().equals(dictKey + ".accept")).findFirst().ifPresent(d -> {
            if (StringUtils.hasText(d.getValue())) {
                acceptTypes.addAll(MediaType.parseMediaTypes(d.getValue()));
            }
            if (StringUtils.hasText(d.getDescription())) {
                acceptSuffix.addAll(Arrays.asList(d.getDescription().trim().toLowerCase(Locale.ROOT).split(",")));
            }
        });
        dict.stream().filter(d -> d.getKey().equals(dictKey + ".blacklist")).findFirst().ifPresent(d -> {
            if (StringUtils.hasText(d.getValue())) {
                blackTypes.addAll(MediaType.parseMediaTypes(d.getValue()));
            }
            if (StringUtils.hasText(d.getDescription())) {
                blackSuffix.addAll(Arrays.asList(d.getDescription().trim().toLowerCase(Locale.ROOT).split(",")));
            }
        });
        int configLength = acceptTypes.size() + acceptSuffix.size() + blackTypes.size() + blackSuffix.size();


        if (configLength > 0) {
            Iterator<String> iterator = request.getFileNames();
            while (iterator.hasNext()) {
                String formFileName = iterator.next();
                MultipartFile multipartFile = request.getFile(formFileName);
                if (multipartFile != null) {
                    String contentType = multipartFile.getContentType();
                    //先判断媒体类型
                    if (StringUtils.hasText(contentType)) {
                        MediaType type = MediaType.parseMediaType(contentType);
                        boolean accept = true;
                        //先判断黑名单
                        for (MediaType configType : blackTypes) {
                            accept = !configType.includes(type);
                            if (!accept) {
                                break;
                            }
                        }
                        if (accept) {
                            //在判断白名单
                            for (MediaType configType : acceptTypes) {
                                accept = configType.includes(type);
                                if (accept) {
                                    break;
                                }
                            }
                        }
                        if (!accept) {
                            returnNotAccept(contentType, response);
                            return false;
                        }
                    }
                    //在判断文件后缀
                    String fileSuffix = "";
                    if (StringUtils.hasText(multipartFile.getOriginalFilename())) {
                        fileSuffix = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
                    }
                    if (StringUtils.hasText(fileSuffix)) {
                        fileSuffix = fileSuffix.toLowerCase(Locale.ROOT);
                        boolean accept = true;
                        //先判断黑名单
                        if (!blackSuffix.isEmpty()) {
                            accept = !blackSuffix.contains(fileSuffix);
                        }
                        if (accept) {
                            //在判断白名单
                            if (!acceptSuffix.isEmpty()) {
                                accept = acceptSuffix.contains(fileSuffix);
                            }
                        }
                        if (!accept) {
                            returnNotAccept(fileSuffix, response);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 返回不支持媒体类型消息
     *
     * @param contentType
     * @param response
     */
    private void returnNotAccept(String contentType, HttpServletResponse response) {
        response.resetBuffer();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        ResultEntity result = ResultEntity.error("不支持指定的文件类型:" + contentType);
        try (OutputStream ops = response.getOutputStream()) {
            objectMapper.writeValue(ops, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
