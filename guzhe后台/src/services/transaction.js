import { get, post } from '@/utils/request';

/**
 * 获取交易流水列表
 * @param {Object} params
 * @returns
 */
export async function getTransactionFlowList(params) {
  return get('/guzhe/transaction/flow/lists', params);
}

/**
 * 获取交易明细列表
 * @param {Object} params
 * @returns
 */
export async function getTransactionDetailList(params) {
  return get('/guzhe/transaction/detail/lists', params);
}

/**
 * 获取交易流水汇总统计
 * @param {Object} params
 * @returns
 */
export async function getTransactionFlowSummary(params) {
  return get('/guzhe/transaction/flow/summary', params);
}

/**
 * 导出交易流水 Excel
 * @param {Object} params
 * @returns
 */
export async function exportTransactionFlow(params) {
  return post('/guzhe/transaction/flow/export', params, {
    responseType: 'blob',
  });
}

/**
 * 导出交易明细 Excel
 * @param {Object} params
 * @returns
 */
export async function exportTransactionDetail(params) {
  return post('/guzhe/transaction/detail/export', params, {
    responseType: 'blob',
  });
}
/**
 * 获取交易汇总列表
 * @param {Object} params
 * @returns
 */
export async function getTransactionSummaryList(params) {
  return get('/guzhe/transaction/summary/lists', params);
}

/**
 * 导出交易汇总 Excel
 * @param {Object} params
 * @returns
 */
export async function exportTransactionSummary(params) {
  return post('/guzhe/transaction/summary/export', params, {
    responseType: 'blob',
  });
}
/**
 * 获取分账汇总列表
 * @param {Object} params
 * @returns
 */
export async function getSubLedgerSummaryList(params) {
  return get('/guzhe/transaction/subledger/summary/lists', params);
}
/**
 * 导出分账汇总 Excel
 * @param {Object} params
 * @returns
 */
export async function exportSubLedgerSummary(params) {
  return post('/guzhe/transaction/subledger/summary/export', params, {
    responseType: 'blob',
  });
}

/**
 * 获取分账明细列表
 * @param {Object} params
 * @returns
 */
export async function getSubLedgerDetailList(params) {
  return get('/guzhe/transaction/subledger/detail/lists', params);
}

/**
 * 获取分账明细汇总统计
 * @param {Object} params
 * @returns
 */
export async function getSubLedgerSummary(params) {
  return get('/guzhe/transaction/subledger/summary', params);
}

/**
 * 导出分账明细 Excel
 * @param {Object} params
 * @returns
 */
export async function exportSubLedgerDetail(params) {
  return post('/guzhe/transaction/subledger/detail/export', params, {
    responseType: 'blob',
  });
}
