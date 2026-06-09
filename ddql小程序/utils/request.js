


import {
  showModal
} from './util'

export const BASE_URI = 'http://58.252.2.132:10192'
export const UPLOAD_IMG_BASE_URI = 'http://58.252.2.132:10192';
// export const BASE_URI = 'https://api.dongqilai.cc'
// export const UPLOAD_IMG_BASE_URI = 'https://api.dongqilai.cc';

export const locationKey = 'VHSBZ-IQRLI-CQ6GL-UGCBD-OFTEK-AFFCL'

export let commonHeader = {
  Authorization: wx.getStorageSync("token")
}

function request({

  method,
  url,
  data,
  header,
  resolve = _ => { },
  reject = _ => { }
}) {
  wx.request({
    url: BASE_URI + url,
    data: data,
    header: {
      token: wx.getStorageSync("token")
    },
    method,
    success: (result) => {
      if (result.data && result.data.code == 10000 || url == '/ddql/wechat/cas/login') {
        resolve(result.data, result.header)
      } else {
        wx.hideLoading()
        let {
          code,
          msg
        } = result.data;
        if (code == 10003 && msg == "token验证失败") {
          setTimeout(_ => {
            const pages = getCurrentPages()
            const isLoginPage = pages.length > 0 &&
              pages[pages.length - 1].route === 'pages/login/login';
            if (!isLoginPage) {
              wx.navigateTo({
                url: '/pages/login/login',
              })
            }
            wx.clearStorageSync();
            getApp().globalData.userInfo = {};
            commonHeader.Authorization = '';
          }, 1000)
        } else if ((code || msg) && msg != '您暂未绑定手机号，请先绑定手机' && url != '/ddql/wechat/user/check_in/cancel') {
          showModal({
            title: '请求错误',
            content: `${msg}`,
            success: (res) => {
              if (msg == '您暂未实名认证，请先实名认证' && res.confirm) {
                console.log(223)
                wx.navigateTo({
                  url: '/pages/verifyName/verifyName',
                })
              }
            }
          })
        }
        reject(result.data)
      }
    },
    fail: (res) => {
      wx.hideLoading()
      console.log(res)
      showModal({
        title: '请求错误',
        content: '请求失败！请检查设备网络或联系管理员。' + JSON.stringify(res)
      })
    },
    complete: (res) => {
      if (res.statusCode == 500) {
        wx.showToast({
          title: '服务器异常',
          icon: 'error'
        })
      }
    },
  })
}

// 上传图片
export function uploadImages(filePath, resolve = _ => { }, reject = _ => { }) {
  wx.uploadFile({
    url: UPLOAD_IMG_BASE_URI + '/ddql/wechat/common/file/upload',
    filePath,
    name: 'file',
    timeout: 60000,
    header: {
      token: wx.getStorageSync("token")
    },
    success: (result) => {
      if (result.statusCode !== 200) {
        wx.showToast({
          title: '上传失败',
          icon: 'none'
        })
        return
      }
      try {
        result.data = JSON.parse(result.data)
      } catch (err) {
        result.data = {}
      }
      if (result.data && result.data.code == 10000) {
        resolve(result.data)
      } else {
        wx.hideLoading()
        let {
          code,
          message
        } = result.data
        if (code == 400) {
          reject(result.data)
          return
        }
        if (code == 40099) {
          wx.clearStorageSync();
          getApp().globalData.userInfo = {};
          commonHeader.Authorization = '';
          setTimeout(_ => {
            jumpTo('/pages/login/login')
          }, 1000)
        } else if (code || msg) {
          showModal({
            title: '请求错误',
            content: `${code}: ${msg}`
          })
        }
        reject(result.data)
      }
    },
    fail: (res) => {
      console.log(res)
      wx.hideLoading()
      showModal({
        title: '请求错误',
        content: '请求失败！请检查设备网络或联系管理员。' + JSON.stringify(res)
      })
    },
    complete: (res) => {
      console.log('res', res)
    },
  })
}

// 手机号登录
export function Login(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/cas/loginByPhone',
    data,
    resolve,
    reject
  })
}

