package com.zemcho.guzhe.config.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
	@Bean
	public FilterRegistrationBean xssFilter(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new XssFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setName("xssFilter");
		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean repeatedlyReadFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		RepeatedlyReadFilter repeatedlyReadFilter = new RepeatedlyReadFilter();
		registration.setFilter(repeatedlyReadFilter);
		registration.addUrlPatterns("/*");
		return registration;
	}
}