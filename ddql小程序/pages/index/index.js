// pages/login/login.js
var QQMapWX = require('../../utils/qqmap-wx-jssdk.min');
var qqmapsdk = new QQMapWX({
  key: '7EZBZ-DNNWZ-L7HXL-TPRJ5-S5PJF-LBFEH'
});
import {
  todaySportInfo,
  setStepNum,
  userInfo,
  placeList,
  userCaheckIn,
  selectUserTeams,
  equipmentInfo,
  runningInfo,
  checkInSetting,
  showList,
  noticeList,
  getPlaceInfo,
  checkiinType,
  checkiincancel
} from '../../utils/request'
import {
  formatSecond,
  WXBizDataCrypt,
} from '../../utils/util'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    checkiinTypeList: [],
    showList: [],
    isOpen: false,
    min: 0,
    checkInfo: {},
    runInfo: {},
    deviceInfo: {},
    teamList: [],
    siteData: [],
    userinfo: {},
    alloWSiteData: [],
    modalVisible: false,
    swiperCurrent: 0,
    sessionKey: '',
    imgUrlsList: [
      '/assets/images/clockImage.png',
      '/assets/images/clockImage.png',
    ],
    noticeList: [],
    showInfo1: false,
    showInfo2: false,
    timer1: null,
    timer2: null,
    showPopup: false,
    showDialog: false,
    pre: 0,
    // src: '',
    sportInfo: {},
    showAudit: false,
    checked: false,
  },
  // canvasReady:function(e){
  //   console.log(e.detail.tempFilePath)
  //   this.setData({
  //     src: e.detail.tempFilePath
  //   })
  // },
  selected() {
    this.setData({
      checked: !this.data.checked,
    });
  },
  closeOver(){
    this.setData({
      showAudit: false,
    });
  },
  getNoticeList: function () {
    noticeList({}, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          noticeList: resd.data.list,
          total: resd.data.total,
        })
      }
    })
  },
  gobaseInfo: function (e) {
    const {
      url,
      type
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url + (type ? '&isAdmin=true' : ''),
    })
  },
  // 获取今日运动信息
  getTodaySportInfo: function () {
    todaySportInfo({}, resd => {
      if (resd.code && resd.code == 10000) {
        const data = resd.data || {}
        const runCheckTime = this.data.runInfo.checkInTime ? this.data.runInfo.checkInTime : 0
        data.checkInTimes = formatSecond(data.checkInTime + runCheckTime)
        if (data.checkInTimes == 'NaN:NaN:NaN') {
          data.checkInTimes = '00:00:00'
        }
        this.setData({
          min: Math.floor((data.checkInTime % 3600) / 60),
          sportInfo: data,
          pre: (data.stepNum / this.data.targetSteps) * 100,
        })
        console.log(this.data.sportInfo)
      }
    })
  },
  jumpNotice: function () {
    wx.navigateTo({
      url: '/pages/notice/notice',
    })
  },
  jump: function (e) {
    const {
      item
    } = e.currentTarget.dataset
    const urlItem = [
      '/pages/index/index',
      '/pages/rank/rank',
      '/pages/exchange/exchange',
      '/pages/center/center'
    ]
    if (item.type == 0) {
      return
    }
    if (item.type == 2) {
      if (urlItem.find(x => x == item.url)) {
        wx.switchTab({
          url: item.url,
        })
      } else {
        wx.navigateTo({
          url: `${item.url}?healthCoin=${this.data.userinfo.healthCoin}&goldCoin=${this.data.userinfo.goldCoin}`,
        })
      }
    } else {
      wx.navigateTo({
        url: `/pages/web-view/web-view?url=${item.link}`,
      })
    }
    console.log(item)
  },
  closesign: function () {
    this.setData({
      modalVisible: false
    })
  },
  // 更新今日步数
  getSetStepNum: function () {
    setStepNum({
      stepNum: this.data.wxStep || 0
    }, resd => {
      if (resd && resd.code == 10000) {
        // 获取今日运动信息
        this.getTodaySportInfo()
      }
    })
  },
  equipmentInfo: function (e) { //获取设备详情
    getPlaceInfo({
      searchId: this.data.placeId
    }, resd => {
      this.setData({
        deviceInfo: resd.data || {}
      })
      if (resd.data) {
        wx.showModal({
          title: '到场打卡',
          content: `场地名称：${this.data.deviceInfo.name}，打卡类型：${this.data.deviceInfo.checkInTypeName}，扫码打卡`,
          confirmText: '立即打卡',
          confirmColor: '#5DD5DE',
          complete: (res) => {
            if (res.confirm) {
              userCaheckIn({
                equipmentId: this.data.deviceId,
                placeId: this.data.placeId,
                checkInMethod: 0,
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
      } else {
        wx.showToast({
          title: '暂无设备',
          icon: "none"
        })
      }
    })
  },
  change: function () { //扫码打卡
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({
        url: '/pages/login/login',
      })
      return
    }
    if (this.data.runInfo.checkInMethod == 1) { //离场打卡判断
      return
    }
    if (this.data.runInfo.checkInMethod == 0) { //离场打卡判断
      this.exitUseCheck()
      return
    }
    wx.scanCode({
      success: (res) => {
        const params = res.path.split('scene=')[1].split('_')
        this.setData({
          deviceId: params[0],
          placeId: params[1]
        })
        this.equipmentInfo()
      },
      fail: (e) => {
        wx.showToast({
          title: '获取失败',
          icon: 'none'
        })
      }
    })
  },
  exitUseCheck: function () { //离场打卡判断
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
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
    if (name == 'showPopup' || name == 'showPopup1') {
      wx.showTabBar({
        animation: true // 可选，是否开启动画效果
      })
    }
  },
  showComponent: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    if (name == 'showPopup') {
      wx.hideTabBar({
        animation: true // 可选，是否开启动画效果
      })
    }
    this.setData({
      [name]: true,
    })
    this.getTodaySportInfo()
  },
  go: function (e) {
    const {
      src
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: `${src}?longitude=${this.data.longitude}&latitude=${this.data.latitude}&healthCoin=${this.data.userinfo.healthCoin}&goldCoin=${this.data.userinfo.goldCoin}`,
    })
  },
  showMos: function () {
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({
        url: '/pages/login/login',
      })
      return
    }
    if (this.data.alloWSiteData.length == 0) {
      return
    }
    if (this.data.runInfo.checkInMethod === 0) { //离场打卡判断
      return
    }
    if (this.data.runInfo.checkInMethod == 1) { //离场打卡判断
      this.exitUseCheck()
      return
    }
    this.setData({
      modalVisible: true
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
              this.setData({
                modalVisible: false
              })
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
  closesgin: function () {
    this.setData({
      modalVisible: false
    })
  },
  swiperChange: function (e) {
    this.setData({
      swiperCurrent: e.detail.current
    })
  },
  showInfo: function (e) {
    const {
      name,
      timer
    } = e.currentTarget.dataset
    if (this.data[timer]) return
    console.log(111)
    const timerr = setTimeout(() => {
      clearTimeout(this.data[timer])
      this.setData({
        [timer]: null,
      })
      this.onClose(e)
    }, 3000)
    this.setData({
      [name]: true,
      [timer]: timerr,
    })
  },

  // 获取微信步数
  getWeRunStep: function () {
    const {
      sessionKey
    } = this.data
    console.log(sessionKey, 2323)
    wx.getWeRunData({
      success: (res) => {
        const encryptedData = res.encryptedData
        const iv = res.iv
        const appId = "wx308569eed4cced50"

        var pc = new WXBizDataCrypt(appId, sessionKey)
        let data = ''
        try {
          data = pc.decryptData(encryptedData, iv)
        } catch (e) {
          wx.clearStorageSync()
          wx.reLaunch({
            url: '/pages/login/login',
          })
        }
        console.log('解密后 data: ', data)
        const dds = data.stepInfoList.find(xx => xx.timestamp * 1000 >= new Date(moment().startOf('day')).getTime()).step
        if (dds != this.data.wxStep) {
          this.setData({
            wxStep: dds
          }, () => {
            // 更新今日步数
            this.getSetStepNum()
          })
        }
      },
      fail(err) {
        console.log(err, 45666666666666)
      }
    })
  },
  showOpenSetting: function () {
    wx.openSetting({
      success: (dataAu) => {
        if (dataAu.authSetting["scope.werun"] == true) {
          wx.showToast({
            title: '授权成功',
            icon: 'success',
            duration: 1000
          })
          this.setData({
            isOpen: false,
          })
          this.getSetting()
        } else {
          wx.showToast({
            title: '授权失败',
            icon: 'error',
            duration: 1000
          })
          this.setData({
            isOpen: true,
          })
        }
      }
    })
  },
  getSetting: function () {
    wx.getSetting({
      success: (res) => {
        console.log(res.authSetting['scope.werun'])
        if (!res.authSetting['scope.werun']) {
          wx.authorize({
            scope: 'scope.werun',
            success: res => {
              console.log(12231231)
              this.getWeRunStep();
              this.x = setInterval(_ => {
                this.getWeRunStep();
              }, 14000)
            },
            fail: res => {
              this.setData({
                isOpen: true
              })
            }
          })
          //非初始化进入该页面,且未授权
          // wx.showModal({
          //   title: '是否授权获取微信运动步数',
          //   content: '需要获取您的微信运动步数，请确认授权，否则无法获取您所需数据',
          //   cancelColor: "#333333",
          //   confirmColor: "#2ED071",
          //   success: (res) => {
          //     if (res.cancel) {
          //       wx.showToast({
          //         title: '授权失败',
          //         icon: 'error',
          //         duration: 1000
          //       })
          //       // this.setData({
          //       //   isOpen: true,
          //       // })
          //     } else if (res.confirm) {
          //       // 获取微信步数
          //       this.getWeRunStep()
          //     }
          //   }
          // })
        } else {
          this.getWeRunStep()
          this.x = setInterval(_ => {
            this.getWeRunStep();
          }, 14000)
        }
      }
    })
  },
  getPlaceList: function () {
    placeList({
      searchStrField2: `${this.data.longitude},${this.data.latitude}`
    }, ss => {
      ss.data.map(xx => {
        xx.distanceM = Math.floor(xx.distance * 1000)
        xx.img = xx.images?.split(';')[0]
      })
      this.setData({
        siteData: ss.data,
        alloWSiteData: ss.data.filter(a => a.checkInMethod == 1 && a.distanceM <= a.checkInDistance)
      })
      console.log(ss.data, 22, this.data.alloWSiteData)
    })
  },
  showLocation: function () {
    wx.openSetting({
      success: (dataAu) => {
        if (dataAu.authSetting["scope.userLocation"] == true) {
          wx.showToast({
            title: '授权成功',
            icon: 'success',
            duration: 1000
          })
          this.setData({
            noLocation: false
          })
          this.getlocation()
          this.getlocaitonname()
        } else {
          wx.showToast({
            title: '授权失败',
            icon: 'error',
            duration: 1000
          })
          this.setData({
            noLocation: true
          })
        }
      }
    })
  },
  distance: function (la1, lo1, la2, lo2) {
    var La1 = la1 * Math.PI / 180.0;
    var La2 = la2 * Math.PI / 180.0;
    var La3 = La1 - La2;
    var Lb3 = lo1 * Math.PI / 180.0 - lo2 * Math.PI / 180.0;
    var s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(La3 / 2), 2) + Math.cos(La1) * Math.cos(La2) * Math.pow(Math.sin(Lb3 / 2), 2)));
    s = s * 6378.137; //地球半径
    s = Math.round(s * 10000) / 10000;
    return s.toFixed(2)
  },
  cancelDaka: function () {
    if (!this.data.showCancel) {
      wx.showModal({
        title: '超出计时范围',
        content: '请回到计时范围内，否则3秒后自动取消打卡',
        showCancel: false,
      })
    }

    this.setData({
      showCancel: true
    })
  },
  getlocation: function () {
    wx.startLocationUpdate({
      success: () => {
        wx.onLocationChange(res => {
          setTimeout(_ => {
            this.setData({
              longitude: res.longitude,
              latitude: res.latitude
            })
            this.getPlaceList()
            if (this.data.runInfo.id) {
              const checkdeitance = this.data.siteData.filter(xx => xx.id == this.data.runInfo.placeId)[0] || {}
              const checkLocation = this.data.runInfo.location.split(',')
              const distance = this.distance(this.data.latitude, this.data.longitude, Number(checkLocation[1]), Number(checkLocation[0])) * 1000
              setTimeout(_ => {
                if (distance > checkdeitance.checkInDistance) {
                  checkiincancel({}, resd => {
                    if (resd.code == 10000) {
                      this.getrunningInfo()
                    }
                  })
                }
              }, 3000)
              if (distance > checkdeitance.checkInDistance) {
                this.cancelDaka()
              }
            }
          }, 1000)
        });
      },
      fail: err => {
        this.setData({
          noLocation: true
        })
      }
    });
  },
  getSelectUserTeams: function () {
    selectUserTeams({}, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          teamList: resd.data,
        })
      }
    })
  },
  getlocaitonname: function () {
    wx.getLocation({
      type: "gcj02",
      success: (res) => {
        console.log(res);
        qqmapsdk.reverseGeocoder({
          location: {
            latitude: res.latitude,
            longitude: res.longitude
          },
          success: (res1) => {
            this.setData({
              locationName: res1.result.formatted_addresses ? res1.result.formatted_addresses.recommend : '未知区域'
            })
          },
          fail: function (res) {
            console.log(res);
          }
        })
      }
    })
  },
  getshowList: function () {
    showList({}, resd => {
      if (resd.code == 10000) {
        this.setData({
          showList: resd.data
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getcheckiinType()
    this.getshowList()
    this.getlocaitonname()
    if (options.scene) { //判断是微信扫码跳转到小程序
      // const params = options.scene.split('_')
      this.setData({
        deviceId: params[0],
        isscene: true,
        placeId: params[1]
      })
      this.equipmentInfo()
    }
    if (wx.getStorageSync('token')) {
      checkInSetting({}, resd => {
        console.log(resd)
        this.setData({
          targetSteps: resd.data.targetSteps,
          targetRecord: resd.data
        })
        wx.setStorageSync('checkInSetting', resd.data)
      })
      // 启动位置更新
      this.getlocation()
    }
    // this.getSetting()
  },
  sharefriend: function () {
    this.setData({
      showPopup1: true,
      showPopup: false
    })
    // wx.showShareImageMenu({
    //   path: '/assets/images/p_code.png',
    //   entrancePath: '/pages/index/index',
    //   entrancePath: true
    // })
  },
  getrunningInfo: function () { //获取获取用户打卡中的数据，判断是否有打卡中数据
    runningInfo({}, resd => {
      this.setData({
        runInfo: resd.data || {},
        showCancel: false
      })
      if (resd.data) {
        todaySportInfo({}, ress => {
          if (ress.code && ress.code == 10000) {
            const data = ress.data || {}
            const times = parseInt(((new Date().getTime() - new Date(resd.data.startTime).getTime()) / 1000))
            this.setData({
              checkInTimesd: data.checkInTime + times
            })
            this.aa = setInterval(_ => {
              let data = Number(this.data.checkInTimesd)
              data += 1
              this.setData({
                checkInTimesd: data,
                min: Math.floor((data % 3600) / 60),
                checkInTimesds: formatSecond(data)
              })
            }, 1000)
          }
        })
      }
    })
  },
  getcheckiinType: function () {
    checkiinType({}, resd => {
      this.setData({
        checkiinTypeList: resd.data
      })
    })
  },
  getUserInfo: function () {
    userInfo({}, resd => {
      if (resd.code == 10000) {
        this.setData({
          userinfo: resd.data
        })
        wx.setStorageSync('userinfo', resd.data)
      }
    })
  },
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    console.log(wx.getStorageSync('sessionKey'), 123123)
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    this.setData({
      token: wx.getStorageSync('token')
    })
    if (this.data.token) {
      if (this.data.longitude) {
        this.getPlaceList()
      }
      this.getTodaySportInfo()
      this.getSelectUserTeams() //获取所属团体列表
      this.getUserInfo()
      this.setData({
        sessionKey: wx.getStorageSync('sessionKey')
      })
      this.getNoticeList()
      this.getrunningInfo()
      this.getSetting()
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    wx.stopLocationUpdate()
    clearInterval(this.aa)
    clearInterval(this.x)
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    wx.stopLocationUpdate()
    clearInterval(this.aa)
    clearInterval(this.x)
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.getUserInfo()
    setTimeout(_ => {
      wx.stopPullDownRefresh();
    }, 300)
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