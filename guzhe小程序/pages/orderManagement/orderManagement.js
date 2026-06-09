// pages/myOrder/myOrder.js
import {
  productOrder,
  shopProductOrderStat,
  productOrderExport,
  unDispatchedExport,
  expressImport,
  productOrderRefund,
  productOrderInfo
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
    showPopup1: false,
    showPopup1: false,
    showStartTime: new Date("2025-10-01").getTime(),
    showEndTime: new Date().getTime(),
    startTime: new Date("2025-10-01").getTime(),
    endTime: new Date().getTime(),
    minDate: new Date("2025-10-01").getTime(),
    maxDate: new Date().getTime(),
    forma: function (type, value) {
      if (type === 'year') {
        return `${value}年`;
      }
      if (type === 'month') {
        return `${value}月`;
      }
      if (type === 'day') {
        return `${value}日`;
      }
      return value;
    },
    showDOverlay: false,
    showAreaPicker: false,
    typeList: [{
        value: 0,
        text: '待支付'
      },
      {
        value: 1,
        text: '待使用'
      },
      {
        value: 2,
        text: '待发货'
      },
      {
        value: 3,
        text: '已发货'
      },
      {
        value: 4,
        text: '已完成'
      },
      {
        value: 5,
        text: '退款中'
      },
      {
        value: 6,
        text: '已退款'
      },
      {
        value: 7,
        text: '已过期'
      },
      {
        value: 8,
        text: '已取消'
      },
    ],
    typeName: '',
    typeStatus: '',
    applyUser: '',
    productName: '',
    orderNo: '',
    orderList: [],
    orderStat: {},
    pageNum: 1,
    pageSize: 10,
    total: 0,
    showRefund: false,
    // 退款理由
    refundReason: '',
    // 退款金额
    refundPrice: 100,
    refundData: {},
    showExport: false,
    x: 0,
    y: 0,
    statusList: [{
        value: 0,
        color: '#f5a031',
        text: '待支付',
      },
      {
        value: 1,
        color: '#8818ff',
        text: '待使用',
      },
      {
        value: 2,
        color: '#f5a031',
        text: '待发货',
      },
      {
        value: 3,
        color: '#2e99ff',
        text: '已发货',
      },
      {
        value: 4,
        color: '#27b43e',
        text: '已完成',
      },
      {
        value: 5,
        color: '#8b8b8b',
        text: '退款中',
      },
      {
        value: 6,
        color: '#da1c30',
        text: '已退款',
      },
      {
        value: 7,
        color: '#8b8b8b',
        text: '已过期',
      },
      {
        value: 8,
        color: '#8b8b8b',
        text: '已取消',
      },
    ],
    step: 100,
    scrollToOrderId: '',
  },
  getRefund: debounce(function () {
    const {
      refundData,
      refundPrice,
      refundReason,
      pageNum,
      pageSize
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
      orderId: refundData.id,
      refundAmount: refundPrice,
      refundReason,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: resd.msg || '退款成功!',
        })
        this.setData({
          showRefund: false,
          pageSize: pageNum * pageSize,
          pageNum: 1,
          scrollToOrderId: `order-${refundData.id}`,
        })
        this.getData()
      }
    })
  }),
  beforeRead(event) {
    const {
      file,
      callback
    } = event.detail;
    const fileExtension = file.url ? file.url.substring(file.url.lastIndexOf('.')).toLowerCase() : '';

    const isValidExtension = file.url ? ['.xlsx', '.xls'].includes(fileExtension) : false;
    console.log(file.type, isValidExtension)
    // 回调结果
    callback(file.type == 'file' && isValidExtension);
    // 如果不合法，可以给出提示
    if (!isValidExtension) {
      wx.showToast({
        title: '只支持 xlsx、xls格式',
        icon: 'none',
        duration: 2000
      });
    }
  },
  afterRead(event) {
    const {
      file
    } = event.detail;
    wx.showLoading({
      title: '上传中',
    })
    expressImport(file.url, resd => {
      wx.showToast({
        title: '导入成功！',
      })
      this.setData({
        showExport: false
      })
    })
  },
  async getData(e) {
    const {
      shopId,
      showStartTime,
      showEndTime,
      applyUser,
      productName,
      orderNo,
      typeStatus,
      pageNum,
      pageSize
    } = this.data

    const params = {
      searchField4: shopId,
      startTime: moment(showStartTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(showEndTime).format("YYYY-MM-DD 23:59:59"),
      keyword: applyUser || undefined,
      searchStrField2: productName || undefined,
      searchStrField1: orderNo || undefined,
      searchIntStatus: typeStatus || undefined,
    }

    wx.showLoading({
      title: '加载中',
    })

    await Promise.allSettled([this.getProductOrder({
      ...params,
      pageNum,
      pageSize
    }, e), this.getProductOrderStat(params)])

    wx.hideLoading()
  },
  getUnDispatchedExport() {
    const {
      shopId,
      showStartTime,
      showEndTime,
      applyUser,
      productName,
      orderNo,
      typeStatus,
      shopName,
    } = this.data
    wx.showLoading({
      title: '下载中',
    })
    unDispatchedExport(shopName + '订单', {
      searchField3: -1,
      searchField4: shopId,
      startTime: moment(showStartTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(showEndTime).format("YYYY-MM-DD 23:59:59"),
      keyword: applyUser || undefined,
      searchStrField2: productName || undefined,
      searchStrField1: orderNo || undefined,
      searchIntStatus: typeStatus || undefined,
    }, resd => {
      wx.showToast({
        title: '下载成功',
        icon: 'success',
        duration: 900
      });
      console.log(resd)
      setTimeout(() => {
        wx.openDocument({
          filePath: resd,
          showMenu: true,
          success() {

          }
        });
      }, 1000)
    })
  },
  getProductOrderExport() {
    const {
      shopId,
      showStartTime,
      showEndTime,
      applyUser,
      productName,
      orderNo,
      typeStatus,
      shopName,
    } = this.data
    wx.showLoading({
      title: '导出中',
    })
    productOrderExport(shopName + '订单', {
      searchField4: shopId,
      startTime: moment(showStartTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(showEndTime).format("YYYY-MM-DD 23:59:59"),
      keyword: applyUser || undefined,
      searchStrField2: productName || undefined,
      searchStrField1: orderNo || undefined,
      searchIntStatus: typeStatus || undefined,
    }, resd => {
      console.log(resd)
      wx.showToast({
        title: '下载成功',
        icon: 'success',
        duration: 900
      });
      console.log(resd)
      setTimeout(() => {
        wx.openDocument({
          filePath: resd,
          showMenu: true,
          success() {

          }
        });
      }, 1000)
    })
  },
  getProductOrder(params, e) {
    return new Promise((resolve, reject) => {
      productOrder(params, resd => {
        if (resd && resd.code == 10000) {
          wx.hideLoading()
          const data = resd.data?.list || []
          this.setData({
            orderList: e ? [...this.data.orderList, ...data] : data,
            total: resd.data.total,
          })
          resolve()
        }
      }, err => {
        reject(err)
      })
    })
  },
  getProductOrderStat(params) {
    params.searchStatusList=[1,2,3,4,5,7]
    return new Promise((resolve, reject) => {
      shopProductOrderStat(params, resd => {
        if (resd && resd.code == 10000) {
          this.setData({
            orderStat: resd.data,
          })
          resolve()
        }
      }, err => {
        reject()
      })
    })
  },
  showExport() {
    this.setData({
      showExport: true,
    });
  },
  cancelExport() {
    this.setData({
      showExport: false,
    });
  },
  onTouchStart(e) {
    this.startX = e.touches[0].clientX
    this.startY = e.touches[0].clientY
    this.elementX = this.data.x
    this.elementY = this.data.y
  },
  onTouchMove(e) {
    const moveX = (e.touches[0].clientX - this.startX) * this.rpxRatio
    const moveY = (e.touches[0].clientY - this.startY) * this.rpxRatio

    let newX = this.elementX + moveX
    let newY = this.elementY + moveY

    if (newX < this.data.minX) {
      newX = this.data.minX
    } else if (newX > this.data.maxX) {
      newX = this.data.maxX
    }

    // 确保新位置不超出Y轴边界
    if (newY < this.data.minY) {
      newY = this.data.minY
    } else if (newY > this.data.maxY) {
      newY = this.data.maxY
    }

    this.setData({
      x: newX,
      y: newY
    });
  },
  onTouchEnd(e) {
    const endX = e.changedTouches[0].clientX;
    const endY = e.changedTouches[0].clientY;
    const diffX = Math.abs(endX - this.startX);
    const diffY = Math.abs(endY - this.startY);

    if (diffX < 5 && diffY < 5) {
      this.getProductOrderExport();
    }
  },
  init() {
    const query = wx.createSelectorQuery().in(this)

    query.select('.swiper').boundingClientRect().select('.showSearch').boundingClientRect().exec((res) => {
      if (res.every(i => i)) {
        const swiper = res[0]
        const showSearch = res[1]
        this.setData({
          maxY: (swiper.top + swiper.height - showSearch.top - showSearch.height) * this.rpxRatio,
          minY: (swiper.top - showSearch.top) * this.rpxRatio,
          maxX: 0,
          minX: (swiper.left - showSearch.left) * this.rpxRatio,
        })
      }
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
    if (numericValue * 100 > this.data.refundData.amount) {
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
    if (flag && this.data.refundPrice + this.data.step > this.data.refundData.amount) {
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
  getProductOrderInfo: function (searchId) {
    return new Promise((resolve, reject) => {
      productOrderInfo({
        searchId,
      }, resd => {
        const data = resd.data
        const hadCheckNum = data?.ticketList?.length ? data?.ticketList.filter(i => i.status == 3).length : 0
        const orderInfo = data?.orderInfo || {}
        resolve(orderInfo.amount - orderInfo.price * hadCheckNum)
      },err=>{
        reject(-1)
      })
    })
  },
  async showRefund(e) {
    const {
      orderitem
    } = e.currentTarget.dataset

    wx.showLoading({
      title: '加载中',
    })
    const amount = await this.getProductOrderInfo(orderitem.id)
    if(amount == -1){
      return
    }

    wx.hideLoading()

    this.setData({
      showRefund: true,
      refundData: orderitem,
      refundPrice: amount,
      refundReason: '',
    })
  },
  changeInput: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: e.detail.value
    })
  },
  onDConfirm: function (e) {
    const pickerData = e.detail.value
    this.setData({
      showAreaPicker: false,
      typeName: pickerData.text,
      typeStatus: pickerData.value,
    })
  },
  onClickIcon() {
    this.setData({
      showAreaPicker: true,
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
  reset: function (e) {
    this.setData({
      productName: '',
      applyUser: '',
      orderNo: '',
      typeName: '',
      typeStatus: '',
      showStartTime: new Date("2025-10-01").getTime(),
      showEndTime: new Date().getTime(),
      startTime: new Date("2025-10-01").getTime(),
      endTime: new Date().getTime(),
      showDOverlay: false,
      pageNum: 1,
    })
    this.getData()
  },
  onConfirm() {
    this.setData({
      showDOverlay: false,
    })
    this.getData()
  },
  showSearch() {
    this.setData({
      showDOverlay: true,
    })
  },
  onInput: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
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
  goBack: function () {
    const app = getApp()
    app.globalData.refreshOrderId = null
    app.globalData.myPageSize = null
    wx.navigateBack({
      delta: 1,
    });
  },
  jump(e) {
    const {
      url
    } = e.currentTarget.dataset

    wx.navigateTo({
      url,
    })
  },
  selectTime() {
    this.setData({
      showPopup1: true,
    })
  },
  comfrimDate: function (e) {
    if (this.data.endTime < this.data.startTime) {
      wx.showToast({
        title: '结束时间不能小于开始时间',
        icon: 'none'
      })
      return
    }
    this.setData({
      showStartTime: this.data.startTime,
      showEndTime: this.data.endTime
    })
    this.onClose(e)
    this.getData()
  },
  scrollTolower() {
    const totalPages = Math.ceil(this.data.total / this.data.pageSize)
    const currentTotalPages = Math.ceil(this.data.orderList.length / this.data.pageSize)
    if (currentTotalPages < totalPages) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getData('e')
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log(options)
    const app = getApp()
    this.setData({
      shopId: +options?.shopId,
      shopName: options?.name,
      isIOS: app.globalData.isIOS,
    })
    this.init()
    this.rpxRatio = wx.getStorageSync('rpxRatio')
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
    let params = {}
    // 检查是否需要刷新（从退款审核页返回）
    if (app.globalData.needRefreshOrderList) {
      app.globalData.needRefreshOrderList = false
      const pageSize = app.globalData.myPageSize
      const targetId = app.globalData.refreshOrderId
      params = {
        scrollToOrderId: targetId ? `order-${targetId}` : '',
        pageSize: pageSize || 10,
      }
    }

    this.setData({
      ...params,
      pageNum: 1,
    })
    this.getData()
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    const app = getApp()
    app.globalData.refreshOrderId = null
    app.globalData.myPageSize = null
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    const app = getApp()
    app.globalData.refreshOrderId = null
    app.globalData.myPageSize = null
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