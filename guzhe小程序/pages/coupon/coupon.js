// pages/coupon/coupon.js
import drawQrcode from 'weapp-qrcode'
import {
  productInfo
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    ticketInfo: {},
    qrCodeImage: '',
    rpxRatio: '',
    ticketList: [],
  },
  changeTicket: function (e) {
    const {
      id
    } = e.currentTarget.dataset
    const ticketInfo = this.data.ticketList.find(i => i.id == id)
    this.setData({
      ticketInfo
    })
    this.generateQRCode(ticketInfo.ticket)
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  getProductInfo: function () {
    const {
      searchId
    } = this.data
    productInfo({
      searchId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        const ticketInfo = data.ticketList.find(i => i.status != 3)
        const isexpired = data.orderInfo.deadline && new Date(data.orderInfo.deadline).getTime() < new Date().getTime() ? true : false
        data.ticketList.map(xx => {
          if (data.orderInfo.deadline && new Date(data.orderInfo.deadline).getTime() < new Date().getTime()) {
            xx.isexpired = true
          }
        })
        this.setData({
          isexpired,
          expiredTime: moment(data.orderInfo.deadline).format("YYYY-MM-DD HH:mm:ss"),
          refundApplyInfo: data.refundApplyInfo,
          orderAddress: data.orderAddress,
          ticketList: data.ticketList,
          orderInfo: data.orderInfo,
          ticketInfo,
        })
        // 默认第一张
        this.generateQRCode(ticketInfo?.ticket);
      }
    })
  },
  copyText:function(){
    wx.setClipboardData({
      data: this.data.ticketInfo.ticket, // 要复制的文本内容
      success: (res) => {
     wx.showToast({
       title: '复制成功',
     })
      },
      fail: (err) => {
        console.error('复制失败', err)
      }
    })
  },
  generateQRCode(text) {
    // 生成二维码到canvas\
    drawQrcode({
      width: 343 / this.data.rpxRatio,
      height: 342 / this.data.rpxRatio,
      canvasId: 'qrcodeCanvas',
      text: text,
      callback: (res) => {
        wx.canvasToTempFilePath({
          canvasId: 'qrcodeCanvas',
          success: (res) => {
            this.setData({
              qrCodeImage: res.tempFilePath,
            });
          }
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let rpxRatio = wx.getStorageSync('rpxRatio')
    if (!rpxRatio) {
      const systemInfo = wx.getSystemInfoSync()
      rpxRatio = 750 / systemInfo.screenWidth
      wx.setStorageSync('rpxRatio', rpxRatio)
    }
    this.setData({
      searchId: +options.searchId,
      rpxRatio
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
    this.getProductInfo()
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