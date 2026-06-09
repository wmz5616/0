// pages/applySettlement/applySettlement.js
import {
  allowedTypes
} from '../../utils/util'
import {
  uploadImages,
  UPLOAD_IMG_BASE_URI,
  shangjiasubmit,
  getIndustry,
  getSuper,
  getshopaudit,
  getshopDetail,
  updateDetail
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    shopAuditInfo: {},
    selectAddress: {
      // title: '东莞体育中心',
      // latitude: '22.39',
      // longitude: '113.31'
    },
    superData: [],
    Industry: [],
    qrcCodeList: [],
    fileList: [],
    bannerList: [],
    typeList: [],
    circleList: [],
    // 0表示类别，1表示商圈
    pickertype: 'typeList',
    showPop: false,
    qrCodeImage: '',
    typeColumns: [{
        value: 0,
        text: '食品',
      },
      {
        value: 1,
        text: '超时百货',
      },
    ],
    circleColumns: [{
        value: 0,
        text: '国贸',
      },
      {
        value: 1,
        text: '万达广场',
      },
    ],
    columns: [],
    showPop1: false,
    showStartTime: '',
    showEndTime: '',
    startTime: '00:00',
    endTime: '23:59',
    minHour: 0,
    maxHour: 23,
    forma: function (type, value) {
      // if (type === 'hour') {
      //   return `${value}时`;
      // }
      // if (type === 'minute') {
      //   return `${value}分`;
      // }
      return value;
    },
  },
  showImg() {
    wx.previewImage({
      urls: [this.data.qrCodeImage],
    })
  },
  submit: function () {
    // if (!this.data.selectAddress.title) {
    //   wx.showToast({
    //     title: '请选择位置信息',
    //     icon: 'none'
    //   })
    //   return
    // }
    const params = {
      coverImageUrl: this.data.fileList[0]?.url,
      galleryImages: this.data.bannerList.map(c => c.url),
      name: this.data.name,
      location: this.data.selectAddress?.latitude ? `${this.data.selectAddress.latitude},${this.data.selectAddress.longitude}` : undefined,
      address: this.data.selectAddress?.title ? this.data.selectAddress.title : undefined,
      // location: '22.39,113.31',
      // address: '东莞体育中兴',
      userName: this.data.userName,
      phone: this.data.phone,
      businessTime: this.data.businessTime,
      // startTime: this.data.showStartTime,
      // endTime: this.data.showEndTime,
      circleIds: this.data.circleList.map(z => z.value),
      industryCategoryIds: this.data.typeList.map(a => a.value),
      description: this.data.description,
      customerPhone: this.data.customerPhone,
      customerCodeImg: this.data.qrcCodeList[0]?.url,
    }
    if (!this.data.shopId && wx.getStorageSync('qualificationCert')) { //判断新增时传入资质认证参数
      params.qualificationCert = wx.getStorageSync('qualificationCert')
    }
    if (this.data.type) {
      updateDetail({
        ...params,
        id: this.data.shopId
      }, resd => {
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
          this.getshopaudit()
        }
      })
    } else {
      wx.requestSubscribeMessage({
        tmplIds: ['_nzBwtw0UA6XeYn3nLLBoyOjD15eHy4IdbRj9DBi8SM', '_aW1f4L5xmqkrN831qbPp1VlZI0uTPA-z737ERCeQWc', '9TsYaPEJc1lLa2o9F3WEn9n8f0pEPyLHRyGJtuNnnWc'],
      })
      shangjiasubmit({
        ...params,
        id: this.data.shopAuditInfo?.id || undefined,
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
  },
  getInputValue: function (e) {
    this.setData({
      [e.currentTarget.dataset.type]: e.detail.value
    })
  },
  isjumpauthority: function () {
    const {
      type
    } = this.data
    // type 存在则是商家详情进入，否则是入驻记录进入
    wx.navigateTo({
      url: `/pages/qualificationVerify/qualificationVerify?id=${this.data.authorityId}&searchType=${type?2:1}&qualificationCert=${this.data.shopAuditInfo.qualificationCert}&shopAuditId=${this.data.shopAuditInfo.id}`,
    })
  },
  // generateQRCode(text) {
  //   // 生成二维码到canvas
  //   drawQrcode({
  //     width: 143 / this.data.rpxRatio,
  //     height: 143 / this.data.rpxRatio,
  //     canvasId: 'qrcodeCanvas',
  //     text: text,
  //     callback: (res) => {
  //       wx.canvasToTempFilePath({
  //         canvasId: 'qrcodeCanvas',
  //         success: (res) => {
  //           this.setData({
  //             qrCodeImage: res.tempFilePath,
  //           });
  //         }
  //       });
  //     }
  //   });
  // },
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
    })
  },
  jumpmap: function () {
    wx.chooseLocation({
      success: (res) => {
        console.log(res)
        // 成功选择位置后，res 对象包含以下信息：
        this.setData({
          selectAddress: {
            title: res.address + res.name,
            longitude: res.longitude,
            latitude: res.latitude
          }
        })
        // 通常将获取到的信息保存到页面 data 中，更新 UI 展示
      },
      fail: (err) => {
        console.error('选点失败：', err);
        // 处理失败情况（详见下文异常处理）
      }
    })
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
    this.setData({
      showStartTime: this.data.startTime,
      showEndTime: this.data.endTime
    })
    this.onClose(e)
  },
  clearItem(e) {
    const {
      value,
      pickertype
    } = e.currentTarget.dataset
    this.setData({
      [pickertype]: this.data[pickertype]?.filter(i => i.value != value)
    })
  },
  onConfirm(e) {
    const pickerData = e.detail.value
    const {
      pickertype
    } = this.data
    const targetList = this.data[pickertype]
    // 判断是否已经选择
    if (targetList.findIndex(i => i.value == pickerData.value) != -1) {
      wx.showToast({
        title: `已选择过该${pickertype == 'typeList'?'类别':'商圈'}`,
        icon: 'none',
      })
      return
    }

    this.setData({
      [pickertype]: [...targetList, pickerData],
      showPop: false,
    })
  },
  onClose(e) {
    const {
      name,
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  openTime() {
    this.setData({
      showPop1: true,
      startTime: this.data.showStartTime ? this.data.showStartTime : '00:00',
      endTime: this.data.showEndTime ? this.data.showEndTime : '23:59'
    })
  },
  openPop(e) {
    const {
      pickertype
    } = e.currentTarget.dataset
    const {
      circleColumns,
      typeColumns
    } = this.data
    let columns = []
    // 行业类别
    if (pickertype == 'typeList') {
      columns = this.data.Industry
    } else if (pickertype == 'circleList') {
      // 商圈
      columns = this.data.superData
    }
    this.setData({
      columns,
      showPop: true,
      pickertype,
    })
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
      if (type == 'qrcCodeList') {
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
      } else {
        wx.cropImage({
          src: f.url, // 需要裁剪的图片临时路径
          cropScale: type == 'fileList' ? '4:3' : '16:9',
          success(res) {
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
          fail(err) {
            console.error('用户取消裁剪或裁剪失败', err);
          }
        })
      }
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
  goBack: function () {
    wx.navigateBack({
      delta: 1,
    });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log(options)
    const app = getApp()
    this.setData({
      type: options.type,
      isIOS: app.globalData.isIOS,
      shopId: options?.shopId ? +options?.shopId : undefined,
      rpxRatio: wx.getStorageSync('rpxRatio')
    })
    // this.generateQRCode('e')
    getIndustry({}, resd => {
      resd.data.map(rex => {
        rex.value = rex.id
        rex.text = rex.name
      })
      this.setData({
        Industry: resd.data
      })
      getSuper({}, resd => {
        resd.data.map(rex => {
          rex.value = rex.id
          rex.text = rex.name
        })
        this.setData({
          superData: resd.data
        })
        if (this.data.shopId) {
          this.getshopaudit()
        }
      })
    })
  },
  getshopaudit: function () {
    const {
      type
    } = this.data
    // 商家详情
    if (type) {
      getshopDetail({
        searchId: this.data.shopId
      }, resd => {
        const data = {
          ...(resd.data?.vo || {}),
          shopPosters: resd.data?.shopPosters || [],
        }

        const industriesData = data.industryCategories.map(q => q.id)
        const circlesData = data.supermarketInfoList.map(q => q.id)
        const circleList = this.data.superData.filter(a => circlesData.findIndex(c => c == a.id) != -1)
        const typeList = this.data.Industry.filter(a => industriesData.findIndex(cc => cc == a.id) != -1)
        const location = data.location ? data.location.split(',') : []
        let plainText = data.description ?
          data.description.replace(/<.+?>/g, '') // 1. 去除所有 HTML 标签
          .replace(/&nbsp;/g, ' ') // 2. 将 &nbsp; 替换为正常空格
          .replace(/\s+/g, ' ') // 3. 将连续的空白字符（换行、制表符等）合并为一个空格
          .trim() : '';
        this.setData({
          authorityId: this.data.shopId,
          typeList,
          circleList,
          shopAuditInfo: {
            qualificationCert: data.qualificationCert
          },
          fileList: data.coverImageUrl.split(',').map(xzc => {
            return {
              url: xzc
            }
          }),
          qrcCodeList: data.customerCodeImg?.split(',').map(xzc => {
            return {
              url: xzc
            }
          }) || [],
          bannerList: data.galleryImages.map(xzc => {
            return {
              url: xzc
            }
          }),
          name: data.name,
          location: data.location,
          selectAddress: {
            title: data.address,
            longitude: location.length ? location[1] : undefined,
            latitude: location ? location[0] : undefined,
          },
          userName: data.userName,
          phone: data.phone,
          businessTime: data.businessTime,
          // showStartTime: data.startTime,
          // showEndTime: data.endTime,
          circleIds: circleList.map(z => z.value),
          industryCategoryIds: typeList.map(a => a.value),
          description: plainText,
          customerPhone: data.customerPhone,
          customerCodeImg: data.customerCodeImg?.split(',').map(xzc => {
            return {
              url: xzc
            }
          }),
        })
      })
    } else {
      // 商家入驻记录
      getshopaudit({
        searchId: this.data.shopId
      }, resd => {
        const formData = resd.data.shopAudit || {}
        const industriesData = resd.data.industries.map(q => q.industryCategoryId)
        const circlesData = resd.data.circles.map(q => q.circleId)
        const location = formData.location ? formData.location.split(',') : []
        let plainText = formData.description ?
          formData.description.replace(/<.+?>/g, '') // 1. 去除所有 HTML 标签
          .replace(/&nbsp;/g, ' ') // 2. 将 &nbsp; 替换为正常空格
          .replace(/\s+/g, ' ') // 3. 将连续的空白字符（换行、制表符等）合并为一个空格
          .trim() : '';
        console.log(this.data.superData)
        this.setData({
          authorityId: formData.shopId,
          typeList: this.data.Industry.filter(a => industriesData.findIndex(cc => cc == a.id) != -1),
          circleList: this.data.superData.filter(a => circlesData.findIndex(c => c == a.id) != -1),
          shopAuditInfo: {
            ...resd.data.settlementApplication,
            ...resd.data.shopAudit
          }
        })
        this.setData({
          fileList: formData.coverImageUrl.split(',').map(xzc => {
            return {
              url: xzc
            }
          }),
          qrcCodeList: formData.customerCodeImg?.split(',').map(xzc => {
            return {
              url: xzc
            }
          }) || [],
          bannerList: JSON.parse(formData.galleryImages).map(xzc => {
            return {
              url: xzc
            }
          }),
          name: formData.name,
          location: formData.location,
          selectAddress: {
            title: formData.address,
            longitude: location.length ? location[1] : undefined,
            latitude: location ? location[0] : undefined,
          },
          userName: formData.userName,
          phone: formData.phone,
          businessTime: formData.businessTime,
          // showStartTime: formData.startTime,
          // showEndTime: formData.endTime,
          circleIds: this.data.circleList.map(z => z.value),
          industryCategoryIds: this.data.typeList.map(a => a.value),
          description: plainText,
          customerPhone: formData.customerPhone,
          customerCodeImg: formData.customerCodeImg?.split(',').map(xzc => {
            return {
              url: xzc
            }
          }),
        })
      })
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
    if (this.data.shopId) {
      this.getshopaudit()
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