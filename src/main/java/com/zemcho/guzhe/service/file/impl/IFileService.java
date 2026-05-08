package com.zemcho.guzhe.service.file.impl;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.service.file.FileService;
import com.zemcho.guzhe.util.file.FileUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class IFileService implements FileService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 上传文件存储在本地的路径
     */
    @Value("${file.upload-path}")
    private String uploadFilePath;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public Result upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new Result(10015, "文件为空");
        }

        Result result = FileUploadUtil.upload(localFilePath + uploadFilePath, file);
        if (!result.success()) {
            return result;
        }

        Map<String, String> data = new HashMap<>();
        data.put("url", uploadFilePath + (String) result.getData());

        return Result.success("上传成功", data);
    }
}
