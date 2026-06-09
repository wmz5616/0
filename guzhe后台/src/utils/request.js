/**
 * 封装 fetch 请求工具
 * 包含请求拦截器、响应拦截器和错误处理
 */
import { message, notification } from 'antd';
import { history } from 'umi';

// HTTP 状态码对应的消息
const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

// 请求控制器管理器
const pendingRequests = new Map();
let isCancellingAll = false;

/**
 * 生成请求唯一标识
 */
function generateRequestId(url, options) {
  return `${options.method || 'GET'}_${url}`;
}

/**
 * 添加请求到管理器
 */
function addPendingRequest(requestId, controller) {
  if (!isCancellingAll) {
    pendingRequests.set(requestId, controller);
  }
}

/**
 * 从管理器移除请求
 */
function removePendingRequest(requestId) {
  pendingRequests.delete(requestId);
}

/**
 * 取消所有正在进行的请求
 */
export function cancelAllRequests() {
  isCancellingAll = true;
  pendingRequests.forEach((controller, requestId) => {
    controller.abort();
    console.log(`请求已取消: ${requestId}`);
  });
  pendingRequests.clear();
  // 延迟重置标志，确保当前批次的请求都被阻止
  setTimeout(() => {
    isCancellingAll = false;
  }, 100);
}

// 请求拦截器列表
const requestInterceptors = [];
// 响应拦截器列表
const responseInterceptors = [];

/**
 * 添加请求拦截器
 * @param {Function} interceptor - 拦截器函数，接收请求配置，返回修改后的配置
 */
export function addRequestInterceptor(interceptor) {
  requestInterceptors.push(interceptor);
}

/**
 * 添加响应拦截器
 * @param {Function} interceptor - 拦截器函数，接收响应数据，返回修改后的数据或抛出错误
 */
export function addResponseInterceptor(interceptor) {
  responseInterceptors.push(interceptor);
}

/**
 * 执行请求拦截器
 * @param {Object} config - 请求配置
 * @returns {Object} 处理后的配置
 */
function executeRequestInterceptors(config) {
  let processedConfig = { ...config };
  for (const interceptor of requestInterceptors) {
    processedConfig = interceptor(processedConfig) || processedConfig;
  }
  return processedConfig;
}

/**
 * 执行响应拦截器
 * @param {Object} response - 响应数据
 * @returns {Object} 处理后的响应
 */
function executeResponseInterceptors(response) {
  let processedResponse = response;
  for (const interceptor of responseInterceptors) {
    processedResponse = interceptor(processedResponse) || processedResponse;
  }
  return processedResponse;
}

/**
 * 处理 HTTP 错误
 * @param {Response} response - fetch 响应对象
 * @returns {Response|{error: true, response: Response, message: string}}
 */
function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }
  const errorText =
    codeMessage[response.status] || `HTTP 错误: ${response.status}`;
  notification.error({
    message: `请求错误 ${response.status}: ${response.url}`,
    description: errorText,
  });
  // 不抛出异常，返回错误对象
  return {
    error: true,
    response,
    msg: errorText,
  };
}

/**
 * 解析响应数据
 * @param {Response|{error: boolean}} response - fetch 响应对象或错误对象
 * @param {Object} options - 请求配置
 */
function parseResponse(response, options = {}) {
  // 如果是错误对象，直接返回
  if (response && response.error) {
    return response;
  }

  // 如果显式要求返回 blob（二进制流）
  if (options.responseType === 'blob') {
    // 检查 Content-Type，如果后端返回的是 JSON（通常是错误信息），则解析为 JSON
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      return response.json();
    }
    return response.blob();
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json();
  }
  return response.text();
}

/**
 * 统一的请求方法（Promise 风格）
 * @param {string} url - 请求地址
 * @param {Object} options - 请求配置
 * @returns {Promise} 请求结果
 */
