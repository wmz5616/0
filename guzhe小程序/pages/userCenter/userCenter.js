// pages/userCenter/userCenter.js
import {
  basicConfig,
} from '../../utils/request'
import {
  tabPath
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    token: ''
  },
  getSysConfig() {
    basicConfig({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        wx.setStorageSync('basicConfig', data)
        this.setData({
          version: data.find(i => i.key == 'version').value,
          miitbeian: data.find(i => i.key == 'miitbeian').value,
          org_name: '技术支持：' + '东莞市蓝睿网络科技有限公司',
        })
      }
    })
  },
  gologin: function () {
    if (wx.getStorageSync('token')) {
      return
    }
    wx.navigateTo({
      url: '/pages/login/login',
    })
  },
  jump(e) {
    const {
      url
    } = e.currentTarget.dataset
    if (url == '/pages/editUser/editUser' && !this.data.token) return
    if (tabPath.includes(url)) {
      wx.switchTab({
        url,
      })
      return
    }
    wx.navigateTo({
      url,
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

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
    this.setData({
      userInfo: wx.getStorageSync('userinfo'),
      token: wx.getStorageSync('token'),
    })
    const app = getApp()
    const isLoginBack = app.globalData.isLoginBack
    console.log(isLoginBack)
    if (isLoginBack) {
      app.globalData.isLoginBack = undefined
      return
    }
    this.getSysConfig()
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
    this.setData({
      token: wx.getStorageSync('token'),
    })
    const app = getApp()
    const isLoginBack = app.globalData.isLoginBack
    console.log(isLoginBack)
    if (isLoginBack) {
      app.globalData.isLoginBack = undefined
      return
    }
    this.getSysConfig()

    wx.stopPullDownRefresh()
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