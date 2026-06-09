// pages/refundPolicy/refundPolicy.js
import {
  merchantNotice
} from '../../utils/request'
const WxParse = require('../../wxParse/wxParse')
Page({

  /**
   * 页面的初始数据
   */
  data: {},
  getMerchantNotice() {
    merchantNotice({}, resd => {
      if (resd && resd.code == 10000) {
        WxParse.wxParse('newsDetail.contentHtml', 'html', resd.data || '', this, 5)
      }
    })
  },
  // 返回上一个页面
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  jump() {
    wx.redirectTo({
      url: '/pages/applySettlement/applySettlement',
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      id: +options.id,
    })
    this.getMerchantNotice()
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