// 绑定手机号
export function bindPhone(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/cas/bindPhone',
    data,
    resolve,
    reject
  })
}

// 获取手机验证码
export function verificationCode(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/common/sms/code',
    data,
    resolve,
    reject
  })
}

//微信登录
export function wechatLogin(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/cas/login',
    data,
    resolve,
    reject
  })
}

// 实名认证
export function certification(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/cas/user/certification',
    data,
    resolve,
    reject
  })
}

//退出登录
export function loginOut(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/cas/logout',
    data,
    resolve,
    reject
  })
}

// 获取用户今日运动信息
export function todaySportInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/today/sport/info',
    data,
    resolve,
    reject
  })
}

// 更新用户今日步数
export function setStepNum(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/today/step_num/set',
    data,
    resolve,
    reject
  })
}

// 查询用户关联的所属团队
export function selectUserTeams(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/selectUserTeams',
    data,
    resolve,
    reject
  })
}

// 创建团体
export function addTeams(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/add',
    data,
    resolve,
    reject
  })
}

// 创建团体
export function updateTeams(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/update',
    data,
    resolve,
    reject
  })
}

// 根据当前地区id查询下级地区
export function nextAreaInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/region/selectLowRegions',
    data,
    resolve,
    reject
  })
}

// 删除团体成员
export function deleteTeamUser(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/deleteTeamUser',
    data,
    resolve,
    reject
  })
}

// 查询团队成员列表
export function teamUserList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/user/list',
    data,
    resolve,
    reject
  })
}

// 查询团队成员列表
export function invitationCode(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/invitation/qr/code',
    data,
    resolve,
    reject
  })
}

// 获取团体下拉列表
export function teamLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/common/team/lists',
    data,
    resolve,
    reject
  })
}

// 修改团队成员信息
export function teamInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/info',
    data,
    resolve,
    reject
  })
}

// 修改团队成员信息
export function updateTeamUser(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/user/update',
    data,
    resolve,
    reject
  })
}

// 新增加入团队审核的记录
export function saveTeamApplicationRecord(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/saveTeamApplicationRecord',
    data,
    resolve,
    reject
  })
}

// 查询团体验证记录
export function selectVerificationRecordList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/selectVerificationRecord',
    data,
    resolve,
    reject
  })
}

// 添加团体资质认证记录
export function addVerificationRecord(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/addVerificationRecord',
    data,
    resolve,
    reject
  })
}

// 更新团体资质验证记录
export function updateVerificationRecord(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/updateVerificationRecord',
    data,
    resolve,
    reject
  })
}

// 查询团队打卡设置
export function selectCheckInSettings(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/teamCheckInSettings/select',
    data,
    resolve,
    reject
  })
}


// 更新团队打卡设置
export function updateCheckInSettings(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/teamCheckInSettings/update',
    data,
    resolve,
    reject
  })
}

// 获取用户是管理员的关联的场所
export function selectUserPlaces(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/checkInPlace/selectUserPlace',
    data,
    resolve,
    reject
  })
}

// 获取用户是管理员的关联的场所
export function updateUserPlaces(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/checkInPlace/update',
    data,
    resolve,
    reject
  })
}

// 获取用户打卡排行榜列表
export function checkInRankLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/check_in/rank/lists',
    data,
    resolve,
    reject
  })
}

//获取当前用户详细信息
export function userInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/cas/user/info',
    data,
    resolve,
    reject
  })
}

// 更新用户信息
export function updateUserInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/cas/user/update',
    data,
    resolve,
    reject
  })
}

// 获取用户提现排行榜列表
export function withdrawalRankLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/withdrawal/rank/lists',
    data,
    resolve,
    reject
  })
}

//发送图形验证码
export function captchaCode(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/common/captcha/code',
    data,
    resolve,
    reject
  })
}

//获取打卡场地列表
export function placeList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/place/lists',
    data,
    resolve,
    reject
  })
}

//用户打卡
export function userCaheckIn(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/check_in',
    data,
    resolve,
    reject
  })
}

//获取用户打卡详情
export function checkInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/check_in/info',
    data,
    resolve,
    reject
  })
}

