import request from '@/utils/request';

export async function query() {
  return request('/api/users');
}
// export async function queryCurrent() {
//   return request('/api/currentUser');
// }
// export async function queryNotices() {
//   return request('/api/notices');
// }
// export async function getLoginInfo(params){
//   return request('/zemcho-table/security/getLoginInfo', {
//     method: 'POST',
//     data: params,
//   });
// }
