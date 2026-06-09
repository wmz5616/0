// pages/addMerchantAdmin/addMerchantAdmin.js
import {
  userLists,
  managerList,
  managerAdd
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showDialog: false,
    keyword: '',
    selectUserColumn: [],
    list: [],
    pageSize: 10,
    pageNum: 1,
    managerList: [],
    total: 0,
  },
  getManagerList() {
    return new Promise((resolve, reject) => {
      managerList({
        searchField1: this.data.id,
      }, resd => {
        resolve(resd.data || [])
      }, err => {
        reject()
      })
    })
  },
  async getUserLists(e) {
    wx.showLoading({
      title: '加载中',
    })

    let includeList = await this.getManagerList()
    includeList = includeList?.map(i => i.phone)

    userLists({
      searchStrField1: this.data.keyword || undefined,
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize,
    }, resd => {
      wx.hideLoading()
      const data = resd.data?.list || []
      this.setData({
        list: e ? [...data, ...resd.data] : data,
        selectUserColumn: [...includeList],
        managerList: [...includeList],
        total: resd.data.total,
      })
    })
  },
  onInput: function (e) {
    this.setData({
      keyword: e.detail
    })
  },
  async add() {
    let {
      selectUserColumn,
      managerList,
      list
    } = this.data

    selectUserColumn = selectUserColumn.filter(i => !managerList.includes(i))

    if (!selectUserColumn.length) {
      wx.showToast({
        title: '请选择已有管理员以外的用户！',
        icon: 'none',
      })
      return
    }

    wx.hideLoading()

    selectUserColumn.map(i => {
      const item = list?.find(l => l.phone == i) || {}
      return {
        promise: this.getManagerAdd(item.id),
        nickName: item.nickname
      }
    })


    const results = await Promise.allSettled(selectUserColumn.map(i => i.promise))
    const failedNames = []
    results.forEach((result, index) => {
      if (result.status === 'rejected') {
        failedNames.push(selectUserColumn[index].nickname)
      }
    })
    wx.showToast({
      title: failedNames.length ? `${failedNames.join(',')}添加失败!` : '添加成功！',
      icon: failedNames.length ? 'error' : 'success',
    })
    if (!failedNames.length) {
      setTimeout(()=>{
        this.goBack()
      },1000)
    }
  },
  getManagerAdd(userId) {
    return new Promise((resolve, reject) => {
      managerAdd({
        shopId: this.data.id,
        userId,
        headManager: 0,
      }, resd => {
        resolve()
      }, err => {
        reject()
      })
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
    });
  },
  choseMember: function (e) {
    const {
      phone
    } = e.currentTarget.dataset

    let {
      selectUserColumn,
      managerList
    } = this.data
    if (managerList.includes(phone)) {
      wx.showToast({
        title: '该用户已经是管理员！',
        icon: 'none'
      })
      return
    } else if (selectUserColumn.includes(phone)) {
      selectUserColumn = selectUserColumn.filter(i => i != phone)
    } else {
      selectUserColumn.push(phone)
    }
    this.setData({
      selectUserColumn,
    })
  },
  onCancel: function () {
    this.setData({
      keyword: '',
      selectUserColumn: [],
      managerList: [],
      list: [],
      total: 0,
      pageNum: 1,
    })
  },
  onSearch() {
    const {
      keyword
    } = this.data
    if (keyword.length != 11) {
      wx.showToast({
        title: '请输入完整的手机号搜索',
        icon: 'none'
      })
      return
    }
    this.setData({
      pageNum: 1,
    })
    this.getUserLists()
  },
  onScroll: function (e) {
    if (Math.ceil(this.data.list.length / this.data.pageSize) < Math.ceil(this.data.total / this.data.pageSize)) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getUserLists('e')
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      id: +options.id,
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