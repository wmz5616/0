  // pages/refundRequest/refundRequest.js
  import {
    productInfo,
    uploadImages,
    UPLOAD_IMG_BASE_URI,
    productOrderRefundd
  } from '../../utils/request'
  import {
    allowedTypes
  } from '../../utils/util'
  import moment from 'moment'
  Page({

    /**
     * 页面的初始数据
     */
    data: {
      images: [],
      message: '',
      orderInfo: {},
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
    goBack() {
      wx.navigateBack({
        delta: 1,
      });
    },

    getProductInfo: function () {
      const {
        searchId
      } = this.data
      productInfo({
        searchId,
      }, resd => {
        if (resd && resd.code == 10000) {
          const data = resd.data
          const refundApplyInfo = data?.refundApplyInfo || {}
          const hadCheckNum = data?.ticketList?.length ? data?.ticketList.filter(i => i.status == 3).length : 0
          const orderInfo = data?.orderInfo || {}
          this.setData({
            orderInfo: {
              ...orderInfo,
              deadline: moment(orderInfo.deadline).format('YYYY-MM-DD'),
              amount: orderInfo.amount - orderInfo.price * hadCheckNum,
            },
            message: refundApplyInfo?.reason || '',
            images: refundApplyInfo?.images?.length ? refundApplyInfo.images.split(',').map(i => ({
              url: i
            })) : [],
          })
        }
      })
    },
    change(e) {
      this.setData({
        message: e.detail
      })
    },
    getProductOrderRefund() {
      wx.requestSubscribeMessage({
        tmplIds: ['Wc4hN5noMvfgnHwO9Qyr2RT_dIjCSxavpfqWUczOYEw'],
        success(res) {}
      })
      if (!this.data.message) {
        wx.showToast({
          title: '请填写退款原因',
          icon: 'none'
        })
        return
      }

      wx.showLoading({
        title: '加载中',
      })
      productOrderRefundd({
        orderId: this.data.searchId,
        refundReason: this.data.message,
        images: this.data.images.length ? this.data.images.map(i => i.url) : undefined
      }, resd => {
        wx.showToast({
          title: '操作成功！',
          icon: 'success',
          duration: 1000,
        })
        setTimeout(() => {
          this.goBack()
        }, 1000)
      })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
      this.setData({
        searchId: +options.searchId,
      })
      this.getProductInfo()
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