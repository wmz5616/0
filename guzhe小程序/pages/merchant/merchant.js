// pages/storeManage/storeManage.js
import {
  merchantAdmin,
  managerAdd,
  merchantNotice
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    refundPendingCount: 0,
    merchantList: [],
    shopList: [],
    showDialog: false,
    isShowNotice: false,
  },
  goTo: function (e) {
    const {
      isShowNotice
    } = this.data
    wx.navigateTo({
      url: isShowNotice?`/pages/onboardingGuidelines/onboardingGuidelines`:'/pages/applySettlement/applySettlement',
    })
  },
  getMerchantNotice() {
    merchantNotice({}, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          isShowNotice: resd.data,
        })
      }
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
  merchantAdmin: function () {
    merchantAdmin({}, resd => {
      const list = resd.data?.shops || []
      this.setData({
        merchantList: list.length?list.map(i=>({
          ...i.shop,
          refundAuditCount: i.refundAuditCount,
        })):list,
        totalRefundAuditCount: resd.data?.totalRefundAuditCount || 0,
      })
    })
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  open(e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: true
    })
  },
  onClose(e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false
    })
  },
  dialogConfirm(e) {
    this.onClose(e)
  },
  // onScroll: function (e) {
  //   if (Math.ceil(this.data.noticeList.length / this.data.pageSize) < Math.ceil(this.data.total / this.data.pageSize)) {
  //     this.setData({
  //       pageNum: this.data.pageNum + 1,
  //     })
  //     this.getNoticeList('e')
  //   }
  // },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    this.setData({
      isIOS: app.globalData.isIOS
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
      this.merchantAdmin()
      this.getMerchantNotice()
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