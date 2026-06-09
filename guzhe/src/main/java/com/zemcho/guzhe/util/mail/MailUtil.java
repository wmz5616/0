package com.zemcho.guzhe.util.mail;

import com.zemcho.guzhe.config.mail.MailConfig;
import com.zemcho.guzhe.util.BeanUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件工具类
 */
public class MailUtil {

    /**
     * 发送邮件
     *
     * @param helper
     * @param templateName
     * @param t
     * @param <T>
     * @return
     */
    public static <T> boolean send(MessageHelper helper, String templateName, T t) {
        try {
            JavaMailSender javaMailSender = BeanUtil.getBean("javaMailSender", JavaMailSender.class);
            FreeMarkerConfigurer configurer = BeanUtil.getBean("freeMarkerConfigurer", FreeMarkerConfigurer.class);
            MailConfig mailConfig = BeanUtil.getBean("mailConfig", MailConfig.class);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(mailConfig.getUsername());
            mimeMessageHelper.setTo(helper.getTo());
            mimeMessageHelper.setSubject(helper.getSubject());

            // 添加附件
            for (MessageHelper.Attachment attachment : helper.getAttachments()) {
                mimeMessageHelper.addAttachment(attachment.getFilename(), attachment.getSource());
            }

            Configuration configuration = configurer.getConfiguration();
            Template template = configuration.getTemplate(templateName);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, t);

            mimeMessageHelper.setText(text, true);
            javaMailSender.send(mimeMessage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Setter
    @Getter
    public static class MessageHelper {
        private String to = "";

        private String subject = "";

        // 附件列表，每个附件包含文件名和数据源
        private List<Attachment> attachments = new ArrayList<>();

        public void addAttachment(String filename, InputStreamSource source) {
            this.attachments.add(new Attachment(filename, source));
        }

        @Getter
        @Setter
        public static class Attachment {
            private String filename;
            private InputStreamSource source;

            public Attachment(String filename, InputStreamSource source) {
                this.filename = filename;
                this.source = source;
            }
        }
    }
}
