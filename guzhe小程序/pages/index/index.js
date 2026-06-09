// index.js
const defaultAvatarUrl = 'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0'
import {
  basicConfig,
  showList,
  topnotice,
  bannerConfig,
  configShow
} from '../../utils/request'
import {
  tabPath
} from '../../utils/util'
const WxParse = require('../../wxParse/wxParse')
Page({
  data: {
    noticeList: [],
    bannerList: [],
    showList: [],
    userInfo: {
      avatarUrl: defaultAvatarUrl,
      nickName: '',
    },
    menuData: [],
    canIUseGetUserProfile: wx.canIUse('getUserProfile'),
    canIUseNicknameComp: wx.canIUse('input.type.nickname'),
  },
  bindViewTap() {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  getbasicConfig: function () {
    basicConfig({}, resd => {
      const content = resd.data.filter(a => a.key == 'introduce')[0]?.value
      WxParse.wxParse('newsDetail.contentHtml', 'html', content, this, 5)
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
    bannerConfig({}, resd => {
      this.setData({
        bannerList: resd.data
      })
    })
    configShow({}, resd => {
      this.setData({
        menuData: resd.data,
      })
    })
  },
  handleSwiperChange(e) {
    this.setData({
      swiperCurrent: e.detail.current
    })
  },
  pushNoticeList: function () {
    wx.navigateTo({
      url: '/pages/notice/notice',
    })
  },
  jumpUrl: function (e) {
    let {
      type,
      url,
      token
    } = e.currentTarget.dataset
    // 为1 H5页面 为0 小程序页面
    if (type == 1) url = `/pages/webView/webView?url=${url}`
    console.log(url)
    if (tabPath.includes(url)) {
      wx.switchTab({
        url,
      })
      return
    }

    wx.navigateTo({
      url,
    })

  },
  onInputChange(e) {
    const nickName = e.detail.value
    const {
      avatarUrl
    } = this.data.userInfo
    this.setData({
      "userInfo.nickName": nickName,
      hasUserInfo: nickName && avatarUrl && avatarUrl !== defaultAvatarUrl,
    })
  },
  getUserProfile(e) {
    // 推荐使用wx.getUserProfile获取用户信息，开发者每次通过该接口获取用户个人信息均需用户确认，开发者妥善保管用户快速填写的头像昵称，避免重复弹窗
    wx.getUserProfile({
      desc: '展示用户信息', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
      success: (res) => {
        console.log(res)
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    })
  },
  onShareAppMessage() {
    return {
      path: '/pages/index/index'
    }
  },
  onShareTimeline() {},
  onLoad() {
    const app = getApp()
    this.setData({
      isIOS: app.globalData.isIOS
    })
  },
  onShow() {
    this.getbasicConfig()
    this.getshowList()
    topnotice({}, resd => {
      this.setData({
        noticeList: resd.data
      })
    })
  }
})