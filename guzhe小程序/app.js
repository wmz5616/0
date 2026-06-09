// app.js
App({
  // 冷启动才会触发，扫码进入页面为热启动，不会触发
  onLaunch() {
    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    const systemInfo = wx.getSystemInfoSync();
    const model = systemInfo.model

    const oldiPhoneModels = [
      'iPhone 8', 'iPhone 7', 'iPhone 6', 'iPhone 5', 'iPhone 4',
      'iPhone 8 Plus', 'iPhone 7 Plus', 'iPhone 6 Plus', 'iPhone 6s'
    ];

    this.globalData.isIOS = model.includes('iPhone') && !oldiPhoneModels.some(i => i == model)

    // 屏幕分辨率
    wx.setStorageSync('rpxRatio', 750 / systemInfo.screenWidth)

    // 登录
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
      }
    })
  },
  globalData: {
    userInfo: null,
    selectAddressId: undefined,
    needRefreshOrderList: false,
    refreshOrderId: null,
    myPageSize: null,
  }
})
