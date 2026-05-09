package com.zemcho.guzhe.util.file;

import com.zemcho.guzhe.common.Result;
import com.zemcho.guzhe.util.uuid.SeqUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;

/**
 * 文件上传工具类
 *
 * @author ruoyi
 */
public class FileUploadUtil {
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 文件名称
     */
    public static final Result upload(String baseDir, MultipartFile file) {
        try {
            return upload(baseDir, file, MimeTypeUtil.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(10011, "文件上传失败，请稍后再试");
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir          相对应用的基目录
     * @param file             上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws IOException 比如读写文件出错时
     */
    public static final Result upload(String baseDir, MultipartFile file, String[] allowedExtension) throws IOException {
        int fileNamelength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNamelength > FileUploadUtil.DEFAULT_FILE_NAME_LENGTH) {
            return new Result(10011, "文件名过长，请重新上传");
        }

        Result checkResult = assertAllowed(file, allowedExtension);
        if (!checkResult.success()) {
            return checkResult;
        }

        String fileName = extractFilename(file);

        String absPath = getAbsoluteFile(baseDir, fileName).getAbsolutePath();
        Path filePath = Paths.get(absPath);
        file.transferTo(filePath);

        // 添加权限设置 644
        setPosixPermissions(filePath, "rw-r--r--");

        return Result.success("success", getPathFileName(fileName));
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        return String.format("%s/%s_%s.%s", DateFormatUtils.format(new Date(), "yyyy/MM/dd"),
                FilenameUtils.getBaseName(file.getOriginalFilename()), SeqUtil.getId(SeqUtil.uploadSeqType),
                FileTypeUtil.getExtension(file));
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
//                desc.getParentFile().mkdirs();

                // 设置目录权限为 755（drwxr-xr-x）
                createDirectoryWithPermissions(desc.getParentFile(), "rwxr-xr-x"); // 对应 755
            }
        }
        return desc.isAbsolute() ? desc : desc.getAbsoluteFile();
    }

    private static final String getPathFileName(String fileName) throws IOException {
        String pathFileName = "/" + fileName;
        return pathFileName;
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     */
    public static final Result assertAllowed(MultipartFile file, String[] allowedExtension) {
        long size = file.getSize();
        if (size > DEFAULT_MAX_SIZE) {
            return new Result(10011, "文件大小超出限制，请重新上传");
        }

        String extension = FileTypeUtil.getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            return new Result(10011, "文件类型错误，请重新上传");
        }

        return Result.success("success");
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension        上传文件类型
     * @param allowedExtension 允许上传文件类型
     * @return true/false
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 递归创建目录并设置权限
     *
     * @param dir
     * @param permissionString
     */
    public static void createDirectoryWithPermissions(File dir, String permissionString) {
        // 记录需要设置权限的目录
        List<File> newDirs = new ArrayList<>();

        // 查找所有需要创建的新目录
        File currentDir = dir;
        while (currentDir != null && !currentDir.exists()) {
            newDirs.add(0, currentDir); // 将当前目录添加到列表开头
            currentDir = currentDir.getParentFile();
        }

        // 创建目录结构
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 为每个新创建的目录设置权限
        for (File newDir : newDirs) {
            setPosixPermissions(newDir.toPath(), permissionString);
        }
    }

    /**
     * 设置 POSIX 文件权限（仅适用于支持POSIX的系统，如 Linux、macOS）
     *
     * @param path
     * @param permissionString
     */
    public static void setPosixPermissions(Path path, String permissionString) {
        try {
            if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
                FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(
                        PosixFilePermissions.fromString(permissionString)
                );
                Files.setPosixFilePermissions(path, attrs.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static Boolean checkFileIsExist(String filePath) {
        File uploadFile = new File(filePath);
        if (!uploadFile.exists() || !uploadFile.isFile()) {
            return false;
        }

        return true;
    }

    /**
     * 删除文件
     *
     * @param path
     */
    public static void delFile(String path) {
        if (checkFileIsExist(path)) {
            File file = new File(path);
            file.delete();
        }
    }
}