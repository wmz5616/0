package com.zemcho.guzhe.mapper.sys;

import com.zemcho.guzhe.controller.sys.param.TransactionFlowSearchParam;
import com.zemcho.guzhe.controller.sys.vo.TransactionFlowVo;
import com.zemcho.guzhe.controller.sys.vo.TransactionFlowSummaryVo;
import com.zemcho.guzhe.controller.sys.vo.TransactionSummaryVo;
import com.zemcho.guzhe.controller.sys.vo.SubLedgerSummaryVo;
import com.zemcho.guzhe.entity.order.Order;
import com.zemcho.guzhe.entity.reconciliation.SubLedgerSummary;
import com.zemcho.guzhe.entity.reconciliation.TransactionFlow;
import com.zemcho.guzhe.entity.reconciliation.TransactionSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易流水 Mapper 接口
 */
@Mapper
public interface TransactionFlowMapper {

    List<SubLedgerSummary> selectSubLedgerSummaryGrouped(@Param("data") TransactionFlowSearchParam param);

    /** 保存交易汇总 */
    int insertTransactionSummary(@Param("item") com.zemcho.guzhe.entity.reconciliation.TransactionSummary item);

    /** 批量保存交易流水快照 */
    int batchInsertTransactionFlow(@Param("list") List<com.zemcho.guzhe.entity.reconciliation.TransactionFlow> list);

    /** 保存分账汇总 */
    int insertSubLedgerSummary(@Param("item") com.zemcho.guzhe.entity.reconciliation.SubLedgerSummary item);

    /** 清理指定日期的流水快照 (保证定时任务重入幂等) */
    int deleteTransactionFlowByDate(@Param("billDate") String billDate);

    /** 清理指定日期的交易汇总 */
    int deleteTransactionSummaryByDate(@Param("billDate") String billDate);

    /** 清理指定日期的分账汇总 */
    int deleteSubLedgerSummaryByDate(@Param("billDate") String billDate);

    /** 从快照表获取流水列表 */
    List<TransactionFlowVo> selectTransactionFlowSnapshotList(@Param("data") TransactionFlowSearchParam param);

    /** 从快照表获取流水总数 */
    long selectTransactionFlowSnapshotList_COUNT(@Param("data") TransactionFlowSearchParam param);

    /** 从快照表获取统计条 */
    TransactionFlowSummaryVo selectTransactionFlowSnapshotSummary(@Param("data") TransactionFlowSearchParam param);

    /** 从汇总表获取交易汇总列表 */
    List<TransactionSummaryVo> selectTransactionSummarySnapshotList(@Param("data") TransactionFlowSearchParam param);

    /** 从汇总表获取交易汇总总数 */
    long selectTransactionSummarySnapshotList_COUNT(@Param("data") TransactionFlowSearchParam param);

    /** 从分账汇总表获取汇总列表 */
    List<SubLedgerSummaryVo> selectSubLedgerSummarySnapshotList(@Param("data") TransactionFlowSearchParam param);

    /** 从分账汇总表获取汇总总数 */
    long selectSubLedgerSummarySnapshotList_COUNT(@Param("data") TransactionFlowSearchParam param);

    /** 获取分账明细统计条 */
    com.zemcho.guzhe.controller.sys.vo.SubLedgerDetailSummaryVo selectSubLedgerDetailSummary(@Param("data") TransactionFlowSearchParam param);

    /** 获取分账明细列表 */
    List<com.zemcho.guzhe.controller.sys.vo.SubLedgerDetailVo> selectSubLedgerDetailList(@Param("data") TransactionFlowSearchParam param);

    /** 获取分账明细总数 */
    long selectSubLedgerDetailList_COUNT(@Param("data") TransactionFlowSearchParam param);

    /** 小程序端获取商家结算记录列表 */
    List<com.zemcho.guzhe.controller.wechat.shop.vo.SettlementRecordItemVo> selectWechatSettlementRecordList(@Param("data") com.zemcho.guzhe.controller.wechat.shop.param.SettlementRecordSearchParam param);

    /** 小程序端获取商家结算记录总数 */
    long selectWechatSettlementRecordListCount(@Param("data") com.zemcho.guzhe.controller.wechat.shop.param.SettlementRecordSearchParam param);

    /** 小程序端获取商家结算记录汇总 */
    com.zemcho.guzhe.controller.wechat.shop.vo.SettlementRecordSummaryVo selectWechatSettlementRecordSummary(@Param("data") com.zemcho.guzhe.controller.wechat.shop.param.SettlementRecordSearchParam param);
}