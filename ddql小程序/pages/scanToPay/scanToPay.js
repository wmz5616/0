// pages/scanToPay/scanToPay.js
const WxParse = require('../../wxParse/wxParse')
import {
  getcoinshop,
  orderCreate,
  orderPay,
  getUserGoldCoin,
} from '../../utils/request'
import {
  debounce
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    shopName: '壹里咖啡店',
    totalAmount: '',
    shopId: '',
    confirmType: false,
    deductCoin: '',
    ruleInfo: {},
    hadRule: false,
    payPrice: 0,
    isPay: false,
    totalCoin: 0,
  },
  getUserGoldCoin() {
    getUserGoldCoin({}, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          totalCoin: resd.data
        })
      }
    })
  },
  getcoinshop() {
    const {
      shopId,
    } = this.data
    getcoinshop({
      searchId: shopId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const ruleInfo = resd.data
        this.setData({
          ruleInfo,
          hadRule: ruleInfo?.beginAmount ? true : false,
        })
        WxParse.wxParse('newsDetail.contentHtml', 'html', ruleInfo.remark, this, 5)
      }
    })
  },
  confirmAmount() {
    const {
      totalAmount,
      confirmType,
      ruleInfo,
      hadRule,
      totalCoin
    } = this.data
    if (!confirmType && !totalAmount) {
      wx.showToast({
        title: '请输入金额！',
        icon: 'none'
      })
      return
    }
    let deductCoin = 0
    // 填了用币规则
    if (hadRule) {
      const gapAmount = totalAmount - ruleInfo?.beginAmount
      deductCoin = gapAmount > 0 ? Math.min(Math.floor(gapAmount / ruleInfo?.threshold) * ruleInfo?.deduct, totalCoin) : 0
    }

    this.setData({
      confirmType: !confirmType,
      deductCoin: !confirmType ? deductCoin : 0,
      payPrice: totalAmount - deductCoin,
    })
  },
  onInput(e) {
    const value = e.detail.value
    this.setData({
      totalAmount: +value || '',
    })
  },
  onInput1(e) {
    const value = +e.detail.value
    const {
      ruleInfo,
      totalAmount,
    } = this.data
    // 当前金额大于起始金额才可扣减
    if (totalAmount < ruleInfo?.beginAmount) {
      wx.showToast({
        title: '金额满' + ruleInfo?.beginAmount + '才可抵扣！',
        icon: 'none'
      })
      this.setData({
        deductCoin: 0,
      })
      return
    }
    // 抵扣金额不能大于当前金额抵扣金币数
    const gapAmount = totalAmount - ruleInfo?.beginAmount
    const maxDeductCoin = gapAmount > 0 ? Math.floor(gapAmount / ruleInfo?.threshold) * ruleInfo?.deduct : 0
    if (value > maxDeductCoin || value > ruleInfo?.maxDeduct || value > totalCoin) {
      wx.showToast({
        title: '抵扣金币数不能大于' + Math.min(maxDeductCoin, ruleInfo?.maxDeduct, totalCoin),
        icon: 'none'
      })
      this.setData({
        deductCoin: this.data.deductCoin,
      })
      return
    }
    this.setData({
      deductCoin: value != null ? value : '',
      payPrice: totalAmount - value,
    })
  },
  clear(e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: '',
    })
  },
  pay: debounce(async function () {
    const {
      shopId,
      totalAmount,
      deductCoin,
      payPrice,
    } = this.data

    if (!totalAmount) {
      wx.showToast({
        title: '请填写订单金额！',
        icon: 'none'
      })
      return
    }

    wx.showLoading({
      title: '加载中',
    })

    if (this.data.isPay) {
      wx.hideLoading()
      return
    }
    this.setData({
      isPay: true,
    })

    orderCreate({
      shopId: shopId,
      totalAmount: totalAmount * 100,
      payAmount: payPrice * 100,
      deductCoin,
    }, resd => {
      if (resd && resd.code == 10000) {
        orderPay({
          ssearchId: resd.data.orderId
        }, ress => {
          if (ress && ress.code == 10000) {
            const payData = ress.data
            wx.hideLoading()
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
                this.setData({
                  isPay: false,
                })
              },
              fail: payRes => {
                wx.showToast({
                  title: '支付失败',
                  icon: 'error',
                  mask: true
                })
                this.setData({
                  isPay: false,
                })
              }
            })
          }
        })
      }
    }, err => {
      this.setData({
        isPay: false,
      })
    })
  }),
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const scene = options.scene
    console.log(options)
    this.setData({
      shopId: +scene
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
    this.getcoinshop()
    this.getUserGoldCoin()
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    this.setData({
      isPay: false,
    })
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    this.setData({
      isPay: false,
    })
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