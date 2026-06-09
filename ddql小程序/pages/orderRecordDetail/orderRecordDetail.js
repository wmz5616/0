// pages/lotteryDetail/lotteryDetail.js
import {
  // productInfo,
  // productorderAdd,
  // updateProductOrder,
  // productOrderPay,
  // cancelProductOrder,
  // selectAddress,
  // EOrderInfo,
  // userInfo
} from '../../utils/request'
import {
  debounce
} from '../../utils/util'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    processData: [],
    refundApplyInfo: {},
    orderAddress: {},
    ticketList: {},
    orderInfo: {},
    remark: '',
    disabled: true,
    showDialog: false,
    defaultAddressId: undefined,
    zcx: [],
    isEOrder: false,
    isPay: false,
    showDialog1: false,
    userInfo: {},
  },
  getUserInfo: function () {
    return new Promise((resolve, reject) => {
      userInfo({}, re => {
        if (re.code == 10000) {
          wx.setStorageSync('userinfo', re.data)
          this.setData({
            userInfo: re.data
          })
          resolve()
        }
      }, err => {
        this.setData({
          userInfo: wx.getStorageSync('userinfo') || {}
        })
        reject()
      })
    })
  },
  call() {
    const {
      customerPhone
    } = this.data
    wx.makePhoneCall({
      phoneNumber: customerPhone,
    })
  },
  onClose(e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: false,
    })
  },
  getCancelProductOrder() {
    wx.showLoading({
      title: '取消中！',
    })
    cancelProductOrder({
      searchId: this.data.searchId,
    }, resd => {
      wx.showToast({
        title: '操作成功！',
      })
      this.getProductInfo()
    }, err => {
      wx.hideLoading()
    })
  },
  open(e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: true,
    })
  },
  watch() {
    const {
      customerCodeImg,
    } = this.data

    wx.previewImage({
      urls: [customerCodeImg],
    })
  },
  getEOrderInfo(productId, num, equipmentId, addressId) {
    EOrderInfo({
      productId,
      num,
      equipmentId,
      addressId,
    }, resd => {
      const data = resd.data
      console.log(data)
      this.setData({
        customerCodeImg: data?.customerCodeImg || '',
        customerPhone: data?.customerPhone || '',
        processData: data.expressOrder ? data.expressOrder : [],
        orderInfo: {
          ...data.productInfo,
          deadline: moment(data.productInfo.deadline).format('YYYY-MM-DD'),
          num: data.num,
          amount: data.amount,
          shopName: data.shopName,
          createTime: moment().format('YYYY-DD-MM HH:mm:ss')
        },
        isEOrder: true,
      })
    })
  },
  getProductInfo() {
    const {
      searchId
    } = this.data
    productInfo({
      searchId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        this.setData({
          processData: data.expressOrder ? data.expressOrder.infoData ? data.expressOrder.infoData : [] : [],
          kuaidiLogList: data.expressOrder ? data.expressOrder.infoData ? data.expressOrder.infoData : [] : [],
          refundApplyInfo: {
            ...data.refundApplyInfo,
            images: data.refundApplyInfo?.images ? data.refundApplyInfo?.images?.split(',') : [],
          },
          orderAddress: data.orderAddress || {},
          ticketList: data.ticketList,
          customerCodeImg: data?.customerCodeImg || '',
          customerPhone: data?.customerPhone || '',
          remark: data.orderInfo?.remark,
          selectAddressId: data.orderAddress?.addressId,
          orderInfo: {
            ...data.orderInfo,
            deadline: moment(data.orderInfo.deadline).format('YYYY-MM-DD')
          },
        })
      }
    })
  },
  changeAble: function (e) {
    const {
      disabled
    } = e.currentTarget.dataset
    const {
      orderInfo
    } = this.data

    // 只有待支付 0 才能修改
    if (orderInfo.status) {
      wx.showToast({
        title: '待支付订单才能修改',
        icon: 'none'
      })
      return
    }
    this.setData({
      disabled,
    }, () => {
      if (disabled) this.getUpdateProductOrder()
    })
  },
  onChange(e) {
    this.setData({
      remark: e.detail
    })
  },
  copy(e) {
    const {
      text
    } = e.currentTarget.dataset
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
  async addOrder() {
    const {
      zcx
    } = this.data
    const addressId = await this.getSelectAddress()

    const productId = zcx[0]
    const num = zcx[1]
    const equipmentId = zcx[2]
    console.log(zcx)

    productorderAdd({
      productId,
      num,
      equipmentId,
      addressId,
    }, resd => {
      if (resd.code == 10000) {
        this.setData({
          searchId: resd.data,
          zcx: [],
        })
        this.getProductInfo()
      }
    }, err => {
      this.getEOrderInfo(productId, num, equipmentId, addressId)
    })
  },
  getSelectAddress() {
    return new Promise((resolve, reject) => {
      selectAddress({}, resd => {
        const data = resd.data || []
        const defaultAddressId = data?.find(i => i.isDefault == 1)?.id || data[0]?.id || undefined

        resolve(defaultAddressId)
      }, error => {
        reject(undefined)
      })
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if (options.searchId) {
      this.setData({
        searchId: +options.searchId,
      })
    } else if (options.scene) {
      const zcx = options.scene.split('_')
      console.log(options, zcx)
      this.setData({
        zcx: zcx.map(i => +i),
      })
    }
  },
  shouldUpdate() {
    const app = getApp()
    const selectAddressId = app.globalData.selectAddressId
    if (!selectAddressId) {
      return
    }
    this.setData({
      selectAddressId: app.globalData.selectAddressId
    })
    this.getUpdateProductOrder()
  },
  getUpdateProductOrder() {
    const {
      selectAddressId,
      orderInfo,
      remark,
    } = this.data
    wx.showLoading({
      title: '更新中',
    })
    updateProductOrder({
      orderId: orderInfo.id,
      addressId: selectAddressId,
      remark,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '修改成功！',
        })
        this.getProductInfo()
        const app = getApp()
        app.globalData.selectAddressId = undefined
      }
    }, err => {
      const app = getApp()
      app.globalData.selectAddressId = undefined
    })
  },
  selectAddress(e) {
    const {
      url,
    } = e.currentTarget.dataset
    const {
      orderInfo
    } = this.data

    // 只有待支付 0 才能修改
    if (orderInfo.status) {
      wx.showToast({
        title: '待支付订单才能修改',
        icon: 'none'
      })
      return
    }
    wx.navigateTo({
      url,
    })
  },
  jump: function (e) {
    const {
      url,
      item
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: item ? url + `?data=${JSON.stringify(item)}` : url,
    })
  },
  goBack: function () {
    const pages = getCurrentPages();

    if (pages.length <= 1) {
      wx.switchTab({
        url: '/pages/index/index'
      });
    } else {
      wx.navigateBack({
        delta: 1,
      });
    }

    const app = getApp();
    app.globalData.selectAddressId = undefined;
  },
  pay: debounce(async function () {
    const {
      orderInfo,
    } = this.data

    wx.showLoading({
      title: '加载中',
    })

    await this.getUserInfo()

    // 判断账号是否被锁定
    if (this.data.userInfo.lock) {
      wx.hideLoading()
      this.setData({
        showDialog1: true,
      })
      return
    }

    if (this.data.isPay) {
      wx.hideLoading()
      return
    }
    this.setData({
      isPay: true,
    })

    productOrderPay({
      searchId: orderInfo.id,
    }, resd => {
      if (resd && resd.code == 10000) {
        const payData = resd.data
        wx.hideLoading()
        wx.requestPayment({
          timeStamp: payData.timeStamp.toString(),
          nonceStr: payData.nonceStr,
          package: payData.package,
          paySign: payData.paySign,
          signType: payData.signType,
          success: payRes => {
            wx.requestSubscribeMessage({
              tmplIds: ['bSwdCAsvwIFyxTjFSIIFCFkHQHlKNK3ry_CbXzeFYBU', 'LoTfGAxJ88WFrSezbxgRfwanYXP2LR_IJ_lBTtXixEQ'],
              success(res) {}
            })
            wx.showToast({
              title: '支付成功',
              icon: 'success',
              duration: 800,
              mask: true
            })
            this.setData({
              isPay: false,
            })
            this.getProductInfo()
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
            console.log(payRes)
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
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    this.shouldUpdate()
    if (this.data.zcx.length) {
      this.addOrder()
    } else {
      this.getProductInfo()
    }
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