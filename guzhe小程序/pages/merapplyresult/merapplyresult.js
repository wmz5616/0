// pages/merapplyresult/merapplyresult.js
import {merchantInfo} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    info: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      merchantId: +options.merchantId,
      shopId: options?.shopId ? +options?.shopId : undefined,
    })
    this.getmerchantInfo()
  },
  onReapply:function(e){
  wx.redirectTo({
    url: '/pages/addmerchant/addmerchant' + `?shopId=${this.data.shopId}&type=${e.currentTarget.dataset.type}` +`&merchantId=${this.data.merchantId}`
  })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  getmerchantInfo: function () {
    merchantInfo({
      searchId: this.data.merchantId,
      searchField1: this.data.shopId
    }, resd => {
      if (resd.code == 10000) {
        this.setData({
          info: resd.data
        })
      }
    })
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