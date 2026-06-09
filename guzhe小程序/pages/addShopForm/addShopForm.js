// pages/applySettlement/applySettlement.js
import {
  allowedTypes
} from '../../utils/util'
import {
  uploadImages,
  UPLOAD_IMG_BASE_URI,
  categoryLists,
  categoryAdd,
  productAdd,
  productDetail,
  updateStock,
  ticketexport,
  categorySort,
  categorydelete
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    step: 1,
    categorylistData: [],
    categoryIds: [],
    categoryLists: [],
    focusIndex: -1,
    scrollToRow: '',
    fileList: [],
    bannerList: [],
    typeList: [],
    circleList: [],
    // 0表示类别，1表示商圈
    pickertype: 'typeList',
    showPop: false,
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
    currentDate: new Date().getTime(),
    forma: function (type, value) {
      // if (type === 'hour') {
      //   return `${value}时`;
      // }
      // if (type === 'minute') {
      //   return `${value}分`;
      // }
      return value;
    },
    dragIndex: -1,
    startY: 0,
    currentIndex: -1,
    hours: Array.from({
      length: 24
    }, (_, i) => ({
      values: i < 10 ? '0' + i : i.toString()
    })),
    // 生成 00-59 的分钟数组
    minutes: Array.from({
      length: 60
    }, (_, i) => ({
      values: i < 10 ? '0' + i : i.toString()
    })),
    // 生成 00-59 的秒数数组
    seconds: Array.from({
      length: 60
    }, (_, i) => ({
      values: i < 10 ? '0' + i : i.toString()
    })),
    timeColumns: [],
    defailfilelist: [],
    productNo: '',
  },
  getProductOrderExport() {
    wx.showLoading({
      title: '导出中',
    })
    ticketexport({
      searchId: this.data.shopId,
    }, resd => {
      wx.showToast({
        title: '下载成功',
        icon: 'success',
        duration: 900
      });
      setTimeout(() => {
        wx.openDocument({
          filePath: resd,
          showMenu: true,
          success() {

          }
        });
      }, 1000)
    }, fail => {
      console.log(fail)
    })
  },
  bindChange: function (e) {
    this.setData({
      discountTimevalue: e.detail.value,
      discountTimeText: e.detail.value.map(i => i < 10 ? '0' + i : i.toString()).join(':')
    })
  },
  addshopptype: function () {
    const data = JSON.parse(JSON.stringify(this.data.categoryLists))
    data.push({
      sort: data.length + 1
    })
    const newIndex = data.length - 1
    this.setData({
      categoryLists: data,
      focusIndex: newIndex,
      scrollToRow: 'row-' + newIndex
    })
  },
  showtypemodel: function () {
    this.setData({
      focusIndex: -1,
      scrollToRow: '',
      classtypemodal: true
    })
  },
  addcode: function () {
    this.setData({
      showExport: true
    })
  },
  getInputValue: function (e) {
    if (e.currentTarget.dataset.type == 'discountNum' && e.detail.value && (e.detail.value > 9 || e.detail.value < 1)) {
      wx.showToast({
        title: '请输入正确的折扣数字',
        icon: 'none'
      })
      return
    }
    this.setData({
      [e.currentTarget.dataset.type]: e.detail.value
    })
    if (e.currentTarget.dataset.type == 'price') {
      this.setData({
        amount: this.data.discountNum ? e.detail.value * this.data.discountNum : e.detail.value
      })
    }
    if (e.currentTarget.dataset.type == 'discountNum') {
      const z = this.data.price || 0
      this.setData({
        amount: z * (this.data.discountNum / 10)
      })
    }
  },
  saveshot: function () {
    const params = {
      coverImage: this.data.fileList[0]?.url,
      galleryImages: this.data.bannerList.map(c => c.url),
      name: this.data.name,
      specification: this.data.specification,
      unit: this.data.unit,
      price: this.data.price * 100,
      shopId: this.data.id,
      discountNum: this.data.discountNum,
      openDiscount: this.data.openDiscount ? Number(this.data.openDiscount) : undefined,
      openDiscountTime: this.data.openDiscountTime ? Number(this.data.openDiscountTime) : undefined,
      discountTime: this.data.discountTimeText ? this.data.discountTimeText : undefined,
      categoryIds: this.data.categoryIds.map(v => v.id).join(),
      sort: this.data.sort,
      status: this.data.status ? Number(this.data.status) : undefined,
      scheduledTime: this.data.scheduledTime,
      isVirtual: this.data.isVirtual ? Number(this.data.isVirtual) : undefined,
      detail: this.data.defailfilelist.length != 0 ? this.data.defailfilelist.map(c => c.url).join() : undefined,
      remark: this.data.remark,
      timeLimit: this.data.timeLimit,
    }
    if (this.data.shopId) {
      params.id = this.data.shopId
    }
    productAdd({
      ...params
    }, resd => {
      if (resd.code == 10000) {
        wx.showToast({
          title: resd.msg,
          icon: 'success'
        })
        setTimeout(_ => {
          wx.navigateBack()
        }, 200)
      }
    })
  },
  getcategoryLists: function () {
    categoryLists({
      searchField4: this.data.id
    }, resd => {
      resd.data.map(a => {
        a.text = a.name
        a.value = a.id
      })
      this.setData({
        categorylistData: resd.data,
        categoryLists: resd.data
      })
    })
  },
  writeTable: function (e) {
    const data = JSON.parse(JSON.stringify(this.data.categoryLists))
    data[e.currentTarget.dataset.index].name = e.detail.value
    this.setData({
      categoryLists: data
    })
  },
  closetypemodel: function () {
    this.setData({
      classtypemodal: false
    })
  },
  submittypemodel: function () {
    categoryAdd({
      shopId: this.data.id,
      categoryList: this.data.categoryLists
    }, resd => {
      this.setData({
        classtypemodal: false
      })
      this.getcategoryLists()
      wx.showToast({
        title: resd.msg,
        icon: 'success'
      })
    })
  },
  closeType: function (e) {
    const data = JSON.parse(JSON.stringify(this.data.categoryLists))
    const {
      id
    } = e.currentTarget.dataset
    if (id) {
      categorydelete({
        deleteId: id
      }, resd => {
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
          this.getcategoryLists()
        }
      })
    } else {
      data.splice(e.currentTarget.dataset.indexs, 1)
      this.setData({
        categoryLists: data
      })
    }
  },
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
    })
  },
  onInput: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: moment(e.detail).format('YYYY-MM-DD HH:mm:ss')
    })

  },
  ondisInput: function (e) {
    console.log(e)
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  comfrimDate: function (e) {
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
        title: `已选择过该分类`,
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
  editStroage: function () {
    this.setData({
      showRefund: true
    })
  },
  openTime() {
    this.setData({
      showPop1: true,
    })
  },
  openTimed() {
    this.setData({
      disshowPop1: true,
    })
  },
  openPop(e) {
    const {
      pickertype
    } = e.currentTarget.dataset
    this.setData({
      columns: this.data.categorylistData,
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
      if (type == 'defailfilelist') {
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
  uploadfile: function () {
    wx.chooseMessageFile({
      count: 1, // 最多选择1个文件
      type: 'file', // 选择普通文件（如PDF、Word等）
      success: (res) => {
        console.log(res)
        const tempFilePath = res.tempFiles[0].path; // 获取文件的临时路径
        const fileName = res.tempFiles.name; // 获取文件名

        wx.showLoading({
          title: '上传中',
        })

        // 第二步：将文件上传到服务器
        wx.uploadFile({
          header: {
            token: wx.getStorageSync("token")
          },
          url: UPLOAD_IMG_BASE_URI + '/guzhe/wechat/product/ticket/import', // 替换为你的后端上传接口
          filePath: tempFilePath,
          name: 'file', // 后端接收文件的字段名（key）
          formData: { // 可以附带其他额外的表单数据
            productId: this.data.shopId
          },
          success: (uploadRes) => {
            console.log(uploadRes)
            const data = uploadRes.data ? JSON.parse(uploadRes.data) : {}
            if (data.code == 10000) {
              wx.showToast({
                title: data.msg,
                icon: 'success'
              });
              this.setData({
                showExport: false
              })
            } else {
              wx.showToast({
                title: data.msg,
                icon: 'none'
              });
            }
          },
          fail: (err) => {
            console.error('上传失败', err);
            wx.showToast({
              title: '上传失败',
              icon: 'none'
            });
          }
        })
      },
      fail: (err) => {
        console.error('选择文件失败', err);
      }
    })
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
    const pages = getCurrentPages()
    const prevPage = pages[pages.length - 2] // 上一个页面实例
    if (!prevPage) {
      wx.switchTab({
        url: '/pages/index/index',
      })
      return
    }
    const app = getApp()
    wx.navigateBack({
      delta: 1,
    });
  },
  onTouchStart(e) {
    const index = e.currentTarget.dataset.index;
    console.log(index, e)
    this.setData({
      dragIndex: index,
      currentIndex: index,
      startY: e.touches[0]?.clientY
    });
  },


  onTouchMove(e) {
    if (this.data.dragIndex === -1) return;

    const currentIndex = this.data.currentIndex;
    const touchY = e.touches[0]?.clientY;
    const deltaY = touchY - this.data.startY;
    const rowHeight = 50; // 行高估算值
    const moveCount = Math.floor(Math.abs(deltaY) / rowHeight);

    if (moveCount > 0) {
      const direction = deltaY > 0 ? 1 : -1;
      const newIndex = currentIndex + direction * moveCount;

      if (newIndex >= 0 && newIndex < this.data.categoryLists.length && newIndex !== this.data.dragIndex) {
        const newList = [...this.data.categoryLists];
        const draggedItem = newList[this.data.dragIndex];
        newList.splice(this.data.dragIndex, 1);
        newList.splice(newIndex, 0, draggedItem);
        console.log(newList)
        this.setData({
          categoryLists: newList,
          dragIndex: newIndex,
          currentIndex: newIndex,
          startY: touchY
        });
        if (this.data.categoryLists.filter(z => z.id).map(a => a.id).length) {
          categorySort({
            searchIds: this.data.categoryLists.filter(s => s.id).map(a => a.id)
          }, resd => {
            if (resd.code == 10000) {
              this.getcategoryLists()
              wx.showToast({
                title: resd.msg,
                icon: 'success'
              })
            }
          })
        }
      }
    }
  },
  touchMove() {},
  onTouchEnd() {
    this.setData({
      dragIndex: -1,
      currentIndex: -1,
      startY: 0
    });
  },
  onChange: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    const value = e.detail
    if (type == 'openDiscount') {
      this.setData({
        discountNum: '',
        amount: this.data.price
      })
    }
    this.setData({
      [type]: value
    })
  },
  getproductDetail: function () {
    productDetail({
      searchId: Number(this.data.shopId)
    }, resd => {
      if (resd.code == 10000) {
        const data = resd.data
        const {
          hours,
          minutes,
          seconds
        } = this.data
        let plainText = data.discountTime.split(':');
        data.categoryList.map(v => {
          v.text = v.name
          v.value = v.id
        })
        this.setData({
          fileList: data.coverImage.split(',').map(xzc => {
            return {
              url: xzc
            }
          }),
          bannerList: data.galleryImages.map(xzc => {
            return {
              url: xzc
            }
          }),
          defailfilelist: data.detail ? data.detail.split(',').map(xzc => {
            return {
              url: xzc
            }
          }) : [],
          name: data.name,
          specification: data.specification,
          unit: data.unit,
          price: data.price / 100,
          shopId: data.id,
          productNo: data.productNo,
          discountNum: data.discountNum,
          openDiscount: data.openDiscount.toString(),
          openDiscountTime: data.openDiscountTime.toString(),
          discountTimevalue: data.openDiscountTime == 1 ? [hours.findIndex(a => a.values == plainText[0]), minutes.findIndex(a => a.values == plainText[1]), seconds.findIndex(a => a.values == plainText[2])] : [0, 0, 0],
          discountTimeText: data.discountTime,
          categoryIds: data.categoryList,
          sort: data.sort,
          status: data.status.toString(),
          scheduledTime: data.scheduledTime,
          isVirtual: data.isVirtual.toString(),
          detail: plainText,
          remark: data.remark,
          timeLimit: data.timeLimit,
          stock: data.stock,
          refundPrice: data.stock,
          amount: data.discountNum ? (data.price / 100) * (data.discountNum / 10) : data.price
        })
        console.log(this.data)
      }
    })
  },
  changePrice: function (e) {
    let inputValue = e.detail.value.trim()
    // 验证是否为有效数字（允许小数和负数）
    if ((inputValue && isNaN(inputValue)) || (inputValue && !this.isValidNumber(inputValue))) {
      wx.showToast({
        title: '请输入有效数字',
        icon: 'none'
      });
      // 清空无效输入
      // this.setData({
      //   refundPrice: this.data.refundPrice,
      // });
      // return;
    }
    // 转换为数字类型
    let numericValue = Number(inputValue);

    // 验证金额不能小于等于0
    if (numericValue && numericValue <= 0) {
      wx.showToast({
        title: '退款金额要大于0',
        icon: 'none'
      });
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return;
    }

    this.setData({
      refundPrice: numericValue
    })
  },
  // 辅助函数：验证数字格式
  isValidNumber: function (value) {
    // 允许整数、小数、正数
    return /^-?\d*\.?\d+$/.test(value);
  },
  calc: function (e) {
    const {
      add
    } = e.currentTarget.dataset
    const flag = add > 0 ? true : false

    this.setData({
      flag,
      refundPrice: Number(this.data.refundPrice) + add
    })
  },
  getRefund: function () {
    updateStock({
      productId: this.data.shopId,
      stock: this.data.refundPrice
    }, resd => {
      if (resd.code == 10000) {
        wx.showToast({
          title: resd.msg,
          icon: 'success'
        })
        this.getproductDetail()
        this.setData({
          showRefund: false
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const {
      hours,
      minutes,
      seconds
    } = this.data;
    const app = getApp()
    this.setData({
      timeColumns: [hours, minutes, seconds],
      shopId: options.shopId,
      isIOS: app.globalData.isIOS,
      id: options?.id ? +options?.id : undefined,
      shopname: options.name
    })
    console.log(this.data)
    if (options.shopId) {
      this.getproductDetail()
    }
    this.getcategoryLists()
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
      path: `pages/addShopForm/addShopForm?name=${this.data.shopname}&id=${this.data.id}&shopId=${this.data.shopId}`
    }
  }
})