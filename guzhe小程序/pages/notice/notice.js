// pages/notice/notice.js
import { noticeList } from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    noticeData: [],
    pageNum: 1,
    total: 0,
    pageSize: 10,
  },
  goBack() {
    wx.navigateBack({
      delta: 1, 
    });
  },
  getNoticeList:function(e){
    const {
      pageNum,
      pageSize,
    } = this.data
    noticeList({
      pageSize,
      pageNum,
    },resd=>{
      if(resd && resd.code == 10000){
        const data = resd.data
        this.setData({
          noticeData: e ?  [...this.data.noticeData,...data.list]  : data.list,
          total: data.total,
        })
      }
    })
  },
  navigateToNoticeDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/noticeDetail/noticeDetail?searchId=${id}`,
    });
  },

  scrollTolower(){
    const totalPages = Math.ceil(this.data.total / this.data.pageSize)
    const currentTotalPages = Math.ceil(this.data.noticeData.length / this.data.pageSize)
    console.log(totalPages,currentTotalPages)
    if(currentTotalPages < totalPages){
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
    this.setData({
      pageNum: 1,
    })
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