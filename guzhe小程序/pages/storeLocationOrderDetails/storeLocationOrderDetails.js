// pages/storeLocationOrderDetails/storeLocationOrderDetails.js
import {
  duration
} from 'moment'
import {
  screenOrderInfo,
  updateScreenOrder,
  screenshotTask,
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    orderInfo: {},
    displayType: 0,
    typeList: {
      0: '/assets/images/daiqueding.svg',
      1: '/assets/images/daishengxiao.svg',
      2: '/assets/images/shengxiaozhon.svg',
      3: '/assets/images/yiwancheng.svg',
      4: '/assets/images/yibohui.svg',
      5: '/assets/images/yichexiao.svg',
    },
    isIOS: false,
  },
  selectSwitch(e) {
    const {
      type,
    } = e.currentTarget.dataset
    const {
      orderInfo
    } = this.data
    // 只有生效中和待生效才能修改
    if (orderInfo.status != 1 && orderInfo.status != 2) {
      wx.showToast({
        title: '生效中和待生效才能修改',
        icon: 'none',
        duration: 1000,
      })
      return
    }

    if (this.data.displayType == type) return

    this.setData({
      displayType: type,
    })
    this.getUpdateScreenOrder()
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  copy() {
    wx.setClipboardData({
      data: this.data.orderInfo.orderNo,
      success() {
        wx.showToast({
          title: '复制成功！',
          duration: 1000,
        })
      },
    })
  },
  getScreenshotTask() {
    const {
      id
    } = this.data
    wx.showLoading({
      title: '加载中',
    })
    screenshotTask({
      orderId: id,
    }, resd => {
      wx.hideLoading()
      wx.navigateTo({
        url: `/pages/screenPage/screenPage?id=${id}&screenshotId=${resd.data.screenshotId}`,
      })
    })
  },
  getScreenOrderInfo() {
    screenOrderInfo({
      orderId: this.data.id,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        this.setData({
          orderInfo: resd.data,
          displayType: data.displayType
        })
      }
    })
  },
  getUpdateScreenOrder() {
    wx.showLoading({
      title: '加载中',
    })
    updateScreenOrder({
      orderId: this.data.id,
      displayType: this.data.displayType,
    }, resd => {
      wx.showToast({
        title: '修改成功！',
      })
      this.getScreenOrderInfo()
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    this.setData({
      id: +options.id,
      isIOS: app.globalData.isIOS,
    })
    this.getScreenOrderInfo()
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