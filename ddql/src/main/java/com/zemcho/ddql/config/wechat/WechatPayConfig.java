package com.zemcho.ddql.config.wechat;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
@Getter
@Setter
@Slf4j
public class WechatPayConfig {
    //商户号
    private String mchId;

    //商户API证书序列号
    private String mchSerialNo;

    //商户私钥文件路径
    private String privateKeyPath;

    //APIv3密钥
    private String apiV3Key;

    //小程序AppID
    private String appId;

    // 转账回调地址
    private String transferNotifyUrl;

    //充值购币订单支付回调地址
    private String rechargeOrderPayNotifyUrl;

    // 是否开启支付功能: true按原金额下单、false固定写死0.01元
    private Boolean payStatus;

    // 私钥内容缓存
    private String privateKeyContent;

    @Bean("wechatPayCoreConfig")
    public Config wechatPayCoreConfig() {
        try {
            String privateKey = getPrivateKeyContent();

            return new RSAAutoCertificateConfig.Builder()
                    .merchantId(mchId)
                    .privateKey(privateKey)
//                    .privateKeyFromPath(privateKeyPath)
                    .merchantSerialNumber(mchSerialNo)
                    .apiV3Key(apiV3Key)
                    .build();
        } catch (IOException e) {
            log.error("读取私钥文件失败: {}", e.getMessage());
            throw new RuntimeException("读取私钥文件失败", e);
        }
    }

    /**
     * 获取私钥内容（带缓存）
     */
    private String getPrivateKeyContent() throws IOException {
        if (privateKeyContent == null) {
            privateKeyContent = readPrivateKeyFromFile(privateKeyPath);
            // 清理私钥格式，确保换行符正确
            privateKeyContent = cleanPrivateKey(privateKeyContent);
        }
        return privateKeyContent;
    }

    /**
     * 清理私钥格式
     */
    private String cleanPrivateKey(String privateKey) {
        if (privateKey == null) {
            return null;
        }

        // 保留完整的私钥格式，只处理换行符问题
        String cleaned = privateKey
                .replace("\\n", "\n")
                .replace("\r\n", "\n")
                .trim();

        // 确保私钥包含完整的 BEGIN 和 END 标记
        if (!cleaned.contains("-----BEGIN PRIVATE KEY-----")) {
            cleaned = "-----BEGIN PRIVATE KEY-----\n" + cleaned;
        }
        if (!cleaned.contains("-----END PRIVATE KEY-----")) {
            cleaned = cleaned + "\n-----END PRIVATE KEY-----";
        }

        return cleaned;
    }

    /**
     * 从文件读取私钥（支持多种路径格式）
     */
    private String readPrivateKeyFromFile(String filePath) throws IOException {
        log.info("尝试读取私钥文件: {}", filePath);

        // 1. 尝试从classpath读取
        if (filePath.startsWith("classpath:")) {
            String path = filePath.substring("classpath:".length());
            Resource resource = new ClassPathResource(path);
            if (resource.exists()) {
                log.info("从classpath读取私钥文件: {}", path);
                return new String(resource.getInputStream().readAllBytes());
            }
        }

        // 2. 尝试从文件系统绝对路径读取
        if (Files.exists(Paths.get(filePath))) {
            log.info("从绝对路径读取私钥文件: {}", filePath);
            return Files.readString(Paths.get(filePath));
        }

        // 3. 尝试从相对路径读取（相对于工作目录）
        String workingDir = System.getProperty("user.dir");
        Path relativePath = Paths.get(workingDir, filePath);
        if (Files.exists(relativePath)) {
            log.info("从相对路径读取私钥文件: {}", relativePath);
            return Files.readString(relativePath);
        }

        // 4. 尝试从resources目录读取
        Resource resource = new ClassPathResource(filePath);
        if (resource.exists()) {
            log.info("从resources目录读取私钥文件: {}", filePath);
            return new String(resource.getInputStream().readAllBytes());
        }

        throw new IOException("私钥文件不存在，尝试的路径: " + filePath);
    }
}
