import {
  showModal
} from './util'
import moment from 'moment'

export const BASE_URI = 'http://58.252.2.132:10196'
export const UPLOAD_IMG_BASE_URI = 'http://58.252.2.132:10196';
// export const BASE_URI = 'https://guzhe.api.dongqilai.cc'
// export const UPLOAD_IMG_BASE_URI = 'https://guzhe.api.dongqilai.cc';

export const locationKey = 'VHSBZ-IQRLI-CQ6GL-UGCBD-OFTEK-AFFCL'

export let commonHeader = {
  Authorization: wx.getStorageSync("token")
}

const Export = ({
  fileName = '导出文件',
  method,
  url,
  data,
  header = {},
  resolve = _ => {},
  reject = _ => {},
  complete = _ => {},
}) => {
  wx.request({
    url: BASE_URI + url,
    method,
    data,
    header: {
      ...header,
      token: wx.getStorageSync('token'),
    },
    responseType: 'arraybuffer',
    success(res) {
      const filePath = `${wx.env.USER_DATA_PATH}/${fileName}${moment().valueOf()}.xlsx`
      const fs = wx.getFileSystemManager()
      console.log(res.data)

      // 写入arraybuffer 
      fs.writeFile({
        filePath,
        data: res.data,
        encoding: 'binary',
        success() {
          resolve(filePath)
        },
        fail(e) {
          wx.showToast({
            title: '下载失败',
            icon: 'error'
          })
          console.log(e)
        },
      })
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
      complete()
    },
  })
}

function request({
  method,
  url,
  data,
  header,
  resolve = _ => {},
  reject = _ => {}
}) {
  wx.request({
    url: BASE_URI + url,
    data: data,
    header: {
      token: wx.getStorageSync("token") || '',
    },
    method,
    success: (result) => {
      if (result.data && result.data.code == 10000 || url == '/guzhe/wechat/cas/login') {
        resolve(result.data, result.header)
      } else {
        wx.hideLoading()
        if (result.data) {
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
          } else if ((code || msg) && msg != '您暂未绑定手机号，请先绑定手机' && url != '/guzhe/wechat/user/check_in/cancel') {
            showModal({
              title: '提示',
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

export function uploadFile({
  url,
  filePath,
  resolve = _ => {},
  reject = _ => {}
}) {
  wx.uploadFile({
    url: UPLOAD_IMG_BASE_URI + url, // 仅为示例，非真实的接口地址
    filePath,
    name: 'file',
    timeout: 60000,
    header: {
      token: wx.getStorageSync("token")
    },
    success: (result) => {
      const res = JSON.parse(result.data)
      if (res && res.code == 10000) {
        resolve(result.data, result.header)
      } else {
        wx.hideLoading()
        let {
          code,
          msg
        } = res;
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
        } else if (code || msg) {
          showModal({
            title: '提示',
            content: `${msg}`,
            success: (res) => {
              console.log(11)
            }
          })
        }
        reject(res)
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
  });
}

// 商户上传图片
export function merchantuploadImages(filePath, resolve = _ => {}, reject = _ => {}) {
  wx.uploadFile({
    url: UPLOAD_IMG_BASE_URI + '/guzhe/wechat/merchant/image/upload',
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
          msg
        } = result.data
        if (code == 400) {
          reject(result.data)
          return
        }
        if (code == 10003) {
          wx.clearStorageSync();
          getApp().globalData.userInfo = {};
          commonHeader.Authorization = '';
          setTimeout(_ => {
            wx.redirectTo({
              url: '/pages/login/login',
            })
          }, 1000)
        } else if (code || msg) {
          showModal({
            title: '请求错误',
            content: `${code}: ${msg || '上传失败'}`
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

// 上传图片
export function uploadImages(filePath, resolve = _ => {}, reject = _ => {}) {
  wx.uploadFile({
    url: UPLOAD_IMG_BASE_URI + '/guzhe/wechat/common/file/upload',
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
        if (code == 10003) {
          wx.clearStorageSync();
          getApp().globalData.userInfo = {};
          commonHeader.Authorization = '';
          setTimeout(_ => {
            wx.redirectTo({
              url: '/pages/login/login',
            })
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

// 根据id进行商品分类排序
export function categorySort(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/category/sort',
    data,
    resolve,
    reject
  })
}

// 获取后台快捷入口配置显示
export function configShow(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/open/config/show',
    data,
    resolve,
    reject
  })
}

// 获取置顶公告
export function topnotice(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/open/top/notice',
    data,
    resolve,
    reject
  })
}

// 查询商品详情
export function productDetail(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/selectDetail',
    data,
    resolve,
    reject

  })

}

export function ex({
  fileName = '导出文件',
  method,
  url,
  data,
  header = {},
  resolve = _ => {},
  reject = _ => {},
  complete = _ => {},
}) {
  wx.request({
    url: BASE_URI + url,
    method,
    data,
    header: {
      ...header,
      token: wx.getStorageSync('token'),
    },
    responseType: 'arraybuffer',
    success(res) {
      const filePath = `${wx.env.USER_DATA_PATH}/${fileName}${moment().valueOf()}.xlsx`
      const fs = wx.getFileSystemManager()
      console.log(res.data)

      // 写入arraybuffer
      fs.writeFile({
        filePath,
        data: res.data,
        encoding: 'binary',
        success() {
          resolve(filePath)
        },
        fail(e) {
          wx.showToast({
            title: '下载失败',
            icon: 'error'
          })
          console.log(e)
        },
      })
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
      complete()
    },
  })
}

//更改库存
export function updateStock(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/updateStock',
    data,
    resolve,
    reject
  })
}

// 新增/编辑商品
export function productAdd(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/save',
    data,
    resolve,
    reject
  })
}

// 新增商品分类
export function categoryAdd(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/category/add',
    data,
    resolve,
    reject
  })
}

// 修改商品上架状态
export function shopUpdatestatus(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/update/status',
    data,
    resolve,
    reject
  })
}

// 小程序查询资质认证详情
export function qualificationDetail(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/qualification/detail',
    data,
    resolve,
    reject
  })
}

// 手机号登录
export function Login(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/loginByPhone',
    data,
    resolve,
    reject
  })
}

