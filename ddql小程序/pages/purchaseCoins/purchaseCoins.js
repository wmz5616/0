// pages/purchaseCoins/purchaseCoins.js
import {
  selectUserTeams,
  rechargeConfig,
  rechargeOrder,
  rechargeLists,
  rechargeOrderPayConfig
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showPopup: false,
    selectCoin: 0,
    coinList: [],
    teamInfo: {},
    columns: [],
    rechargeConfigData: {},
    price: '',
  },
  getRechargeOrder: function () {
    const {
      selectCoin,
      coinList,
      teamInfo,
      price
    } = this.data
    const flag = selectCoin == coinList.length
    if (flag && !price) {
      wx.showToast({
        title: '请输入自定义充值金额！',
        icon: 'none'
      })
      return
    }
    rechargeOrder({
      teamId: teamInfo.value,
      amount: flag ? +price * 100 : coinList[selectCoin].rechargeAmount,
      actId: flag ? undefined : coinList[selectCoin].id
    }, resd => {
      if (resd && resd.code == 10000) {
        rechargeOrderPayConfig({
          searchId: resd.data
        }, rr => {
          if (rr && rr.code == 10000) {
            const payData = rr.data
            wx.requestPayment({
              timeStamp: payData.timeStamp.toString(),
              nonceStr: payData.nonceStr,
              package: payData.package,
              paySign: payData.paySign,
              signType: payData.signType,
              success: payRes => {
                wx.showToast({
                  title: '支付成功',
                  icon: 'success',
                  duration: 800,
                  mask: true
                })
                this.getSelectUserTeams()
                this.getRechargeLists()
              },
              fail: payRes => {
                wx.showToast({
                  title: '支付失败',
                  icon: 'error',
                  mask: true
                })
                console.log(payRes)
              }
            })
          }
        })
      }
    })
  },
  getRechargeLists: function () {
    rechargeLists({}, resd => {
      if (resd && resd.code == 10000) {
        const coinList = resd.data.map(i => ({
          ...i,
          totalPrice: i.rechargeAmount + i.giftAmount,
        }))
        this.setData({
          coinList,
        })
      }
    })
  },
  blur: function (e) {
    const {
      value
    } = e.detail
    const {
      minAmount,
      criticalAmount
    } = this.data.rechargeConfigData
    if (!value) return
    if (value < minAmount) {
      wx.showToast({
        title: `最低充值金额为${minAmount}`,
        icon: 'none'
      })
      this.setData({
        price: minAmount,
      })
      return
    }
    if (value > criticalAmount) {
      wx.showToast({
        title: `最高充值金额为${criticalAmount}`,
        icon: 'none'
      })
      this.setData({
        price: criticalAmount,
      })
      return
    }
    this.setData({
      price: value
    })
  },
  confirm: function (e) {
    const data = e.detail.value
    this.setData({
      teamInfo: data,
    })
    this.onClose(e)
  },
  getRechargeConfig: function () {
    rechargeConfig({}, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          rechargeConfigData: resd.data
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
    selectUserTeams({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data.filter(i=>i.type != 2)
        const columns = data.map(i => ({
          text: i.team.name,
          value: i.teamId,
          healthyCoin: i.team.healthyCoin,
          avatar: i.avatar,
        }))
        this.setData({
          columns,
          teamInfo: Object.keys(this.data.teamInfo).length ?columns.find(i=>i.value == this.data.teamInfo.value):columns[0],
        })
      }
    })
  },
  gotixian:function(){
    wx.navigateTo({
      url: '/pages/withdrawalSettings/withdrawalSettings',
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
  select: function (e) {
    const {
      index,
    } = e.currentTarget.dataset
    this.setData({
      selectCoin: +index,
      price: index != this.selectCoin ? '' : this.data.price
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
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
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
    // 获取关联团体
    this.getSelectUserTeams()
    // 获取充值配置信息
    this.getRechargeConfig()
    // 获取充值活动列表
    this.getRechargeLists()
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