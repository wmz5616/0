const formatNumber = (num) => {
  if (Math.abs(num) >= 10000) {
    // 超过万，转换为万单位
    return (num / 10000).toFixed(2) + '万';
  }
  return num
}

function debounce(fn, delay = 300) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

const formatSecond = (seconds = 0) => {
  // 计算小时、分钟和秒
  const hrs = Math.floor(seconds / 3600);
  const mins = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;

  const format = (num) => num.toString().padStart(2, '0');

  return `${format(hrs)}:${format(mins)}:${format(secs)}`;
}
const mchId = '1728733634' //商户号
function showModal({
  content,
  title = "提示",
  showCancel = true,
  confirmText = "确定",
  cancelText = "取消",
  success = (_) => {},
  fail = (_) => {},
  complete = (_) => {},
}) {
  wx.showModal({
    title: title,
    content: content,
    cancelColor: "#333333",
    confirmColor: "#5DD5DE",
    confirmText: confirmText,
    cancelText: cancelText,
    showCancel: showCancel,
    success,
    fail,
    complete,
  });
}

const validators = {
  scanCodeTime: (val, record) => {
    if (!val) return '打卡时长不能为空';
    if (isNaN(Number(val))) return '打卡时长必须是有效数字';
    if (Number(val) <= 0) return '打卡时长必须大于0';
    return '';
  },
  scanCodeHealthyCoin: (val, record) => {
    console.log(typeof val)
    if (!val) return '扫码可得健康币数不能为空';
    if (isNaN(Number(val))) return '扫码可得健康币数必须是有效数字';
    if (Number(val) <= 0) return '扫码可得健康币数必须大于0';
    return '';
  },
  targetSteps: (val, record) => {
    if (record.stepsOpen) return
    if (!val) return '目标步数不能为空';
    if (isNaN(Number(val))) return '目标步数必须是有效数字';
    if (Number(val) <= 0) return '目标步数必须大于0';
    return '';
  },
  stepsHealthyCoin: (val, record) => {
    if (record.stepsOpen) return
    if (!val) return '行走可得健康币数不能为空';
    if (isNaN(Number(val))) return '行走可得健康币数必须是有效数字';
    if (Number(val) <= 0) return '行走可得健康币数必须大于0';
    return '';
  },
  lowestWithdrawalMoney: (val, record) => {
    if (!val) return '最低提现金额不能为空';
    if (isNaN(Number(val))) return '最低提现金额必须是有效数字';
    if (Number(val) <= 0) return '最低提现金额必须大于0';
    return '';
  },
  name: (val) => {
    console.log(val)
    if (!val) return '姓名不能为空';
    return '';
  },
  type: (val) => val == -1 ? '请选择团体' : '',
  contactPerson: (val) => {
    if (!val || val.trim() === '') return '联系人不能为空';
    if (val.length < 2 || val.length > 20) return '联系人长度应在2-20个字符之间';
    return '';
  },
  licenseImageList: (val) => {
    if (!val.length) return '请上传营业执照或法人证书'
    return ''
  },
  contactPhone: (val) => {
    const phoneRegex = /^((1[3-9]\d{9})|(0\d{2,3}-?\d{7,8})|(\d{7,8}))$/;
    if (!val || val.trim() === '') return '手机号不能为空';
    if (!phoneRegex.test(val)) return '请输入正确的手机号码格式';
    return '';
  },
  phone: (val) => {
    const phoneRegex = /^((1[3-9]\d{9})|(0\d{2,3}-?\d{7,8})|(\d{7,8}))$/;
    if (!val || val.trim() === '') return '手机号不能为空';
    if (!phoneRegex.test(val)) return '请输入正确的手机号码格式';
    return '';
  },
  contactEmail: (val) => {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (val && !emailRegex.test(val)) return '请输入正确的邮箱格式';
    return '';
  },
  regionId: (val) => {
    if (!val) return '地区不能为空';
    return '';
  },
  address: (val) => {
    if (!val) return '详细地址不能为空';
    return '';
  },
};

// 校验团体信息
const validateField = (data) => {
  for (let item in data) {
    const fn = validators[item]
    if (fn && fn(data[item], data)) {
      return fn(data[item], data)
    }
  }
  return '';
}

// 存放团体类型数组
const teamStatus = ['企事业单位', '政府部门', '家庭', '朋友']
const auditStatus = ['是', '否']
const sysList = ['version', 'miitbeian', 'org_name']

const CryptoJS = require('crypto-js');

function WXBizDataCrypt(appId, sessionKey) {
  this.appId = appId;
  this.sessionKey = sessionKey;
}

WXBizDataCrypt.prototype.decryptData = function (encryptedData, iv) {
  // 使用 CryptoJS 的 Base64 解码
  const sessionKeyBuffer = CryptoJS.enc.Base64.parse(this.sessionKey);
  const encryptedDataBuffer = CryptoJS.enc.Base64.parse(encryptedData);
  const ivBuffer = CryptoJS.enc.Base64.parse(iv);

  try {
    // 解密操作
    const decrypted = CryptoJS.AES.decrypt({
        ciphertext: encryptedDataBuffer
      },
      sessionKeyBuffer, {
        iv: ivBuffer,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
      }
    );

    // 将解密后的数据转换为字符串
    const decryptedText = decrypted.toString(CryptoJS.enc.Utf8);

    // 将字符串解析为 JSON 对象
    const decryptedData = JSON.parse(decryptedText);

    // 校验 appid
    if (decryptedData.watermark.appid !== this.appId) {
      throw new Error('Invalid appid');
    }

    return decryptedData;
  } catch (err) {
    console.error('解密失败:', err);
    throw new Error('Illegal Buffer');
  }
}

const allowedTypes = ['.jpg', '.jpeg', '.png'];

const withdrawalMessage = {
  ACCEPTED: '转账已受理',
  PROCESSING: '转账锁定资金中,请检查账户余额是否足够',
  WAIT_USER_CONFIRM: '待收款用户确认',
  TRANSFERING: '转账中',
  CANCELING: '转账撤销中',
  CANCELLED: '转账撤销完成'
}

module.exports = {
  showModal,
  teamStatus,
  validateField,
  formatSecond,
  WXBizDataCrypt,
  allowedTypes,
  formatNumber,
  sysList,
  auditStatus,
  withdrawalMessage,
  debounce
}