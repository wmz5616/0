import request from '@/utils/request';

/**
 * 获取交易流水列表
 * @param {Object} params 
 * @returns 
 */
export async function getTransactionFlowList(params) {
  return request('/ddql/transaction/flow/lists', { params });
}

/**
 * 获取交易流水汇总统计
 * @param {Object} params 
 * @returns 
 */
export async function getTransactionFlowSummary(params) {
  return request('/ddql/transaction/flow/summary', { params });
}

/**
 * 导出交易流水 Excel
 * @param {Object} params 
 * @returns 
 */
export async function exportTransactionFlow(params) {
  return request('/ddql/transaction/flow/export', {
    method: 'POST',
    data: params,
    responseType: 'blob',
  });
}

/**
 * 导出交易明细 Excel
 * @param {Object} params 
 * @returns 
 */
export async function exportTransactionDetail(params) {
  return request('/ddql/transaction/detail/export', {
    method: 'POST',
    data: params,
    responseType: 'blob',
  });
}
/**
 * 获取交易汇总列表
 * @param {Object} params 
 * @returns 
 */
export async function getTransactionSummaryList(params) {
  return request('/ddql/transaction/summary/lists', { params });
}

/**
 * 导出交易汇总 Excel
 * @param {Object} params 
 * @returns 
 */
export async function exportTransactionSummary(params) {
  return request('/ddql/transaction/summary/export', {
    method: 'POST',
    data: params,
    responseType: 'blob',
  });
}
/**
 * 获取分账汇总列表
 * @param {Object} params 
 * @returns 
 */
export async function getSubLedgerSummaryList(params) {
  return request('/ddql/transaction/subledger/summary/lists', { params });
}
/**
 * 导出分账汇总 Excel
 * @param {Object} params 
 * @returns 
 */
export async function exportSubLedgerSummary(params) {
  return request('/ddql/transaction/subledger/summary/export', {
    method: 'POST',
    data: params,
    responseType: 'blob',
  });
}

/**
 * 获取分账明细列表
 * @param {Object} params 
 * @returns 
 */
export async function getSubLedgerDetailList(params) {
  return request('/ddql/transaction/subledger/detail/lists', { params });
}

/**
 * 获取分账明细汇总统计
 * @param {Object} params 
 * @returns 
 */
export async function getSubLedgerSummary(params) {
  return request('/ddql/transaction/subledger/summary', { params });
}

/**
 * 导出分账明细 Excel
 * @param {Object} params 
 * @returns 
 */
export async function exportSubLedgerDetail(params) {
  return request('/ddql/transaction/subledger/detail/export', {
    method: 'POST',
    data: params,
    responseType: 'blob',
  });
}
