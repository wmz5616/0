import {
  checkInRankLists,
  withdrawalRankLists,
  selectUserTeams,
  submitFeedback,
  getFeedbackList,
  getDepartmentList,
  BASE_URI
} from '../../utils/request'
import {
  formatSecond
} from '../../utils/util'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    dateType: 'date',
    teamInfo: {},
    departmentInfo: { text: '全部部门', value: '' },
    columns: [],
    columns2: [{
      text: '全部部门',
      value: '',
    }],
    startTime: new Date().getTime(),
    endTime: new Date().getTime(),
    minDate: new Date("2025-10-01").getTime(),
    maxDate: new Date().getTime(),
    showPopup: false,
    showPopup1: false,
    showPopup2: false,
    active: 1,
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
    // 个人排行
    selfRankData: {},
    // 排行列表
    rankList: [],
    total: 0,
    showAudit: false,
    checked: false,
    textarea: '',
    x: 629,
    y: 1060,
    showExport: false,
    exportType: 1,
  },
  showExport(){
    this.setData({
      showExport: true,
      exportType: 1,
    });
  },
  cancelExport(){
    this.setData({
      showExport: false,
    });
  },
  confirmExport() {
    const { exportType, teamInfo, departmentInfo, startTime, endTime } = this.data;
    if (!teamInfo || !teamInfo.value) {
      wx.showToast({ title: '请选择团队', icon: 'none' });
      return;
    }
    if (exportType === 1) {
      wx.showLoading({ title: '正在获取反馈...' });
      getFeedbackList({ teamId: teamInfo.value }, res => {
        wx.hideLoading();
        if (res && res.code === 10000) {
          const list = res.data || [];
          if (list.length === 0) {
            wx.showToast({ title: '暂无反馈数据', icon: 'none' });
            return;
          }
          const text = list.map(item => `时间：${item.createTime}\n用户：${item.userName}\n内容：${item.content}`).join('\n\n');
          wx.setClipboardData({
            data: text,
            success: () => {
              wx.showToast({ title: '反馈内容已复制', icon: 'success' });
              this.setData({ showExport: false });
            }
          });
        } else {
          wx.showToast({ title: res.msg || '获取失败', icon: 'none' });
        }
      });
    } else if (exportType === 2) {
      wx.showLoading({ title: '正在导出月榜...' });
      const param = {
        searchField1: teamInfo.value,
        searchField2: departmentInfo && departmentInfo.value !== '' ? departmentInfo.value : null,
        searchField4: 0,
        startTime: moment(startTime).format("YYYY-MM-DD 00:00:00"),
        endTime: moment(endTime).format("YYYY-MM-DD 23:59:59")
      };
      
      wx.request({
        url: BASE_URI + '/ddql/wechat/user/check_in/rank/export',
        method: 'POST',
        data: param,
        header: {
          'content-type': 'application/json',
          'token': wx.getStorageSync("token")
        },
        responseType: 'arraybuffer',
        success: (res) => {
          wx.hideLoading();
          if (res.statusCode === 200) {
            const fs = wx.getFileSystemManager();
            const tempFilePath = `${wx.env.USER_DATA_PATH}/月榜排名.xlsx`;
            fs.writeFile({
              filePath: tempFilePath,
              data: res.data,
              encoding: 'binary',
              success: () => {
                wx.openDocument({
                  filePath: tempFilePath,
                  showMenu: true,
                  fileType: 'xlsx',
                  success: function (res) {
                    console.log('打开文档成功')
                  }
                })
                this.setData({ showExport: false });
              },
              fail: (err) => {
                wx.showToast({ title: '文件保存失败', icon: 'none' });
              }
            });
          } else {
            wx.showToast({ title: '导出失败', icon: 'none' });
          }
        },
        fail: () => {
          wx.hideLoading();
          wx.showToast({ title: '网络错误', icon: 'none' });
        }
      });
    }
  },
  onTouchStart(e) {
    this.startX = e.touches[0].clientX
    this.startY = e.touches[0].clientY
    this.elementX = this.data.x
    this.elementY = this.data.y
  },
  onTouchMove(e) {
    const moveX = (e.touches[0].clientX - this.startX) * this.rpxRatio
    const moveY = (e.touches[0].clientY - this.startY) * this.rpxRatio
    this.setData({
      x: this.elementX + moveX,
      y: this.elementY + moveY
    });
  },
  onTouchEnd(e) {
    const endX = e.changedTouches[0].clientX;
    const endY = e.changedTouches[0].clientY;
    const diffX = Math.abs(endX - this.startX);
    const diffY = Math.abs(endY - this.startY);

    if (diffX < 5 && diffY < 5) {
      this.showAudit();
    }
  },
  textareaInput(e) {
    this.setData({
      textarea: e.detail
    })
  },
  selected() {
    this.setData({
      checked: !this.data.checked,
    });
  },
  Eselected(e) {
    this.setData({
      exportType: parseInt(e.currentTarget.dataset.value),
    });
  },
  closeOver() {
    this.setData({
      showAudit: false,
    });
  },
  showAudit() {
    this.setData({
      showAudit: true,
    });
  },
  submitFeedback() {
    const { textarea, checked, teamInfo } = this.data;
    if (!textarea.trim()) {
      wx.showToast({
        title: '请输入反馈内容',
        icon: 'none'
      });
      return;
    }
    if (!teamInfo || !teamInfo.value) {
      wx.showToast({
        title: '请选择团队',
        icon: 'none'
      });
      return;
    }
    submitFeedback({
      teamId: teamInfo.value,
      content: textarea,
      isAnonymous: checked ? 1 : 0
    }, res => {
      if (res && res.code === 10000) {
        wx.showToast({
          title: '提交成功',
          icon: 'success'
        });
        this.setData({
          showAudit: false,
          textarea: '',
          checked: false
        });
      } else {
        wx.showToast({
          title: res.msg || '提交失败',
          icon: 'none'
        });
      }
    });
  },
  comfrimDate: function (e) {
    const {
      active
    } = this.data
    if (this.data.endTime < this.data.startTime) {
      wx.showToast({
        title: '结束时间不能小于开始时间',
        icon: 'none'
      })
      return
    }
    if (active != 3) {
      this.getCheckInRankLists()
    } else {
      this.getWithdrawalRankLists()
    }
    this.onClose(e)
  },
  onInput: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  onChange: function (e) {
    const active = e.detail.index
    const data = {}
    if (active == 0) {
      data.startTime = moment().subtract(1, 'days').startOf('day').valueOf()
      data.endTime = moment().subtract(1, 'days').endOf('day').valueOf()
      data.dateType = 'date'
    } else if (Object.keys(this.data.teamInfo).length && active == 1) {
      data.startTime = moment().startOf('day').valueOf()
      data.endTime = moment().startOf('day').valueOf()
      data.dateType = 'date'
    } else if (active == 2 || active == 3) {
      data.startTime = moment().startOf('month').valueOf()
      data.endTime = moment().endOf('month').valueOf()
      data.dateType = 'year-month'
    }
    this.setData({
      active,
      ...data
    })
    if (Object.keys(this.data.teamInfo).length && active !== 3) {
      this.getCheckInRankLists()
      return
    }
    if (Object.keys(this.data.teamInfo).length && active == 3) {
      this.getWithdrawalRankLists()
    }
  },
  getDepartmentList: function (searchId) {
    getDepartmentList({
      searchId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const list = (resd.data || []).map(i => ({
          text: i.name,
          value: i.id
        }))
        list.unshift({ text: '全部部门', value: '' })
        this.setData({
          columns2: list
        })
      } else {
        this.setData({
          columns2: [{ text: '全部部门', value: '' }]
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
        }))
        this.setData({
          columns,
          teamInfo: columns[0],
        })
        if (columns.length > 0) {
          this.getDepartmentList(columns[0].value)
        }
        this.getCheckInRankLists()
      }
    })
  },
  getWithdrawalRankLists: function () {
    const {
      teamInfo,
      departmentInfo,
      pageNum,
      pageSize,
      endTime,
      startTime
    } = this.data
    console.log(teamInfo)
    withdrawalRankLists({
      searchField2: teamInfo.value,
      searchField3: departmentInfo && departmentInfo.value !== '' ? departmentInfo.value : null,
      startTime: moment(startTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(endTime).format("YYYY-MM-DD 23:59:59"),
      pageNum,
      pageSize,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        const selfRankData = data.selfRankData ? {
          ...data.selfRankData,
        } : null
        
        const listData = data.pageInfo ? data.pageInfo.list : (data.list || [])
        const totalCount = data.pageInfo ? data.pageInfo.total : (data.total || 0)

        this.setData({
          rankList: listData,
          selfRankData: selfRankData,
          total: totalCount,
        })
      }
    })
  },
  getCheckInRankLists: function () {
    const {
      teamInfo,
      departmentInfo,
      pageNum,
      pageSize,
      endTime,
      startTime
    } = this.data
    console.log(teamInfo)
    checkInRankLists({
      searchField1: teamInfo.value,
      searchField2: departmentInfo && departmentInfo.value !== '' ? departmentInfo.value : null,
      startTime: moment(startTime).format("YYYY-MM-DD 00:00:00"),
      endTime: moment(endTime).format("YYYY-MM-DD 23:59:59"),
      pageNum,
      pageSize,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        let selfRankData = {}
        if (data.selfRankData) {
          selfRankData = {
            ...data.selfRankData,
            placeName: data.selfRankData.placeList?.map(a => a.name).join('、'),
            time: formatSecond(data.selfRankData.checkInTime),
          }
          const x = []
          if (selfRankData.placeList) {
            const dcs = JSON.parse(JSON.stringify(selfRankData.placeList))
            selfRankData.placeList.map(s => {
              if (x.filter(z => z.checkInTypeImages == s.checkInTypeImages).length == 0) {
                x.push({
                  img: s,
                  num: dcs.filter(z => z.checkInTypeImages == s.checkInTypeImages).length
                })
              }
            })
            selfRankData.placeList = x
          }
          selfRankData.radio = data.checkInNumLimit == 0 ? 100 : ((selfRankData.checkInNum / data.checkInNumLimit) * 100).toFixed(2)
        }

        const listData = data.pageInfo ? data.pageInfo.list : (data.list || [])
        const totalCount = data.pageInfo ? data.pageInfo.total : (data.total || 0)
        const limitCount = data.checkInNumLimit || 0

        const rankList = listData.map(i => {
          return {
            ...i,
            placeName: i?.placeList?.map(a => a.name).join('、'),
            time: formatSecond(i?.checkInTime)
          }
        })
        
        rankList.map(xx => {
          xx.radio = limitCount == 0 ? 100 : ((xx.checkInNum / limitCount) * 100).toFixed(2)
          const x = []
          if (xx.placeList) {
            const dc = JSON.parse(JSON.stringify(xx.placeList))
            xx.placeList.map(s => {
              if (x.filter(z => z.checkInTypeImages == s.checkInTypeImages).length == 0) {
                x.push({
                  img: s.checkInTypeImages,
                  num: dc.filter(z => z.id == s.id).length
                })
              }
              xx.placeList = x
            })
          }
        })
        this.setData({
          rankList: rankList,
          selfRankData: data.selfRankData ? selfRankData : null,
          total: totalCount,
          checkInNumLimit: limitCount
        })
      }
    })
  },
  showPopup: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    wx.hideTabBar({
      animation: true // 可选，是否开启动画效果
    })
    this.setData({
      [type]: true,
    })
  },
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
    wx.showTabBar({
      animation: true // 可选，是否开启动画效果
    })
  },
  confirm: function (e) {
    const data = e.detail.value
    const {
      name
    } = e.currentTarget.dataset
    const {
      active
    } = this.data
    
    if (name === 'showPopup2') {
      this.setData({
        departmentInfo: data,
      })
    } else {
      this.setData({
        teamInfo: data,
        departmentInfo: { text: '全部部门', value: '' },
      })
      this.getDepartmentList(data.value)
    }
    
    if (active != 3) {
      this.getCheckInRankLists()
    } else {
      this.getWithdrawalRankLists()
    }
    this.onClose(e)
  },
  goBack: function () {
    const pages = getCurrentPages()
    // 如果是返回其他页面用navigateBack
    if (pages.length > 2) {
      wx.navigateBack({
        delta: 1,
      });
      return
    }
    // 否则返回首页
    wx.switchTab({
      url: '/pages/index/index',
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.rpxRatio = wx.getStorageSync('rpxRatio')
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
    const app = getApp()
    const isLoginBack = app.globalData.isLoginBack
    console.log(isLoginBack)
    if (isLoginBack) {
      app.globalData.isLoginBack = undefined
      return
    }
    // 获取个人信息
    this.setData({
      userInfo: wx.getStorageSync('userinfo'),
      token: wx.getStorageSync('token')
    })
    if (this.data.token) {
      // 获取关联团体
      this.getSelectUserTeams()
    }
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
  // onPullDownRefresh() {
  //   if(this.data.showPopup1){
  //     return
  //   }
  //   this.getSelectUserTeams()
  //   setTimeout(_ => {
  //     wx.stopPullDownRefresh();
  //   }, 300)
  // },

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