  // pages/refundRequest/refundRequest.js
  import {
    productExchangeRefund,
    auditRefund,
    uploadImages,
    UPLOAD_IMG_BASE_URI,
    refundInfo,
    productOrderInfo
  } from '../../utils/request'
  import {
    allowedTypes,
    debounce
  } from '../../utils/util'
  Page({

    /**
     * 页面的初始数据
     */
    data: {
      images: [],
      message: '',
      isIOS: false,
      showRefund: false,
      showRefund1: false,
      refundPrice: 100,
      status: 2,
      remark: '',
      step: 100,
    },
    showRefund1() {

      this.setData({
        showRefund1: true,
      })
    },
    showRefund(e) {
      const {
        orderInfo
      } = this.data

      this.setData({
        showRefund: true,
        refundPrice: orderInfo.refundAmount,
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
    // 上传校验
    beforeRead(event) {
      const {
        file,
        callback
      } = event.detail;
      console.log(file.fileType)
      const fileExtension = file.url ? file.url.substring(file.url.lastIndexOf('.')).toLowerCase() : '';
      console.log(fileExtension)
      const isValidExtension = file.url ? allowedTypes.includes(fileExtension) : false;
      // 回调结果
      callback(file.fileType == 'image' && isValidExtension);
      // 如果不合法，可以给出提示
      if (!isValidExtension) {
        wx.showToast({
          title: '只支持jpg、jpeg、png格式',
          icon: 'none',
          duration: 2000
        });
      }
    },
    afterRead(event) {
      const {
        images = []
      } = this.data;
      const {
        file
      } = event.detail;
      console.log(file)
      // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
      uploadImages(file.url, resd => {
        if (resd && resd.code == 10000) {
          wx.showToast({
            title: '上传成功!',
            icon: 'success'
          })
          images.push({
            ...file,
            url: UPLOAD_IMG_BASE_URI + resd.data.url
          });
          this.setData({
            images
          });
        }
      })
    },
    deleteRead: function (e) {
      const index = e.detail.index
      this.setData({
        images: this.data.images.filter((item, i) => i != index)
      })
    },
    goBack: function () {
      const app = getApp()
      app.globalData.needRefreshOrderList = true
      app.globalData.refreshOrderId = this.data.type == 'orderJump' ? this.data.jumpOrderId : this.data.searchId
      app.globalData.myPageSize = this.data.myPageSize
      wx.navigateBack({
        delta: 1,
      });
    },
    jump: function (e) {
      const {
        url,
      } = e.currentTarget.dataset
      wx.navigateTo({
        url,
      })
    },
    change: function (e) {
      this.setData({
        message: e.detail
      })
    },
    changeRemark: function (e) {
      this.setData({
        remark: e.detail.value
      })
    },
    getProductExchangeRefund: function () {
      const {
        searchId,
        message,
        images
      } = this.data
      if (!message.trim()) {
        wx.showToast({
          title: '退款原因不能为空!',
          icon: 'none'
        })
        return
      }
      wx.showLoading()
      productExchangeRefund({
        orderId: searchId,
        refundReason: message,
        images: images.map(i => i.url),
      }, resd => {
        if (resd && resd.code == 10000) {
          wx.showToast({
            title: '退款成功!',
            icon: 'success'
          })
          setTimeout(() => {
            wx.redirectTo({
              url: `/pages/exchangeDetail/exchangeDetail?searchId=${this.data.searchId}`,
            })
          }, 300)
        }
      })
    },
    productOrderInfo() {
      const {
        searchId
      } = this.data
      productOrderInfo({
        searchId
      }, resd => {
        const data = resd.data
        const refundApplyInfo = data?.refundApplyInfo || {}
        const hadCheckNum = data?.ticketList?.length ? data?.ticketList.filter(i => i.status == 3).length : 0
        const orderInfo = data?.orderInfo || {}
        this.setData({
          jumpOrderId: searchId,
          searchId: refundApplyInfo?.id,
          refundApplyInfo,
          orderAddress: data.orderAddress,
          ticketList: data.ticketList,
          orderInfo: {
            ...orderInfo,
            refundAmount: orderInfo.amount - orderInfo.price * hadCheckNum,
          },
          message: refundApplyInfo?.reason || '',
          images: refundApplyInfo?.images ? refundApplyInfo?.images.split(',').map(i => ({
            url: i
          })) : [],
        })
      })
    },
    getRefundInfo: function () {
      const {
        searchId
      } = this.data
      refundInfo({
        searchId,
      }, resd => {
        if (resd && resd.code == 10000) {
          const data = resd.data
          const refundApplyInfo = data?.refundApplyInfo || {}
          const hadCheckNum = data?.ticketList?.length ? data?.ticketList.filter(i => i.status == 3).length : 0
          const orderInfo = data?.orderInfo || {}
          this.setData({
            refundApplyInfo,
            orderAddress: data.orderAddress,
            ticketList: data.ticketList,
            orderInfo: {
              ...orderInfo,
              refundAmount: orderInfo.amount - orderInfo.price * hadCheckNum,
            },
            message: refundApplyInfo?.reason || '',
            images: refundApplyInfo?.images ? refundApplyInfo?.images.split(',').map(i => ({
              url: i
            })) : [],
          })
        }
      })
    },
    geTauditRefund() {
      wx.showLoading({
        title: '操作中',
      })
      const {
        searchId,
        status,
        refundPrice
      } = this.data
      auditRefund({
        applyId: searchId,
        status,
        refundAmount: refundPrice || undefined,
        remark: this.data.remark || '1',
      }, resd => {
        wx.showToast({
          title: '审核成功！',
        })
        this.setData({
          showRefund: false,
          showRefund1: false,
        })
        this.goBack()
      })
    },
    onConfirm: debounce(function (e) {
      const {
        status
      } = e.currentTarget.dataset
      this.setData({
        status: +status,
      })
      this.geTauditRefund()
    }),
    changePrice: function (e) {
      let inputValue = e.detail.value.trim()
      // 验证是否为有效数字（允许小数和负数）
      if (!inputValue || isNaN(inputValue) || !this.isValidNumber(inputValue)) {
        wx.showToast({
          title: '请输入有效数字',
          icon: 'none'
        });
        // 清空无效输入
        this.setData({
          refundPrice: this.data.refundPrice,
        });
        return;
      }
      // 转换为数字类型
      let numericValue = Number(inputValue);

      // 验证金额不能小于等于0
      if (numericValue <= 0) {
        wx.showToast({
          title: '退款金额要大于0',
          icon: 'none'
        });
        this.setData({
          refundPrice: this.data.refundPrice,
        });
        return;
      }

      // 验证金额不能大于退款金额
      if (numericValue * 100 > this.data.orderInfo.amount) {
        wx.showToast({
          title: '超出支付金额',
          icon: 'none'
        });
        this.setData({
          refundPrice: this.data.refundPrice,
        });
        return;
      }

      this.setData({
        refundPrice: numericValue * 100
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

      if (!flag && this.data.refundPrice <= this.data.step) {
        wx.showToast({
          title: '退款金额要大于0',
          icon: 'none'
        })
        this.setData({
          refundPrice: this.data.refundPrice,
        });
        return
      }
      if (flag && this.data.refundPrice + this.data.step > this.data.orderInfo.amount) {
        wx.showToast({
          title: '超出支付金额',
          icon: 'none'
        })
        this.setData({
          refundPrice: this.data.refundPrice,
        });
        return
      }
      this.setData({
        flag,
        refundPrice: Number(this.data.refundPrice) + add
      })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      const app = getApp()
      this.setData({
        searchId: +options.searchId,
        type: options.type,
        isIOS: app.globalData.isIOS,
        myPageSize: +options.myPageSize,
      })
      // 订单列表跳转
      if (this.data.type == 'orderJump') {
        this.productOrderInfo()
      } else {
        // 审核列表跳转
        this.getRefundInfo()
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