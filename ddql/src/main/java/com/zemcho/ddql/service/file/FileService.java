package com.zemcho.ddql.service.file;

import com.zemcho.ddql.common.Result;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    Result upload(MultipartFile file);
}