//获取用户打卡列表
export function checkInList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/check_in/list',
    data,
    resolve,
    reject
  })
}
// 获取充值配置信息
export function rechargeConfig(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/recharge/config',
    data,
    resolve,
    reject
  })
}

//获取用户打卡统计信息
export function checkInCount(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/check_in/count',
    data,
    resolve,
    reject
  })
}
// 获取充值活动列表
export function rechargeLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/recharge/activity/lists',
    data,
    resolve,
    reject
  })
}

// 添加充值订单
export function rechargeOrder(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/recharge/order/add',
    data,
    resolve,
    reject
  })
}

// 获取充值订单详情
export function rechargeOrderInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/recharge/order/info',
    data,
    resolve,
    reject
  })
}

// 获取充值订单支付配置信息
export function rechargeOrderPayConfig(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/recharge/order/pay/config',
    data,
    resolve,
    reject
  })
}

// 获取充值订单列表
export function rechargeOrderLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/recharge/order/lists',
    data,
    resolve,
    reject
  })
}

// 查询团队申请记录
export function selectTeamApplicationRecord(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/selectTeamApplicationRecord',
    data,
    resolve,
    reject
  })
}

// 查询团队申请记录
export function updateTeamApplicationRecord(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/updateTeamApplicationRecord',
    data,
    resolve,
    reject
  })
}

// 获取充值订单统计数据
export function rechargeOrderCount(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/recharge/order/count',
    data,
    resolve,
    reject
  })
}

// 获取打卡店铺列表
export function shopList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/lists',
    data,
    resolve,
    reject
  })
}

// 获取币变更日志列表
export function coinList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/coin/log/lists',
    data,
    resolve,
    reject
  })
}
// 获取设备信息
export function equipmentInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/equipment/info',
    data,
    resolve,
    reject
  })
}

// 获取商品分类列表
export function categoryLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/category/lists',
    data,
    resolve,
    reject
  })
}

// 获取商品列表
export function productLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/lists',
    data,
    resolve,
    reject
  })
}

// 获取商品详情
export function productInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/info',
    data,
    resolve,
    reject
  })
}

// 商品兑换
export function productExchange(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/exchange',
    data,
    resolve,
    reject
  })
}

// 获取商品兑换列表
export function productExchangeLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/exchange/lists',
    data,
    resolve,
    reject
  })
}

// 获取商品兑换详情
export function productExchangeInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/exchange/info',
    data,
    resolve,
    reject
  })
}

// 商品兑换退货申请
export function productExchangeRefund(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/exchange/refund',
    data,
    resolve,
    reject
  })
}

// 兑换券码核销
export function productExchangeTicketCheck(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/exchange/ticket/check',
    data,
    resolve,
    reject
  })
}

// 商品兑换订单支付配置
export function productExchangeOrderPayConfig(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/exchange/order/pay/config',
    data,
    resolve,
    reject
  })
}

// 新增收货地址
export function addAddress(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/deliveryAddress/add',
    data,
    resolve,
    reject
  })
}

// 编辑收货地址
export function updateAddress(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/deliveryAddress/update',
    data,
    resolve,
    reject
  })
}

// 查询收货地址
export function selectAddress(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/deliveryAddress/select',
    data,
    resolve,
    reject
  })
}

// 删除收货地址
export function deleteAddress(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/deliveryAddress/delete',
    data,
    resolve,
    reject
  })
}
// 获取用户打卡中的数据
export function runningInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/check_in/running/info',
    data,
    resolve,
    reject
  })
}

// 用户提现
export function userwithdrawal(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/withdrawal',
    data,
    resolve,
    reject
  })
}

// 用户提现
export function userwithdrawalInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/withdrawal/info',
    data,
    resolve,
    reject
  })
}

// 安全配置
export function sysConfig(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/system/basic/config',
    data,
    resolve,
    reject
  })
}

// 用户提现
export function checkInSetting(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/check_in/setting',
    data,
    resolve,
    reject
  })
}


// 用户提现
export function getPlaceInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/place/info',
    data,
    resolve,
    reject
  })
}


