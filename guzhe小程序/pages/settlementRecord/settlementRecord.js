// pages/settlementRecord/settlementRecord.js
import {
  ruzhuAuditList
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    indexs: 0,
    listData: [],
    tabList: [{
        id: 0,
        value: '全部',
        isActive: true
      },
      {
        id: 1,
        value: '待审核',
        isActive: false
      },
      {
        id: 2,
        value: '已通过',
        isActive: false
      },
      {
        id: 3,
        value: '已驳回',
        isActive: false
      }
    ]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  onLoad(options) {
    const app = getApp()
    this.setData({
      isIOS: app.globalData.isIOS,
    })
  },
  handleItemTap(e) {
    const {
      index
    } = e.currentTarget.dataset
    let {
      tabList
    } = this.data
    tabList.forEach((v, i) => i === index ? v.isActive = true : v.isActive = false);
    this.setData({
      indexs: index,
      tabList
    })
    this.getruzhuAuditList()
  },
  getruzhuAuditList: function () {
    const type = this.data.indexs == 0 ? undefined : this.data.indexs - 1
    ruzhuAuditList({
      searchType: type
    }, resd => {
      this.setData({
        listData: resd.data
      })
    })
  },
  previewImg: function (e) {
    wx.previewImage({
      urls: [e.currentTarget.dataset.url],
    })
  },
  goDetail: function (e) {
    wx.navigateTo({
      url: `/pages/applySettlement/applySettlement?shopId=${e.currentTarget.dataset.id}`,
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
    this.getruzhuAuditList()
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