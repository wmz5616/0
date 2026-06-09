// pages/myOrder/myOrder.js
import {
  formate
} from '../../utils/util'
import {
  productExchangeLists,
  shopOrderList
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    tabList: ['门店订单', '商城订单'],
    tabList1: ['全部', '待支付', '待处理', '已发货', '已完成', '售后'],
    activeTab: -1,
    active: 0,
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
        text: '已完成'
      },
      {
        value: 2,
        text: '已退款'
      },
    ],
    typeData: {},
    shopName: '',
    orderList: [],
    mallPageNum: 1,
    mallPageSize: 10,
    mallTotal: 0,
    storePageNum: 1,
    storePageSize: 10,
    storeTotal: 0,
    productList: [],
    storeTotalNum: 0,
    reserveData: {},
  },
  onChange: function (e) {
    this.setData({
      active: e.detail.name,
      mallPageNum: 1,
    })
    this.getProductExchangeLists()
  },
  getShopOrderList() {
    wx.showLoading({
      title: '加载中'
    })
    const {
      storePageNum,
      storePageSize,
      shopName,
      showStartTime,
      showEndTime,
      typeData
    } = this.data
    shopOrderList({
      pageNum: storePageNum,
      pageSize: storePageSize,
      shopName: shopName || undefined,
      status: typeData?.value || undefined,
      startTime: moment(showStartTime).format('YYYY-DD-MM'),
      endTime: moment(showEndTime).format('YYYY-DD-MM'),
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.hideLoading()
        const data = resd.data || {}
        this.setData({
          orderList: data?.list || [],
          storeTotal: data.total,
          storeTotalNum: Math.ceil(data.total / storePageSize) || 0,
          reserveData: resd?.reserveData || {},
        })
      }
    })
  },
  changePage(e) {
    const {
      num
    } = e.currentTarget.dataset
    const {
      storePageNum
    } = this.data
    this.setData({
      storePageNum: storePageNum + num,
    })
  },
  getProductExchangeLists: function (e) {
    const {
      active,
      mallPageSize,
      mallPageNum
    } = this.data
    wx.showLoading({
      title: '加载中',
    })
    let statusParams = {};
    if (active == 1) statusParams.searchIntStatus = 0;
    if (active == 2) statusParams.searchStatusList = [1, 2];
    if (active == 3) statusParams.searchIntStatus = 3;
    if (active == 4) statusParams.searchIntStatus = 4;
    if (active == 5) statusParams.searchStatusList = [5, 6];

    productExchangeLists({
      ...statusParams,
      pageNum: mallPageNum,
      pageSize: mallPageSize,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.hideLoading()
        const data = resd.data
        this.setData({
          productList: e ? [...this.data.productList, resd.data.list] : resd.data.list,
          mallTotal: data.total,
        })
      }
    })
  },
  mallOnScroll: function (e) {
    if (Math.ceil(this.data.productList.length / this.data.mallPageSize) < Math.ceil(this.data.mallTotal / this.data.mallPageSize)) {
      this.setData({
        mallPageNum: this.data.mallPageNum + 1,
      })
      this.getProductExchangeLists('e')
    }
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
      typeData: pickerData || {},
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
      typeData: {},
      shopName: '',
      showStartTime: new Date("2025-10-01").getTime(),
      showEndTime: new Date().getTime(),
      startTime: new Date("2025-10-01").getTime(),
      endTime: new Date().getTime(),
      showDOverlay: false,
    })
    this.getShopOrderList()
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
  changeTab(e) {
    const {
      index
    } = e.currentTarget.dataset
    this.setData({
      activeTab: index
    })
    this.data.activeTab ? this.getProductExchangeLists() : this.getShopOrderList()
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
      showEndTime: this.data.endTime
    })
    this.onClose(e)
  },
  onConfirm(){
    this.setData({
      showDOverlay: false,
    })
    this.getShopOrderList()
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    this.setData({
      isIOS: app.globalData.isIOS,
      activeTab: +options.activeTab,
    })
    this.data.activeTab ? this.getProductExchangeLists() : this.getShopOrderList()
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