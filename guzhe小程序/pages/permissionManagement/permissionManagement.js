// pages/permissionManagement/permissionManagement.js
import {
  managerList,
  managerAdd,
  userLists
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showSearch: false,
    keyword: '',
    selectRadio: '',
    showDialog: false,
    selectId: undefined,
    list: [],
    pageNum: 1,
    pageSize: 10,
    total: 1,
  },
  onClose(e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
      selectRadio: '',
    })
  },
  choseMember: function (e) {
    const {
      index,
      id,
      name,
    } = e.currentTarget.dataset
    const selectRadio = index === this.data.selectRadio ? '' : index
    this.setData({
      selectRadio,
      tearnuserName: name,
      showDialog: selectRadio !== '' ? true : false,
      selectId: id,
    })
  },

  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
    })
  },

  goBack() {
    if (this.data.showSearch) {
      this.setData({
        showSearch: false,
        keyword: '',
        list: [],
        pageNum: 1,
      })
      return
    }
    wx.navigateBack({
      delta: 1,
    });
  },
  onInput: function (e) {
    this.setData({
      keyword: e.detail,
    })
  },
  onSearch() {
    const {
      keyword
    } = this.data
    if (keyword.length != 11) {
      wx.showToast({
        title: '请输入完整的手机号搜索',
        icon: 'none'
      })
      return
    }
    this.setData({
      pageNum: 1,
    })
    this.getUserLists()
  },
  onCancel: function () {
    this.setData({
      keyword: '',
      list: [],
      pageNum: 1,
    })
  },

  changeSelect() {
    this.setData({
      showSearch: true,
    })
  },
  getUserLists(e) {
    wx.showLoading({
      title: '加载中',
    })
    userLists({
      searchStrField1: this.data.keyword || undefined,
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.hideLoading()
        const data = resd.data?.list || []
        this.setData({
          list: e ? [...data, ...resd.data] : data,
          total: resd.data.total,
        })
      }
    })
  },
  onScroll: function (e) {
    if (Math.ceil(this.data.list.length / this.data.pageSize) < Math.ceil(this.data.total / this.data.pageSize)) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getUserLists('e')
    }
  },
  dialogConfirm() {
    managerAdd({
      shopId: this.data.shopId,
      userId: this.data.selectId,
      headManager: 1,
    }, resd => {
      wx.showToast({
        title: '操作成功！',
        icon: 'success',
        duration: 1000
      })
      this.setData({
        showSearch: false,
        pageNum: 1,
      })
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/merchant/merchant',
        })
      }, 1000)
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      shopId: +options.shopId,
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
  onShow() {},

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