// pages/myGroup/myGroup.js
import {
  formatTime
} from "../../utils/util"
import {
  selectUserTeams,
  deleteTeamUser,
  teamLists,
  saveTeamApplicationRecord,
  isOpenDepartment,
  getDepartmentList,
} from "../../utils/request"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showDialog: false,
    showOverlay: false,
    teamList: [],
    teamName: '',
    name: '',
    flag: true,
    allTeamList: [],
    isMultiDepartment: false,
    showAreaPicker: false,
    pickerData: {},
    departmentList: [],
  },
  getDepartmentList(searchId) {
    getDepartmentList({
      searchId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const list = (resd.data || []).map(i => ({
          ...i,
          text: i.name,
          value: i.id
        }))
        this.setData({
          departmentList: list
        })
      } else {
        this.setData({
          departmentList: []
        })
        if (resd && resd.msg) {
          wx.showToast({
            title: resd.msg,
            icon: 'none'
          })
        }
      }
    })
  },
  reset(e) {
    this.setData({
      pickerData: {},
    })
    this.onClose(e)
  },
  getIsOpenDepartment(searchId) {
    isOpenDepartment({
      searchId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const isMultiDepartment = resd.data?.isMultiDepartment == 1 ? true : false
        this.setData({
          isMultiDepartment
        })
        isMultiDepartment && this.getDepartmentList(searchId)
      }
    })
  },
  onClickIcon() {
    this.setData({
      showAreaPicker: true,
    })
  },
  getSaveTeamApplicationRecord() {
    const {
      allTeamInfo,
      userInfo,
      name,
      isMultiDepartment,
      pickerData
    } = this.data
    if (!allTeamInfo) {
      wx.showToast({
        title: '请选择要加入的团体名称',
        icon: 'none',
      })
      return
    }
    if (isMultiDepartment && !pickerData?.text) {
      wx.showToast({
        title: '请选择加入的部门',
        icon: 'none'
      })
      return
    }
    saveTeamApplicationRecord({
      teamId: allTeamInfo.id,
      userId: userInfo.id,
      userName: name || undefined,
      departmentId: isMultiDepartment ? pickerData.value : undefined,
      userPhone: this.data.userInfo.phone ? this.data.userInfo.phone : '',
      joinType: 0
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '申请成功!',
          icon: 'success'
        })
        this.setData({
          name: '',
          teamName: '',
          allTeamInfo: undefined,
          showOverlay: false,
        })
        this.getSelectUserTeams()
      }
    })
  },
  chose: function (e) {
    const {
      item
    } = e.currentTarget.dataset
    this.setData({
      allTeamInfo: item,
      teamName: item.name,
      flag: false,
      pickerData: {},
      departmentList: [],
    })
    this.getIsOpenDepartment(item.id)
  },
  getTeamLists: function () {
    teamLists({
      name: this.data.teamName,
      status: 0,
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          allTeamList: resd.data,
        })
      }
    })
  },
  changeTeam: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: e.detail.value,
      flag: name == 'teamName' ? true : false,
    })
    console.log(name)
    if (name == 'teamName') {
      this.getTeamLists()
    }
  },
  getDeleteTeamUser() {
    const {
      teamInfo
    } = this.data
    if (teamInfo.type == 0) return
    deleteTeamUser({
      teamId: teamInfo.teamId,
      userId: this.data.userInfo.id,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '退出成功!',
          icon: 'success'
        })
        this.getSelectUserTeams()
      }
    })
  },
  jump: function (e) {
    const {
      url,
      type
    } = e.currentTarget.dataset
    console.log(type, url + (type ? '&isAdmin=true' : ''))
    wx.navigateTo({
      url: url + (type ? '&isAdmin=true' : ''),
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
      teamName: name == 'flag' && this.data.flag ? '' : this.data.teamName,
      allTeamInfo: name == 'flag' && this.data.flag ? undefined : this.data.allTeamInfo,
    })
  },
  onDConfirm: function (e) {
    const pickerData = e.detail.value
    this.setData({
      showAreaPicker: false,
      pickerData,
    })
  },
  showComponent: function (e) {
    console.log(e)
    const {
      name,
      teaminfo = undefined,
    } = e.currentTarget.dataset
    this.setData({
      teamInfo: teaminfo,
      [name]: true,
    }, () => {
      if (name == 'showOverlay') this.getTeamLists()
    })
  },
  getSelectUserTeams: function () {
    selectUserTeams({}, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          teamList: resd.data.filter(xx => xx.status == 0)
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      userInfo: wx.getStorageSync('userinfo')
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
    // 获取关联团体
    this.getSelectUserTeams()
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