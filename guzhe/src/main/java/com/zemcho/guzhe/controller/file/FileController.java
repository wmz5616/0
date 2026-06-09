package com.zemcho.guzhe.controller.file;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @title: FileController
 * @Description:
 * @Date: 2025/04/30 11:50
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    FileService fileService;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        return fileService.upload(file);
    }
}
