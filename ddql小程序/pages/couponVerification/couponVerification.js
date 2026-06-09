// pages/couponVerification/couponVerification.js
import {
  ticketCheck
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showDialog: false,
    searchStrField1: '',
    qrcodeUrl: ''
  },
  onChange: function (e) {
    this.setData({
      searchStrField1: e.detail,
    })
  },
  getTicketCheck: function () {
    const {
      searchStrField1
    } = this.data
    if (!searchStrField1.trim()) {
      wx.showToast({
        title: '券码不能为空！',
        icon: 'none',
      })
      return
    }
    wx.showLoading()
    ticketCheck({
      searchStrField1,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '核销成功',
          icon: 'success',
        })
      }
    })
  },
  onClose: function (e) {
    const {
      name,
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  scan: function () {
    wx.scanCode({
      success: (res) => {
        const data = res.result
        wx.showToast({
          title: '扫码成功',
        })
        ticketCheck({
          searchStrField1: data,
        }, resd => {
          if (resd && resd.code == 10000) {
            wx.showToast({
              title: '核销成功',
              icon: 'success',
            })
          }
        })
      },
      fail: (e) => {
        wx.showToast({
          title: '获取失败',
          icon: 'none'
        })
      }
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
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