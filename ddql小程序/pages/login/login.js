// pages/bea/login.js 
import {
  verificationCode,
  Login,
  userInfo,
  wechatLogin,
  bindPhone,
  sysConfig,
  bindWechatPhone
} from '../../utils/request'


Page({

  /**
   * 页面的初始数据
   */
  data: {
    isCounting: false,
    countdown: 60,
    bindPhone: true,
    showPrivacy: false,
  },
  getSysConfig() {
    sysConfig({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        wx.setStorageSync('sysConfig', data)
        this.setData({
          src: resd.data.find(i => i.key == 'login_page_pic').value,
        })
      }
    })
  },
  goBack(e) {
    if(e.currentTarget.dataset.type=='cancel'){
      wx.clearStorageSync()
    }
    // 判断上一个页面是否为order,是则加上全局参数
    const pages = getCurrentPages()
    const prevPage = pages[pages.length - 2] // 上一个页面实例
    const prevRoute = prevPage?.route // 上一个页面路由
    console.log(!prevPage)
    console.log(prevPage)
    if (!prevPage) {
      console.log(1111)
      wx.switchTab({
        url: '/pages/index/index',
      })
      return
    }
    // if(prevPage && prevRoute.includes('pages/order/order')){
    console.log(111)
    const app = getApp()
    app.globalData.isLoginBack = true
    // }
    wx.navigateBack({
      delta: 1,
    });
  },
  startCountdown: function () {
    this.setData({
      isCounting: true,
    })
    const timer = setInterval(() => {
      const newCountDown = this.data.countdown - 1
      this.setData({
        countdown: newCountDown,
      })
      if (newCountDown <= 0) {
        clearInterval(timer);
        this.setData({
          isCounting: false,
          countdown: 60,
        })
      }
    }, 1000)
  },
  open: function () {
    this.setData({
      showPrivacy: true,
    })
  },
  close: function () {
    this.setData({
      showPrivacy: false,
    })
  },
  // 校验手机号
  isValidPhone() {
    const {
      phone
    } = this.data
    if (!this.data.phone) {
      wx.showToast({
        title: '请输入手机号码',
        icon: 'error',
      })
      return false
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '请输入合法号码',
        icon: 'error',
      })
      return false
    }
    return true
  },
  // 获取验证码
  getVerificationCode: function () {
    if (!this.isValidPhone()) return
    if (this.data.isCounting) return

    verificationCode({
      type: 1, //小程序登录绑定手机号
      phone: this.data.phone,
    }, resd => {
      if (resd.code == 10000) {
        // 倒计时
        this.startCountdown()
      }
    })
  },
  // 微信登录
  quickLogin: async function (e,a) {
    wx.showLoading()
    this.setData({c:a})
    wx.requestSubscribeMessage({
      tmplIds: ['_s4-hLadE9vazuwgWZZOrXvVm6EJLF7RpcJCF0oWDxE', '0MUceUhZ3J_iBcVMOYQRqG9dZQiRUp1q5iTZGMn0-Lg', 'dOLxXPLViMotzzIMHUp3z5D5I5_W6b5RyoK3QFHo54Y'],
      fail: (res) => {
        console.log(res)
      }
    })
    // 获取临时登录的code
    wx.login({
      success: res => {
        if (res.code) this.setData({
          loginCode: res.code
        })
      }
    })
    // wx.showLoading({
    //   title: '加载中',
    // })
    // this.close()
    wx.getUserInfo({
      desc: '用于完善用户资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
      success: (res => {
        const {
          encryptedData,
          iv
        } = res;
        console.log(res)
        const params = {
          encryptedData,
          iv,
          code: this.data.loginCode
        }
        wechatLogin(params, (resd) => {
          if (resd.code == 10000) {
            wx.hideLoading()
            // wx.showToast({
            //   title: '登录成功',
            //   icon: 'success',
            //   duration: 3000,
            // })
            const phone = resd.data.phone
            // 已经绑定直接登录
            console.log(phone ,!a)
            if (phone && !a) {
              wx.setStorageSync('token', resd.data.token)
              wx.setStorageSync('sessionKey', resd.data.sessionKey)
              this.getUserInfo()
              return
            }
            wx.login({
              success: res => {
                if (res.code) this.setData({
                  loginCode: res.code
                })
              }
            })
            // 未绑定手机号，则跳转绑定页面
            this.setData({
              isSuccess: true,
              phone,
              token:resd.data.token,
              bindPhone: false
            })
            // wx.navigateTo({
            //   url: `/pages/bindPhone/bindPhone?login=login`,
            // })
          } else if (resd.code == 403) {
            wx.navigateTo({
              url: `/pages/bindAccount/bindAccount?ticket=${resd.data.ticket}`,
            })
          }
        })
      }),
      fail: (err) => {
        wx.hideLoading()
        console.log(err)
        this.setData({
          isLogin: false
        })
        wx.login({
          success: res => {
            if (res.code || true) {
              this.setData({
                code: res.code
              })
            }
          }
        })
      }
    })
  },
  getThePhoneNumber(e) {
    let {
      encryptedData,
      iv
    } = e.detail
    console.log(e.detail)
    if (!encryptedData || !iv) {
      showModal({
        content: '注册失败，请您同意授权获取手机号码！',
        showCancel: false
      })
      return
    }
    wx.checkSession({
      success: result => {
        wx.setStorageSync('token', this.data.token)
        console.log(result)
        bindWechatPhone({
            code: this.data.loginCode,
            encryptedData,
            iv
          },
          res => {
            if (res.code == 10000) {
              wx.showToast({
                title: res.msg,
                icon: 'success'
              })
              this.getUserInfo()
            }
          },
          err => {
            if (err.code == 400) wx.login({
              success: res => {
                if (res.code) this.setData({
                  loginCode: res.code
                })
                this.getThePhoneNumber(e)
              }
            })
          }
        )
      },
      fail: res => {
        console.log(res)
        wx.login({
          success: res => {
            if (res.code) this.setData({
              loginCode: res.code
            })
            this.getThePhoneNumber(e)
          }
        })
        // showModal({
        //   confirmText: '好的',
        //   content: '登录态已过期，请点击重新授权！',
        //   showCancel: false
        // })
      }
    })
  },
  inputChange: function (e) {
    this.setData({
      [e.target.dataset.type]: e.detail.value
    })
  },
  // 登录
  submit: function () {
    const {
      code,
      phone
    } = this.data
    if (!this.isValidPhone()) return
    if (!code) {
      wx.showToast({
        title: '请输入验证码',
        icon: 'error',
      })
      return
    }
    // 没有绑定手机号，验证码绑定手机号
    if (!this.data.bindPhone) {
      bindPhone({
        code,
        phone,
      }, rs => {
        if (rs.code == 10000) {
          this.getUserInfo()
        }
      })
      return
    }
    // 绑定成功后，登录
    Login({
      code,
      phone
    }, resd => {

      if (resd.code == 10000) {

        wx.showToast({
          title: '登录成功',
          icon: 'success',
        })
        wx.setStorageSync('token', resd.data.token)
        // 获取用户信息
        this.getUserInfo()
      }
    })
  },

  getUserInfo: function () {
    userInfo({}, re => {
      if (re.code == 10000) {
        wx.setStorageSync('userinfo', re.data)
        // 获取页面路由栈信息
        const pages = getCurrentPages();
        // 判断是否有上一页（是否从其他页面跳转过来）
        if (pages.length >= 2) {
          // 有上一页，返回上一页
          const app = getApp()
          app.globalData.centerFlage = app.globalData.scene ? true : false
          wx.navigateBack({
            delta: pages.length
          });
        } else {
          // 没有上一页，跳转到首页
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/index/index',
            });
          }, 600);
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getSysConfig()
    // console.log(wx.onNeedPrivacyAuthorization)
    // if (wx.onNeedPrivacyAuthorization) {
    //   wx.onNeedPrivacyAuthorization(resolve => {
    //     console.log(1111)
    //     // this.selectComponent('#agreement').show();
    //     this.setData({
    //       showPrivacy: true,
    //     })
    //     this.resolvePrivacyAuthorization = resolve
    //   })
    // }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },
  // clickRefuse() {
  //   this.resolvePrivacyAuthorization({
  //     buttonId: 'disagree-btn',
  //     event: 'disagree'
  //   })
  // },
  // handleAgreePrivacyAuthorization() {
  //   this.resolvePrivacyAuthorization({
  //     buttonId: 'agree-btn',
  //     event: 'agree'
  //   })
  // },
  /**
   * 生命周期函数--监听页面显示
   */
  onChooseAvatar: function () {
    console.log(11)
  },
  onShow() {
    this.quickLogin('c','s')
    // wx.requirePrivacyAuthorize({
    //   success: () => {
    //     // 用户同意授权
    //     // 继续小程序逻辑
    //     console.log('同意')
    //   },
    //   fail: () => {}, // 用户拒绝授权
    //   complete: () => {}
    // })
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})