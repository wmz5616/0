package com.zemcho.guzhe.config.database;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = "com.zemcho.guzhe.mapper")
public class DataSourceConfig {

    @Value("${spring.datasource.common.mapperLocation}")
    private String mapperLocation;

    /* ************************************************************ */
    /*                           DataSource                         */
    /* ************************************************************ */

    //系统数据库连接
    @ConfigurationProperties(prefix = "spring.datasource.common")
    @Bean(name = "basicDataSource")
    @Primary
    public DataSource basicDataSource() {
        DruidDataSource dataSource = new DruidDataSource();

        return dataSource;
    }

    /* ************************************************************ */
    /*                      SqlSessionFactory                       */
    /* ************************************************************ */

    public SqlSessionFactory basicSqlSessionFactory(DataSource dataSource,String mapperLocation){

        System.out.println("mapperLocation = " + mapperLocation);
        try {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocation);
            System.out.println("length = " + resources.length);
            sqlSessionFactoryBean.setMapperLocations(resources);

            Properties properties = new Properties();
            properties.setProperty("offsetAsPageNum", "true");
            properties.setProperty("rowBoundsWithCount", "true");
            properties.setProperty("reasonable", "true");
            properties.setProperty("supportMethodsArguments", "true");
            properties.setProperty("returnPageInfo", "check");
            properties.setProperty("params", "count=countSql");

            PageHelper pageHelper = new PageHelper();
            pageHelper.setProperties(properties);

            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.setMapUnderscoreToCamelCase(true);
            sqlSessionFactoryBean.setConfiguration(configuration);

            sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});

            return sqlSessionFactoryBean.getObject();

        }catch (Exception e){

            e.printStackTrace();
        }

        return null;
    }

    @Bean(name = "commonSqlSessionFactory")
    @Primary
    public SqlSessionFactory commonSqlSessionFactory(@Qualifier("basicDataSource") DataSource dataSource) {

        return basicSqlSessionFactory(dataSource,mapperLocation);
    }

    /* ************************************************************ */
    /*                  DataSourceTransactionManager                */
    /* ************************************************************ */


    @Bean(name = "basicDataSourceTransactionManager")
    @Primary
    public DataSourceTransactionManager basicDataSourceTransactionManager(@Qualifier("basicDataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        return dataSourceTransactionManager;
    }

    @Bean(name = "basicDataSourceSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate basicDataSourceSqlSessionTemplate(@Qualifier("commonSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);

        return sqlSessionTemplate;
    }

    @Bean

    public WallFilter wallFilter(){

        WallFilter wallFilter=new WallFilter();

        wallFilter.setConfig(wallConfig());

        return wallFilter;

    }

    @Bean
    public WallConfig wallConfig(){

        WallConfig config =new WallConfig();
//        是否允许一次执行多条语句，缺省关闭，设置为true, 解决批量更新的问题
        config.setMultiStatementAllow(true);
        //允许非基本语句的其他语句
        config.setNoneBaseStatementAllow(true);
        return config;

    }
}
