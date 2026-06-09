// pages/baseInfo/baseInfo.js
import {
  teamStatus,
  validateField,
  auditStatus
} from '../../utils/util'
import {
  deleteTeamUser,
  nextAreaInfo,
  updateTeams,
  selectUserTeams,
  teamUserList,
  updateTeamUser,
  rechargeOrderLists,
  rechargeOrderCount,
  selectTeamApplicationRecord,
  updateTeamApplicationRecord,
  updateTeamUserType,
  selectVerificationRecordList,
  teamDelete,
  selectCheckInSettings,
  updateCheckInSettings,
  checkInList,
  checkInCount,
  withdrawalExport,
  coinList,
  checkiinexport,
  orderExport,
  createDepartment,
  updateDepartment,
  deleteDepartment,
  batchSetUserDepartment
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    consTodal: 0,
    startDate: moment().startOf('month').format('YYYY-MM-DD'),
    start: moment().startOf('month').format('YYYY年MM月'),
    tixianlist: [],
    dateTypes: 'date',
    columns1: ['全部', '扫码打卡', '场地打卡'],
    columns2: ['9点', '8点', '7点'],
    activeTitle: '基础信息',
    teamStatus: {},
    value: '',
    active: 0,
    listData: [],
    columns: [],
    showPopup1: false,
    startTimes: new Date("2025-10-01").getTime(),
    endTimes: new Date().getTime(),
    startTime: new Date("2025-10-01").getTime(),
    endTime: new Date().getTime(),
    minDate: new Date("2025-10-01").getTime(),
    maxDate: new Date().getTime(),
    showOverlay: false,
    showDialog: false,
    showDDialog: false,
    name: '',
    type: '',
    region: '',
    address: '',
    contactPerson: '',
    contactEmail: '',
    isMultiDepartment: '',
    // 0表示团体，1表示地区
    pickertype: 0,
    areaColumns: [],
    showAreaPicker: false,
    teamInfo: {},
    selectItemInfo: {},
    selectTeamId: '',
    // 选择团体成员的index
    selectRadio: '',
    // 切换团体管理的内容
    changeFlag: false,
    keyword: '',
    teamUserLists: [],
    total: 0,
    pageNum: 1,
    pageSize: 10,
    forma: function (type, value) {
      if (type === 'year') {
        return `${value}年`;
      }
      if (type === 'month') {
        return `${value}月`;
      }
      if (type === 'day') {
        return `${value}日`;
      }
      return value;
    },
    // 部门列表
    departmentList: [],
    // 新增还是编辑部门
    isAddD: true,
    // 是否批量编辑部门
    isbatch: false,
    // 选中的人id
    isSelectList: [],
    showDOverlay: false,
    departmentName: '',
    showAreaPicker: false,
    showDOverlay1: false,
    editDName: '',
  },
  onDConfirm: function (e) {
    const pickerData = e.detail.value
    this.setData({
      showDepartmentPicker: false,
      departmentName: pickerData.text,
    })
  },
  onDepartmentClickIcon() {
    this.setData({
      showDepartmentPicker: true,
    })
  },
  openBatch() {
    const {
      isSelectList
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
      departmentName: '',
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
  goaudit: function () {
    this.setData({
      active: 6,
      activeTitle: '申请审核'
    })
  },
  onSearch: function (e) {
    this.setData({
      keywords: e.detail
    })
    this.getcoinList()
  },
  comfrimsearch: function () {
    this.getcoinList()
  },
  search: function (e) {
    this.setData({
      keywords: e.detail
    })
  },
  changedate: function (e) {
    this.setData({
      start: moment(e.detail.value).format('YYYY年MM月'),
      startDate: e.detail.value
    })
    this.getcoinList()
  },
  reachbottomsd: function () { //提现记录分页
    if (Math.ceil(this.data.tixiantotal / 10) > Math.ceil(this.data.tixianlist.length / 10)) {
      this.setData({
        pageNum: this.data.pageNum += 1
      })
      this.getcoinList()
    }
  },
  getcoinList: function (e) { //提现记录列表
    console.log(e)
    coinList({
      startTime: moment(this.data.startDate).format('YYYY-MM-DD 00:00:00'),
      endTime: moment(this.data.startDate).endOf('month').format('YYYY-MM-DD 23:59:59'),
      coinType: 1,
      teamId: this.data.teamInfo.teamId,
      pageNum: this.data.pageNum,
      numType: 2,
      keyword: this.data.keywords,
    }, resd => {
      this.setData({
        tixianlist: e ? this.data.tixianlist.concat(resd.data.list) : resd.data.list,
        tixiantotal: resd.data.total
      })
    })
  },
  getSaveTeamApplicationRecord: function () {

    const pattern = /^[\w-]+(\.[\w-]+)*@[a-z0-9]+(\.[a-z]+)*(\.[a-z]{2,})$/i;
    if (!this.data.email) {
      wx.showToast({
        title: '邮箱不可为空',
        icon: 'none'
      })
      return
    }
    if (!pattern.test(this.data.email)) {
      wx.showToast({
        title: '邮箱格式有误',
        icon: 'none'
      })
      return
    }
    wx.showLoading()
    if (this.data.types == 1) {
      checkiinexport({
        searchIntList: [this.data.teamId],
        isReturnPermsData: false,
        searchStrField3: this.data.email,
        searchField3: this.data.searchField3 || undefined,
        searchStatusList: [2],
        searchField2: this.data.searchField2 == 0 || !this.data.searchField2 ? undefined : this.data.searchField2 - 1,
        startTime: moment(this.data.startTimes).format('YYYY-MM-DD 00:00:00'),
        endTime: moment(this.data.endTimes).format('YYYY-MM-DD 23:59:59'),
      }, resd => {
        wx.showLoading()
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
          this.setData({
            showOverlays: false
          })
        }
      })
    } else if (this.data.types == 2) {
      withdrawalExport({
        numType: 2,
        keyword: this.data.keyword,
        teamId: this.data.teamInfo.teamId,
        email: this.data.email,
        startTime: moment(this.data.startDate).format('YYYY-MM-DD 00:00:00'),
        endTime: moment(this.data.startDate).endOf('month').format('YYYY-MM-DD 23:59:59'),
      }, resd => {
        wx.showLoading()
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
          this.setData({
            showOverlays: false
          })
        }
      })
    } else {
      orderExport({
        searchStrField3: this.data.email,
        searchField2: this.data.selectItemInfo.value,
        startTime: moment(this.data.startTime).format("YYYY-MM-DD 00:00:00"),
        endTime: moment(this.data.endTime).format("YYYY-MM-DD 23:59:59"),
      }, resd => {
        wx.showLoading()
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
          this.setData({
            showOverlays: false
          })
        }
      })
    }
  },
  onInputs: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  closeemail: function (e) {
    this.setData({
      [e.currentTarget.dataset.name]: false
    })
  },
  comfrimPicker: function () {
    this.setData({
      showPopup11: false
    })
    this.getcheckInList(false)
    this.getcheckInCount()
  },
  getSelectVerificationRecordList: function () {
    selectVerificationRecordList({
      searchId: +this.data.teamId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data || {}
        this.setData({
          teamStatus: data
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
  onDepartmentConfirm(e) {
    const {
      value
    } = e.detail
    this.setData({
      editDepartmentName: value,
      showDepartmentModal: false
    })
  },
  onConfirm: function (e) {
    const value = e.detail
    this.setData({
      searchField2: value.index,
      showPicker: false,
    })
    this.getcheckInList(false)
    this.getcheckInCount()
  },
  reachbottoms: function () {
    console.log(this.data.listData.length, this.data.total)
    if (Math.ceil(this.data.total / 10) > Math.ceil(this.data.listData.length / 10)) {
      this.setData({
        pageNum: this.data.pageNum += 1
      })
      this.getcheckInList(true)
    }
  },
  update: function () { //更新打卡提现设置
    const params = {
      teamId: this.data.teamInfo.teamId,
      scanCodeTime: (this.data.hour * 60) + this.data.minute,
      scanCodeHealthyCoin: this.data.scanCodeHealthyCoin,
      stepsOpen: this.data.stepsOpen,
      targetSteps: !this.data.stepsOpen ? this.data.targetSteps : 0,
      stepsHealthyCoin: !this.data.stepsOpen ? this.data.stepsHealthyCoin : 0,
      lowestWithdrawalMoney: this.data.lowestWithdrawalMoney,
      isUserAuth: this.data.isUserAuth,
    }
    const title = validateField(params)
    if (title) {
      wx.showToast({
        title,
        icon: 'none'
      })
      return
    }
    // wx.showLoading({
    //   title: '加载中',
    // })
    updateCheckInSettings(params, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '更新成功！',
          icon: 'success'
        })
      }
    })
  },
  onScroll: function () {
    if (Math.ceil(this.data.list.length / this.data.pageSize) < Math.ceil(this.data.total / this.data.pageSize)) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getRechargeOrderLists('e')
    }
  },
  getUpdateTeamApplicationRecord: function (status, id) {
    const {
      departmentName1,
      departmentList
    } = this.data
    let deptId = undefined
    if (departmentName1) {
      let dept = departmentList.find(i => i.text == departmentName1)
      deptId = dept ? dept.id : undefined;
    }
    updateTeamApplicationRecord({
      id,
      departmentId: deptId,
      status,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '审核成功!',
          icon: 'success'
        })
        this.getSelectTeamApplicationRecord()
      }
    })
  },
  getSelectTeamApplicationRecord() {
    selectTeamApplicationRecord({
      searchId: this.data.teamId,
      searchIntStatus: 0,
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        this.setData({
          applyList: data.list,
          consTodal: data.total,
          total: data.total,
        })
      }
    })
  },
  showTime: function () {
    this.setData({
      showPopup11: true
    })
  },
  showPicker: function (e) {
    const {
      type
    } = e.currentTarget.dataset;
    const {
      columns1,
      columns2
    } = this.data
    this.setData({
      columns: type == 'status' ? columns1 : columns2,
      showPicker: true,
    })
  },
  confirm: function (e) {
    const data = e.detail.value
    this.setData({
      selectItemInfo: data,
      selectTeamId: data.value,
    })
    this.getRechargeOrderLists()
    this.getRechargeOrderCount()
    this.onClose(e)
  },
  onInput: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  comfrimDate: function (e) {
    if (this.data.endTime < this.data.startTime) {
      wx.showToast({
        title: '结束时间不能小于开始时间',
        icon: 'none'
      })
      return
    }
    this.getRechargeOrderLists()
    this.getRechargeOrderCount()
    this.onClose(e)
  },
  getRechargeOrderLists: function (e) {
    const {
      startTime,
      endTime,
      selectItemInfo,
      pageNum,
      pageSize
    } = this.data
    rechargeOrderLists({
      searchField2: selectItemInfo.value,
      // 订单状态: 0无、1待支付、2已支付、3已取消、4已退款
      // searchIntStatus: 2,
      startTime: moment(startTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(endTime).format("YYYY-MM-DD 23:59:59"),
      pageNum,
      pageSize
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        this.setData({
          list: e ? this.data.list.concat(data.list) : data.list,
          total: data.total
        })
      }
    })
  },

  accSubtr: function (arg1, arg2) {
    var r1, r2, m, n;
    try {
      r1 = arg1.toString().split(".")[1].length;
    } catch (e) {
      r1 = 0;
    }
    try {
      r2 = arg2.toString().split(".")[1].length;
    } catch (e) {
      r2 = 0;
    }
    m = Math.pow(10, Math.max(r1, r2));
    //动态控制精度长度
    n = (r1 >= r2) ? r1 : r2;
    return ((arg1 * m - arg2 * m) / m).toFixed(n);
  },
  getRechargeOrderCount: function () {
    const {
      startTime,
      endTime,
      selectItemInfo
    } = this.data
    rechargeOrderCount({
      searchStatusList: [0, 1, 2, 4],
      searchField2: selectItemInfo.value,
      startTime: moment(startTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(endTime).format("YYYY-MM-DD 23:59:59"),
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          totalAmount: this.accSubtr(resd.data.amount / 100, resd.data.refundAmount / 100),
          refundAmount: resd.data.refundAmount
        })
      }
    })
  },
  getMoreUpdateDepartment() {
    const {
      isbatch,
      isSelectList,
      teamId,
      departmentName,
      departmentList
    } = this.data
    if (!departmentName) {
      wx.showToast({
        title: '请选择部门！',
        icon: 'none',
      })
      return
    }
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
            icon: 'success'
          })
          this.setData({
            showDOverlay1: false,
            isbatch: false,
            isSelectList: []
          })
          this.getTeamUserList()
        }
      })
    }
  },
  getUpdateDepartment() {
    const {
      isAddD,
      editDName,
      selectDepartment,
      teamId
    } = this.data
    if (!editDName) {
      wx.showToast({
        title: '请输入部门名称',
        icon: 'none'
      })
      return
    }
    // 新增
    if (isAddD) {
      createDepartment({
        teamId: teamId,
        name: editDName,
        sort: 0,
        status: 1
      }, resd => {
        if (resd && resd.code == 10000) {
          wx.showToast({
            title: '创建成功',
            icon: 'success'
          })
          this.setData({
            showDOverlay: false
          })
          this.getTeamUserList()
        }
      })
    } else {
      // 编辑部门
      updateDepartment({
        id: selectDepartment.id,
        teamId: teamId,
        name: editDName,
        sort: selectDepartment.sort || 0,
        status: 1
      }, resd => {
        if (resd && resd.code == 10000) {
          wx.showToast({
            title: '更新成功',
            icon: 'success'
          })
          this.setData({
            showDOverlay: false
          })
          this.getTeamUserList()
        }
      })
    }
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
  onCancel: function () {
    console.log(11)
    this.setData({
      keyword: '',
    })
    this.getTeamUserList()
  },
  onSearch: function (e) {
    this.setData({
      keyword: e.detail
    })
    this.getTeamUserList()
  },
  getTeamUserList: function () {
    const {
      keyword,
      pageNum,
      pageSize,
      teamId,
      userInfo
    } = this.data
    teamUserList({
      teamId,
      keyword,
      status: 0,
      pageNum,
      pageSize,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        this.setData({
          teamUserLists: this.data.activeTitle == '团体管理' ? data.user.filter(i => i.type != 0) : data.user,
          departmentList: data?.departments?.length ? data.departments.map(i => ({
            ...i,
            text: i.name,
            value: i.id,
          })) : [],
          total: data.total,
        })
      }
    })
  },
  changeTeam: function () {
    this.setData({
      changeFlag: true,
    })
    this.getTeamUserList()
  },
  v: function () {

  },
  getacceptEmail: function (e) {
    this.setData({
      email: e.detail.value
    })
  },
  exportrecord: function (e) {
    this.setData({
      showOverlays: true,
      types: e.currentTarget.dataset.types
    })
  },
  onCloselays: function () {
    this.setData({
      showOverlays: false
    })
  },
  change: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: +e.detail.value
    })
  },
  changeRadio: function () {
    this.setData({
      stepsOpen: this.data.stepsOpen ? 0 : 1
    })
  },
  choseMember: function (e) {
    const {
      index,
      name
    } = e.currentTarget.dataset
    const selectRadio = index === this.data.selectRadio ? '' : index
    this.setData({
      selectRadio,
      tearnuserName: name,
      showDialog: selectRadio !== '' ? true : false,
    })
  },
  getselectCheckInSettings: function () {
    selectCheckInSettings({
      searchId: this.data.teamInfo.teamId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        this.setData({
          settingData: data,
          hour: Math.floor(data.scanCodeTime / 60),
          minute: data.scanCodeTime % 60,
          scanCodeHealthyCoin: data.scanCodeHealthyCoin,
          stepsOpen: data.stepsOpen,
          targetSteps: data.targetSteps,
          stepsHealthyCoin: data.stepsHealthyCoin,
          lowestWithdrawalMoney: data.lowestWithdrawalMoney,
        })
      }
    })
  },
  getSelectUserTeams: function () {
    selectUserTeams({}, resd => {
      if (resd && resd.code == 10000) {
        const columns = resd.data.map(i => ({
          text: i.team.name,
          value: i.teamId,
          healthyCoin: i.healthyCoin,
        }))
        const teamInfo = resd.data.length ? resd.data.find(i => i.teamId == this.data.teamId) : {}
        teamInfo.isUserAuth = teamInfo.team.isUserAuth == 1 ? '是' : '否'
        teamInfo.isMultiDepartment = teamInfo.team.isMultiDepartment == 1 ? '是' : '否'
        this.setData({
          teamInfo,
          name: teamInfo?.team?.name || '',
          type: teamStatus[teamInfo?.team?.type ?? ''] ?? '',
          region: teamInfo?.team?.region || '',
          address: teamInfo?.team?.address || '',
          contactPerson: teamInfo?.team?.contactPerson || '',
          checkInNumLimit: teamInfo?.team?.checkInNumLimit || 0,
          contactPhone: teamInfo?.team?.contactPhone || '',
          contactEmail: teamInfo?.team?.contactEmail || '',
          isUserAuth: teamInfo.isUserAuth,
          isMultiDepartment: teamInfo.isMultiDepartment,
          columns,
          selectItemInfo: columns.find(i => i.value == this.data.selectTeamId),
        })
      }
    })
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
        const areaColumns = this.data.areaColumns
        areaColumns[item.level - 1] = {
          values: data,
          defaultIndex: region ? data.findIndex(i => i.id == item.id) : 0
        }
        this.setData({
          areaColumns,
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
      region,
      isAdmin,
      departmentList
    } = this.data
    if (isAdmin) return
    let areaColumns = []
    if (pickertype == 'status') {
      areaColumns = teamStatus
    } else if (pickertype == 'audit' || pickertype == 'multiDept') {
      areaColumns = auditStatus
    } else if (pickertype == 'department' || pickertype == 'department1') {
      areaColumns = departmentList
    } else {
      this.getNextAreaInfo(100000, region)
    }
    this.setData({
      areaColumns,
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
    if (pickertype != 'area') return
    const {
      index,
      value
    } = e.detail
    index != 2 && this.getNextAreaInfo(value[index].id)
  },
  onAreaConfirm: function (e) {
    const pickerData = e.detail
    const {
      pickertype
    } = this.data
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
    } else if (pickertype == 'department') {
      this.setData({
        departmentName: pickerData.value.text
      })
    } else if (pickertype == 'department1') {
      this.setData({
        departmentName1: pickerData.value.text
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
  onRadioChange(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({
      [type]: e.detail,
    });
  },
  getUpdateTeams: function () {
    const params = {
      id: this.data.teamId,
      name: this.data.name,
      type: teamStatus.indexOf(this.data.type),
      region: this.data.region,
      contactPerson: this.data.contactPerson,
      checkInNumLimit: this.data.checkInNumLimit || 0,
      contactPhone: this.data.contactPhone,
      contactEmail: this.data.contactEmail || undefined,
      isUserAuth: this.data.isUserAuth == '是' ? 1 : 0,
      isMultiDepartment: this.data.isMultiDepartment == '是' ? 1 : 0,
    }
    const title = validateField(params)
    if (title) {
      wx.showToast({
        title,
        icon: 'none'
      })
      return
    }
    updateTeams({
      ...params,
      address: this.data.address || '',
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '修改成功',
        })
        this.getSelectUserTeams()
      }
    })
  },
  jump: function (e) {
    if (this.data.isAdmin) return
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
    })
  },
  jumpD: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
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
  open: function (e) {
    const {
      name,
      selectteam,
      applyitem
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
      selectTeam: selectteam ? selectteam : undefined,
      editName: selectteam ? selectteam.userName : '',
      editPhone: selectteam ? selectteam.userPhone : '',
      applyItem: applyitem ? applyitem : undefined,
      departmentName: selectteam ? (selectteam.departmentName || this.data.name) : '',
      departmentName1: applyitem ? applyitem.departmentName : '',
    })
  },
  batchEdit() {
    this.setData({
      isbatch: !this.data.isbatch,
      isSelectList: [],
    })
  },
  openD(e) {
    const {
      name,
      selectdepartment,
      type
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
      selectDepartment: selectdepartment ? selectdepartment : undefined,
      editDName: selectdepartment ? selectdepartment.name : '',
      isAddD: type ? true : false,
      departmentName: ''
    })
  },
  onChange: function (e) {
    this.setData({
      activeTitle: e.detail.title,
      pageNum: 1,
      isbatch: false,
    })
    const active = e.detail.index
    if (this.data.activeTitle == '充值记录') {
      this.getRechargeOrderLists()
      this.getRechargeOrderCount()
    }

    if (e.detail.title == '打卡记录') {
      this.getcheckInList(false)
      this.getcheckInCount()
    }
    if (e.detail.title == '打卡提现设置') {
      this.getselectCheckInSettings()
    }
    if (e.detail.title == '提现记录') {
      this.getcoinList()
    }
    if (active == 0 || active == 2) {
      this.getSelectUserTeams()
    } else if (this.data.activeTitle == '成员管理' || this.data.activeTitle == '打卡提现设置') {
      this.setData({
        keyword: '',
        selectRadio: ''
      })
      this.getTeamUserList()
    } else if (this.data.activeTitle == '申请审核') {
      this.getTeamUserList()
      this.getSelectTeamApplicationRecord()
    } else if (this.data.activeTitle == '团体管理') {
      this.setData({
        teamUserLists: [],
      })
    }
    this.setData({
      active,
      changeFlag: false,
      updateAll: false,
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
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
      pickerInstance,
      updateAll,
      applyList
    } = this.data
    let data = {}
    if (reset && pickertype == 'status') {
      data.type = ''
    } else if (reset && pickertype == 'area') {
      data.region = ''
    } else if (reset && pickertype == 'department') {
      data.departmentName = ''
    } else if (reset && pickertype == 'department1') {
      data.departmentName1 = ''
    }
    if (name == 'showDialog' && this.data.activeTitle == '申请审核') {
      if (updateAll) {
        applyList.forEach(i => {
          this.getUpdateTeamApplicationRecord(2, i.id)
        });
      } else {
        this.getUpdateTeamApplicationRecord(2, this.data.applyItem.id)
      }
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
  btn: function () {
    const {
      active,
      isbatch
    } = this.data
    if (active == 0) {
      this.getUpdateTeams()
    } else if (this.data.activeTitle == '成员管理' && !isbatch) {
      wx.navigateTo({
        url: `/pages/inviteMembers/inviteMembers?teamId=${this.data.teamId}&name=${this.data.teamInfo.team.name}`,
      })
    } else if (this.data.activeTitle == '申请审核') {
      if (this.data.applyList.length) {
        this.setData({
          showDialog: true,
          updateAll: true,
        })
      } else {
        wx.showToast({
          title: '暂无申请审核!',
          icon: 'none'
        })
      }
    } else if (this.data.activeTitle == '团体管理') {
      wx.showModal({
        title: '确定要解散该团体吗？',
        content: '',
        confirmColor: '#04C0D9',
        complete: (res) => {
          if (res.confirm) {
            teamDelete({
              deleteId: this.data.teamId
            }, resd => {
              if (resd.code == 10000) {
                wx.showToast({
                  title: resd.msg,
                  icon: 'success'
                })
                setTimeout(_ => {
                  wx.navigateBack()
                }, 300)
              }
            })
          }
        }
      })
    }
  },
  dialogDConfirm() {
    const {
      selectDepartment
    } = this.data
    deleteDepartment({
      searchId: selectDepartment.id
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '删除成功',
          icon: 'success'
        })
        this.getTeamUserList()
      }
    })
  },
  dialogConfirm: function (e) {
    const {
      active,
      updateAll,
      applyList
    } = this.data
    if (this.data.activeTitle == '成员管理') {
      this.getDeleteTeamUser()
    } else if (this.data.activeTitle == '申请审核') {
      if (updateAll) {
        applyList.forEach(i => {
          this.getUpdateTeamApplicationRecord(1, i.id)
        });
      } else {
        this.getUpdateTeamApplicationRecord(1, this.data.applyItem.id)
      }
    } else if (this.data.activeTitle == '团体管理') {
      this.getUpdateTeamUserType()
    }
  },
  // 转让团体用户类型
  getUpdateTeamUserType: function () {
    const {
      selectRadio,
      teamUserLists,
      teamId
    } = this.data
    wx.showLoading({
      title: '加载中',
    })
    updateTeamUserType({
      teamId,
      userId: teamUserLists[selectRadio].userId,
      // 0 创建者, 1 管理员, 2 普通用户
      type: 0,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '转让成功!',
          icon: 'success'
        })
        this.setData({
          keyword: '',
          selectRadio: ''
        })
        this.goBack()
      }
    })
  },
  getDeleteTeamUser() {
    const {
      selectTeam
    } = this.data
    if (selectTeam.type == 0) return
    deleteTeamUser({
      teamId: selectTeam.teamId,
      userId: selectTeam.userId,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: selectTeam.userId == this.data.userInfo.id ? '退出成功!' : '移除成功!',
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
    const {
      teamId,
      active,
      isAdmin
    } = options
    this.setData({
      teamId: +teamId,
      selectTeamId: +teamId,
      active: active || 0,
      userInfo: wx.getStorageSync('userinfo'),
      isAdmin,
    }, () => {
      !active && this.getSelectUserTeams()
    })
  },
  getcheckInList: function (isAppend) {
    const {
      teamId
    } = this.data
    checkInList({
      searchIntList: [teamId],
      isReturnPermsData: false,
      searchField3: this.data.searchField3 || undefined,
      searchStatusList: [2],
      searchField2: this.data.searchField2 == 0 || !this.data.searchField2 ? undefined : this.data.searchField2 - 1,
      startTime: moment(this.data.startTimes).format('YYYY-MM-DD 00:00:00'),
      endTime: moment(this.data.endTimes).format('YYYY-MM-DD 23:59:59'),
      pageNum: this.data.pageNum
    }, resd => {
      this.setData({
        listData: isAppend ? this.data.listData.concat(resd.data.list || []) : (resd.data.list || []),
        total: resd.data.total
      })
    })
  },
  getcheckInCount: function () {
    const {
      teamId
    } = this.data
    checkInCount({
      searchIntList: [teamId],
      isReturnPermsData: false,
      searchField2: this.data.searchField2 == 0 || !this.data.searchField2 ? undefined : this.data.searchField2 - 1,
      startTime: moment(this.data.startTimes).format('YYYY-MM-DD 00:00:00'),
      endTime: moment(this.data.endTimes).format('YYYY-MM-DD 23:59:59'),
      searchStatusList: [2],
    }, resd => {
      this.setData({
        checkCount: resd.data
      })
    })
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
    this.getSelectVerificationRecordList()
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