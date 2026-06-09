// pages/userMag/userMag.js
import {
  updateUserInfo,
  userInfo,
  uploadImages,
  UPLOAD_IMG_BASE_URI,
  phonebywechat
} from '../../utils/request'
import {
  loginOut
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    // 昵称
    nickname: '',
    disabled: true,
    showDialog: false
  },
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  // 改绑手机号
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url,
    })
  },
  getUserInfo: function () {
    userInfo({}, re => {
      if (re.code == 10000) {
        wx.setStorageSync('userinfo', re.data)
        console.log(re.data)
        this.setData({
          userInfo: re.data
        })
      }
    })
  },
  onChooseAvatar: function (e) {
    const {
      avatarUrl
    } = e.detail;
    console.log(avatarUrl, e)
    uploadImages(avatarUrl, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '上传成功!',
          icon: 'success'
        })
        const userinfo = this.data.userInfo
        userinfo.avatar = UPLOAD_IMG_BASE_URI + resd.data.url
        this.setData({
          avatar: UPLOAD_IMG_BASE_URI + resd.data.url,
          userInfo: userinfo
        }, () => {
          this.getUpdateUserInfo()
        })
      }
    })
  },
  getThePhoneNumber(e) {
    let {
      encryptedData,
      iv
    } = e.detail
    console.log(e.detail)
    if (!encryptedData || !iv) {
      showModal({
        content: '注册失败，请您同意授权获取手机号码！',
        showCancel: false
      })
      return
    }
    wx.checkSession({
      success: result => {
        console.log(result)
        phonebywechat({
            code: this.data.loginCode,
            encryptedData,
            iv
          },
          res => {
            wx.login({
              success: res => {
                if (res.code) this.setData({
                  loginCode: res.code
                })
              }
            })
            if (res.code == 10000) {
              wx.showToast({
                title: res.msg,
                icon: 'success'
              })
            }
          },
          err => {
            wx.login({
              success: res => {
                if (res.code) this.setData({
                  loginCode: res.code
                })
              }
            })
            if (err.code == 400) wx.login({
              success: res => {
                if (res.code) this.setData({
                  loginCode: res.code
                })
                this.getThePhoneNumber(e)
              }
            })
          }
        )
      },
      fail: res => {
        console.log(res)
        wx.login({
          success: res => {
            if (res.code) this.setData({
              loginCode: res.code
            })
            this.getThePhoneNumber(e)
          }
        })
        // showModal({
        //   confirmText: '好的',
        //   content: '登录态已过期，请点击重新授权！',
        //   showCancel: false
        // })
      }
    })
  },
  changeAble: function (e) {
    const {
      disabled
    } = e.currentTarget.dataset
    this.setData({
      disabled,
    }, () => {
      if (disabled) this.getUpdateUserInfo()
    })
  },
  getUpdateUserInfo: function () {
    const {
      avatar,
      nickname
    } = this.data
    updateUserInfo({
      avatar,
      nickname
    }, resd => {
      if (resd && resd.code == 10000) {
        this.getUserInfo()
      }
    })
  },
  afterRead: function (e) {
    const {
      file
    } = e.detail
    uploadImages(file.url, resd => {
      if (resd && resd.code == 10000) {
        console.log(resd)
        this.setData({
          avatar: UPLOAD_IMG_BASE_URI + resd.data.url
        }, () => {
          this.getUpdateUserInfo()
        })
      }
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
  open: function (e) {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: true,
    })
  },
  showDialog() {
    // loginOut({}, resd => {
    //   if (resd && resd.code == 10000) {
        wx.clearStorageSync()
        wx.reLaunch({
          url: '/pages/login/login',
        })
    //   }
    // })
  },
  onChange(e) {
    this.setData({
      nickname: e.detail
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    wx.login({
      success: res => {
        if (res.code) this.setData({
          loginCode: res.code
        })
      }
    })
    const data = wx.getStorageSync('userinfo')
    // 获取个人信息
    this.setData({
      userInfo: data,
      nickname: data.nickname,
      avatar: data.avatar,
    })
    this.getUserInfo()
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