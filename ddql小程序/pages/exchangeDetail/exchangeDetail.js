// pages/exchangeDetail/exchangeDetail.js
import {
  productExchangeInfo
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    processData: [
    ],
    refundApplyInfo: {},
    orderAddress: {},
    ticketList: {},
    orderInfo: {},
    showDialog: false,
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
        this.setData({
          processData:data.expressOrder ? data.expressOrder.infoData ? data.expressOrder.infoData : [] : [],
          kuaidiLogList: data.expressOrder ? data.expressOrder.infoData ? data.expressOrder.infoData : [] : [],
          refundApplyInfo: data.refundApplyInfo,
          orderAddress: data.orderAddress,
          ticketList: data.ticketList,
          orderInfo: {
            ...data.orderInfo,
            deadline: moment(data.orderInfo.deadline).format('YYYY-MM-DD')
          },
        })
      }
    })
  },
  open: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
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
  dialogConfirm: function (e) {
    this.onClose(e)
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  jump: function (e) {
    const {
      url,
      item
    } = e.currentTarget.dataset
    wx.redirectTo({
      url: item ? url + `?data=${JSON.stringify(item)}` : url,
    })
  },
  go: function (e) {
    const {
      url,
    } = e.currentTarget.dataset
    wx.switchTab({
      url,
    })
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