// 查询用户消息公告
export function messageAnnouncement(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/messageAnnouncement/select',
    data,
    resolve,
    reject
  })
}


// 已读用户消息公告
export function messageAnnouncementRead(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/messageAnnouncement/read',
    data,
    resolve,
    reject
  })
}

// 获取用户端--文章列表
export function articleList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/common/article/list',
    data,
    resolve,
    reject
  })
}

// 兑换券码核销
export function ticketCheck(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/product/exchange/ticket/check',
    data,
    resolve,
    reject
  })
}

// 修改团队中用户类型
export function updateTeamUserType(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/updateTeamUserType',
    data,
    resolve,
    reject
  })
}


// 前台获取首页轮播图
export function showList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/homePage/banner/showList',
    data,
    resolve,
    reject
  })
}


// 前台获取首页轮播图
export function noticeList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/common/notice/list',
    data,
    resolve,
    reject
  })
}

// 前台获取首页轮播图
export function placeEquipment(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/place/equipment',
    data,
    resolve,
    reject
  })
}


//获取场所打卡记录列表
export function checkInPlaceList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/place/check_in/list',
    data,
    resolve,
    reject
  })
}

//获取商圈下的店铺列表
export function shangquanshopList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/circle/shop/list',
    data,
    resolve,
    reject
  })
}


//获取商圈信息
export function circleInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/circle/info',
    data,
    resolve,
    reject
  })
}


//删除团队
export function teamDelete(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/delete',
    data,
    resolve,
    reject
  })
}

//一键授权绑定手机
export function bindWechatPhone(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/cas/bindWechatPhone',
    data,
    resolve,
    reject
  })
}

//店铺点击次数+1
export function clickcountInc(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/click_count/inc',
    data,
    resolve,
    reject
  })
}


//用户提现数据导出到邮箱
export function withdrawalExport(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/withdrawal/export',
    data,
    resolve,
    reject
  })
}
//用户打卡数据导出到邮箱
export function checkiinexport(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/check_in/export',
    data,
    resolve,
    reject
  })
}

//获取打卡类型列表
export function checkiinType(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/common/check_in_type/list',
    data,
    resolve,
    reject
  })
}

//取消用户打卡
export function checkiincancel(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/user/check_in/cancel',
    data,
    resolve,
    reject
  })
}

//充值订单数据导出到邮箱
export function orderExport(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/recharge/order/export',
    data,
    resolve,
    reject
  })
}


//获取商家列表
export function consumptionList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/consumption/list',
    data,
    resolve,
    reject
  })
}


//查询商家详情
export function getconsumption(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/consumption/get',
    data,
    resolve,
    reject
  })
}

//获取行业类别列表
export function getIndustry(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/common/industry/list',
    data,
    resolve,
    reject
  })
}

//提交商家审核
export function shangjiasubmit(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/audit/submit',
    data,
    resolve,
    reject
  })
}

//获取商圈下拉列表
export function getSuper(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/common/circle/list',
    data,
    resolve,
    reject
  })
}

// 获取团队部门列表
export function getDepartmentList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/department/list',
    data,
    resolve,
    reject
  })
}


//获取商圈下拉列表
export function ruzhuAuditList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/audit/list',
    data,
    resolve,
    reject
  })
}
// 创建部门
export function createDepartment(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/department/create',
    data,
    resolve,
    reject
  })
}

//获取商圈下拉列表
export function getshopaudit(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/audit/get',
    data,
    resolve,
    reject
  })
}

// 更新部门
export function updateDepartment(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/department/update',
    data,
    resolve,
    reject
  })
}

//获取商家管理首页数据
export function merchantAdmin(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/audit/check/merchant/admin',
    data,
    resolve,
    reject
  })
}

// 删除部门
export function deleteDepartment(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/department/delete',
    data,
    resolve,
    reject
  })
}



// 获取部门成员
export function getDepartmentUserList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/department/user/list',
    data,
    resolve,
    reject
  })
}

// 设置单人部门
export function setUserDepartment(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/department/setUserDepartment',
    data,
    resolve,
    reject
  })
}

