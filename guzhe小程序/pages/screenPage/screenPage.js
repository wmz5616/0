// pages/screenPage/screenPage.js
import {
  screenshotInfo,
  UPLOAD_IMG_BASE_URI
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    screenshotInfo: {},
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
    clearInterval(this.timer)
  },
  getScreenshotInfo() {
    screenshotInfo({
      orderId: this.data.id,
      screenshotId: this.data.screenshotId,
    }, resd => {
      // 判断 是否返回截图
      const screenshotInfo = resd.data

      if (screenshotInfo.screenshotStatus == 2) {
        clearInterval(this.timer)
        wx.showToast({
          title: screenshotInfo.failReason || '截图失败！',
        })
        return
      }

      if (screenshotInfo.screenshotStatus == 1) {
        // 截图成功 !
        clearInterval(this.timer)
        wx.showToast({
          title: '获取成功！',
        })
      } else {
        wx.hideLoading()
      }

      this.setData({
        screenshotInfo: {
          ...screenshotInfo,
          screenshotUrl: screenshotInfo?.screenshotUrl ? UPLOAD_IMG_BASE_URI + screenshotInfo?.screenshotUrl : '',
          searchTime: screenshotInfo.screenshotStatus == 1 ? screenshotInfo.createTime : screenshotInfo.updateTime,
        }
      })
    })
  },
  getData() {
    wx.showLoading({
      title: '加载中',
    })
    this.timer = setInterval(() => {
      this.getScreenshotInfo()
    }, 3000)
  },
  showImg() {
    const {
      screenshotInfo,
    } = this.data
    wx.previewImage({
      urls: [screenshotInfo.screenshotUrl],
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      id: +options.id,
      screenshotId: +options.screenshotId,
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
    this.getScreenshotInfo()
    this.getData()
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    clearInterval(this.timer)
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    clearInterval(this.timer)
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