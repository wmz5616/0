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
    qrcodeUrl: '',
    msg: '',
    msgType: {
      '无权限核销': '该券码不属于你的核销范围',
      '券码已失效': '',
      '券码核销失败': '该券码未到使用时间',
      '已核销券码': '该券码已核销',
      '无效券码': '该券码不存在'
    },
    shopId: '',
  },
  onChange: function (e) {
    this.setData({
      searchStrField1: e.detail.replace(/\s/g, ''),
    })
  },
  getTicketCheck: function () {
    const {
      searchStrField1,
      shopId
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
      searchId: shopId,
    }, resd => {
      if (resd && resd.code == 10000) {
        if (resd.msg == '核销成功') {
          wx.showToast({
            title: resd.msg,
            icon: 'none',
            duration: 1500,
          })
          return
        }
        wx.hideLoading()
        this.setData({
          showDialog: true,
          msg: resd.msg
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
        ticketCheck({
          searchStrField1: data,
          searchId: this.data.shopId,
        }, resd => {
          if (resd && resd.code == 10000) {
            if (resd.msg == '核销成功') {
              wx.showToast({
                title: resd.msg,
                icon: 'none',
                duration: 1500,
              })
              return
            }
            wx.hideLoading()
            this.setData({
              showDialog: true,
              msg: resd.msg
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

  dialogConfirm(){
    this.setData({
      showDialog: false,
      searchStrField1: '',
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
    console.log(options)
    this.setData({
      shopId: +options?.shopId
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