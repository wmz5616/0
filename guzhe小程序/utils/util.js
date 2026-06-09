const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return `${[year, month, day].map(formatNumber).join('/')} ${[hour, minute, second].map(formatNumber).join(':')}`
}
const allowedTypes = ['.jpg', '.jpeg', '.png'];
const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

const validatorsAddress = {
  name: (val) => {
    console.log(val)
    if (!val) return '姓名不能为空';
    return '';
  },
  phone: (val) => {
    const phoneRegex = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
    if (!val || val.trim() === '') return '手机号不能为空';
    if (!phoneRegex.test(val)) return '请输入正确的手机号码格式';
    return '';
  },
  regionId: (val) => {
    if (!val) return '地区不能为空';
    return '';
  },
  address: (val) => {
    if (!val) return '请填写详细地址';
    return '';
  },
};

const validateFieldAddress = (data) => {
  for (let item in data) {
    const fn = validatorsAddress[item]
    if (fn && fn(data[item], data)) {
      return fn(data[item], data)
    }
  }
  return '';
}

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
    confirmColor: "#8153FC",
    confirmText: confirmText,
    cancelText: cancelText,
    showCancel: showCancel,
    success,
    fail,
    complete,
  });
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

const tabPath = ['/pages/index/index','/pages/lottery/lottery','/pages/merchant/merchant','/pages/userCenter/userCenter']

module.exports = {
  showModal,
  formatTime,
  allowedTypes,
  validateFieldAddress,
  showModal,
  debounce,
  tabPath
}