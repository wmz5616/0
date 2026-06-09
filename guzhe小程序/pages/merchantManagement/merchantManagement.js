// pages/merchantManagement/merchantManagement.js
import {
  getshopDetail,
  managerList,
  businessDataDetail
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    businessInfo: {},
    startTime: new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
    endTime: new Date().getTime(),
    minDate: new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
    maxDate: new Date(new Date().getFullYear() + 1, 11, 31).getTime(),
    tempStartTime: new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
    tempEndTime: new Date().getTime(),
    time: new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
    showTime: false,
    activeTime: 1,
    businessData: {},
    formatter(type, value) {
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
    iconList: [{
        url: '/pages/applySettlement/applySettlement',
        imgIcon: '/assets/images/merchantInfo.svg',
        text: '商家信息',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/productManagement.svg',
        text: '商品管理',
        url: '/pages/merchantShjoppList/merchantShjoppList',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/commodityOrderManagement.svg',
        text: '商品订单管理',
        url: '/pages/orderManagement/orderManagement',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/orderRefundReview.svg',
        text: '订单退款审核',
        url: '/pages/refundList/refundList',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/productVerification.svg',
        text: '商品核销',
        url: '/pages/couponVerification/couponVerification',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/screenStoreRental.svg',
        text: '屏幕店位租用',
        url: '/pages/screenBoothRental/screenBoothRental',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/storeLocationOrder.svg',
        text: '店位订单',
        url: '/pages/storeLocationOrder/storeLocationOrder',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/posterManagement.svg',
        text: '海报管理',
        url: '/pages/imgManageMent/imgManageMent',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/settlementRecord.svg',
        text: '结算记录',
        url: '/pages/settleTheOrder/settleTheOrder',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/permissionManagement.svg',
        text: '权限管理',
        url: '/pages/permissionManagement/permissionManagement'
      },
      {
        imgIcon: '/assets/images/collectionMerchantManagement.svg',
        text: '收款商户管理',
        url: '/pages/addmerchant/addmerchant',
        isShow: true,
      },
      {
        imgIcon: '/assets/images/contractPhoto.svg',
        text: '合同照片',
        url: '/pages/imgManageMent/imgManageMent',
        isShow: true,
      },
    ],
    isIOS: false,
    shopDetail: {},
  },
  getshopDetail: function () {
    getshopDetail({
      searchId: this.data.id
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          merchantId: resd.data?.vo.merchantId || 0,
          shopDetail: {
            ...(resd.data?.vo || {}),
            shopPosters: resd.data?.shopPosters || [],
          }
        })
      }
    })
  },
  getbusinessDataDetail: function () {
    businessDataDetail({
      searchField4: this.data.id,
      startTime: moment(this.data.startTime).format('YYYY-MM-DD 00:00:00'),
      endTime: moment(this.data.endTime).format('YYYY-MM-DD 23:59:59')
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        this.setData({
          businessData: {
            ...data,
            revenue: data.revenue ? data.revenue.toFixed(2) : 0,
            pendingAmount: data.pendingAmount ? data.pendingAmount.toFixed(2) : 0
          },
          iconList: this.data.iconList.map(i => {
            if (i.text == '订单退款审核') {
              return {
                ...i,
                badge: data?.refundCount || 0,
              }
            }
            return i
          }),
        })
      }
    })
  },
  close: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: false,
    })
  },

  showTime() {
    this.setData({
      showTime: true,
    })
  },

  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  jump(e) {
    const {
      url,
      text
    } = e.currentTarget.dataset
    if (url == '/pages/addmerchant/addmerchant' && this.data.shopDetail.receiptStatus == 0) {
      wx.showToast({
        title: '门店收款功能未开启，请联系管理员处理',
        icon: 'none'
      })
      return
    }
    const otherUrl = text == '合同照片' ? `&imgType=${2}` : text == '海报管理' ? `&imgType=${1}` : ''
    wx.navigateTo({
      url: url + `?shopId=${this.data.id}&name=${this.data.shopDetail.name}&type=shoplist` + otherUrl + `&merchantId=${this.data.merchantId}`,
    })
  },

  changeTime(e) {
    const index = Number(e.currentTarget.dataset.index)
    console.log(index)
    this.setData({
      activeTime: index,
      time: index == 1 ? this.data.tempStartTime : this.data.tempEndTime,
    })
  },

  onInput(e) {
    const {
      activeTime
    } = this.data
    const value = e.detail
    this.setData({
      [activeTime == 1 ? "tempStartTime" : "tempEndTime"]: value,
      time: value,
    })
  },

  reset() {
    const {
      activeTime
    } = this.data
    const app = getApp()
    const tempStartTime = app.globalData.LstartTime || new Date(new Date().getFullYear(), new Date().getMonth() - 3, 1).getTime()
    const tempEndTime = app.globalData.LendTime || new Date().getTime()
    this.setData({
      tempStartTime,
      tempEndTime,
      time: activeTime == 1 ? tempStartTime : tempEndTime,
    })
  },

  confirm() {
    const {
      tempStartTime,
      tempEndTime
    } = this.data
    if (tempStartTime > tempEndTime) {
      wx.showToast({
        title: '开始时间大于结束时间',
        icon: 'none'
      })
      return
    }
    this.setData({
      startTime: tempStartTime,
      endTime: tempEndTime,
      showTime: false,
    })
    this.getbusinessDataDetail()
  },
  getManagerList() {
    managerList({
      searchField1: this.data.id,
    }, resd => {
      if (resd && resd.code == 10000) {
        const list = resd.data || []
        const isCreater = list.some(i => i.headManager == 1 && this.data.userInfo?.phone == i.phone)
        let iconList = [...this.data.iconList]
        const index = iconList.findIndex(i => i.text == '权限管理')
        iconList[index].isShow = isCreater
        this.setData({
          iconList,
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    this.setData({
      id: +options.shopId,
      isIOS: app.globalData.isIOS,
      userInfo: wx.getStorageSync('userinfo')
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
    this.getManagerList()
    this.getshopDetail()
    this.getbusinessDataDetail()
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