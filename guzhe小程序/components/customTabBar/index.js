// custom-tab-bar/index.js
import {
  totalAuditCount,
} from '../../utils/request'
Component({
  /**
   * 组件的初始数据
   */
  properties: {
    selectIndex: {
      type: Number,
      value: 0,
      observer: function (newVal, oldVal) {
        // 属性值变化时执行
        this.setData({
          selected: newVal
        })
      }
    }
  },
  data: {
    selected: 0, // 当前菜单中的索引
    color: '#5A5A68',
    tabListd: [{
        "pagePath": "/pages/index/index", // 页面地址
        "text": "首页", // 菜单名
        "iconPath": "/assets/images/tabbar/index.svg", // 菜单图标
        "selectedIconPath": "/assets/images/tabbar/indexslcet.svg" // 选中时的菜单图标
      },
      {
        "pagePath": "/pages/lottery/lottery",
        "text": "商品订单",
        "iconPath": "/assets/images/tabbar/shopp.svg",
        "selectedIconPath": "/assets/images/tabbar/shoppselect.svg"
      },
      {
        "pagePath": "/pages/merchant/merchant",
        "text": "商家管理",
        "iconPath": "/assets/images/tabbar/mer.svg",
        "selectedIconPath": "/assets/images/tabbar/marselect.svg"
      },
      {
        "pagePath": "/pages/userCenter/userCenter",
        "text": "我的",
        "iconPath": "/assets/images/tabbar/usercenter.svg",
        "selectedIconPath": "/assets/images/tabbar/activeUser.svg"
      },
    ],
  },
  pageLifetimes: {
    show() {
      const token = wx.getStorageSync('token')
      token && this.getTotalAuditCount()
    },
  },
  lifetimes: {
    attached() {
      this.setData({
        selected: this.data.selectIndex
      })
    },
  },
  observers: {
    'numberA, numberB': function (numberA, numberB) {
      // 在 numberA 或者 numberB 被设置时，执行这个函数
      this.setData({
        sum: numberA + numberB
      })
    }
  },
  /**
   * 组件的方法列表
   */
  methods: {
    switchTab(e) {
      let dataset = e.currentTarget.dataset;
      const app = getApp()
      app.globalData.pagePath = dataset.path
      console.log(dataset.path)
      wx.switchTab({
        url: dataset.path,
      })
    },
    getTotalAuditCount() {
      totalAuditCount({}, resd => {
        const tabListd = [...this.data.tabListd]
        const index = tabListd.findIndex(i => i.pagePath == '/pages/merchant/merchant')
        if (index != -1) tabListd[index].bulge = resd.data?.totalRefundAuditCount || 0
        console.log(tabListd, this.data.selected)
        this.setData({
          tabListd,
        })
      })
    },
  }
})