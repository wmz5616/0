// pages/healthCoin/healthCoin.js
import moment from 'moment'
import {
  coinList,
  selectUserTeams
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    pageNum: 1,
    showPopup: false,
    active: 0,
    team: '全部',
    selectTeam: '',
    date: '2016-09-01',
    start: moment().startOf('month').format('YYYY年MM月'),
    startDate: moment().startOf('month').format('YYYY-MM-DD'),
    end: moment().endOf('month').format('YYYY年月'),
    columns: [{
        text: '白云集团',
        value: 1
      },
      {
        text: '蓝天企业',
        value: 1
      },
      {
        text: '家庭',
        value: 1
      },
      {
        text: '松山湖朋友圈',
        value: 1
      },
    ],
    tabList: [{
        id: 0,
        title: '全部',
      },
      {
        id: 1,
        title: '收入',
      },
      {
        id: 2,
        title: '支出',
      }
    ]
  },
  goTixian: function (e) {
    wx.navigateTo({
      url: `/pages/onlineWithdrawal/onlineWithdrawal?healthCoin=${this.data.healthCoin}`,
    })
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  changedate: function (e) {
    this.setData({
      start: moment(e.detail.value).format('YYYY年MM月'),
      startDate: e.detail.value
    })
    this.getcoinList()
  },
  getcoinList: function (e) {
    coinList({
      startTime: moment(this.data.startDate).format('YYYY-MM-DD 00:00:00'),
      endTime: moment(this.data.startDate).endOf('month').format('YYYY-MM-DD 23:59:59'),
      coinType: 1,
      teamId: this.data.teamId,
      pageNum: this.data.pageNum,
      numType: this.data.active == 0 ? undefined : this.data.active
    }, resd => {
      this.setData({
        list:e?this.data.list.concat(resd.data.list):resd.data.list,
        total: resd.data.total
      })
    })
  },
  getSelectUserTeams: function () {
    selectUserTeams({}, resd => {
      if (resd && resd.code == 10000) {
        if (resd.data.length != 0) {
          const d = []
          resd.data.map(rs => {
            d.push({
              text: rs.team.name,
              value: rs.team.id
            })
          })
          this.setData({
            columns: d,
            amount: resd.data[0].team.healthyCoin,
            teamInfo: resd.data[0],
            teamList: resd.data
          })
        }
      }
    })
  },
  confirm: function (e) {
    const {
      selectTeam
    } = this.data
    this.setData({
      team: selectTeam ? selectTeam.text : this.data.teamInfo.team.name,
      teamId: selectTeam ? selectTeam.value : this.data.teamInfo.team.id
    })
    this.getcoinList()
    this.onClose(e)
  },
  clearpicker: function (e) {
    this.setData({
      team: '全部',
      teamId: undefined
    })
    this.getcoinList()
    this.onClose(e)
  },
  onChange: function (e) {
    const {
      value,
      index
    } = e.detail

    this.setData({
      selectTeam: value,
    })
  },
  open: function (e) {
    this.setData({
      showPopup: true,
    })
  },
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  changeTab: function (e) {
    const {
      id
    } = e.target.dataset
    const {
      active
    } = this.data
    if (id == undefined || id == null || id == active) return
    this.setData({
      active: id,
    })
    this.getcoinList()
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      healthCoin: options.healthCoin
    })
    if (options.type) {
      this.setData({
        active: 2
      })
    }
    this.getcoinList()
    this.getSelectUserTeams()
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
    console.log(22)
    if (Math.ceil(this.data.total / 10) > Math.ceil(this.data.list.length / 10)) {
      this.setData({
        pageNum: this.data.pageNum += 1
      })
      this.getcoinList('e')
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})