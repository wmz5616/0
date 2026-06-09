// pages/clock/clock.js
import {
  checkInfo,
  shopList,
  clickcountInc
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    shopList: [],
    showPopup: false,
    info: {
      teamData: [],
      recordInfo: {}
    },
    recordInfo: {},
    pageNum: 1
  },



  jump: function (e) {
    const {
      type,
      id
    } = e.currentTarget.dataset
    clickcountInc({searchId:id})
    // 1表示店铺信息，2表示商圈信息
    wx.navigateTo({
      url: `/pages/storeDetail/storeDetail?type=${+type}&storeId=${id}`,
    })
  },
  openlocations: function () {
    console.log(22)
    wx.openLocation({
      name:this.data.recordInfo.placeName,
      latitude: Number(this.data.recordInfo.location.split(',')[1]),
      longitude: Number(this.data.recordInfo.location.split(',')[0]),
      address: this.data.recordInfo.placeAddress,
      scale: 18,
      fail: e => {
        console.log(e)
      }
    })
  },
  openlocationsd: function (e) {
    const {item} =e.currentTarget.dataset
    wx.openLocation({
      name:item.name,
      latitude: Number(item.location.split(',')[1]),
      longitude: Number(item.location.split(',')[0]),
      address: item.address,
      scale: 18,
      fail: e => {
        console.log(e)
      }
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
  showComponent: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
    })
  },
  sharetimeLine: function () {
    this.setData({
      showPopup: false,
      showPopup2: true
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  getshopList: function () {
    shopList({
      location: `${this.data.longitude},${this.data.latitude}`,
      pageNum: this.data.pageNum
    }, resd => {
      this.setData({
        shopList: this.data.shopList.concat(resd.data.list),
        total: resd.data.total
      })
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log(options)
    this.setData({
      type: options.type,
      id: options.id,
    })
    this.getDisInfo()
    wx.getLocation({
      type: "gcj02",
      success: (res) => {
        this.setData({
          longitude: options.longitude,
          latitude: options.latitude
        })
        this.getshopList()
      }
    })
  },
  getDisInfo: function () {
    checkInfo({
      searchId: this.data.id
    }, resd => {
      this.setData({
        info: resd.data,
        recordInfo: resd.data.recordInfo
      })
      console.log(resd.data, 46546546)
    }, fa => {
      console.log(2312312, fa)
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
    if (Math.ceil(this.data.total / 10) > Math.ceil(this.data.shopList.length / 10)) {
      this.setData({
        pageNum: this.data.pageNum += 1
      })
      this.getshopList()
    }
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