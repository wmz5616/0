// pages/withdrawalSettings/withdrawalSettings.js
import {
  selectUserTeams,
  updateCheckInSettings,
  selectCheckInSettings
} from '../../utils/request'
import {
  validateField
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showPopup: false,
    searchId: '',
    settingData: {},
    hour: 0,
    minute: 0,
    scanCodeHealthyCoin: 0,
    stepsOpen: 1,
    targetSteps: 0,
    stepsHealthyCoin: 0,
    lowestWithdrawalMoney: 0,
  },
  update: function () {
    const params = {
      teamId: this.data.searchId,
      scanCodeTime: (this.data.hour * 60) + this.data.minute,
      scanCodeHealthyCoin: this.data.scanCodeHealthyCoin,
      stepsOpen: this.data.stepsOpen,
      targetSteps: !this.data.stepsOpen ? this.data.targetSteps : 0,
      stepsHealthyCoin: !this.data.stepsOpen ? this.data.stepsHealthyCoin : 0,
      lowestWithdrawalMoney: this.data.lowestWithdrawalMoney,
    }
    console.log(params)
    const title = validateField(params)
    if (title) {
      console.log(11111)
      wx.showToast({
        title,
        icon: 'none'
      })
      return
    }
    // wx.showLoading({
    //   title: '加载中',
    // })
    updateCheckInSettings(params, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '更新成功！',
          icon: 'success'
        })
        this.getselectCheckInSettings()
      }
    })
  },
  change: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: +e.detail.value
    })
  },
  changeRadio: function () {
    this.setData({
      stepsOpen: this.data.stepsOpen ? 0 : 1
    })
  },
  getselectCheckInSettings: function () {
    const {
      searchId,
      teamList
    } = this.data
    selectCheckInSettings({
      searchId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        const teamInfo = teamList.find(i => i.teamId == searchId)
        this.setData({
          settingData: data,
          teamInfo,
          hour: Math.floor(data.scanCodeTime / 60),
          minute: data.scanCodeTime % 60,
          scanCodeHealthyCoin: data.scanCodeHealthyCoin,
          stepsOpen: data.stepsOpen,
          targetSteps: data.targetSteps,
          stepsHealthyCoin: data.stepsHealthyCoin,
          lowestWithdrawalMoney: data.lowestWithdrawalMoney,
        })
      }
    })
  },
  getSelectUserTeams: function () {
    selectUserTeams({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data.filter(i=>i.type != 2)
        const item = data.length ? data[0] : {}
        console.log(item)
        this.setData({
          teamList: data,
          searchId: item.teamId,
        }, () => {
          this.getselectCheckInSettings()
        })
      }
    })
  },
  bindChange: function (e) {
    const val = e.detail.value[0]
    const {
      teamList
    } = this.data
    const item = teamList[val]
    this.setData({
      searchId: item.teamId,
    })
  },
  confirm: function (e) {
    this.getselectCheckInSettings()
    this.onClose(e)
  },
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  open: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
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
    this.getSelectUserTeams()
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