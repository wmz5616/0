import request from '@/utils/request';
export async function commonServices(payload, url, method) {
    return request(url, {
      method,
      data: payload,
    });
  }
  
  // GET
  export async function getServices(url) {
    return request(url, {
      method: 'GET',
    });
  }