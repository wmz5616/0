// pages/exchangeDetail/exchangeDetail.js
import {
  productOrderInfo,
  productOrderRefund
} from '../../utils/request'
// import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    processData: [{
        context: '已签收',
        time: '2025-06-28 11:23:50'
      },
      {
        context: '已签收',
        time: '2025-06-28 11:23:50'
      },
      {
        context: '已签收',
        time: '2025-06-28 11:23:50'
      },
      {
        context: '已签收',
        time: '2025-06-28 11:23:50'
      },
      {
        context: '已签收',
        time: '2025-06-28 11:23:50'
      },
    ],
    typeList: {
      0: '/assets/images/daizhifu.svg',
      1: '/assets/images/daishiyon.svg',
      2: '/assets/images/daifahuo.svg',
      3: '/assets/images/yifahuo.svg',
      4: '/assets/images/yiwancheng.svg',
      5: '/assets/images/tuikuanzhon.svg',
      6: '/assets/images/yituikuan.svg',
      7: '/assets/images/yiguoqi.svg',
      8: '/assets/images/yiquxiao.svg',
    },
    refundApplyInfo: {},
    orderAddress: {},
    ticketList: {},
    orderInfo: {},
    showDialog: false,
    showRefund: false,
    refundReason: '',
    step: 100,
    images: [],
  },
  copy(e) {
    const {
      text
    } = e.currentTarget.dataset
    if (!text) {
      wx.showToast({
        title: '暂无文本可复制',
        icon: "none"
      })
      return
    }
    wx.setClipboardData({
      data: text,
      success(res) {
        wx.showToast({
          title: '复制成功',
          icon: "success"
        })
      }
    })
  },
  showRefund(e) {
    const {
      orderInfo
    } = this.data

    this.setData({
      showRefund: true,
      refundPrice: orderInfo.refundAmount,
      refundReason: '',
    })
  },
  changePrice: function (e) {
    let inputValue = e.detail.value.trim()
    // 验证是否为有效数字（允许小数和负数）
    if (!inputValue || isNaN(inputValue) || !this.isValidNumber(inputValue)) {
      wx.showToast({
        title: '请输入有效数字',
        icon: 'none'
      });
      // 清空无效输入
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return;
    }
    // 转换为数字类型
    let numericValue = Number(inputValue);

    // 验证金额不能小于等于0
    if (numericValue <= 0) {
      wx.showToast({
        title: '退款金额要大于0',
        icon: 'none'
      });
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return;
    }

    // 验证金额不能大于退款金额
    if (numericValue * 100 > this.data.orderInfo.amount) {
      wx.showToast({
        title: '超出支付金额',
        icon: 'none'
      });
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return;
    }

    this.setData({
      refundPrice: numericValue * 100
    })
  },
  // 辅助函数：验证数字格式
  isValidNumber: function (value) {
    // 允许整数、小数、正数
    return /^-?\d*\.?\d+$/.test(value);
  },
  calc: function (e) {
    const {
      add
    } = e.currentTarget.dataset
    const flag = add > 0 ? true : false

    if (!flag && this.data.refundPrice <= this.data.step) {
      wx.showToast({
        title: '退款金额要大于0',
        icon: 'none'
      })
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return
    }
    if (flag && this.data.refundPrice + this.data.step > this.data.orderInfo.amount) {
      wx.showToast({
        title: '超出支付金额',
        icon: 'none'
      })
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return
    }
    this.setData({
      flag,
      refundPrice: Number(this.data.refundPrice) + add
    })
  },
  changeReason: function (e) {
    let inputValue = e.detail.value.trim()
    this.setData({
      refundReason: inputValue
    })
  },
  getRefund() {
    const {
      searchId,
      refundPrice,
      refundReason
    } = this.data
    if (!refundReason) {
      wx.showToast({
        title: '请填写退款理由！',
        icon: 'none'
      })
      return
    }
    if (!refundPrice) {
      wx.showToast({
        title: '不支持退款0元商品！',
        icon: 'none'
      })
      return
    }
    productOrderRefund({
      orderId: searchId,
      refundAmount: refundPrice,
      refundReason,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: resd.msg || '退款成功!',
        })
        this.setData({
          showRefund: false,
        })
        this.getProductOrderInfo()
      }
    })
  },
  getProductOrderInfo: function () {
    const {
      searchId
    } = this.data
    productOrderInfo({
      searchId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        const hadCheckNum = data?.ticketList?.length ? data?.ticketList.filter(i => i.status == 3).length : 0
        const orderInfo = data?.orderInfo || {}
        this.setData({
          processData: data.expressOrder ? data.expressOrder.infoData ? data.expressOrder.infoData : [] : [],
          kuaidiLogList: data.expressOrder ? data.expressOrder.infoData ? data.expressOrder.infoData : [] : [],
          refundApplyInfo: data.refundApplyInfo,
          orderAddress: data.orderAddress,
          ticketList: data.ticketList,
          orderInfo: {
            ...orderInfo,
            // deadline: moment(data.orderInfo.deadline).format('YYYY-MM-DD'),
            refundAmount: orderInfo.amount - orderInfo.price * hadCheckNum,
          },
          images: data?.refundApplyInfo?.images?.length ? data?.refundApplyInfo?.images.split(',') : []
        })
      }
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
      name,
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  dialogConfirm: function (e) {
    this.onClose(e)
  },
  goBack: function () {
    const app = getApp()
    app.globalData.needRefreshOrderList = true
    app.globalData.refreshOrderId = this.data.searchId
    app.globalData.myPageSize = this.data.myPageSize
    wx.navigateBack({
      delta: 1,
    });
  },
  jump: function (e) {
    const {
      url,
      item
    } = e.currentTarget.dataset
    wx.redirectTo({
      url: item ? url + `?data=${JSON.stringify(item)}` : url,
    })
  },
  go: function (e) {
    const {
      url,
    } = e.currentTarget.dataset
    wx.switchTab({
      url,
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      searchId: +options.searchId,
      myPageSize: +options.myPageSize,
    })
    this.getProductOrderInfo()
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