// 绑定手机号
export function bindPhone(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/bindPhone',
    data,
    resolve,
    reject
  })
}

// 获取手机验证码
export function verificationCode(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/common/sms/code',
    data,
    resolve,
    reject
  })
}

//微信登录
export function wechatLogin(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/login',
    data,
    resolve,
    reject
  })
}

// 实名认证
export function certification(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/user/certification',
    data,
    resolve,
    reject
  })
}

//退出登录
export function loginOut(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/logout',
    data,
    resolve,
    reject
  })
}

// 获取商家管理首页数据
export function merchantAdmin(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/check/merchant/admin',
    data,
    resolve,
    reject
  })
}

// 申请商家入驻提交
export function shangjiasubmit(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/submit',
    data,
    resolve,
    reject
  })
}

//获取行业类别
export function getIndustry(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/industry/get',
    data,
    resolve,
    reject
  })
}

// 获取已启用商超信息
export function getSuper(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/super/get',
    data,
    resolve,
    reject
  })
}

//查询申请入驻记录
export function ruzhuAuditList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/list',
    data,
    resolve,
    reject
  })
}

// 根据当前地区id查询下级地区
export function nextAreaInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/region/low/list',
    data,
    resolve,
    reject
  })
}

// 获取系统基础配置信息
export function basicConfig(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/open/basic/config',
    data,
    resolve,
    reject
  })
}

//获取商品订单列表
export function productorderList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/lists',
    data,
    resolve,
    reject
  })
}

//获取商品订单统计信息
export function productOrderStat(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/stat',
    data,
    resolve,
    reject
  })
}

// 根据id查询申请入驻记录详情
export function getshopaudit(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/get',
    data,
    resolve,
    reject
  })
}

//提交资质认证
export function qualificationSubmit(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/qualification/submit',
    data,
    resolve,
    reject
  })
}

// 新增商家管理者
export function managerAdd(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/manager/add',
    data,
    resolve,
    reject
  })
}

// 添加商品订单
export function productorderAdd(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/add',
    data,
    resolve,
    reject
  })
}

// 获取商家信息
export function getshopDetail(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/detail/get',
    data,
    resolve,
    reject
  })
}

//获取商家经营数据
export function businessDataDetail(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/detail/businessData',
    data,
    resolve,
    reject
  })
}

//查询商品
export function productSelectList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/select',
    data,
    resolve,
    reject
  })
}

// 删除商品
export function productDelete(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/delete',
    data,
    resolve,
    reject
  })
}

