// pages/settleTheOrder/settleTheOrder.js
import { settlementRecordLists, ex } from '../../utils/request'
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
    list: [],
    pageNum: 1,
    pageSize: 10,
    pages: 1,
    totalCount: 0,
    totalAmount: '0.00',
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
      list: []
    })
    this.getList()
    this.onClose(e)
  },
  
  getList() {
    const { shopId, showStartTime, showEndTime, pageNum, pageSize } = this.data;
    if (!shopId) return;
    
    wx.showLoading({ title: '加载中' })
    settlementRecordLists({
      shopId,
      startDate: moment(showStartTime).format('YYYY-MM-DD'),
      endDate: moment(showEndTime).format('YYYY-MM-DD'),
      pageNum,
      pageSize
    }, res => {
      wx.hideLoading()
      if (res.code === 10000) {
        this.setData({
          list: res.data.pageInfo.list || [],
          totalCount: res.data.totalCount || 0,
          totalAmount: res.data.totalAmount || '0.00',
          pages: res.data.pageInfo.pages || 1,
        })
      }
    }, err => {
      wx.hideLoading()
    })
  },
  
  prevPage() {
    if (this.data.pageNum > 1) {
      this.setData({ pageNum: this.data.pageNum - 1 })
      this.getList()
    }
  },
  
  nextPage() {
    if (this.data.pageNum < this.data.pages) {
      this.setData({ pageNum: this.data.pageNum + 1 })
      this.getList()
    }
  },
  
  handleExport() {
    const { shopId, showStartTime, showEndTime } = this.data;
    if (!shopId) return;
    wx.showLoading({ title: '正在导出' })
    ex({
      fileName: '结算记录',
      method: 'post',
      url: '/guzhe/wechat/shop/settlement_record/export',
      data: {
        shopId,
        startDate: moment(showStartTime).format('YYYY-MM-DD'),
        endDate: moment(showEndTime).format('YYYY-MM-DD'),
      },
      resolve: (filePath) => {
        wx.hideLoading()
        wx.openDocument({
          filePath,
          showMenu: true
        })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },
  onInput: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  selectTime() {
    this.setData({
      showPopup1: true,
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if (options.shopId) {
      this.setData({
        shopId: Number(options.shopId)
      })
      this.getList()
    }
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