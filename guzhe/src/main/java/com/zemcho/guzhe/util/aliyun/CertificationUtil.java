package com.zemcho.guzhe.util.aliyun;

import com.alibaba.fastjson.JSON;
import com.zemcho.guzhe.config.aliyun.CertificationConfig;
import com.zemcho.guzhe.util.aliyun.dto.CertificationIdCardInforDto;
import com.zemcho.guzhe.util.aliyun.dto.CertificationResultDto;
import com.zemcho.guzhe.util.BeanUtil;
import com.zemcho.guzhe.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @title: CertificationUtil
 * @Description: 实名认证工具类
 * @Date: 2025/10/10 15:32
 */
public class CertificationUtil {
    private static final Logger log = LoggerFactory.getLogger(CertificationUtil.class);

    /**
     * 大陆实名认证检查
     *
     * @param cardNum
     * @param name
     * @return
     */
    public static CertificationIdCardInforDto check(String cardNum, String name) {
        try {
            CertificationConfig certificationConfig = BeanUtil.getBean("certificationConfig",
                    CertificationConfig.class);

            Map<String, String> param = new HashMap<>();
            param.put("cardNo", cardNum);
            param.put("realName", name);

            Map<String, String> header = new HashMap<>();
            header.put("Authorization", "APPCODE " + certificationConfig.getAppCode());

            String result = HttpUtil.sendGet(certificationConfig.getDomain(), param, header);
            log.info("Certification check param ：{} result ：{}", param, result);
            if (result == null) {
                return null;
            }
            CertificationResultDto resultDto = JSON.parseObject(result, CertificationResultDto.class);
            if (resultDto == null || resultDto.getError_code() != 0 || resultDto.getResult() == null
                    || !resultDto.getResult().getIsok()) {
                return null;
            }

            return resultDto.getResult().getIdCardInfor();
        } catch (Exception e) {
            log.error("实名认证异常", e);
            return null;
        }
    }
}