//查询商品分类列表
export function productClass(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/listCategory',
    data,
    resolve,
    reject
  })
}

//一键授权绑定手机
export function bindWechatPhone(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/bindWechatPhone',
    data,
    resolve,
    reject
  })
}



// 更新团队打卡设置
export function updateCheckInSettings(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/personalCenter/teamCheckInSettings/update',
    data,
    resolve,
    reject
  })
}

// 获取用户是管理员的关联的场所
export function selectUserPlaces(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/personalCenter/checkInPlace/selectUserPlace',
    data,
    resolve,
    reject
  })
}

// 获取用户是管理员的关联的场所
export function updateUserPlaces(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/personalCenter/checkInPlace/update',
    data,
    resolve,
    reject
  })
}

// 获取用户打卡排行榜列表
export function checkInRankLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/check_in/rank/lists',
    data,
    resolve,
    reject
  })
}

//获取当前用户详细信息
export function userInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/user/info',
    data,
    resolve,
    reject
  })
}

// 更新用户信息
export function updateUserInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/user/update',
    data,
    resolve,
    reject
  })
}

// 获取用户提现排行榜列表
export function withdrawalRankLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/withdrawal/rank/lists',
    data,
    resolve,
    reject
  })
}

//发送图形验证码
export function captchaCode(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/common/captcha/code',
    data,
    resolve,
    reject
  })
}

//获取打卡场地列表
export function placeList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/place/lists',
    data,
    resolve,
    reject
  })
}

//用户打卡
export function userCaheckIn(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/check_in',
    data,
    resolve,
    reject
  })
}

//获取用户打卡详情
export function checkInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/check_in/info',
    data,
    resolve,
    reject
  })
}

//获取用户打卡列表
export function checkInList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/check_in/list',
    data,
    resolve,
    reject
  })
}
// 获取充值配置信息
export function rechargeConfig(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/recharge/config',
    data,
    resolve,
    reject
  })
}

//获取用户打卡统计信息
export function checkInCount(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/check_in/count',
    data,
    resolve,
    reject
  })
}
// 获取充值活动列表
export function rechargeLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/recharge/activity/lists',
    data,
    resolve,
    reject
  })
}

// 添加充值订单
export function rechargeOrder(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/recharge/order/add',
    data,
    resolve,
    reject
  })
}

// 获取充值订单详情
export function rechargeOrderInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/recharge/order/info',
    data,
    resolve,
    reject
  })
}

// 获取充值订单支付配置信息
export function rechargeOrderPayConfig(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/recharge/order/pay/config',
    data,
    resolve,
    reject
  })
}

// 获取充值订单列表
export function rechargeOrderLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/recharge/order/lists',
    data,
    resolve,
    reject
  })
}

// 查询团队申请记录
export function selectTeamApplicationRecord(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/team/selectTeamApplicationRecord',
    data,
    resolve,
    reject
  })
}

// 查询团队申请记录
export function updateTeamApplicationRecord(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/team/updateTeamApplicationRecord',
    data,
    resolve,
    reject
  })
}

// 获取充值订单统计数据
export function rechargeOrderCount(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/recharge/order/count',
    data,
    resolve,
    reject
  })
}

// 获取打卡店铺列表
export function shopList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/lists',
    data,
    resolve,
    reject
  })
}

// 获取币变更日志列表
export function coinList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/coin/log/lists',
    data,
    resolve,
    reject
  })
}
// 获取设备信息
export function equipmentInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/equipment/info',
    data,
    resolve,
    reject
  })
}

// 获取商品分类列表
export function categoryLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/category/lists',
    data,
    resolve,
    reject
  })
}

// 获取商品列表
export function productLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/lists',
    data,
    resolve,
    reject
  })
}

// 获取商品详情
export function productInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/info',
    data,
    resolve,
    reject
  })
}

// 商品兑换
export function productExchange(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/exchange',
    data,
    resolve,
    reject
  })
}

// 获取商品兑换列表
export function productExchangeLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/exchange/lists',
    data,
    resolve,
    reject
  })
}

// 商品兑换退货申请
export function productExchangeRefund(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/exchange/refund',
    data,
    resolve,
    reject
  })
}

// 兑换券码核销
export function productExchangeTicketCheck(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/exchange/ticket/check',
    data,
    resolve,
    reject
  })
}

// 新增收货地址
export function addAddress(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/delivery_address/add',
    data,
    resolve,
    reject
  })
}

