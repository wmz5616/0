package com.zemcho.guzhe.util.pdf;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.util.FastStringWriter;

import java.io.Writer;
import java.util.Map;

/**
 * @title: ThymeleafUtil
 * @Description:
 * @Date: 2024/2/23 17:04
 */
@Component
public class ThymeleafUtil {
    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    private static final Logger logger = LoggerFactory.getLogger(ThymeleafUtil.class);

    /**
     * 获取Thymeleaf模板内容，并将数据替换到模板内容里
     *
     * @param templateFileName
     * @param params
     * @param request
     * @return
     */
    public String getThymeleafTemHtml(String templateFileName, Map<String, Object> params,
                                      HttpServletRequest request) {
        Writer writer = new FastStringWriter();
        Context context = new Context(localeResolver.resolveLocale(request), params);
        // springboot对thymeleaf的默认配置为 prefix="classpath:templates", suffix=".html"

        try {
            springTemplateEngine.process(templateFileName, context, writer);
            String html = writer.toString();
            return html;
        } catch (Exception e) {
            logger.error("Error processing template: {}", templateFileName, e);
            throw e;
        }
    }
}
