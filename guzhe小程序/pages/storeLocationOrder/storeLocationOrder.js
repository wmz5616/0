// pages/myOrder/myOrder.js
import {
  screenOrderList,
  getSuper
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showPopup1: false,
    showStartTime: new Date("2025-10-01").getTime(),
    showEndTime: new Date().getTime(),
    startTime: new Date("2025-10-01").getTime(),
    endTime: new Date().getTime(),
    minDate: new Date("2025-10-01").getTime(),
    maxDate: new Date().getTime(),
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
    showDOverlay: false,
    showAreaPicker: false,
    showSuperPicker: false,
    typeList: [{
        value: 0,
        text: '待确定',
        color: 'blue'
      },
      {
        value: 1,
        text: '待生效',
        color: 'orange'
      },
      {
        value: 2,
        text: '生效中',
        color: 'red'
      },
      {
        value: 3,
        text: '已完成',
        color: 'green'
      },
      {
        value: 4,
        text: '已驳回',
        color: 'gray'
      },
      {
        value: 5,
        text: '已撤销',
        color: 'gray'
      },
    ],
    pickerData: {},
    circleName: '',
    eqtNumber: '',
    orderList: [],
    total: 0,
    pageNum: 1,
    pageSize: 10,
    superList: [],
  },
  getSuperList() {
    getSuper({}, resd => {
      const data = resd.data || []
      this.setData({
        superList: data.map(i => ({
          value: i.id,
          text: i.name
        })),
      })
    })
  },

  onConfirm() {
    this.setData({
      showDOverlay: false,
      pageNum: 1,
    })
    this.getScreenOrderList()
  },
  getScreenOrderList(e) {
    const {
      showStartTime,
      showEndTime,
      pickerData,
      eqtNumber,
      circleName,
      shopId
    } = this.data
    wx.showLoading({
      title: '加载中',
    })
    screenOrderList({
      startDate: moment(showStartTime).format("YYYY-MM-DD"),
      endDate: moment(showEndTime).format("YYYY-MM-DD"),
      businessCircleName: circleName || undefined,
      status: pickerData.value || undefined,
      serialNumber: eqtNumber || undefined,
      shopId,
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.hideLoading()
        const data = resd.data
        const list = data.list || []
        this.setData({
          orderList: e ? [...list, ...this.data.orderList] : list,
          orderInfo: data,
          total: data.orderCount,
        })
      }
    })
  },
  onDConfirm: function (e) {
    const pickerData = e.detail.value
    const {
      name
    } = e.currentTarget.dataset

    let data = {}

    if (name == 'showAreaPicker') {
      data.pickerData = pickerData
    } else if (name == 'showSuperPicker') {
      data.circleName = pickerData.text
    }

    this.setData({
      [name]: false,
      ...data,
    })
  },
  onClickIcon(e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: true,
    })
  },
  changeInput(e) {
    const {
      type
    } = e.currentTarget.dataset
    console.log(e.detail.value)
    this.setData({
      [type]: e.detail.value
    })
  },
  reset: function (e) {
    this.setData({
      circleName: '',
      eqtNumber: '',
      pickerData: {},
      showDOverlay: false,
      showStartTime: new Date("2025-10-01").getTime(),
      showEndTime: new Date().getTime(),
      startTime: new Date("2025-10-01").getTime(),
      endTime: new Date().getTime(),
      pageNum: 1,
    })
    this.getScreenOrderList()
  },
  showSearch() {
    this.setData({
      showDOverlay: true,
    })
  },
  onInput: function (e) {
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
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  jump(e) {
    const {
      url
    } = e.currentTarget.dataset

    wx.navigateTo({
      url,
    })
  },
  selectTime() {
    this.setData({
      showPopup1: true,
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
    this.setData({
      showStartTime: this.data.startTime,
      showEndTime: this.data.endTime,
      pageNum: 1,
    })
    this.onClose(e)
    this.getScreenOrderList()
  },
  scrollTolower() {
    const totalPages = Math.ceil(this.data.total / this.data.pageSize)
    const currentTotalPages = Math.ceil(this.data.orderList.length / this.data.pageSize)
    if (currentTotalPages < totalPages) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getScreenOrderList('e')
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    this.setData({
      shopId: +options.shopId,
      isIOS: app.globalData.isIOS,
    })
    this.getSuperList()
    this.getScreenOrderList()
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