// 编辑收货地址
export function updateAddress(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/delivery_address/update',
    data,
    resolve,
    reject
  })
}

// 查询收货地址
export function selectAddress(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/delivery_address/list',
    data,
    resolve,
    reject
  })
}

// 删除收货地址
export function deleteAddress(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/delivery_address/delete',
    data,
    resolve,
    reject
  })
}
// 获取用户打卡中的数据
export function runningInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/check_in/running/info',
    data,
    resolve,
    reject
  })
}

// 用户提现
export function userwithdrawal(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/withdrawal',
    data,
    resolve,
    reject
  })
}

// 用户提现
export function userwithdrawalInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/withdrawal/info',
    data,
    resolve,
    reject
  })
}

// 安全配置
export function sysConfig(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/system/basic/config',
    data,
    resolve,
    reject
  })
}

// 用户提现
export function checkInSetting(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/check_in/setting',
    data,
    resolve,
    reject
  })
}


// 用户提现
export function getPlaceInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/place/info',
    data,
    resolve,
    reject
  })
}


// 查询用户消息公告
export function messageAnnouncement(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/personalCenter/messageAnnouncement/select',
    data,
    resolve,
    reject
  })
}


// 已读用户消息公告
export function messageAnnouncementRead(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/personalCenter/messageAnnouncement/read',
    data,
    resolve,
    reject
  })
}

// 获取用户端--文章列表
export function articleList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/article/list',
    data,
    resolve,
    reject
  })
}

// 文章详情
export function articleDetail(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/about/article/detail',
    data,
    resolve,
    reject
  })
}

// 修改团队中用户类型
export function updateTeamUserType(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/team/updateTeamUserType',
    data,
    resolve,
    reject
  })
}


// 前台获取首页轮播图
export function showList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/open/homePage',
    data,
    resolve,
    reject
  })
}


// 前台获取首页轮播图
export function noticeList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/notice/list',
    data,
    resolve,
    reject
  })
}

// 前台获取公告详情
export function noticeInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/notice/info',
    data,
    resolve,
    reject
  })
}

//获取后台banner图配置
export function bannerConfig(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/open/config/banner',
    data,
    resolve,
    reject
  })
}


//获取场所打卡记录列表
export function checkInPlaceList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/place/check_in/list',
    data,
    resolve,
    reject
  })
}


//获取打卡类型列表
export function checkiinType(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/check_in_type/list',
    data,
    resolve,
    reject
  })
}

//取消用户打卡
export function checkiincancel(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/user/check_in/cancel',
    data,
    resolve,
    reject
  })
}

//充值订单数据导出到邮箱
export function orderExport(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/recharge/order/export',
    data,
    resolve,
    reject
  })
}

// 根据屏幕店地址获取商超下拉列表
export function commonSupermarketList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/supermarket/lists',
    data,
    resolve,
    reject
  })
}
// 获取可租用店位列表
export function availableLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_rental/available/lists',
    data,
    resolve,
    reject
  })
}

// 屏幕店位租用
export function screenRent(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_rental/rent',
    data,
    resolve,
    reject
  })
}


// 获取店位订单列表
export function screenOrderList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_order/lists',
    data,
    resolve,
    reject
  })
}

// 获取店位订单详情
export function screenOrderInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_order/info',
    data,
    resolve,
    reject
  })
}


// 获取是否展示屏幕店租用合约
export function contractStatus(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_order/contract/status',
    data,
    resolve,
    reject
  })
}


// 获取屏幕店租用合约
export function contractContent(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_order/contract/content',
    data,
    resolve,
    reject
  })
}

// 获取商品订单列表
export function productOrder(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/lists',
    data,
    resolve,
    reject
  })
}

// 获取商品订单统计信息
export function shopProductOrderStat(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/stat',
    data,
    resolve,
    reject
  })
}

// 导出商品订单数据
export function productOrderExport(fileName, data, resolve = _ => {}, reject = _ => {}) {
  Export({
    fileName,
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/export',
    data,
    resolve,
    reject
  })
}

// 商品订单--未发货数据导出
export function unDispatchedExport(fileName, data, resolve = _ => {}, reject = _ => {}) {
  Export({
    fileName,
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/un_dispatched/export',
    data,
    resolve,
    reject
  })
}

