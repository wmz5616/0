// pages/storeDetail/storeDetail.js
import {
  shopList,
  shangquanshopList,
  circleInfo
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    shangquanInfo:{},
    ctrieList: [],
    shopData: [],
    // 顶部title,
    title: "有家咖啡店",
    swiperCurrent: 0,
    imgUrlsList: [
    ],
    showPopup: false,
    pageNum: 1,
  },
  openlocations: function () {
    console.log(22)
    if(this.data.type==1){
      wx.openLocation({
        name: this.data.shopData.name,
        latitude: Number(this.data.shopData.location.split(',')[1]),
        longitude: Number(this.data.shopData.location.split(',')[0]),
        address: this.data.shopData.address,
        scale: 18,
      })
    }
    else{
      console.log(this.data.shangquanInfo)
      wx.openLocation({
        name: this.data.shangquanInfo.name,
        latitude: Number(this.data.shangquanInfo.location.split(',')[1]),
        longitude: Number(this.data.shangquanInfo.location.split(',')[0]),
        address: this.data.shangquanInfo.locationName,
        scale: 18,
      })
    }
  },
  openlocationsd: function (e) {
    const {
      item
    } = e.currentTarget.dataset
    console.log(22)
    wx.openLocation({
      name: item.name,
      latitude: Number(item.location.split(',')[1]),
      longitude: Number(item.location.split(',')[0]),
      address: item.address,
      scale: 18,
      fail: e => {
        console.log(e)
      }
    })
  },
  getshopList: function () {
    console.log(`${this.data.longitude},${this.data.latitude}`)
    shopList({
      location: `${this.data.longitude},${this.data.latitude}`,
      pageNum: this.data.pageNum
    }, resd => {
      const shopData = resd.data.list.find(i => i.id == this.data.storeId)
      this.setData({
        shopData,
        title: shopData.name,
        text: shopData.circleList ? shopData.circleList.map(xx=>xx.circleName).join('、') : [],
        total: resd.data.total,
        imgUrlsList: shopData.galleryImages,
      })
    })
  },
  swiperChange: function (e) {
    this.setData({
      swiperCurrent: e.detail.current
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  jump: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    // 1表示店铺信息，2表示商圈信息
    wx.navigateTo({
      url: `/pages/storeDetail/storeDetail?type=${+type}`,
    })
  },
  showComponent: function (e) {
    // const {
    //   name
    // } = e.currentTarget.dataset
    // this.setData({
    //   [name]: true,
    // })
  },
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  getshangquanshopList: function () {
    shangquanshopList({
      searchId: this.data.storeId,
      searchStrField2: `${this.data.longitude},${this.data.latitude}`,
      pageSize: 999
    }, resd => {
      if (resd.code == 10000) {
        resd.data.list.map(x => {
          x.distance = x.distance.toFixed(2)
        })
        this.setData({
          ctrieList: resd.data.list
        })
      }
    })
  },
  getcircleInfo: function () {
    circleInfo({
      searchId: this.data.storeId
    }, resd => {
      if(resd.code==10000){
        this.setData({
          shangquanInfo:resd.data
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const {
      type,
      storeId
    } = options
    // type存在,判断是2商圈还是1店铺
    if (type) {
      this.setData({
        type,
        storeId: +storeId,
      })
    }
    console.log(storeId)
    wx.getLocation({
      type: "gcj02",
      success: (res) => {
        this.setData({
          longitude: res.longitude,
          latitude: res.latitude
        })
        if (+type == 1) {
          this.getshopList()
        } else if (+type == 2) {
          this.getshangquanshopList()
          this.getcircleInfo()
        }
      }
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
    return {
      title: `都动起来`,
      path: '/pages/index/index'
    };
  },

  onShareTimeline() {
    return {
      title: `都动起来`,
      path: '/pages/index/index'
    };
  }
})