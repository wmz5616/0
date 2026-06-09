// pages/myOrder/myOrder.js
import {
  formate
} from '../../utils/util'
import {
  refundLists,
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
    typeList: [{
        value: 1,
        text: '审核中'
      },
      {
        value: 2,
        text: '审核通过'
      },
      {
        value: 3,
        text: '审核驳回'
      },
    ],
    typeName: '',
    searchStrField1: '',
    orderList: [],
    teamUserLists: [],
    pageNum: 1,
    pageSize: 10,
    total: 0,
    pickerData: {},
    scrollToRefundId: '',
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
      pickerData,
      shopId
    } = this.data

    const params = {
      startTime: moment(showStartTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(showEndTime).format("YYYY-MM-DD 23:59:59"),
      searchStrField1: searchStrField1 || undefined,
      searchIntStatus: pickerData.value,
      searchField4: +shopId,
      pageNum,
      pageSize
    }

    wx.showLoading({
      title: '加载中',
    })

    await this.getproductorderList(params, e)

    wx.hideLoading()
  },
  getproductorderList: function (params, e) {
    refundLists(params, resd => {
      const list = resd.data?.list || []
      this.setData({
        teamUserLists: e ? [...this.data.teamUserLists, ...list] : list,
        total: resd.data.total
      })
    })
  },
  goBack: function () {
    const app = getApp()
    app.globalData.refreshOrderId = null
    app.globalData.myPageSize = null
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
      this.getTeamUserList('e')
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    this.setData({
      isIOS: app.globalData.isIOS,
      shopId: +options.shopId
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
    const app = getApp()
    let params = {}
    // 检查是否需要刷新（从退款审核页返回）
    if (app.globalData.needRefreshOrderList) {
      app.globalData.needRefreshOrderList = false
      const pageSize = app.globalData.myPageSize
      const targetId = app.globalData.refreshOrderId
      params = {
        scrollToRefundId: targetId ? `refund-${targetId}` : '',
        pageSize: pageSize || 10,
      }
    }

    this.setData({
      ...params,
      pageNum: 1,
    })
    this.getData()
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    const app = getApp()
    app.globalData.refreshOrderId = null
    app.globalData.myPageSize = null
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    const app = getApp()
    app.globalData.refreshOrderId = null
    app.globalData.myPageSize = null
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