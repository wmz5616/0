package com.zemcho.guzhe.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class XssFilter implements Filter {
    // 定义排除路径
    private static final String[] EXCLUDE_PATHS = {
            "/guzhe/wechat/product_order/pay/callBack",
            "/guzhe/open/express/subscribe/callback"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
//        XSSHttpServletRequestWrapper xssRequest =
//                new XSSHttpServletRequestWrapper((HttpServletRequest) request);
//        chain.doFilter(xssRequest, response);
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // 检查是否在排除路径中
        if (isExcludePath(path)) {
            // 直接放行，不进行XSS过滤
            chain.doFilter(request, response);
            return;
        }

        // 非排除路径，进行XSS过滤
        XSSHttpServletRequestWrapper xssRequest = new XSSHttpServletRequestWrapper(httpRequest);
        chain.doFilter(xssRequest, response);
    }

    @Override
    public void destroy() {

    }

    private boolean isExcludePath(String path) {
        for (String excludePath : EXCLUDE_PATHS) {
            if (excludePath.endsWith("/*")) {
                // 处理通配符路径
                String prefix = excludePath.substring(0, excludePath.length() - 2);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (path.equals(excludePath)) {
                // 精确匹配
                return true;
            }
        }
        return false;
    }
}
