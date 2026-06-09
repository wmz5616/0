// pages/coupon/coupon.js
import {
  productExchangeInfo
} from '../../utils/request'
import drawQrcode from 'weapp-qrcode'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    status: 1,
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  getProductExchangeInfo: function () {
    const {
      searchId
    } = this.data
    productExchangeInfo({
      searchId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        const ticketInfo = data.ticketList.find(i => i.status != 3)
        const isexpired=data.orderInfo.deadline && new Date(data.orderInfo.deadline).getTime() < new Date().getTime()?true:false
        data.ticketList.map(xx => {
          if (data.orderInfo.deadline && new Date(data.orderInfo.deadline).getTime() < new Date().getTime()) {
            xx.isexpired = true
          }
        })
        this.setData({
          isexpired,
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
  generateQRCode(text) {
    // 生成二维码到canvas
    drawQrcode({
      width: 190,
      height: 190,
      canvasId: 'qrcodeCanvas',
      text: text,
      callback: (url) => {
        console.log(url)
        this.setData({
          qrcodeUrl: url
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      searchId: +options.searchId,
    })
    this.getProductExchangeInfo()
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