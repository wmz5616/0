// pages/clockRecord/clockRecord.js
import {
  checkInList,
  checkInCount,
  checkInPlaceList
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    startTime: new Date('2025-10-01').getTime(),
    endTime: new Date().getTime(),
    dateType: 'date',
    minDate: new Date("2025-01-01").getTime(),
    maxDate: new Date().getTime(),
    checkCount: {},
    listData: [],
    columns1: ['全部', '扫码打卡', '场地打卡'],
    columns2: ['9点', '8点', '7点'],
    columns: [],
    showPicker: false,
    pageNum: 1,
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

  showPicker: function (e) {
    const {
      type
    } = e.currentTarget.dataset;
    const {
      columns1,
      columns2
    } = this.data
    this.setData({
      columns: type == 'status' ? columns1 : columns2,
      showPicker: true,
    })
  },
  shartimeLine: function () {
    this.setData({
      showPopup2: true,
      showPopup: false
    })
  },
  stertformatDate: function (timestamp) {
    var date = new Date(timestamp);
    var dateString = date.toString();
    var matched = dateString.match(/\w{3} (\w{3}) (\d{2}) (\d{4})/);
    console.log(matched)
    var month = date.getMonth() + 1;
    var day = matched[2];
    var year = matched[3];
    return year + "-" + month + "-" + day + ' ' + '00:00:00';
  },
  endformatDate: function (timestamp) {
    var date = new Date(timestamp);
    var dateString = date.toString();
    var matched = dateString.match(/\w{3} (\w{3}) (\d{2}) (\d{4})/);
    var month = date.getMonth() + 1;
    var day = matched[2];
    var year = matched[3];
    return year + "-" + month + "-" + day + ' ' + '23:59:59';
  },
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  onInput: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: e.detail,
    })
  },

  showTime: function () {
    this.setData({
      showPopup1: true
    })
  },
  cancelPicker: function () {
    this.setData({
      showPopup1: false
    })
  },
  comfrimPicker: function () {
    this.setData({
      showPopup1: false
    })
    this.getcheckInList(false)
    this.getcheckInCount()
  },
  showComponent: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    if (name == 'showPopup') {
      wx.hideTabBar({
        animation: true // 可选，是否开启动画效果
      })
    }
    this.setData({
      info: e.currentTarget.dataset.info,
      [name]: true,
    })
  },
  onConfirm: function (e) {
    const value = e.detail
    this.setData({
      searchField2: value.index,
      showPicker: false,
    })
    this.getcheckInList(false)
    this.getcheckInCount()
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  getcheckInList: function () {

  },
  getcheckInList: function (isAppend) {
    if (this.data.type == 'place') {
      checkInPlaceList({
        isReturnPermsData: true,
        searchField3: this.data.searchField3 || undefined,
        // searchStatusList: [2],
        searchField2: this.data.searchField2 == 0 || !this.data.searchField2 ? undefined : this.data.searchField2 - 1,
        startTime: moment(this.data.startTime).format('YYYY-MM-DD 00:00:00'),
        endTime: moment(this.data.endTime).format('YYYY-MM-DD 23:59:59'),
        pageNum: this.data.pageNum
      }, resd => {
        this.setData({
          listData: isAppend ? this.data.listData.concat(resd.data.list || []) : (resd.data.list || []),
          total: resd.data.total
        })
      })
    } else {
      checkInList({
        isReturnPermsData: true,
        searchField3: this.data.searchField3 || undefined,
        searchStatusList: [2],
        searchField2: this.data.searchField2 == 0 || !this.data.searchField2 ? undefined : this.data.searchField2 - 1,
        startTime: moment(this.data.startTime).format('YYYY-MM-DD 00:00:00'),
        endTime: moment(this.data.endTime).format('YYYY-MM-DD 23:59:59'),
        pageNum: this.data.pageNum
      }, resd => {
        this.setData({
          listData: isAppend ? this.data.listData.concat(resd.data.list || []) : (resd.data.list || []),
          total: resd.data.total
        })
      })
    }
  },
  getcheckInCount: function () {
    checkInCount({
      startTime: moment(this.data.startTime).format('YYYY-MM-DD 00:00:00'),
      endTime: moment(this.data.endTime).format('YYYY-MM-DD 23:59:59'),
      isReturnPermsData: true,
      searchField2: this.data.searchField2 == 0 || !this.data.searchField2 ? undefined : this.data.searchField2 - 1,
      searchStatusList: [2],
    }, resd => {
      this.setData({
        checkCount: resd.data
      })
    })
  },
  reachbottoms: function () {
    console.log(this.data.listData.length, this.data.total)
    if (Math.ceil(this.data.total / 10) > Math.ceil(this.data.listData.length / 10)) {
      this.setData({
        pageNum: this.data.pageNum += 1
      })
      this.getcheckInList(true)
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      searchField3: +options.placeId,
      type: options.type
    })
    this.getcheckInList(false)
    this.getcheckInCount()
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

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: `都动起来`,
      path: '/pages/index/index'
    };
  },

  onShareTimeline() {
    return {
      title: `都动起来`,
      path: '/pages/index/index'
    };
  }
})