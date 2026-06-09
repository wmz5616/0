// pages/bindPhone/bindPhone.js
import {
  verificationCode,
  bindPhone,
  userInfo
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    isCounting: false,
    countdown: 60,
  },
  inputChange: function (e) {
    this.setData({
      [e.target.dataset.type]: e.detail.value
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
        wx.showToast({
          title: '获取成功!',
          icon: 'success'
        })
        // 倒计时
        this.startCountdown()
      }
    })
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
  // 绑定
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
    // wx.showLoading({
    //   title: '加载中',
    // })
    bindPhone({
      code,
      phone,
    }, rs => {
      if (rs.code == 10000) {
        wx.showToast({
          title: '绑定成功！',
          icon: 'success',
        })
        this.setData({
          phone: '',
          code: '',
        })
        this.getUserInfo()
        if (this.data.login) {
          // 获取页面路由栈信息
          const pages = getCurrentPages();
          // 判断是否有上一页（是否从其他页面跳转过来）
          if (pages.length >= 2) {
            // 有上一页，返回上一页
            wx.navigateBack({
              delta: 1
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
      }
    })
  },

  getUserInfo: function () {
    userInfo({}, re => {
      if (re.code == 10000) {
        wx.setStorageSync('userinfo', re.data)
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  onLoad(options) {
    this.setData({
      login: options.login
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

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