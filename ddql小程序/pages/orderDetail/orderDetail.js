// pages/orderDetail/orderDetail.js
import {
  shopOrderInfo
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    id: '',
    orderInfo: {},
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  getShopOrderInfo(){
    shopOrderInfo({
      id: this.data.id,
    },resd=>{
      if(resd && resd.code == 10000){
        this.setData({
          orderInfo: resd.data,
        })
      }
    })
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
  jump(e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url,
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      id: +options.id
    })
    this.getShopOrderInfo()
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