// 批量设置成员部门
export function batchSetUserDepartment(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/department/batchSetUserDepartment',
    data,
    resolve,
    reject
  })
}

// 查询门店订单列表
export function shopOrderList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/shopOrder/lists',
    data,
    resolve,
    reject
  })
}

// 查询门店订单详情
export function shopOrderInfo(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/personalCenter/shopOrder/info',
    data,
    resolve,
    reject
  })
}

// 团队是否是多部门
export function isOpenDepartment(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/isMultiDepartment',
    data,
    resolve,
    reject
  })
}

// 提交意见反馈
export function submitFeedback(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/feedback/submit',
    data,
    resolve,
    reject
  })
}

// 查询团队反馈列表
export function getFeedbackList(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/team/feedback/list',
    data,
    resolve,
    reject
  })
}

// 商家详情
export function shopDetail(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/audit/getDetail',
    data,
    resolve,
    reject
  })
}

// 生成收款二维码
export function generateQrcode(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/receipt/qrcode/generate',
    data,
    resolve,
    reject
  })
}

// 下载收款二维码
export function downloadQrcode(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/receipt/qrcode/download',
    data,
    resolve,
    reject
  })
}

// 根据商家Id查询用币规则数据
export function getcoinshop(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/business/shop/coin/get',
    data,
    resolve,
    reject
  })
}

// 新增用币规则
export function addcoinshop(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/business/shop/coin/add',
    data,
    resolve,
    reject
  })
}

// 编辑用币规则
export function editcoinshop(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/business/shop/coin/update',
    data,
    resolve,
    reject
  })
}

// 创建门店扫码支付订单
export function orderCreate(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/receipt/qrcode/order/create',
    data,
    resolve,
    reject
  })
}

// 获取扫码支付订单支付配置信息
export function orderPay(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/order/pay',
    data,
    resolve,
    reject
  })
}

// 获取用户总金币数
export function getUserGoldCoin(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/business/shop/coin/getUserGoldCoin',
    data,
    resolve,
    reject
  })
}

// 获取商家经营数据
export function shopBusinessData(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/detail/businessData',
    data,
    resolve,
    reject
  })
}

//查询商家交易记录
export function shoporderSelect(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/order/select',
    data,
    resolve,
    reject
  })
}

//订单退款
export function orderrefund(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/order/refund',
    data,
    resolve,
    reject
  })
}

// 商家结算记录列表
export function shopSettlementRecordLists(data, resolve = _ => { }, reject = _ => { }) {
  request({
    method: 'post',
    url: '/ddql/wechat/shop/settlement_record/lists',
    data,
    resolve,
    reject
  })
}

// 注销商家
export function shopStatus(data, resolve = _ => { }, reject = _ => { }) {
  request({ method: 'post', url: '/ddql/wechat/shop/rule/shopStatus', data, resolve, reject })
}
// 根据电话查询商家管理人员
export function managerSearch(data, resolve = _ => { }, reject = _ => { }) {
  request({ method: 'post', url: '/ddql/wechat/shop/rule/select', data, resolve, reject })
}
// 设置管理员为店长
export function managerUpdate(data, resolve = _ => { }, reject = _ => { }) {
  request({ method: 'post', url: '/ddql/wechat/shop/rule/update', data, resolve, reject })
}
// 新增商家管理人员
export function managerAdd(data, resolve = _ => { }, reject = _ => { }) {
  request({ method: 'post', url: '/ddql/wechat/shop/rule/add', data, resolve, reject })
}
// 删除商家管理人员
export function managerDel(data, resolve = _ => { }, reject = _ => { }) {
  request({ method: 'post', url: '/ddql/wechat/shop/rule/delete', data, resolve, reject })
}
// 根据商家ID查询管理人员
export function managerList(data, resolve = _ => { }, reject = _ => { }) {
  request({ method: 'post', url: '/ddql/wechat/shop/rule/get', data, resolve, reject })
}
// 修改商家合同照片
export function shopContract(data, resolve = _ => { }, reject = _ => { }) {
  request({ method: 'post', url: '/ddql/wechat/shop/contract', data, resolve, reject })
}
