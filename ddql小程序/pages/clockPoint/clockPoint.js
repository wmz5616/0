// pages/clockPoint/clockPoint.js
import {
  placeList,
  userCaheckIn,
  runningInfo
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    runInfo: {},
    active: 0,
    value: '',
    siteData: []
  },
  jump: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    // 1表示店铺信息，2表示商圈信息
    // wx.navigateTo({
    //   url: `/pages/storeDetail/storeDetail?type=${+type}`,
    // })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  getrunningInfo: function () { //获取获取用户打卡中的数据，判断是否有打卡中数据
    runningInfo({}, resd => {
      this.setData({
        runInfo: resd.data || {}
      })
    })
  },
  getLocation: function () {
    wx.getLocation({
      type: "gcj02",
      success: (res) => {
        this.setData({
          latitude: res.latitude,
          longitude: res.longitude
        })
        this.getPlaceList()
      }
    })
  },
  getOderDetail: function () {
    wx.getSetting({
      success: (res) => {
        if (res.authSetting['scope.userLocation'] != undefined && res.authSetting['scope.userLocation'] != true) { //非初始化进入该页面,且未授权
          wx.showModal({
            title: '是否授权当前位置',
            content: '需要获取您的地理位置，请确认授权，否则无法获取您所需数据',
            cancelColor: "#333333",
            confirmColor: "#5DD5DE",
            success: (res) => {
              if (res.cancel) {
                wx.showToast({
                  title: '授权失败',
                  icon: 'success',
                  duration: 1000
                })
                this.getLocation();
              } else if (res.confirm) {
                wx.openSetting({
                  success: (dataAu) => {
                    if (dataAu.authSetting["scope.userLocation"] == true) {
                      wx.showToast({
                        title: '授权成功',
                        icon: 'success',
                        duration: 1000
                      })
                      //再次授权，调用getLocationt的API
                      this.getLocation();
                    } else {
                      wx.showToast({
                        title: '授权失败',
                        icon: 'error',
                        duration: 1000
                      })
                    }
                  }
                })
              }
            }
          })
        } else if (res.authSetting['scope.userLocation'] == undefined) { //初始化进入
          this.getLocation();
        } else { //授权后默认加载
          this.getLocation();
        }
      }
    })
  },
  search: function (e) {
    this.setData({
      keyword: e.detail
    })
  },
  comfrimsearch: function () {
    this.getPlaceList()
  },
  onChange: function (e) {
    this.setData({
      active: e.detail.index
    })
    this.getPlaceList()
  },
  callPhone: function (e) {
    const data = e.currentTarget.dataset.info
    wx.makePhoneCall({
      phoneNumber: data.contactPhone
    })
  },
  openlocations: function (e) {
    const data = e.currentTarget.dataset.info
    wx.openLocation({
      name: data.name,
      latitude: Number(data.location.split(',')[1]),
      longitude: Number(data.location.split(',')[0]),
      address: data.address,
      scale: 18,
      fail: e => {
        console.log(e)
      }
    })
  },
  getPlaceList: function () {
    placeList({
      keyword: this.data.keyword,
      searchType: this.data.active == 0 ? undefined : this.data.active - 1,
      searchStrField2: `${this.data.longitude},${this.data.latitude}`
    }, ss => {
      ss.data.map(xx => {
        xx.distanceM = Math.floor(xx.distance * 1000)
        xx.distance = xx.distance.toFixed(2)
        xx.img = xx.images?.split(';')[0]
      })
      this.setData({
        siteData: ss.data,
      })
    })
  },
  usecheck: function (e) { //距离打卡
    console.log(e)
    wx.showModal({
      title: '确定要打卡吗？',
      content: '',
      confirmColor: '#04C0D9',
      complete: (res) => {
        if (res.confirm) {
          userCaheckIn({
            placeId: e.currentTarget.dataset.placeid,
            checkInMethod: 1,
            userLocation: `${this.data.longitude},${this.data.latitude}`
          }, resd => {
            if (resd.code == 10000) {
              wx.showToast({
                title: resd.msg,
                icon: 'success'
              })
              wx.navigateTo({
                url: `/pages/clock/clock?id=${resd.data}&longitude=${this.data.longitude}&latitude=${this.data.latitude}`,
              })
            }
          })
        }
      }
    })
  },
  backcheck: function () { //离场打卡
    wx.showModal({
      title: '确定要打卡离场吗？',
      content: '',
      confirmColor: '#04C0D9',
      complete: (res) => {
        if (res.confirm) {
          userCaheckIn({
            placeId: this.data.runInfo.placeId,
            checkInMethod: this.data.runInfo.checkInMethod,
            equipmentId: this.data.runInfo.equipmentId,
            userLocation: `${this.data.longitude},${this.data.latitude}`
          }, resd => {
            if (resd.code == 10000) {
              this.setData({
                modalVisible: false
              })
              wx.showToast({
                title: resd.msg,
                icon: 'success'
              })
              wx.navigateTo({
                url: `/pages/clock/clock?id=${resd.data}&longitude=${this.data.longitude}&latitude=${this.data.latitude}&type=exit`,
              })
            }
          })
        }
      }
    })
  },
  getLocation: function () {
    wx.getLocation({
      type: "gcj02",
      success: (res) => {
        this.setData({
          latitude: res.latitude,
          longitude: res.longitude
        })
        this.getPlaceList()
      }
    })
  },
  getOderDetail: function () {
    wx.getSetting({
      success: (res) => {
        if (res.authSetting['scope.userLocation'] != undefined && res.authSetting['scope.userLocation'] != true) { //非初始化进入该页面,且未授权
          wx.showModal({
            title: '是否授权当前位置',
            content: '需要获取您的地理位置，请确认授权，否则无法获取您所需数据',
            cancelColor: "#333333",
            confirmColor: "#5DD5DE",
            success: (res) => {
              if (res.cancel) {
                wx.showToast({
                  title: '授权失败',
                  icon: 'success',
                  duration: 1000
                })
                this.getLocation();
              } else if (res.confirm) {
                wx.openSetting({
                  success: (dataAu) => {
                    if (dataAu.authSetting["scope.userLocation"] == true) {
                      wx.showToast({
                        title: '授权成功',
                        icon: 'success',
                        duration: 1000
                      })
                      //再次授权，调用getLocationt的API
                      this.getLocation();
                    } else {
                      wx.showToast({
                        title: '授权失败',
                        icon: 'error',
                        duration: 1000
                      })
                    }
                  }
                })
              }
            }
          })
        } else if (res.authSetting['scope.userLocation'] == undefined) { //初始化进入
          this.getLocation();
        } else { //授权后默认加载
          this.getLocation();
        }
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getOderDetail()
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
    if (wx.getStorageSync('token')) {
      this.getrunningInfo()
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