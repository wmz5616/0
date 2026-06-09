// pages/refundProgress/refundProgress.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active:0,
    steps: [
      {
        text: '申请退款',
        desc: '2025-05-29 21:10:25',
        inactiveIcon: '/assets/images/unactiveProcess.svg',
        activeIcon: '/assets/images/activeProcess.svg',
      },
      {
        text: '退款审核',
        desc: '驳回，原因：不符合退款条件\n2025-06-10 15:30:00',
        inactiveIcon: '/assets/images/unactiveProcess.svg',
        activeIcon: '/assets/images/activeProcess.svg',
      },
      // {
      //   text: '退款到账',
      //   desc: '2025-06-10 15:30:00',
      //   inactiveIcon: '/assets/images/unactiveProcess.svg',
      //   activeIcon: '/assets/images/activeProcess.svg',
      // },
    ],
  },
  goBack() {
    wx.navigateBack({
      delta: 1, 
    });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const data = JSON.parse(options.data)
    this.setData({
      steps: this.data.steps.map((i,index)=>({
        ...i,
        desc: index ? data?.auditRemark? data?.auditRemark + data?.auditTime: '' :data?.createTime
      })),
      active: data?.auditRemark?1:0
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