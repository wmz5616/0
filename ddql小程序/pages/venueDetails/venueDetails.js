// pages/venueDetails/venueDetails.js
import {
  getconsumption
} from '../../utils/request'
const WxParse = require('../../wxParse/wxParse')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    activeTab: 0,
    tabList: ['用币规则', '门店介绍'],
    consumptionInfo: {}
  },
  callPhone: function (e) {
    // const data = e.currentTarget.dataset.info
    wx.makePhoneCall({
      phoneNumber: '13790938592'
    })
  },
  openLocation() {
    wx.openLocation({
      latitude: 22.906518,
      longitude: 113.863719,
      scale: 18,
      address: '11111',
      name: '2222'
    })
  },
  changeTab(e) {
    const index = e.currentTarget.dataset.index
    this.setData({
      activeTab: index,
    })
    if (index == 1 && this.data.consumptionInfo.description) {
      WxParse.wxParse('newsDetail.contentHtml', 'html', this.data.consumptionInfo.description, this, 5)
    }
  },

  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  getconsumption: function () {
    getconsumption({
      shopId: this.data.shopId
    }, resd => {
      const data = resd?.data || {}
      if (resd.code == 10000) {
        this.setData({
          consumptionInfo: data
        })
        if (this.data.activeTab == 1 && data.description) {
          WxParse.wxParse('newsDetail.contentHtml', 'html', data.description, this, 5)
        }
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      shopId: options.searchId
    })
    this.getconsumption()
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
    WxParse.wxParse('newsDetail.contentHtml', 'html', '壹里咖啡是一个知名的餐饮连锁品牌，属于壹里餐饮集团。自2002年在东莞松山湖开设首家门店以来，壹里咖啡以其优雅的欧亚气息和“诚信、尊重、关怀”的核心价值观，成为了咖啡行业的品质典范。', this, 5)
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