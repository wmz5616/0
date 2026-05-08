package com.zemcho.guzhe.util.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @title: ExcelUtil
 * @Description: Excel读写工具类
 * @Date: 2025/7/8 17:55
 */
public class ExcelUtil {
    /**
     * 导出Excel(07版.xlsx)到web
     *
     * @param response  HttpServletResponse
     * @param data      数据 list
     * @param fileName  文件名
     * @param sheetName sheet名
     * @param clazz     实体类.class
     * @param <T>       泛型
     */
    public static <T> void exportToWeb(HttpServletResponse response, List<T> data, String fileName, String sheetName,
                                       Class<T> clazz) {
        exportToWeb(response, data, fileName, sheetName, clazz, ExcelTypeEnum.XLSX);
    }

    /**
     * 导出Excel到web
     *
     * @param response  HttpServletResponse
     * @param data      数据 list
     * @param fileName  文件名
     * @param sheetName sheet名
     * @param clazz     实体类.class
     * @param excelType Excel类型
     * @param <T>       泛型
     */
    public static <T> void exportToWeb(HttpServletResponse response, List<T> data, String fileName, String sheetName,
                                       Class<T> clazz, ExcelTypeEnum excelType) {
        try {
            // 防止中文乱码
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + encodedFileName + excelType.getValue());

            EasyExcel.write(response.getOutputStream(), clazz)
                    .excelType(excelType)
                    .sheet(sheetName)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动列宽
                    .doWrite(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从web导入Excel数据(07版.xlsx)
     *
     * @param file     上传的文件
     * @param clazz    实体类.class
     * @param listener 自定义监听器
     * @param <T>      泛型
     */
    public static <T> void importFromWeb(MultipartFile file, Class<T> clazz, ReadListener<T> listener) {
        importFromWeb(file, clazz, listener, ExcelTypeEnum.XLSX);
    }

    /**
     * 从web导入Excel数据
     *
     * @param file      上传的文件
     * @param clazz     实体类.class
     * @param listener  自定义监听器
     * @param excelType Excel类型
     * @param <T>       泛型
     */
    public static <T> void importFromWeb(MultipartFile file, Class<T> clazz, ReadListener<T> listener,
                                         ExcelTypeEnum excelType) {
        try {
            EasyExcel.read(file.getInputStream(), clazz, listener)
                    .excelType(excelType)
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件路径导入Excel数据(07版.xlsx)
     *
     * @param filePath 文件路径
     * @param clazz    实体类.class
     * @param listener 自定义监听器
     * @param <T>      泛型
     */
    public static <T> void importFromFile(String filePath, Class<T> clazz, ReadListener<T> listener) {
        importFromFile(filePath, clazz, listener, ExcelTypeEnum.XLSX);
    }

    /**
     * 从文件路径导入Excel数据
     *
     * @param filePath  文件路径
     * @param clazz     实体类.class
     * @param listener  自定义监听器
     * @param excelType Excel类型
     * @param <T>       泛型
     */
    public static <T> void importFromFile(String filePath, Class<T> clazz, ReadListener<T> listener,
                                          ExcelTypeEnum excelType) {
        EasyExcel.read(filePath, clazz, listener)
                .excelType(excelType)
                .sheet()
                .doRead();
    }

    /**
     * 从输入流导入Excel数据(07版.xlsx)
     *
     * @param inputStream 输入流
     * @param clazz       实体类.class
     * @param listener    自定义监听器
     * @param <T>         泛型
     */
    public static <T> void importFromStream(InputStream inputStream, Class<T> clazz, ReadListener<T> listener) {
        importFromStream(inputStream, clazz, listener, ExcelTypeEnum.XLSX);
    }

    /**
     * 从输入流导入Excel数据
     *
     * @param inputStream 输入流
     * @param clazz       实体类.class
     * @param listener    自定义监听器
     * @param excelType   Excel类型
     * @param <T>         泛型
     */
    public static <T> void importFromStream(InputStream inputStream, Class<T> clazz, ReadListener<T> listener,
                                            ExcelTypeEnum excelType) {
        EasyExcel.read(inputStream, clazz, listener)
                .excelType(excelType)
                .sheet()
                .doRead();
    }

    /**
     * 导出Excel到文件(07版.xlsx)
     *
     * @param filePath  文件路径
     * @param data      数据 list
     * @param sheetName sheet名
     * @param clazz     实体类.class
     * @param <T>       泛型
     */
    public static <T> void exportToFile(String filePath, List<T> data, String sheetName, Class<T> clazz) {
        exportToFile(filePath, data, sheetName, clazz, ExcelTypeEnum.XLSX);
    }

    /**
     * 导出Excel到文件
     *
     * @param filePath  文件路径
     * @param data      数据 list
     * @param sheetName sheet名
     * @param clazz     实体类.class
     * @param excelType Excel类型
     * @param <T>       泛型
     */
    public static <T> void exportToFile(String filePath, List<T> data, String sheetName, Class<T> clazz,
                                        ExcelTypeEnum excelType) {
        EasyExcel.write(filePath, clazz)
                .excelType(excelType)
                .sheet(sheetName)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动列宽
                .doWrite(data);
    }
}
