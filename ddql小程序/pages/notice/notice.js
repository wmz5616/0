// pages/notice/notice.js
import {
  messageAnnouncement,
  noticeList
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: 0,
    tabList: [{
        title: '通知公告',
        name: 0
      },
      {
        title: '消息提醒',
        name: 1
      },
    ],
    noticeList: [],
    pageNum: 1,
    pageSize: 10,
  },
  // getMessageAnnouncement:function(){
  //   messageAnnouncement({
  //     // userId: wx.getStorageSync('userinfo').id
  //   },resd=>{
  //     if(resd && resd.code == 10000){
  //       this.setData({
  //         noticeList: resd.data,
  //       })
  //     }
  //   })
  // },
  getNoticeList: function (e) {
    noticeList({
      pageSize:this.data.pageSize,
      pageNum:this.data.pageNum,
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          noticeList: e?[...this.data.noticeList,resd.data.list]: resd.data.list,
          total: resd.data.total,
        })
      }
    })
  },
  jump: function (e) {
    const {
      id
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/noticeInfo/noticeInfo?data=${JSON.stringify(this.data.noticeList.find(i=>i.id == id))}`,
    })
  },
  onChange: function (e) {
    this.setData({
      active: e.detail.index,
    })
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  onScroll: function (e) {
    if (Math.ceil(this.data.noticeList.length / this.data.pageSize)  < Math.ceil(this.data.total / this.data.pageSize)) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getNoticeList('e')
    }
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
    this.getNoticeList()
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