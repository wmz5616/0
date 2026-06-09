import {
  getDepartmentUserList,
  getDepartmentList,
  batchSetUserDepartment,
  setUserDepartment,
  updateTeamUser
} from '../../utils/request'

Page({
  /**
   * 页面的初始数据
   */
  data: {
    keyword: '',
    teamUserLists: [],
    showOverlay: false,
    showDialog: false,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    showAreaPicker: false,
    showDOverlay1: false,
    departmentName: '',
    // 是否批量编辑部门
    isbatch: false,
    // 选中的人id
    isSelectList: [],
    // 部门列表
    departmentList: [],
    name: '',
    defaultIndex: 0,
    selectTeam: {},
  },
  onClose(e) {
    const {
      name,
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  reset(e) {
    this.setData({
      departmentName: '',
      defaultIndex: 0,
    })
    this.onClose(e)
  },
  openBatch() {
    const {
      isSelectList,
      name,
      departmentList
    } = this.data
    if (!isSelectList.length) {
      wx.showToast({
        title: '请勾选要编辑的成员',
        icon: 'none'
      })
      return
    }
    this.setData({
      showDOverlay1: true,
      departmentName: name,
      defaultIndex: departmentList?.findIndex(i => i.name == name),
    })
  },
  batchEdit() {
    this.setData({
      isbatch: !this.data.isbatch,
      isSelectList: [],
    })
  },
  onDConfirm: function (e) {
    const pickerData = e.detail.value
    const val = pickerData.text || pickerData
    if (this.data.pickerType === 'edit') {
      this.setData({
        showAreaPicker: false,
        editDepartmentName: val,
      })
    } else {
      this.setData({
        showAreaPicker: false,
        departmentName: val,
      })
    }
  },
  onEditDepartmentClickIcon: function (e) {
    this.setData({
      showAreaPicker: true,
      pickerType: 'edit'
    })
  },
  onClickIcon: function (e) {
    this.setData({
      showAreaPicker: true,
      pickerType: 'batch'
    })
  },
  selectBatch(e) {
    // 开启批量编辑部门，才会记录多选
    const {
      isSelectList,
      isbatch,
      teamUserLists
    } = this.data
    if (!isbatch) return

    const {
      id
    } = e.currentTarget.dataset

    let newSelectList = []
    // 列表全部选项
    const allItemIds = teamUserLists.map(i => i.id)

    const hasAll = isSelectList.includes('all')

    // 判断是否点击 全选
    if (id == 'all' && !hasAll) {
      newSelectList = ['all', ...allItemIds]
    } else if (id == 'all' && hasAll) {
      // 点击取消 全选
      newSelectList = []
    } else {
      // 点击 普通选项
      newSelectList = isSelectList.includes(id) ? isSelectList.filter(i => i != id) : [...isSelectList, id]

      if (hasAll && newSelectList.length == allItemIds.length) {
        // 是否自动取消 全部
        newSelectList = newSelectList.filter(i => i !== 'all')
      } else if (newSelectList.length == allItemIds.length && allItemIds.length > 0) {
        // 是否自动选中 全部
        newSelectList = ['all', ...newSelectList]
      }
    }

    this.setData({
      isSelectList: newSelectList,
    })
  },
  getSetUserDepartment() {
    const {
      selectTeam,
      editName,
      editPhone,
      editDepartmentName,
      departmentList
    } = this.data
    if (editPhone && !/^1[3-9]\d{9}$/.test(editPhone)) {
      wx.showToast({
        title: '请输入合法号码',
        icon: 'error',
      })
      return
    }

    let dept = departmentList.find(i => i.text == editDepartmentName)
    let deptId = dept ? dept.id : 0;

    updateTeamUser({
      id: selectTeam.id,
      userName: editName,
      userPhone: editPhone,
      departmentId: deptId,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '操作成功！',
          icon: 'success',
          duration: 1000,
        })
        this.setData({
          showOverlay: false,
          showDialog: false,
        })
        setTimeout(() => {
          this.getTeamUserList()
        }, 1000)
      }
    })
  },
  changeInput: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: e.detail.value
    })
  },
  getTeamUserList: function (e) {
    const {
      keyword,
      pageNum,
      pageSize,
      id,
    } = this.data
    wx.showLoading({
      title: '加载中',
    })
    getDepartmentUserList({
      searchId: id,
      keyword: keyword || undefined,
      pageNum,
      pageSize,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.hideLoading()
        const data = resd.data || {
          list: [],
          total: 0
        }
        this.setData({
          teamUserLists: e ? [...this.data.teamUserLists, ...data.list] : data.list,
          total: data.total,
        })
      }
    })
  },
  onClosed: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  getUpdateTeamUser() {
    const {
      selectTeam,
      editName,
      editPhone,
      editDepartmentName,
      departmentList
    } = this.data
    if (editPhone && !/^1[3-9]\d{9}$/.test(editPhone)) {
      wx.showToast({
        title: '请输入合法号码',
        icon: 'error',
      })
      return
    }

    let dept = departmentList.find(i => i.text == editDepartmentName)
    let deptId = dept ? (dept.value || dept.id) : 0;

    updateTeamUser({
      id: selectTeam.id,
      userName: editName,
      userPhone: editPhone,
      departmentId: deptId,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '编辑成功！',
          icon: 'success'
        })
        this.setData({
          showOverlay: false,
        })
        this.getTeamUserList()
      }
    })
  },
  open: function (e) {
    const {
      name,
      selectteam,
    } = e.currentTarget.dataset
    const {
      departmentList
    } = this.data
    this.setData({
      [name]: true,
      selectTeam: selectteam ? selectteam : undefined,
      editName: selectteam ? selectteam.userName : '',
      editPhone: selectteam ? selectteam.userPhone : '',
      editDepartmentName: selectteam ? (selectteam.departmentName || '无部门') : '',
    })
  },

  onSearch: function (e) {
    this.setData({
      keyword: e.detail
    })
    this.getTeamUserList()
  },
  onCancel: function () {
    this.setData({
      keyword: '',
    })
  },

  onLoad(options) {
    const {
      isAdmin,
      id,
      teamId,
      name
    } = options
    this.setData({
      id: +id,
      teamId: +teamId,
      name,
      userInfo: wx.getStorageSync('userinfo'),
      isAdmin,
    })
    this.getTeamUserList()
    this.getDepartmentList()
  },
  getDepartmentList() {
    getDepartmentList({
      searchId: this.data.teamId
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
      }
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  getMoreUpdateDepartment() {
    const {
      isbatch,
      isSelectList,
      teamId,
      departmentName,
      departmentList
    } = this.data
    // 批量编辑
    if (isbatch && isSelectList.length > 0) {
      let dept = departmentList.find(i => i.text == departmentName)
      let deptId = dept ? (dept.value || dept.id) : null;
      let userIds = isSelectList.filter(id => id !== 'all');

      batchSetUserDepartment({
        teamId: teamId,
        departmentId: deptId,
        teamUserIds: userIds
      }, resd => {
        if (resd && resd.code == 10000) {
          wx.showToast({
            title: '批量编辑成功',
            icon: 'success',
            duration: 1000,
          })
          this.setData({
            showDOverlay1: false,
            isbatch: false,
            isSelectList: []
          })
          setTimeout(() => {
            this.getTeamUserList()
          }, 1000)
        }
      })
    }
  },
  onScroll: function (e) {
    if (Math.ceil(this.data.teamUserLists.length / this.data.pageSize) < Math.ceil(this.data.total / this.data.pageSize)) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getTeamUserList('e')
    }
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