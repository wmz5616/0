// pages/rechargeRecord/rechargeRecord.js
import {
  selectUserTeams,
  rechargeOrderLists,
  rechargeOrderCount
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showPopup: false,
    columns: [],
    showPopup1: false,
    startTime: new Date("2025-10-01").getTime(),
    endTime: new Date().getTime(),
    minDate: new Date("2025-10-01").getTime(),
    maxDate: new Date().getTime(),
    teamInfo: {},
    totalAmount: 0,
    pageNum: 1,
    pageSize: 10,
    list: [],
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
  getRechargeOrderLists:function(){
    const { startTime,endTime,teamInfo,pageNum,pageSize } = this.data
    rechargeOrderLists({
      searchField2: teamInfo.value,
      startTime: moment(startTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(endTime).format("YYYY-MM-DD 23:59:59"),
      pageNum,
      pageSize
    },resd=>{
      if(resd && resd.code==10000){
        const data = resd.data
        this.setData({
          list: data.list,
          total: data.total
        })
      }
    })
  },
  getRechargeOrderCount:function(){
    const { startTime,endTime,teamInfo } = this.data
    rechargeOrderCount({
      searchField2: teamInfo.value,
      startTime: moment(startTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(endTime).format("YYYY-MM-DD 23:59:59"),
    },resd=>{
      if(resd && resd.code == 10000){
        this.setData({
          totalAmount: resd.data.amount
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
    this.getRechargeOrderLists()
    this.getRechargeOrderCount()
    this.onClose(e)
  },
  onInput: function (e) {
    console.log(e)
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  getSelectUserTeams: function () {
    selectUserTeams({}, resd => {
      if (resd && resd.code == 10000) {
        const columns = resd.data.map(i => ({
          text: i.team.name,
          value: i.teamId,
          healthyCoin: i.healthyCoin,
        }))
        this.setData({
          columns,
          teamInfo: columns[0],
        })
        this.getRechargeOrderLists()
        this.getRechargeOrderCount()
      }
    })
  },
  open: function (e) {
    const {
      type
    } = e.currentTarget.dataset;
    this.setData({
      [type]: true,
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
  confirm: function (e) {
    const data = e.detail.value
    this.setData({
      teamInfo: data,
    })
    this.getRechargeOrderLists()
    this.getRechargeOrderCount()
    this.onClose(e)
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
    // 获取关联团体
    this.getSelectUserTeams()
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