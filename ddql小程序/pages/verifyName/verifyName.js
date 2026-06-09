// pages/verifyName/verifyName.js
import {
  certification,
  userInfo
} from '../../utils/request'
import {
  validateField
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    verify: false,
    cardNum: '',
    name: '',
    userInfo: {}
  },
  change: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: e.detail
    })
  },
  getCertification() {
    const params = {
      cardNum: this.data.cardNum,
      name: this.data.name
    }
    certification(params, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '认证成功!',
          icon: 'success'
        })
        setTimeout(_ => {
          wx.navigateBack()
        }, 300)
        // this.setData({
        //   verify: true,
        // })
        // this.getUserInfo()
      }
    })
  },
  getUserInfo: function () {
    userInfo({}, re => {
      if (re.code == 10000) {
        this.setData({
          userInfo: re.data
        })
        wx.setStorageSync('userinfo', re.data)
      }
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

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