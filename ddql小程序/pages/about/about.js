// pages/about/about.js
import {articleList} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    list:[]
    // list: ['关于我们', '服务协议', '隐私协议'],
  },
  // 跳转关于我们详情
  navigateTo(e) {
    const {
      list
    } = this.data
    const {
      type
    } = e.currentTarget.dataset
    wx.navigateTo({
      // 1 关于我们
      url: `/pages/aboutDetail/aboutDetail?title=${list[type].title}`,
    });
  },
  // 顶部返回上一个页面
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    articleList({},resd=>{
      this.setData({list:resd.data})
    })
    const sysData = wx.getStorageSync('sysConfig')
    this.setData({
      version: sysData.find(i => i.key == 'version').value,
      miitbeian: sysData.find(i => i.key == 'miitbeian').value,
      org_name: '技术支持：' + sysData.find(i => i.key == 'org_name').value,
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

  }
})