package com.zemcho.ddql.service.order.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.zemcho.ddql.controller.order.vo.ExchangeOrderUnDispatchedExportVo;
import com.zemcho.ddql.entity.express.ExpressCompany;
import com.zemcho.ddql.entity.express.ExpressOrder;
import com.zemcho.ddql.entity.order.ExchangeOrder;
import com.zemcho.ddql.mapper.express.ExpressOrderMapper;
import com.zemcho.ddql.mapper.order.ExchangeOrderMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @title: ImportExchangeOrderExpressListener
 * @Description:
 * @Date: 2025/7/9 8:59
 */
@Slf4j
public class ImportExchangeOrderExpressListener implements ReadListener<ExchangeOrderUnDispatchedExportVo> {
    private ExchangeOrderMapper exchangeOrderMapper;

    private ExpressOrderMapper expressOrderMapper;

    private Map<String, ExchangeOrder> orderMap;

    private Map<String, ExpressCompany> expressCompanyMap;

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 200;

    /**
     * 当前行号
     */
    private int currentRowNum = 1; // 从1开始，对应Excel行号

    /**
     * 错误信息列表
     */
    private final List<String> errorList = new ArrayList<>();

    /**
     * 缓存的数据
     */
    private List<ExpressOrder> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    public ImportExchangeOrderExpressListener(ExchangeOrderMapper exchangeOrderMapper,
                                              ExpressOrderMapper expressOrderMapper,
                                              Map<String, ExchangeOrder> orderMap,
                                              Map<String, ExpressCompany> expressCompanyMap) {
        this.exchangeOrderMapper = exchangeOrderMapper;
        this.expressOrderMapper = expressOrderMapper;
        this.orderMap = orderMap;
        this.expressCompanyMap = expressCompanyMap;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    @Override
    public void invoke(ExchangeOrderUnDispatchedExportVo data, AnalysisContext context) {
        currentRowNum++;
        log.info("解析到一条数据:{}", data);

        //数据校验
        if (data.getOrderNo() == null || data.getOrderNo().isEmpty()) {
            errorList.add("第" + currentRowNum + "行 订单编号为空");
            return;
        }
        if (!orderMap.containsKey(data.getOrderNo())) {
            errorList.add("第" + currentRowNum + "行 订单不存在");
            return;
        }
        if (data.getExpressCompanyName() == null || data.getExpressCompanyName().isEmpty()) {
            errorList.add("第" + currentRowNum + "行 快递公司名称为空");
            return;
        }
        if (!expressCompanyMap.containsKey(data.getExpressCompanyName())) {
            errorList.add("第" + currentRowNum + "行 快递公司名称错误");
            return;
        }
        if (data.getExpressNo() == null || data.getExpressNo().isEmpty()) {
            errorList.add("第" + currentRowNum + "行 快递单号为空");
            return;
        }
        ExchangeOrder orderInfo = orderMap.get(data.getOrderNo());
        ExpressCompany expressCompany = expressCompanyMap.get(data.getExpressCompanyName());

        ExpressOrder expressOrder = new ExpressOrder();
        expressOrder.setTxnType(1);
        expressOrder.setTxnId(orderInfo.getId());
        expressOrder.setExpressCompanyName(expressCompany.getName());
        expressOrder.setExpressCompanyCode(expressCompany.getCode());
        expressOrder.setExpressNo(data.getExpressNo());
        expressOrder.setStatus(-1);
        expressOrder.setIsRecord(1);
        expressOrder.setIsSub(0);
        expressOrder.setCreateTime(LocalDateTime.now());
        cachedDataList.add(expressOrder);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 数据存储处理
     */
    private void saveData() {
        if (cachedDataList.isEmpty()) {
            return;
        }
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());

        expressOrderMapper.insertAll(cachedDataList);
        for (ExpressOrder expressOrder : cachedDataList) {
            ExchangeOrder exchangeOrderUpdate = new ExchangeOrder();
            exchangeOrderUpdate.setId(expressOrder.getTxnId());
            exchangeOrderUpdate.setStatus(3);
            exchangeOrderUpdate.setExpressCompanyName(expressOrder.getExpressCompanyName());
            exchangeOrderUpdate.setExpressCompanyCode(expressOrder.getExpressCompanyCode());
            exchangeOrderUpdate.setExpressNo(expressOrder.getExpressNo());
            exchangeOrderUpdate.setExpressStatus(-1);
            exchangeOrderMapper.update(exchangeOrderUpdate);
        }

        log.info("存储数据库成功！");
    }
}
