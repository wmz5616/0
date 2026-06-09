// pages/map/map.js
import {
  locationKey
} from '../../utils/request'

var QQMapWX = require('../../lib/qqmap-wx-jssdk.min.js');
var qqmapsdk = new QQMapWX({
  key: locationKey,
})

Page({

  /**
   * 页面的初始数据
   */
  data: {
    keyword: '',
    myLatitude: '',
    myLongitude: '',
    latitude: '',
    longitude: '',
    keyword: '',
    markers: [],
    showSuggestion: true,
    suggestionList: [],
    selectAddress: {}
  },
  searchLocation() {
    var _this = this;
    qqmapsdk.search({
      keyword: this.data.keyword,
      success: function (res) {
        console.log(res)
        var mks = []
        for (var i = 0; i < res.data.length; i++) {
          const data = res.data[i]
          mks.push({ // 获取返回结果，放到mks数组中
            title: data.title,
            id: i,
            latitude: data.location.lat,
            longitude: data.location.lng,
            iconPath: "/assets/images/locationIcon.svg",
            width: 20,
            height: 20,
            callout: {
              content: data.title,
              color: '#333',
              fontSize: 12,
              borderRadius: 8,
              bgColor: '#FFFFFF',
              padding: 6,
              display: 'ALWAYS', // 始终显示
              textAlign: 'center',
              borderWidth: 1,
              borderColor: '#E0E0E0',
              boxShadow: '0 2rpx 8rpx rgba(0,0,0,0.1)'
            },
          })
        }
        _this.setData({
          markers: mks,
          longitude: mks.length ? mks[0]?.longitude : this.data.longitude,
          latitude: mks.length ? mks[0]?.latitude : this.data.latitude,
        })
      },
      fail: function (res) {
        console.log(res);
      },
      complete: function (res) {
        console.log(res);
      }
    })
  },
  getLocationList() {
    var _this = this;
    qqmapsdk.getSuggestion({
      keyword: this.data.keyword,
      success: function (res) {
        var sug = [];
        for (var i = 0; i < res.data.length; i++) {
          const data = res.data[i]
          sug.push({
            title: data.title,
            id: data.id,
            addr: data.address,
            city: data.city,
            district: data.district,
            latitude: data.location.lat,
            longitude: data.location.lng
          });
        }
        _this.setData({
          suggestionList: sug,
          showSuggestion: true,
        });
      },
      fail: function (error) {
        console.error(error);
      },
      complete: function (res) {
        console.log(res);
      }
    })
  },

  selectLocation(e) {
    const data = this.data.markers[e.detail.markerId]
    this.setData({
      selectAddress: data
    })
  },
  submitAddress:function(){
  if(!this.data.selectAddress.title){
    wx.showToast({
      title: '请选择位置信息',
      icon:'none'
    })
    return
  }
  console.log(this.data.selectAddress)
  wx.setStorageSync('selectAddress', this.data.selectAddress)
  wx.navigateBack({
    delta: 1,
  });
  },
  getva: function (e) {
    this.setData({
      keyword: e.detail
    })
  },
  getUserLocation() {
    // wx.getLocation({
    //   type: 'gcj02',
    //   success: (res) => {
    //     console.log(res)
    //     this.setData({
    //       latitude: +res.latitude,
    //       longitude: +res.longitude,
    //       myLongitude: +res.longitude,
    //       myLatitude: +res.latitude
    //     })
    //   },
    //   fail: (a) => {
    //     console.log(a)
    //   }
    // })
    this.setData({
      longitude: 113.31,
      latitude: 22.39,
    })
  },
  onCancel: function () {
    this.setData({
      keyword: '',
    })
  },
  onSearch: function (e) {
    this.searchLocation()
  },
  goBack: function () {
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
    this.getUserLocation()
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