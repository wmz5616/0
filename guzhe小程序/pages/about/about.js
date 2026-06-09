// pages/about/about.js
import {
  articleList,
  basicConfig
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    articleLists: [{
      title: '111',
      id: 0,
    }, {
      title: '111',
      id: 1,
    }, ],
    logo: '/assets/images/logo.png',
    systemName: '',
    version: '',
    org_name: '',
    miitbeian: '',
  },
  getSysConfig() {
    basicConfig({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data || []
        wx.setStorageSync('basicConfig', data)
        if (Array.isArray(data)) {
          const versionItem = data.find(i => i.key == 'version')
          const miitbeianItem = data.find(i => i.key == 'miitbeian')
          this.setData({
            version: versionItem ? versionItem.value : '',
            miitbeian: miitbeianItem ? miitbeianItem.value : '',
            org_name: '技术支持：' + '东莞市蓝睿网络科技有限公司',
          })
        }
      }
    })
  },
  getArticleList: function () {
    articleList({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data || []
        this.setData({
          articleLists: Array.isArray(data) ? data : [],
        })
      }
    })
  },
  jumpH5() {
    wx.navigateTo({
      url: `/pages/webView/webView?url=https://www.bluewise.cc/`,
    })
  },
  // 跳转关于我们详情
  navigateTo(e) {
    wx.navigateTo({
      url: `/pages/aboutDetails/aboutDetails?id=${e.currentTarget.dataset.id}`,
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
    this.getArticleList()
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
  onShow() {},

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