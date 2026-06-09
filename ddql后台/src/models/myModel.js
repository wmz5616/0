import { message } from 'antd';
import { history, connect, Link } from 'umi';
import _ from 'lodash';
import * as myServices from '../services/myServices';
export default {
  // 命名空间
  namespace: 'myModel',
  // 初始值
  state: {
    systemConfig:{}
    // 数据清洗列表
  },
  // 用于处理异步操作和业务逻辑，不直接修改 state。由 action 触发，可以触发 action，可以和服务器交互，可以获取全局 state 的数据等等
  effects: {
    // 公用方法
    // 通用方法
    *getSetData({ payload, dataName = '', url, method, myData=_=>{}}, { call, put }) {
      try {
        let data;
        if (method !== 'GET') {
          // POST
          data = yield call(myServices.commonServices, payload, url, method);
          if (data && data.code === 10000) {
            if (dataName !== '') {
              yield put({ type: 'save', payload: { [`${dataName}`]: data.data } });
            } else {
              // message.success(data.message);
              // Toast.success(data.message);
            
              if (data.code === 40002) {
                window.localStorage.clear();
                history.push('/user/login');
              }

              if (data.code === 40099) {
                window.localStorage.clear();
                history.push('/user/login');
              }
            }
            if (myData && typeof myData === 'function') {
              myData(data);
            }
          } else {
            if (data.msg=="token验证失败") {
              window.localStorage.clear();
              history.push('/user/login');
              return
            }
            // 错误信息
            if (myData && typeof myData === 'function') {
              myData(data);
            }
          }
          return data;
        }
        if (method === 'GET') {
          const paramsArray = [];
          let url1 = url;
          // 拼接参数
          Object.keys(payload).forEach((key) => {
            if (payload[key] === 0 || payload[key]) {
              paramsArray.push(`${key}=${payload[key]}`);
            }
          });
          if (!_.isEmpty(payload)) {
            if (url1.search(/\?/) === -1) {
              url1 += `?${paramsArray.join('&')}`;
            } else {
              url1 += `&${paramsArray.join('&')}`;
            }
          }
          data = yield call(myServices.getServices, url1);
          // 成功信息
          if (data && data.code === 200) {
            if (myData && typeof myData === 'function') {
              myData(data);
            }
            return data;
          }else{
            // console.log(data.code,333)
            if (data.code === 40002) {
              // window.localStorage.clear();
              history.push('/user/login');
            }
            if (data.code === 40099) {
              window.localStorage.clear();
              history.push('/user/login');
            }
          }


          // 错误信息
          if (myData && typeof myData === 'function') {
            
            myData(data);
          }
        }
      } catch (e) {
        console.error(e);
        return false;
      }
    },
  },
  // 用于处理同步操作，唯一可以修改 state 的地方
  reducers: {
    // 6
    save(state, action) {
      return { ...state, ...action.payload };
    },
  },
};
