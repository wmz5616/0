// pages/onlineWithdrawal/onlineWithdrawal.js
import Wxml2Canvas from 'wxml2canvas'
import {
  selectUserTeams,
  userwithdrawal,
  userwithdrawalInfo,
  checkInSetting
} from '../../utils/request'
import {
  withdrawalMessage
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    withdrawalInstruction: [],
    withdrawalPictureList: [],
    checkInSetting: wx.getStorageSync('checkInSetting') || {},
    showDialog: false,
    showPopup: false,
    // 0分享，1团体
    showPopupType: 0,
    teamList: [],
    teamInfo: {
      team: {}
    },
    amount: '',
  },
  onClose: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
    if (name == 'showPopup2') {
      wx.showModal({
        title: '确认提现',
        content: '是否已确认分享',
        confirmColor: '#04C0D9',
        complete: (res) => {
          if (res.confirm) {
            userwithdrawal({
              teamId: this.data.teamInfo.team.id,
              amount: this.data.amount
            }, resd => {
              if (resd.code == 10000) {
                const {
                  state,
                  withdrawalId
                } = resd.data
                if (!state || state == 'FAIL') {
                  wx.showToast({
                    title: '提现失败',
                    icon: 'error'
                  })
                } else if (state == 'SUCCESS') {
                  wx.showToast({
                    title: '提现成功!',
                    icon: 'success'
                  })
                  this.getUserwithdrawalInfo()
                  // this.setData({
                  //   showPopup2: true
                  // })
                  this.getSelectUserTeams()
                } else if (state == 'CANCELING' || state == 'CANCELLED') {
                  wx.showToast({
                    title: withdrawalMessage[state],
                    icon: 'success'
                  })
                } else {
                  wx.showToast({
                    title: withdrawalMessage[state],
                    icon: 'success'
                  })
                  this.setData({
                    withdrawalId,
                    //  showPopup2: true
                  })
                  this.getUserwithdrawalInfo()
                }
              }
            })
          }
        }
      })
    }
  },
  downLoadFile: function () {
    wx.downloadFile({
      url: this.data.teamInfo.avatar,
      success: (res) => {
        this.setData({
          avatar: res.tempFilePath
        })
      }
    })
  },
  goti: function () {
    wx.navigateTo({
      url: `/pages/healthCoin/healthCoin?type=olineti&healthCoin=${this.data.healthCoin}`,
    })
  },
  withdrawal: function () {
    if (!this.data.amount) {
      wx.showToast({
        title: '提现金额不能为空！',
        icon: 'none'
      })
      return
    }
    this.setData({
      showPopupType: false,
      showPopup: true
    })
    wx.showLoading()
    const that = this
    const query = wx.createSelectorQuery().in(this);
    query.select('#my-canvas').fields({ // 选择需要生成canvas的范围
      size: true,
      scrollOffset: true
    }, data => {
      let width = data.width;
      let height = data.height;
      that.setData({
        width,
        height
      })
      setTimeout(() => {
        that.startDraw()
      }, 1500);
    }).exec()
  },
  startDraw() {
    let that = this
    console.log(22)
    // 创建wxml2canvas对象
    let drawMyImage = new Wxml2Canvas({
      fileType: 'png',
      element: 'myCanvas', // canvas的id,
      obj: that, // 传入当前组件的this
      // width: that.data.width * 2,
      // height: that.data.height * 2,
      background: '#fff', // 生成图片的背景色
      progress(percent) { // 进度
        // console.log(percent);
      },
      finish(url) { // 生成的图片
        wx.hideLoading()
        that.setData({
          imgUrl: url,
          showPopup: true
        })
        console.log(url, 123123)
        // that.savePoster(url)
      },
      error(res) { // 失败原因
        console.log(res);
        wx.hideLoading()
      }
    }, this);
    let data = {
      // 获取wxml数据
      list: [{
        type: 'wxml',
        class: '.my_canvas .my_draw_canvas', // my_canvas要绘制的wxml元素根类名， my_draw_canvas单个元素的类名（所有要绘制的单个元素都要添加该类名）
        limit: '.my_canvas', // 要绘制的wxml元素根类名
        x: 0,
        y: 0
      }]
    }
    // 绘制canvas
    drawMyImage.draw(data, this);
  },
  savePoster() {
    const that = this
    wx.saveImageToPhotosAlbum({
      filePath: that.data.imgUrl,
      success: function () {
        wx.showToast({
          title: '保存成功',
          icon: 'none',
          duration: 1500
        });
      },
      fail(err) {
        if (err.errMsg === "saveImageToPhotosAlbum:fail:auth denied" || err.errMsg === "saveImageToPhotosAlbum:fail auth deny" || err.errMsg === "saveImageToPhotosAlbum:fail authorize no response") {
          wx.showModal({
            title: '提示',
            content: '需要您授权保存相册',
            showCancel: false,
            success: modalSuccess => {
              wx.openSetting({
                success(settingdata) {
                  if (settingdata.authSetting['scope.writePhotosAlbum']) {
                    wx.saveImageToPhotosAlbum({
                      filePath: that.data.imgUrl,
                      success: function () {
                        wx.showToast({
                          title: '保存成功',
                          icon: 'success',
                          duration: 2000
                        })
                      },
                    })
                  } else {
                    wx.showToast({
                      title: '授权失败，请稍后重新获取',
                      icon: 'none',
                      duration: 1500
                    });
                  }
                }
              })
            }
          })
        }
      }
    })
  },
  getUserwithdrawalInfo: function (searchId) {
    userwithdrawalInfo({
      searchId: this.data.withdrawalId,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = JSON.parse(resd.data.wxResult)
        console.log(data)
        wx.requestMerchantTransfer({
          mchId: '1728733634',
          appId: 'wx308569eed4cced50',
          package: data.packageInfo,
          success() {
            wx.showToast({
              title: e.errMsg,
              icon: 'success'
            })
            setTimeout(_ => {
              this.getSelectUserTeams()
            }, 1500)
          },
          fail(e) {
            console.log(e)
            wx.showToast({
              title: e.errMsg,
              icon: 'error'
            })
          },
        })
      }
    })
  },
  shareLine: function () {
    if (!this.data.imgUrl) {
      return
    }
    wx.showShareImageMenu({
      path: this.data.imgUrl,
      entrancePath: '/pages/index/index',
      entrancePath: true
    })
    this.setData({
      showPopup: false,
      showPopup2: true
    })
  },
  open: function (e) {
    const {
      name,
      showpopuptype
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
      showPopupType: showpopuptype,
    })
  },
  getAmount: function (e) {
    this.setData({
      amount: +e.detail.value,
      amountLength: e.detail.value?.toString().length
    })
  },
  switchteam: function (e) {
    const {
      teamList,
      searchId
    } = this.data
    this.setData({
      showPopup: false,
      teamInfo: teamList.find(i => i.teamId == searchId),
      healthCoin: teamList.find(i => i.teamId == searchId).healthyCoin
    })
  },
  bindChange: function (e) {
    const val = e.detail.value[0]
    const {
      teamList
    } = this.data
    const item = teamList[val]
    this.setData({
      searchId: item.teamId,
    })
  },
  getSelectUserTeams: function () {
    selectUserTeams({
      searchType: 1
    }, resd => {
      if (resd && resd.code == 10000) {
        if (resd.data.length != 0) {
          const data = resd.data
          const teamInfo = this.data.teamInfo?.id ? data.find(x => x.id == this.data.teamInfo.id) : resd.data[0]
          this.setData({
            healthCoin: teamInfo.healthyCoin,
            teamInfo,
            teamList: resd.data
          })
          this.downLoadFile()
        }
      }
    })
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
    wx.getSystemInfo({
      success: (res) => {
        console.log(res)
        this.setData({
          screenWidth: res.screenWidth
        });
      }
    });
    wx.showShareMenu({
      withShareTicket: true,
      menus: ['shareAppMessage', 'shareTimeline']
    })
    checkInSetting({}, resd => {
      this.setData({
        withdrawalUrl: Math.floor(Math.random() * (this.data.withdrawalPictureList.length)),
        withdrawalPictureList: resd.data.withdrawalPictureList || [],
        withdrawalInstruction: resd.data.withdrawalInstruction?.split('<br>')
      })
      wx.downloadFile({
        url: this.data.withdrawalPictureList[this.data.withdrawalUrl],
        success: (res) => {
          this.setData({
            csdUrl: res.tempFilePath
          })
        }
      })
      wx.setStorageSync('checkInSetting', resd.data)
    })
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

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {
    return {
      title: `都动起来`,
      path: '/pages/index/index'
    };
  },

  onShareTimeline() {
    return {
      title: `都动起来`,
      path: '/pages/index/index'
    };
  }
})