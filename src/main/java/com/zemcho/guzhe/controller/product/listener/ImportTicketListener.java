package com.zemcho.guzhe.controller.product.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.zemcho.guzhe.controller.product.dto.TicketDto;
import com.zemcho.guzhe.entity.product.ProductTicket;
import com.zemcho.guzhe.mapper.product.ProductMapper;
import com.zemcho.guzhe.mapper.product.ProductTicketMapper;
import com.zemcho.guzhe.util.StringUtil;
import com.zemcho.guzhe.util.redis.RedisUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.zemcho.guzhe.util.Constant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class ImportTicketListener implements ReadListener<TicketDto> {


    private ProductTicketMapper productTicketMapper;

    private ProductMapper productMapper;

    private RedisUtil redisUtil;

    private Integer productId;

    public ImportTicketListener(ProductTicketMapper productTicketMapper, ProductMapper productMapper,
                                Integer productId, RedisUtil redisUtil) {
        this.productTicketMapper = productTicketMapper;
        this.productMapper = productMapper;
        this.productId = productId;
        this.redisUtil = redisUtil;
    }


    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;

    /**
     * 当前行号
     */
    private int currentRowNum = 0; // 从1开始，对应Excel行号

    /**
     * 错误信息列表
     */
    private final List<String> errorList = new ArrayList<>();

    /**
     * 缓存的数据
     */
    public List<ProductTicket> cachedDataList = new ArrayList<>();

    public List<String> getErrorList() {
        return errorList;
    }


    @Override
    public void invoke(TicketDto data, AnalysisContext analysisContext) {
        currentRowNum++;
        log.info("解析到一条数据:{}", data);

        // EquipmentDto转Equipment
        ProductTicket productTicket = new ProductTicket();
        productTicket.setTicket(data.getTicket());
        productTicket.setSort(data.getSort());
        productTicket.setProductId(this.productId);
        productTicket.setStatus(1);
        productTicket.setCreateTime(LocalDateTime.now());
        cachedDataList.add(productTicket);
//        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
//        if (cachedDataList.size() >= BATCH_COUNT) {
//            saveData();
//            // 存储完成清理 list
//            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
//        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 对券码做唯一校验
        List<ProductTicket> productTickets = productTicketMapper.selectByProductId(this.productId, null);
        List<String> ticketList = productTickets.stream().map(ProductTicket::getTicket).toList();
        List<String> existTickets = new ArrayList<>();
        for (int i = 0; i < cachedDataList.size(); i++) {
            ProductTicket productTicket = cachedDataList.get(i);
            if (StringUtil.isBlank(productTicket.getTicket())) {
                errorList.add("第" + (i + 1) + "行券码不能为空");
            }
            if (ticketList.contains(productTicket.getTicket())) {
                errorList.add("第" + (i + 1) + "行券码已存在");
            }
            if (existTickets.contains(productTicket.getTicket())) {
                errorList.add("第" + (i + 1) + "行券码重复");
            }
            existTickets.add(productTicket.getTicket());
        }
        // 如果有错误信息则不保存 直接返回
        if (!errorList.isEmpty()) {
            return;
        }
        saveData();
        log.info("所有数据解析完成！");
    }


    // 保存数据
    private void saveData() {
        if (cachedDataList.isEmpty()) {
            return;
        }
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        productTicketMapper.saveBatch(cachedDataList);
//        Product product = productMapper.selectById(this.productId);
//        product.setStock(product.getStock() + cachedDataList.size());
//        productMapper.update(product);
        productMapper.inc(this.productId, cachedDataList.size(), null);
        //将券码存在redis列表中，下单时使用
        String key = Constant.PRODUCT_TICKET_LIST + this.productId;
        List<String> ticketList = cachedDataList.stream().map(ProductTicket::getTicket).toList();
        redisUtil.rightPushAll(key, ticketList);
        log.info("存储数据库成功！");
    }

}
