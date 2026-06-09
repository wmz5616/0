// pages/inviteMembers/inviteMembers.js
import {
  invitationCode,
  UPLOAD_IMG_BASE_URI
} from '../../utils/request'
import Wxml2Canvas from 'wxml2canvas'
Page({
  /**
   * 页面的初始数据
   */
  data: {
    // showOverlay: false,
  },
  getInvitationCode() {
    invitationCode({
      searchId: this.data.teamId,
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          codeUrl: UPLOAD_IMG_BASE_URI + resd.data.filePath,
        })
      }
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
    })
  },

  drawMyCanvas() {
    wx.showLoading();
    const that = this;
    const query = wx.createSelectorQuery().in(this);
    query
      .select("#canvasId")
      .fields({
        // 选择需要生成canvas的范围
        size: true,
        node: true, // 获取节点信息
      })
      .exec((data) => {
        let cvsWidth = data.width;
        let cvsHeight = data.height;
        that.setData({
          cvsWidth,
          cvsHeight,
        });
        setTimeout(() => {
          that.startDraw();
        }, 500);
      });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  share: function () {
    wx.showLoading()
    const that = this
    const query = wx.createSelectorQuery().in(this);
    query.select('#my-canvas').fields({ // 选择需要生成canvas的范围
      size: true,
      scrollOffset: true
    }, data => {
      setTimeout(() => {
        that.startDraw(data.width*2,data.height*2)
      }, 1500)
    }).exec()
  },
  startDraw(width,height) {
    let that = this
    // 创建wxml2canvas对象
    let drawMyImage = new Wxml2Canvas({
      element: 'myCanvas', // canvas的id,
      obj: that, // 传入当前组件的this
      width,
      height,
      background: '#FFFFFF', // 生成图片的背景色
      progress(percent) { // 进度
        // console.log(percent);
      },
      finish(url) { // 生成的图片
        console.log(url)
        wx.hideLoading()
        that.setData({
          saveImage: url
        })
        that.download()
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

  saveImageToAlbum: function (filePath) {
    wx.saveImageToPhotosAlbum({
      filePath,
      success: function (res) {
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        })
      },
      fail: function (err) {
        console.log(err)
      }
    })
  },
  handleSaveImageFail: function (filePath) {
    wx.showModal({
      title: '是否授权添加到相册',
      content: '需要获取保存图片到相册的权限，否则无法保存图片',
      confirmText:'去授权',
      success: (modalRes) => {
        if (modalRes.confirm) {
          wx.openSetting({
            success: (settingRes) => {
              console.log(settingRes.authSetting)
              if (settingRes.authSetting['scope.writePhotosAlbum']) {
                this.saveImageToAlbum(filePath); // 用户授权后再次尝试保存
              } else {
                wx.authorize({
                  scope: 'scope.writePhotosAlbum',
                  success:()=>{
                    // 用户同意授权，递归调用自身以保存图片
                    this.saveImageToAlbum(filePath)
                  },
                  fail() {
                    // 用户拒绝过授权，引导去设置页
                    wx.showModal({
                      title: '需要相册权限',
                      confirmText:'去授权',
                      content: '需要获取保存图片到相册的权限，否则无法保存图片',
                      success(modalRes) {
                        if (modalRes.confirm) {
                          wx.openSetting({
                            success(settingRes) {
                              if (settingRes.authSetting['scope.writePhotosAlbum']) {
                                // 用户在设置页开启了权限，递归调用自身以保存图片
                                this.saveImageToAlbum(filePath)
                              }
                            }
                          });
                        }
                      }
                    });
                  }
                });
              }
            }
          });
        } else {
          wx.showToast({
            title: '授权失败',
            icon: 'none'
          });
        }
      }
    });
  },
  download: function () {
    wx.getSetting({
      success: (settingRes) => {
        if (settingRes.authSetting['scope.writePhotosAlbum']) {
          // 已授权，直接保存
          this.saveImageToAlbum(this.data.saveImage);
        } else {
          this.handleSaveImageFail(this.data.saveImage);
        }
      }
    })
    // console.log(this.data.saveImage)
    // wx.downloadFile({
    //   url: this.data.saveImage,
    //   success: (res) => {
    //     const filePath = res.tempFilePath

    //     // 判断是否授权
 
    //   },
    //   fail: (err) => {
    //     console.error('下载图片失败:', err);
    //   }
    // })
  },

  onLoad(options) {
    this.setData({
      name: options.name,
      teamId: +options.teamId,
    })
    this.getInvitationCode()
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
      path: `/pages/center/center?teamId=${this.data.teamId}`
    }
  }
})