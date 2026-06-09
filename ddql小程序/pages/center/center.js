// pages/center/center.js
import {
  selectUserTeams,
  saveTeamApplicationRecord,
  teamInfo,
  sysConfig,
  userInfo,
  rechargeConfig,
  getDepartmentList,
  isOpenDepartment,
} from "../../utils/request"
import {
  sysList
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    hours: new Date().getHours(),
    userInfo: {},
    columns: [{
        img: '/assets/images/goldWaller.svg',
        title: '充值购币',
        url: '/pages/purchaseCoins/purchaseCoins',
      },
      {
        img: '/assets/images/calendar.svg',
        title: '打卡提现设置',
        url: '/pages/withdrawalSettings/withdrawalSettings',
      },
      {
        img: '/assets/images/people.svg',
        title: '我的团体',
        url: '/pages/myGroup/myGroup',
        need: true,
      },
      {
        img: '/assets/images/greenLocation.svg',
        title: '收货地址',
        url: '/pages/address/address',
        need: true,
      },
      {
        img: '/assets/images/manager.svg',
        title: '场地信息管理',
        url: '/pages/venueInfo/venueInfo',
      },
      {
        img: '/assets/images/concat.svg',
        title: '联系客服',
        url: '/pages/notice/notice',
        need: true,
      },
      {
        img: '/assets/images/check.svg',
        title: '券码核销',
        url: '/pages/couponVerification/couponVerification',
      },
      {
        img: '/assets/images/myOrder.svg',
        title: '我的订单',
        url: `/pages/myOrder/myOrder?activeTab=0`,
        need: true,
      },
      {
        img: '/assets/images/storeManage.svg',
        title: '商家管理',
        url: '/pages/storeManage/storeManage',
        need: true,
      },
      {
        img: '/assets/images/laba.svg',
        title: '通知公告',
        url: '/pages/notice/notice',
        need: true,
        token: true,
      },
      {
        img: '/assets/images/aboutOus.svg',
        title: '关于我们',
        url: '/pages/about/about',
        need: true,
        token: true,
      },
    ],
    token: '',
    showOverlay: false,
    name: '',
    showAreaPicker: false,
    departmentList: [],
    pickerData: {},
  },
  onDConfirm: function (e) {
    const pickerData = e.detail.value
    this.setData({
      showAreaPicker: false,
      pickerData,
    })
    wx.showTabBar({
      animation: true // 可选，是否开启动画效果
    })
  },
  getIsOpenDepartment() {
    isOpenDepartment({
      searchId: this.data.teamId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const isMultiDepartment = resd.data?.isMultiDepartment == 1 ? true : false
        this.setData({
          isMultiDepartment
        })
        isMultiDepartment && this.getDepartmentList()
      }
    })
  },
  reset(e) {
    this.setData({
      pickerData: {},
    })
    this.onClose(e)
  },
  onClickIcon() {
    wx.hideTabBar({
      animation: true // 可选，是否开启动画效果
    })
    this.setData({
      showAreaPicker: true,
    })
  },
  aa: function () {

  },
  getTeamInfo() {
    teamInfo({
      searchId: this.data.teamId
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          teamInfos: resd.data,
        })
      }
    })
  },
  changename: function (e) {
    this.setData({
      name: e.detail.value
    })
  },

  getSaveTeamApplicationRecord() {
    const {
      userInfo,
      name,
      teamInfos,
      pickerData,
      isMultiDepartment,
    } = this.data
    if (isMultiDepartment && !pickerData?.text) {
      wx.showToast({
        title: '请选择加入的部门',
        icon: 'none'
      })
      return
    }
    saveTeamApplicationRecord({
      teamId: teamInfos.id,
      userId: userInfo.id,
      userName: name || undefined,
      departmentId: isMultiDepartment ? pickerData.value : undefined,
      userPhone: this.data.userInfo.phone ? this.data.userInfo.phone : '',
      joinType: 1
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '操作成功!',
          icon: 'success'
        })
        this.setData({
          name: '',
          pickerData: {},
          showOverlay: false,
        })
        wx.showTabBar({
          animation: true // 可选，是否开启动画效果
        })
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
    wx.showTabBar({
      animation: true // 可选，是否开启动画效果
    })
  },
  getSelectUserTeams: function () {
    selectUserTeams({}, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          teamList: resd.data
        })
        this.init()
      }
    })
  },
  init: function () {
    let {
      userInfo,
      columns,
      teamList
    } = this.data
    console.log(teamList)
    rechargeConfig({}, resd => {
      if (resd && resd.code == 10000) {
        // 是否为后台管理员
        if (userInfo.adminId) {
          const data = columns?.find(i => i.title == '场地信息管理')
          data && (data.need = true)
        }
        // 是否为券码核销人员
        if (userInfo.canCheckTicket) {
          const data = columns?.find(i => i.title == '券码核销')
          data && (data.need = true)
        }
        // 是否为团体管理员、创建者
        if (teamList && teamList.length && teamList.find(i => i.type != 2)) {
          const needTrueTitles = ['充值购币', '打卡提现设置']
          columns?.forEach(item => {
            if (needTrueTitles.includes(item.title)) {
              if (item.title == '充值购币') {
                if (resd.data.enableRecharge == 1) {
                  item.need = true;
                }
              } else {
                item.need = true;
              }
            }
          });
        }
        this.setData({
          columns,
        })
      }
    })
  },
  goBack: function () {
    const pages = getCurrentPages()
    // 如果是返回其他页面用navigateBack
    if (pages.length > 2) {
      wx.navigateBack({
        delta: 1,
      });
      return
    }
    // 否则返回首页
    wx.switchTab({
      url: '/pages/index/index',
    })
  },
  gologin: function () {
    if (wx.getStorageSync('token')) {
      return
    }
    wx.navigateTo({
      url: '/pages/login/login',
    })
  },
  jump: function (e) {
    const {
      url,
      title,
    } = e.currentTarget.dataset
    if (title == '联系客服') {
      return
    }
    if (url == '/pages/setting/setting' && !this.data.token) return
    if (url == '/pages/verifyName/verifyName' && this.data.userInfo.hasCertification) return
    wx.navigateTo({
      url: `${url}?healthCoin=${this.data.userInfo.healthCoin}&goldCoin=${this.data.userInfo.goldCoin}`,
    })
  },
  getSysConfig() {
    sysConfig({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        wx.setStorageSync('sysConfig', data)
        this.setData({
          version: data.find(i => i.key == 'version').value,
          miitbeian: data.find(i => i.key == 'miitbeian').value,
          org_name: '技术支持：' + data.find(i => i.key == 'org_name').value,
        })
      }
    })
  },
  getDepartmentList() {
    getDepartmentList({
      searchId: this.data.teamId
    }, resd => {
      if (resd && resd.code == 10000) {
        const list = (resd.data || []).map(i => ({
          ...i,
          text: i.name,
          value: i.id
        }))
        this.setData({
          departmentList: list
        })
      }
    })
  },
  getUserInfo() {
    userInfo({}, resd => {
      if (resd.code == 10000) {
        wx.stopPullDownRefresh();
        this.setData({
          userInfo: resd.data
        })
        wx.setStorageSync('userinfo', resd.data)
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if (options.scene || options.teamId) { //判断是微信扫码跳转到小程序
      // if(!wx.getStorageSync('token')){
      const app = getApp()
      app.globalData.scene = options.scene || options.teamId
      // }
      wx.hideTabBar({
        animation: true // 可选，是否开启动画效果
      })
      this.setData({
        teamId: options.scene || options.teamId,
        showOverlay: true,
      })
      this.getTeamInfo()
      this.getIsOpenDepartment()
    }
    this.getSysConfig()
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
    this.setData({
      token: wx.getStorageSync('token'),
    })
    const app = getApp()
    const isLoginBack = app.globalData.isLoginBack
    console.log(isLoginBack)
    if (isLoginBack) {
      app.globalData.isLoginBack = undefined
      return
    }
    this.setData({
      userInfo: wx.getStorageSync('userinfo')
    })

    if (app.globalData.centerFlage) {
      this.setData({
        teamId: app.globalData.scene,
        showOverlay: true,
      })
      app.globalData.scene = undefined
      app.globalData.centerFlage = undefined

      this.getTeamInfo()
      this.getIsOpenDepartment()
    }
    if (this.data.token) {
      this.getUserInfo()
      this.getSelectUserTeams()
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
    this.getUserInfo()
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