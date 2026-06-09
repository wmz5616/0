// pages/createGroup/createGroup.js
import {
  addTeams,
  nextAreaInfo
} from '../../utils/request'
import {
  teamStatus,
  validateField,
  auditStatus
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    name: '',
    type: '',
    region: '',
    address: '',
    contactPerson: '',
    contactPhone: '',
    contactEmail: '',
    isMultiDepartment: '',
    // 0表示团体，1表示地区
    pickertype: 0,
    columns: [],
    showAreaPicker: false,
  },
  getNextAreaInfo: function (id, region = '') {
    nextAreaInfo({
      id,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data.map(i => ({
          ...i,
          text: i.name,
          value: i.id
        }))
        const item = region ? data.find(i => region.includes(i.name)) : data[0]
        const columns = this.data.columns
        columns[item.level - 1] = {
          values: data,
          defaultIndex: region ? data.findIndex(i => i.id == item.id) : 0
        }
        this.setData({
          columns,
        }, () => {
          item.level != 3 && this.getNextAreaInfo(item.id, region)
        })
      }
    })
  },
  onClickIcon: function (e) {
    const {
      pickertype
    } = e.currentTarget.dataset
    const {
      pickerInstance,
      region
    } = this.data
    // status为团队类型，area为地区,动态切换团体类型和地区的数据
    let columns = []
    if (pickertype == 'status') {
      columns = teamStatus
    } else if (pickertype == 'audit' || pickertype == 'multiDept') {
      columns = auditStatus
    } else {
      this.getNextAreaInfo(100000, region)
    }
    this.setData({
      columns,
      pickertype,
      showAreaPicker: true,
    }, () => {
      if (pickertype == 'status') {
        pickerInstance?.setColumnValue(0, this.data.type)
      } else if (pickertype == 'audit') {
        pickerInstance?.setColumnValue(0, this.data.isUserAuth)
      } else if (pickertype == 'multiDept') {
        pickerInstance?.setColumnValue(0, this.data.isMultiDepartment)
      }
    })
  },
  onAreaChange: function (e) {
    const {
      pickertype
    } = this.data
    if (pickertype == 'status') return
    const {
      index,
      value
    } = e.detail
    index != 2 && this.getNextAreaInfo(value[index].id)
  },
  onConfirm: function (e) {
    const pickerData = e.detail
    const {
      pickertype
    } = this.data
    console.log(pickerData.value)
    if (pickertype == 'status') {
      this.setData({
        type: pickerData.value
      })
    } else if (pickertype == 'audit') {
      this.setData({
        isUserAuth: pickerData.value
      })
    } else if (pickertype == 'multiDept') {
      this.setData({
        isMultiDepartment: pickerData.value
      })
    } else {
      this.setData({
        region: pickerData.value.map(i => i.name).join('')
      })
    }
    this.setData({
      showAreaPicker: false,
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
    })
  },
  getAddTeams: function () {
    const params = {
      name: this.data.name,
      type: teamStatus.indexOf(this.data.type),
      region: this.data.region,
      contactPerson: this.data.contactPerson,
      contactPhone: this.data.contactPhone,
      isUserAuth: this.data.isUserAuth == '是' ? 1 : 0,
      isMultiDepartment: this.data.isMultiDepartment == '是' ? 1 : 0,
      contactEmail: this.data.contactEmail || undefined,
      checkInNumLimit: this.data.checkInNumLimit || 0,
    }
    const title = validateField(params)
    if (title) {
      wx.showToast({
        title,
        icon: 'none'
      })
      return
    }
    addTeams({
      ...params,
      address: this.data.address || '',
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '创建成功',
        })
        const timer = setTimeout(() => {
          wx.navigateBack({
            delta: 1,
          })
          clearTimeout(timer)
        }, 1500)
      }
    })
  },
  onRadioChange(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({
      [type]: e.detail,
    });
  },
  onClose: function (e) {
    const {
      name,
      reset
    } = e.currentTarget.dataset
    const {
      pickertype,
      pickerInstance
    } = this.data
    let data = {}
    if (reset && pickertype == 'status') {
      data.type = ''
    } else if (reset && pickertype == 'area') {
      data.region = ''
    }
    this.setData({
      [name]: false,
      ...data
    }, () => {
      if (reset && pickertype == 'status') {
        pickerInstance.setColumnValue(0, teamStatus[0])
      }
    })
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
    this.setData({
      pickerInstance: this.selectComponent('#myPicker'),
    })
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