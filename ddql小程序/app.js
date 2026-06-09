// app.js
App({
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
    // 判断是否从登录页跳转回来
    isLoginBack: false,
    selectAddressId: undefined,
    scene: undefined,
  }
})