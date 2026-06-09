// pages/merchantAdmin/merchantAdmin.js
import {
  managerList,
  managerDel
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showDialog: false,
    // 是否添加
    pageNum: 1,
    pageSize: 10,
    list: [],
    deleteName: '',
  },
  getManagerList() {
    managerList({
      searchField1: this.data.id,
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          list: resd.data || [],
        })
      }
    })
  },
  getManagerDel() {
    managerDel({
      deleteIds: [this.data.deleteId],
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '移除成功！',
          icon: 'success',
          duration: 1000,
        })
        this.setData({ showDialog: false })
        this.getManagerList()
      }
    })
  },
  dialogConfirm() {
    this.getManagerDel()
  },
  open: function (e) {
    const {
      name,
      deleteid,
      username,
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
      deleteId: deleteid,
      deleteName: username,
    })
  },
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url,
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  onClose: function (e) {
    const {
      name,
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      id: +options.id,
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
    this.getManagerList()
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