// 商品订单--导入物流单号
export function expressImport(filePath, resolve = _ => {}, reject = _ => {}) {
  uploadFile({
    url: '/guzhe/wechat/shop/product_order/express_no/import',
    filePath,
    resolve,
    reject
  })
}

// 商品订单--退款
export function productOrderRefund(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/refund',
    data,
    resolve,
    reject
  })
}

// 获取商品订单详情
export function productOrderInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/info',
    data,
    resolve,
    reject
  })
}

// 管理员核销
export function ticketCheck(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/ticket/check',
    data,
    resolve,
    reject
  })
}

// 获取商家管理者列表
export function managerList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/manager/list',
    data,
    resolve,
    reject
  })
}


// 删除商家管理者
export function managerDel(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/manager/del',
    data,
    resolve,
    reject
  })
}

// 更新商品订单基础信息
export function updateProductOrder(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/base/update',
    data,
    resolve,
    reject
  })
}

// 更新商品订单基础信息
export function productOrderPay(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/pay/config',
    data,
    resolve,
    reject
  })
}

// 获取入驻前须知
export function merchantNotice(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/merchant/notice',
    data,
    resolve,
    reject
  })
}

// 编辑商家信息
export function updateDetail(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/detail/update',
    data,
    resolve,
    reject
  })
}

// 编辑商家信息
export function cancelProductOrder(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/cancel',
    data,
    resolve,
    reject
  })
}


// 修改商家合同照片
export function shopContract(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/contract',
    data,
    resolve,
    reject
  })
}

// 修改商家海报照片
export function posterUpdate(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/poster/update',
    data,
    resolve,
    reject
  })
}

// 商品订单退款申请
export function productOrderRefundd(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/refund',
    data,
    resolve,
    reject
  })
}


// 获取商品订单商品信息--目前主要用于下单失败前端渲染基础信息
export function EOrderInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product_order/product/info',
    data,
    resolve,
    reject
  })
}

// 获取用户下拉列表
export function userLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/user/lists',
    data,
    resolve,
    reject
  })
}

// 获取商家总退款审核订单数
export function totalAuditCount(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/audit/count/get',
    data,
    resolve,
    reject
  })
}

// 通过微信更新手机号
export function phonebywechat(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/cas/update/phone_by_wechat',
    data,
    resolve,
    reject
  })
}

// 获取结算记录
export function settlementRecordLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/settlement_record/lists',
    data,
    resolve,
    reject
  })
}

//导出券码
export function ticketexport(data, resolve = _ => {}, reject = _ => {}) {
  Export({
    method: 'post',
    url: '/guzhe/wechat/product/ticket/export',
    data,
    resolve,
    reject
  })
}

// 获取商家总退款审核订单数
export function refundLists(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/refund/apply/lists',
    data,
    resolve,
    reject
  })
}
// 获取商家总退款审核订单数
export function refundInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/refund/apply/info',
    data,
    resolve,
    reject
  })
}
// 获取商家总退款审核订单数
export function auditRefund(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/shop/product_order/refund/audit',
    data,
    resolve,
    reject
  })
}

// 更新商品订单展示内容
export function updateScreenOrder(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_order/display/update',
    data,
    resolve,
    reject
  })
}

// 发起设备截图任务
export function screenshotTask(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_order/screenshot/task',
    data,
    resolve,
    reject
  })
}


// 发起设备截图任务
export function screenshotInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/screen_order/screenshot/info',
    data,
    resolve,
    reject
  })
}

// 商户图片上传
export function merchantUpload(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/merchant/image/upload',
    data,
    resolve,
    reject
  })
}

// 获取商户信息
export function merchantInfo(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/merchant/info',
    data,
    resolve,
    reject
  })
}

// 添加商户
export function merchantAdd(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/merchant/add',
    data,
    resolve,
    reject
  })
}

// 编辑商户
export function merchantUpdate(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/merchant/update',
    data,
    resolve,
    reject
  })
}

// 根据银行代码查询银行代码和银行名称下拉列表
export function bankList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/bank/lists',
    data,
    resolve,
    reject
  })
}

//条件查询联行号和联行名称下拉列表
export function banklinkList(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/common/bank/link/lists',
    data,
    resolve,
    reject
  })
}


//根据id删除商品分类
export function categorydelete(data, resolve = _ => {}, reject = _ => {}) {
  request({
    method: 'post',
    url: '/guzhe/wechat/product/category/delete',
    data,
    resolve,
    reject
  })
}