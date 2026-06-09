package com.zemcho.guzhe.job.init_task;

import com.zemcho.guzhe.config.other.OtherConfig;
import com.zemcho.guzhe.controller.product.param.ProductSearchParam;
import com.zemcho.guzhe.entity.cas.CasAdmin;
import com.zemcho.guzhe.entity.product.Product;
import com.zemcho.guzhe.entity.product.ProductTicket;
import com.zemcho.guzhe.mapper.cas.*;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.mapper.product.ProductTicketMapper;
import com.zemcho.guzhe.service.cas.async.AdminPerAsync;
import com.zemcho.guzhe.util.Constant;
import com.zemcho.guzhe.util.redis.RedisUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 *
 */
@Component
@Slf4j
public class InitDataTask {
    @Autowired
    CasRuleMapper casRuleMapper;

    @Autowired
    CasAdminMapper casAdminMapper;

    @Autowired
    OtherConfig otherConfig;

    @Autowired
    AdminPerAsync adminPerAsync;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductTicketMapper productTicketMapper;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        //初始化菜单数据
        initMenuRule();

        //初始化管理员权限缓存
        initAdminPerCache();

        initProductTicketRedis();
    }

    /**
     * 初始化菜单数据
     */
    public void initMenuRule() {
        if (otherConfig.getIsUpdateRule()) {
            System.out.println("initMenuRule running");

            try {
                // 加载SQL脚本
                Resource resource = resourceLoader.getResource("classpath:db/seed/cas_rule_seed.sql");

                casRuleMapper.truncateTableData();

                // 创建并配置ResourceDatabasePopulator来执行SQL脚本
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
                populator.execute(dataSource);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("initMenuRule end");
        }
    }

    /**
     * 初始化管理员权限缓存
     */
    public void initAdminPerCache() {
        List<CasAdmin> adminList = casAdminMapper.selectAll();
        if (adminList != null && !adminList.isEmpty()) {
            for (CasAdmin admin : adminList) {
                adminPerAsync.saveAdminPermissionCache(admin.getId());
            }
        }
    }

    /**
     * 初始化商品券码redis数据
     */
    public void initProductTicketRedis() {
        log.info("开始初始化虚拟商品券码缓存...");
        ProductSearchParam searchParam = new ProductSearchParam();
        List<Product> products = productMapper.selectList(searchParam);
        if (products != null && !products.isEmpty()) {
            int count = 0;
            for (Product product : products) {
                if (product.getIsVirtual() != null && product.getIsVirtual() == 1) {
                    String key = Constant.PRODUCT_TICKET_LIST + product.getId();
                    redisUtil.del(key);
                    List<ProductTicket> productTickets = productTicketMapper.selectByProductId(product.getId(), 1);
                    if (productTickets != null && !productTickets.isEmpty()) {
                        List<String> ticketList = productTickets.stream().map(ProductTicket::getTicket).toList();
                        redisUtil.rightPushAll(key, ticketList);
                        count += ticketList.size();
                    }
                }
            }
            log.info("虚拟商品券码缓存初始化完成，共处理 {} 个券码", count);
        }
    }
}
