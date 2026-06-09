package com.zemcho.guzhe.config.database;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ryan
 * @title: DataSourceMapperScanConfig
 * @projectName master
 * @description: ZEMCHO
 * @date 2020/12/22 0022 14:56
 */
@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
public class DataSourceMapperScanConfig {
    public MapperScannerConfigurer basicMapperScannerConfigurer(String basePackage, String sqlSessionTemplate){

        MapperScannerConfigurer mapperScanner = new MapperScannerConfigurer();
        mapperScanner.setBasePackage(basePackage);
        mapperScanner.setSqlSessionTemplateBeanName(sqlSessionTemplate);

        return mapperScanner;
    }

    @Bean(name = "basicDataSourceMapperScanner")
    public MapperScannerConfigurer gpMapperScanner(){
        return basicMapperScannerConfigurer("com.zemcho.guzhe.mapper", "basicDataSourceSqlSessionTemplate");
    }
}
