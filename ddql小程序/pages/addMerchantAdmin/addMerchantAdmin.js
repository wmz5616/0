// pages/addMerchantAdmin/addMerchantAdmin.js
import {
  managerAdd
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    id: 0,
    name: '',
    phone: '',
  },

  onInputName: function (e) {
    this.setData({
      name: e.detail
    })
  },
  
  onInputPhone: function (e) {
    this.setData({
      phone: e.detail
    })
  },

  add() {
    const { id, name, phone } = this.data;
    if (!name.trim()) {
      wx.showToast({ title: '请输入管理员姓名', icon: 'none' });
      return;
    }
    if (phone.length !== 11) {
      wx.showToast({ title: '请输入正确的11位手机号', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '添加中' });
    managerAdd({
      shopId: id,
      sort: 2, // 默认排序2
      name: name.trim(),
      phone: phone,
      headManager: 0
    }, resd => {
      wx.hideLoading();
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '添加成功！',
          icon: 'success',
        });
        setTimeout(() => {
          this.goBack();
        }, 1000);
      }
    }, err => {
      wx.hideLoading();
    });
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
    this.setData({
      id: +(options.id || 0),
    })
  },

  onReady() {},
  onShow() {},
  onHide() {},
  onUnload() {},
  onPullDownRefresh() {},
  onReachBottom() {},
  onShareAppMessage() {}
})