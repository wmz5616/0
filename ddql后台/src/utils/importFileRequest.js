import fetch from 'dva/fetch';
import { getToken } from './authority';
import { history } from 'umi';
export function request(url, obj, successCallback, failCallback, functionCallback) {
  const params = new FormData();
  Object.keys(obj).forEach((key) => {
    params.append(key, obj[key]);
  });
  const { dataType=undefined, data={} } = obj;
  const header = {
    method: 'POST',
    headers: {
      Authorization:getToken()
    },
    // origin: 'http://dgut.zemcho.com',
    body: dataType==='json'?JSON.stringify(data):params,

  };
  // if (!IS_DEBUG) {
  //   header.credentials = 'include';
  // }
  fetch(url, header).then((response) => {
    if (response.ok) {
      return response.json();
    }
  }).then((json) => {
    if (json.code === 402 || json.code === 403 || json.code === 1002) { // 402表示未登录  403表示sessionId过期
      console.log(3)
      if (url.includes('/admin/')) {
        saveAdminInfo('');
        browserHistory.push('/loginpage');
      } else {
        saveAuthInfo('');
        browserHistory.push('/');
      }
    } else if (json.code === 404) {
      // 数据为空才报404
      // message.error(json.message)
      if (functionCallback !== null) {
        functionCallback();
      }
    } else {
      successCallback(json);
    }
  }).catch((error) => {
    failCallback(error);
  });
}