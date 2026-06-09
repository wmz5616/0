// pages/myOrder/myOrder.js
import {
  formate
} from '../../utils/util'
import {
  productorderList,
  productOrderStat,
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    statInfo: {},
    tabList: ['门店订单', '商城订单'],
    activeTab: 0,
    showPopup1: false,
    showPopup1: false,
    showStartTime: new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
    showEndTime: new Date().getTime(),
    startTime:new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
    endTime: new Date().getTime(),
    minDate: new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
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
    typeList: [{
        value: 0,
        text: '待支付'
      },
      {
        value: 1,
        text: '待使用'
      },
      {
        value: 2,
        text: '待发货'
      },
      {
        value: 3,
        text: '已发货'
      },
      {
        value: 4,
        text: '已完成'
      },
      {
        value: 5,
        text: '退款中'
      },
      {
        value: 6,
        text: '已退款'
      },
      {
        value: 7,
        text: '已过期'
      },
      {
        value: 8,
        text: '已取消'
      },
    ],
    typeName: '',
    searchStrField1: '',
    orderList: [],
    pageSize: 10,
    teamUserLists: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    pickerData: {},
  },
  changeInput: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: e.detail.value
    })
  },
  onDConfirm: function (e) {
    const pickerData = e.detail.value
    this.setData({
      showAreaPicker: false,
      typeName: pickerData.text,
      pickerData,
    })
  },
  onClickIcon() {
    this.setData({
      showAreaPicker: true,
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
  reset: function (e) {
    this.setData({
      typeName: '',
      searchStrField1: '',
      showDOverlay: false,
      pickerData: {},
      showStartTime: new Date("2025-10-01").getTime(),
      showEndTime: new Date().getTime(),
      startTime: new Date("2025-10-01").getTime(),
      endTime: new Date().getTime(),
      pageNum: 1,
    })
    this.getData()
  },
  confirmSelect() {
    this.setData({
      showDOverlay: false,
    })
    this.getData()
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
  async getData(e) {
    const {
      showStartTime,
      showEndTime,
      pageNum,
      pageSize,
      searchStrField1,
      pickerData
    } = this.data

    const params = {
      startTime: moment(showStartTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(showEndTime).format("YYYY-MM-DD 23:59:59"),
      searchStrField1: searchStrField1 || undefined,
      searchIntStatus: pickerData.value,
      pageNum,
      pageSize
    }

    wx.showLoading({
      title: '加载中',
    })

    await Promise.allSettled([this.getproductorderList(params, e), this.getproductOrderStat(params)])

    wx.hideLoading()
  },
  getproductorderList: function (params, e) {
    productorderList(params, resd => {
      const list = resd.data?.list || []
      this.setData({
        teamUserLists: e ? [...this.data.teamUserLists, ...list] : list,
        total: resd.data.total
      })
    })
  },
  getproductOrderStat: function (params) {
    params.searchStatusList=[1,2,3,4,5,7]
    productOrderStat(params, resd => {
      this.setData({
        statInfo: resd.data
      })
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
  changeTab(e) {
    const {
      index
    } = e.currentTarget.dataset
    this.setData({
      activeTab: index
    })
  },
  selectTime() {
    console.log(223)
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
      showEndTime: this.data.endTime
    })  
    this.getData()
    this.onClose(e)
  },
  onScroll: function (e) {
    if (Math.ceil(this.data.teamUserLists.length / this.data.pageSize) < Math.ceil(this.data.total / this.data.pageSize)) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getData('e')
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    this.setData({
      isIOS: app.globalData.isIOS,
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
    if (wx.getStorageSync('token')) {
      this.setData({
        pageNum: 1,
      })
      this.getData()
    }
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