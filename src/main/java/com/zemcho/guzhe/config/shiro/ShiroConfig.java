package com.zemcho.guzhe.config.shiro;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Ryan
 * @title: ShiroConfig
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/6/23 0023 9:59
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean webFilter() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JWTFilter());
        filterMap.put("mini", new MiniFilter());
        filterMap.put("app", new AppFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        Map<String, String> filterChainMap = new LinkedHashMap<>();
        //anon表示可以匿名访问
        filterChainMap.put("/cas/login", "anon");
        filterChainMap.put("/common/sms/code", "anon");
        filterChainMap.put("/cas/forgetPassword", "anon");
        filterChainMap.put("/common/captcha/code", "anon");
        filterChainMap.put("/system/basic/config", "anon");
        filterChainMap.put("/open/express/subscribe/callback", "anon");
        filterChainMap.put("/transaction/flow/**", "anon");

        //小程序相关
        filterChainMap.put("/wechat/cas/login", "anon");
        filterChainMap.put("/wechat/cas/test/login", "anon");
        filterChainMap.put("/wechat/product_order/pay/callBack", "anon");
        filterChainMap.put("/wechat/**", "mini");

        //app端相关
        filterChainMap.put("/app/accessToken", "anon");
        filterChainMap.put("/app/**", "app");

        filterChainMap.put("/**", "jwt");

        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        securityManager.setRealm(new CustomRealm());

        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        securityManager.setCacheManager(new CustomCacheManager());
        return securityManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor attributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        //设置安全管理器
        attributeSourceAdvisor.setSecurityManager(securityManager);
        return attributeSourceAdvisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }
}