export function request(url, options = {}) {
  const requestUrl =
    !url.startsWith('http') && !url.startsWith('/guzhe') ? '/guzhe' + url : url;

  console.log(pendingRequests);
  // 如果正在取消所有请求，直接返回被拒绝的 Promise
  if (isCancellingAll) {
    return Promise.reject();
  }

  // 创建 AbortController 用于取消请求
  const controller = new AbortController();
  const requestId = generateRequestId(requestUrl, options);

  // 默认配置
  const defaultOptions = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      token: localStorage.getItem('token') || '',
    },
    signal: controller.signal, // 添加取消信号
  };

  // 合并配置
  let config = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers,
    },
  };

  // 如果外部传入了 signal，需要保持外部 signal 的能力
  // 但不覆盖我们的 controller.signal
  if (options.signal) {
    options.signal.addEventListener('abort', () => {
      controller.abort();
    });
  }

  // 执行请求拦截器
  config = executeRequestInterceptors(config);

  // 处理请求体
  let finalUrl = requestUrl;
  if (config.data) {
    if (config.method.toUpperCase() === 'GET') {
      // GET 请求将数据转换为查询字符串，过滤掉 undefined、null 和空字符串
      const filteredData = {};
      Object.keys(config.data).forEach((key) => {
        const val = config.data[key];
        if (val !== undefined && val !== null && val !== '') {
          filteredData[key] = val;
        }
      });
      const params = new URLSearchParams(filteredData).toString();
      finalUrl = finalUrl.includes('?')
        ? `${finalUrl}&${params}`
        : `${finalUrl}?${params}`;
    } else if (config.headers['Content-Type'] === 'application/json') {
      config.body = JSON.stringify(config.data);
    } else if (
      config.headers['Content-Type'] === 'application/x-www-form-urlencoded'
    ) {
      const params = new URLSearchParams();
      Object.keys(config.data).forEach((key) => {
        params.append(key, config.data[key]);
      });
      config.body = params.toString();
    } else {
      // 默认使用 FormData
      const params = new FormData();
      Object.keys(config.data).forEach((key) => {
        params.append(key, config.data[key]);
      });
      config.body = params;
      delete config.headers['Content-Type']; // 让浏览器自动设置 Content-Type
    }
  }

  addPendingRequest(requestId, controller);

  return fetch(finalUrl, config)
    .then(checkStatus)
    .then((res) => parseResponse(res, config))
    .then((data) => {
      removePendingRequest(requestId);

      if (data && data.error) {
        return data;
      }

      console.log(data);
      if (data.msg === 'token验证失败') {
        cancelAllRequests();
        // message.error(data.msg || '登录已过期，请重新登录');
        localStorage.removeItem('token');
        history.push('/user/login');
        return;
      }

      return executeResponseInterceptors(data);
    })
    .catch((error) => {
      // 从管理器移除请求
      removePendingRequest(requestId);

      // 如果是取消请求导致的错误，不显示错误信息
      if (error.name === 'AbortError') {
        console.log(`请求被取消: ${requestId}`);
        return;
      }

      if (error.name === 'TypeError' && error.message === 'Failed to fetch') {
        message.error('网络请求失败，请检查网络连接');
      }
      throw error;
    });
}

/**
 * GET 请求
 * @param {string} url - 请求地址
 * @param {Object} params - 查询参数
 * @param {Object} options - 其他配置
 */
export function get(url, params = {}, options = {}) {
  return request(url, {
    method: 'GET',
    data: params,
    ...options,
  });
}

/**
 * POST 请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求数据
 * @param {Object} options - 其他配置
 */
export function post(url, data = {}, options = {}) {
  return request(url, {
    method: 'POST',
    data,
    ...options,
  });
}

/**
 * PUT 请求
 * @param {string} url - 请求地址
 * @param {Object} data - 请求数据
 * @param {Object} options - 其他配置
 */
export function put(url, data = {}, options = {}) {
  return request(url, {
    method: 'PUT',
    data,
    ...options,
  });
}

/**
 * DELETE 请求
 * @param {string} url - 请求地址
 * @param {Object} params - 查询参数
 * @param {Object} options - 其他配置
 */
export function del(url, params = {}, options = {}) {
  return request(url, {
    method: 'DELETE',
    data: params,
    ...options,
  });
}

/**
 * 上传文件
 * @param {string} url - 上传地址
 * @param {File} file - 文件对象
 * @param {Object} options - 其他配置
 */
export function upload(url, file, options = {}) {
  const formData = new FormData();
  formData.append('file', file);

  return request(url, {
    method: 'POST',
    data: formData,
    headers: {},
    ...options,
  });
}

export default request;
