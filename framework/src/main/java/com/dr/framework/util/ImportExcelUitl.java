package com.dr.framework.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

public class ImportExcelUitl {

    /**
     * <p>
     * 导入带有头部标题行的Excel<br>
     * 单元格内容默认全为String类型<br>
     * 时间格式为yyyy/MM/dd
     * </p>
     *
     * @param personId
     * @param file    Excel文件
     * @param headers 实体类中需导入的字段，与Excel中标题行对应
     * @param obj     对象类型
     */
    public static List<Map> readExcel(String personId, MultipartFile file, String[] headers, Object obj) throws Exception {

        String fileName = file.getOriginalFilename();
        boolean isExcel2003 = !fileName.matches("^.+\\.(?i)(xlsx)$");
        Map<String, Object> map;
        InputStream is = null;
        Workbook wb = null;
        Object value = null;
        Row row = null;
        Cell cell = null;
        String firstLetter;
        String getter;
        Method method;
        try {
            is = file.getInputStream();
            //判断Excel是2003或者是其他版本的
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else {
                wb = new XSSFWorkbook(is);
            }
            Sheet sheet = wb.getSheetAt(0);
            List<Map> linked = new LinkedList<>();

            for (int i = 1; i <= sheet
                    .getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                map = new HashMap<>();
                map.put("id", UUID.randomUUID());
                for (int j = 0; j <= headers.length; j++) {
                    cell = row.getCell(j);
                    if (cell == null) {
                        continue;
                    }
                    //调用getXxx()方法得到属性值
                    firstLetter = headers[j].substring(0, 1).toUpperCase();
                    getter = "get" + firstLetter + headers[j].substring(1);
                    method = obj.getClass().getMethod(getter, new Class[]{});
                    value = method.invoke(obj, new Object[]{});
                    // 判断类型后进行类型转换
                    cell.setCellType(CellType.STRING);
                    if (value instanceof Integer) {
                        map.put(headers[j], Integer.parseInt(cell.getStringCellValue()));
                    } else if (value instanceof Float) {
                        map.put(headers[j], Float.parseFloat(cell.getStringCellValue()));
                    } else if (value instanceof Double) {
                        map.put(headers[j], Double.valueOf(cell.getStringCellValue()));
                    } else if (value instanceof Long) {
                        map.put(headers[j], DateTimeUtils.stringToMillis(cell.getStringCellValue(), "yyyy/MM/dd"));
                    } else {
                        map.put(headers[j], cell.getStringCellValue());
                    }
                    map.put("createDate",System.currentTimeMillis());
                    map.put("createPerson",personId);
                }
                linked.add(map);
            }
            return linked;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("解析excel异常！");
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


