// pages/imgManageMent/imgManageMent.js
import {
  allowedTypes
} from '../../utils/util'
import {
  getshopDetail,
  uploadImages,
  UPLOAD_IMG_BASE_URI,
  shopContract,
  posterUpdate
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    shopName: '',
    bannerList: [],
    isIOS: false,
  },
  // 上传校验
  beforeRead(event) {
    const {
      file,
      callback
    } = event.detail;
    // 当设置 multiple 为 true 时，file 为数组格式，否则为对象格式
    const files = Array.isArray(file) ? file : [file];

    // 校验所有文件
    const allValid = files.every(f => {
      const fileExtension = f.url ? f.url.substring(f.url.lastIndexOf('.')).toLowerCase() : '';
      const isValidExtension = f.url ? allowedTypes.includes(fileExtension) : false;
      return f.fileType == 'image' && isValidExtension;
    });

    // 回调结果
    callback(allValid);

    // 如果有不合法的文件，给出提示
    if (!allValid) {
      wx.showToast({
        title: '只支持jpg、jpeg、png格式',
        icon: 'none',
        duration: 2000
      });
    }
  },
  afterRead(event) {
    const {
      file
    } = event.detail;
    const {
      type
    } = event.currentTarget.dataset
    // 当设置 multiple 为 true 时，file 为数组格式，否则为对象格式
    const files = Array.isArray(file) ? file : [file];
    const list = this.data[type];

    let successCount = 0;

    // 同步处理：递归逐个处理文件
    const processFile = (index) => {
      if (index >= files.length) {
        // 所有文件处理完成
        if (successCount > 0) {
          wx.showToast({
            title: successCount > 1 ? `成功上传${successCount}张图片!` : '上传成功!',
            icon: 'success'
          });
          this.setData({
            [type]: list,
          });
        }
        return;
      }

      const f = files[index];
      wx.editImage({
        src: f.url,
        success: (res) => {
          wx.showLoading({
            title: '上传中',
          })
          uploadImages(res.tempFilePath, resd => {
            if (resd && resd.code == 10000) {
              wx.hideLoading()
              successCount++;
              list.push({
                url: UPLOAD_IMG_BASE_URI + resd.data.url
              });
            }
            // 处理下一个文件
            processFile(index + 1);
          }, err => {
            wx.hideLoading()
          });
        },
        fail: () => {
          // 处理下一个文件
          processFile(index + 1);
        }
      });
    };

    // 开始处理第一个文件
    processFile(0);
  },
  deleteRead: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    const index = e.detail.index
    this.setData({
      [type]: this.data[type].filter((item, i) => i != index)
    })
  },
  goBack() {
    wx.navigateBack({
      delta: 1,
      shopDetail: {},
    });
  },
  getshopDetail: function () {
    const {
      imgType
    } = this.data
    getshopDetail({
      searchId: this.data.shopId
    }, resd => {
      if (resd && resd.code == 10000) {
        const shopDetail = {
          ...(resd.data?.vo || {}),
          shopPosters: resd.data?.shopPosters || [],
        }
        const bannerList = imgType == 2 ? shopDetail.contract.length ? shopDetail.contract.split(',') : [] : imgType == 1 ? shopDetail.shopPosters.length ? shopDetail.shopPosters.map(i => i.url) : [] : []
        console.log(bannerList)
        this.setData({
          shopDetail,
          bannerList: bannerList.map(xzc => {
            return {
              url: xzc
            }
          }),
        })
      }
    })
  },
  submit() {
    let {
      imgType,
      shopId,
      bannerList
    } = this.data
    // if (!bannerList.length) {
    //   wx.showToast({
    //     title: imgType == 2 ? '请填写商家合同照片' : '请填写商家海报照片',
    //     icon: 'none'
    //   })
    //   return
    // }
    bannerList = bannerList.length ? bannerList.map(c => c.url).join(',') : undefined

    wx.showLoading({
      title: '加载中',
    })
    // 合同
    if (imgType == 2) {
      shopContract({
        searchId: shopId,
        searchStrField1: bannerList,
      }, resd => {
        wx.showToast({
          title: '操作成功！',
        })
        this.getshopDetail()
      }, err => {
        wx.hideLoading()
      })
    } else {
      posterUpdate({
        searchId: shopId,
        searchStrField1: bannerList,
      }, resd => {
        wx.showToast({
          title: '操作成功！',
        })
        this.getshopDetail()
      }, err => {
        wx.hideLoading()
      })
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const app = getApp()
    this.setData({
      shopName: options.name,
      shopId: +options.shopId,
      imgType: +options.imgType,
      isIOS: app.globalData.isIOS,
    })
    this.getshopDetail()
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