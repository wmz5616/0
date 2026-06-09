// pages/groupAdmin/groupAdmin.js
import { teamUserList,updateTeamUserType } from '../../utils/request' 
Page({
  /**
   * 页面的初始数据
   */
  data: {
    showDialog: false,
    selectUserColumn: [],
    // 是否添加
    keyword: '',
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
        const teamUserLists = data.user.filter(i=>i.type != 0)
        this.setData({
          teamUserLists,
          selectUserColumn: teamUserLists.filter(i=>i.type == 1).map(i=>i.userId),
          total: data.total,
        })
      }
    })
  },
  onCancel:function(){
    console.log(11)
    this.setData({
      keyword: '',
    })
    this.getTeamUserList()
  },
  onSearch:function(e){
    this.setData({
      keyword: e.detail
    })
    this.getTeamUserList()
  },
  goBack() {
    wx.navigateBack({
      delta: 1, 
    });
  },
  choseMember:function(e){
    const { userid } = e.currentTarget.dataset
    let { selectUserColumn } = this.data
    if(selectUserColumn.length >= 3){
      wx.showToast({
        title: '添加的管理员超过3个',
        icon: 'error'
      })
    }
    if(selectUserColumn.includes(userid)){
      // selectUserColumn = selectUserColumn.filter(i=>i != userid)
      return
    }else{
      selectUserColumn.push(userid)
    }
    this.setData({
      selectUserColumn,
    })
  },
  getUpdateTeamUserType:function(userId){
    return new Promise((resolve,reject)=>{
      updateTeamUserType({
        teamId: this.data.teamId,
        userId,
        type: 1,
      },resd=>{
        if(resd && resd.code == 10000){
          resolve(resd)
        }
      })
    })
  },
  add:async function(){
    if(!this.data.selectUserColumn.length){
      wx.showToast({
        title: '请选择管理员！',
        icon: 'none',
      })
      return
    }
    const promises = this.data.selectUserColumn.map(i=>this.getUpdateTeamUserType(i))

    await Promise.all(promises)

    wx.showToast({
      title: '添加成功!',
      icon: 'success'
    })
     setTimeout(_=>{
       wx.navigateBack()
     },300)
    this.getTeamUserList()
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      teamId: +options.teamId,
    })
    this.getTeamUserList()
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