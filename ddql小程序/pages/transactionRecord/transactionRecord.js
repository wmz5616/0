// pages/rechargeRecord/rechargeRecord.js
import {
  coinList
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    columns: [],
    keyword: '',
    showPopup1: false,
    startTime: new Date("2025-10-01").getTime(),
    endTime: new Date().getTime(),
    minDate: new Date("2025-10-01").getTime(),
    maxDate: new Date().getTime(),
    pageNum: 1,
    pageSize: 10,
    total: 0,
    coinLists: [],
    forma: function (type, value) {
      if (type === 'year') {
        return `${value}年`;
      }
      if (type === 'month') {
        return `${value}月`;
      }
      if (type === 'day') {
        return `${value}日`;
      }
      return value;
    },
  },
  onSearch:function(e){
    this.setData({
      keyword: e.detail
    })
    this.getCoinList()
  },
  onCancel:function(){
    console.log(11)
    this.setData({
      keyword: '',
    })
    this.getCoinList()
  },
  getCoinList:function(){
    const { startTime,endTime,keyword,teamId,pageNum,pageSize } = this.data
    coinList({
      teamId,
      keyword,
      startTime: moment(startTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(endTime).format("YYYY-MM-DD 23:59:59"),
      pageNum,
      pageSize
    },resd=>{
      if(resd && resd.code==10000){
        const data = resd.data
        this.setData({
          coinLists: data.list,
          total: data.total
        })
      }
    })
  },
  comfrimDate: function (e) {
    if (this.data.endTime < this.data.startTime) {
      wx.showToast({
        title: '结束时间不能小于开始时间',
        icon: 'none'
      })
      return
    }
    this.getCoinList()
    this.onClose(e)
  },
  open: function (e) {
    const {
      type
    } = e.currentTarget.dataset;
    this.setData({
      [type]: true,
    })
  },
  onInput: function (e) {
    console.log(e)
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  onConfirm: function (e) {
    const value = e.detail
    console.log(value)
    this.setData({
      showPicker: false,
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
    this.setData({
      teamId: +options.id,
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
    this.getCoinList()
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