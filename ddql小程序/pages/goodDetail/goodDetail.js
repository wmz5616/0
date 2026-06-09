// pages/goodDetail/goodDetail.js
import {
  productInfo,
  selectAddress,
  productExchange,
  productExchangeOrderPayConfig
} from '../../utils/request'
import {
  formatNumber
} from '../../utils/util'
const WxParse = require('../../wxParse/wxParse')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    swiperCurrent: 0,
    productInfo: {},
    totalPrice: '',
    amount: 1,
    searchId: '',
    imgUrlsList: [],
    showPopup: false,
    addressInfo: {},
  },
  getProductExchange: function () {
    const {
      productInfo,
      amount,
      totalPrice,
      addressInfo
    } = this.data
    console.log(addressInfo, addressInfo?.id)
    if (!productInfo.isVirtual && !addressInfo?.id) {
      wx.showToast({
        title: '请先填写收货地址!',
        icon: 'none'
      })
      return
    }
    productExchange({
      productId: productInfo.id,
      num: amount,
      amount: totalPrice,
      addressId: !productInfo.isVirtual ? addressInfo.id : undefined,
    }, resd => {
      if (resd && resd.code == 10000) {
        if (productInfo.payWay == 1 || productInfo.payWay == 2) {
          // Requires WeChat Pay
          productExchangeOrderPayConfig({ orderId: resd.data }, payRes => {
            if (payRes && payRes.code == 10000) {
              const payData = payRes.data;
              wx.requestPayment({
                timeStamp: payData.timeStamp,
                nonceStr: payData.nonceStr,
                package: payData.packageVal,
                signType: payData.signType,
                paySign: payData.paySign,
                success: (res) => {
                  wx.showToast({ title: '支付成功！', icon: 'success' });
                  setTimeout(() => {
                    wx.navigateTo({ url: '/pages/myOrder/myOrder?activeTab=1' });
                    this.setData({ amount: 1, showOverlay: false });
                  }, 300);
                },
                fail: (err) => {
                  wx.showToast({ title: '支付取消', icon: 'none' });
                  setTimeout(() => {
                    wx.navigateTo({ url: '/pages/myOrder/myOrder?activeTab=1' });
                    this.setData({ amount: 1, showOverlay: false });
                  }, 300);
                }
              });
            }
          });
        } else {
          // Gold coin only
          wx.showToast({
            title: '支付成功！',
            icon: 'success'
          })
          setTimeout(() => {
            wx.navigateTo({
              url: '/pages/myOrder/myOrder?activeTab=1',
            })
            this.setData({
              amount: 1,
              showOverlay: false,
            })
          }, 300)
        }
      }
    })
  },
  change: function (e) {
    const amount = e.detail
    this.setData({
      amount,
      totalPriceStr: this.formatPrice(this.data.productInfo, amount),
      totalPrice: this.data.productInfo.payWay == 2 ? 0 : amount * this.data.productInfo.exchangeAmount,
    })
  },
  getSelectAddress: function () {
    const {
      selectAddressId
    } = this.data
    selectAddress({}, resd => {
      if (resd && resd.code == 10000) {
        // 先看也没有传进来选择的使用id，没有则使用默认
        const data = resd.data.find(i => selectAddressId ? i.id === selectAddressId : i.isDefault)
        console.log(data)
        this.setData({
          regionName: data?.regionList.map(xx => xx.name).join(""),
          addressInfo: data || {},
        })
        const app = getApp()
        app.globalData.selectAddressId = undefined
      }
    })
  },
  getProductInfo: function () {
    const {
      searchId
    } = this.data
    productInfo({
      searchId
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        WxParse.wxParse('newsDetail.contentHtml', 'html', data.detail, this, 5)
        let priceStr = this.formatPrice(data, 1);
        this.setData({
          productInfo: data,
          imgUrlsList: data.galleryImages,
          text: `已兑${formatNumber(data.exchangeNum)}，剩余：${formatNumber(data.stock)}个`,
          priceStr: priceStr,
          totalPriceStr: priceStr,
          totalPrice: data.payWay == 2 ? 0 : data.exchangeAmount,
        })
      }
    })
  },
  formatPrice: function (productInfo, amount = 1) {
    let totalCoin = productInfo.exchangeAmount * amount;
    let totalCashStr = (productInfo.payAmount * amount / 100).toFixed(2);
    let totalCashNum = parseFloat(totalCashStr);
    
    if (productInfo.payWay == 0) {
      return `${totalCoin}金币`;
    } else if (productInfo.payWay == 2) {
      return `${totalCashNum}元`;
    } else if (productInfo.payWay == 1) {
      return `${totalCoin}金币+${totalCashNum}元`;
    }
    return `${totalCoin}金币`;
  },
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url,
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
  open: function (e) {
    wx.requestSubscribeMessage({
      tmplIds: ['_s4-hLadE9vazuwgWZZOrXvVm6EJLF7RpcJCF0oWDxE', '0MUceUhZ3J_iBcVMOYQRqG9dZQiRUp1q5iTZGMn0-Lg', 'dOLxXPLViMotzzIMHUp3z5D5I5_W6b5RyoK3QFHo54Y'],
      fail: (res) => {
        console.log(res)
      }
    })
    if (!this.data.token) {
      wx.navigateTo({
        url: '/pages/login/login',
      })
      return
    }
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
    })
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  swiperChange: function (e) {
    this.setData({
      swiperCurrent: e.detail.current
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      searchId: +options.searchId,
      token: wx.getStorageSync('token')
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
    const app = getApp()
    this.setData({
      selectAddressId: app.globalData.selectAddressId,
    })
    this.getProductInfo()
    if (wx.getStorageSync('token')) {
      this.getSelectAddress()
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