// pages/groupAdmin/groupAdmin.js
import { teamUserList,updateTeamUserType } from '../../utils/request' 
Page({
  /**
   * 页面的初始数据
   */
  data: {
    showDialog: false,
    // 是否添加
    pageNum: 1,
    pageSize: 10,
  },
  getTeamUserList:function(){
    const { keyword,pageNum,pageSize,teamId } = this.data
    teamUserList({
      teamId,
      keyword,
      status: 0,
      pageNum,
      pageSize,
    },resd=>{
      if(resd && resd.code == 10000){
        const data = resd.data
        this.setData({
          teamUserLists: data.user?.filter(i=>i.type == 1),
          total: data.total,
        })
      }
    })
  },
  jump:function(e){
    const { url } = e.currentTarget.dataset
    wx.navigateTo({
      url,
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1, 
    });
  },
  open:function(e){
    const { name,deleteid } = e.currentTarget.dataset
    this.setData({
      [name]: true,
      deleteId: deleteid,
    })
  },
  onClose: function (e) {
    const {
      name,
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  dialogConfirm:function(e){
    this.getUpdateTeamUserType()
    this.onClose(e)
  },
  getUpdateTeamUserType:function(){
    updateTeamUserType({
      teamId: this.data.teamId,
      userId: this.data.deleteId,
      type: 2,
    },resd=>{
      if(resd && resd.code == 10000){
        wx.showToast({
          title: '移除成功!',
          icon: 'success'
        })
        this.getTeamUserList()
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      teamId: +options.teamId,
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
    this.getTeamUserList()
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