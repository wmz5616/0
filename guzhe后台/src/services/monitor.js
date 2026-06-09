import { get, post } from '@/utils/request';

export async function getMonitorOrderData() {
  return post('/monitor/order/get');
}

export async function getMonitorUserStat() {
  return get('/monitor/user/stat');
}

export async function getMonitorActiveStat() {
  return get('/monitor/active/stat');
}

export async function getMonitorVisitTrendStat(params) {
  return get('/monitor/visit/trend/stat', params);
}

export async function getMonitorBusinessEquipment(params) {
  return get('/monitor/business/equipment', params);
}

export async function exportMonitorBusinessEquipment(params) {
  return post('/monitor/export', params, { responseType: 'blob' });
}
