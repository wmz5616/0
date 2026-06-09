import {
  productSelectList,
  productDelete,
  productClass,
  shopUpdatestatus
} from '../../utils/request'

Page({

  data: {
    keyId: 0,
    productClassList: [],
    clsModelvisible: false,
    list: [{
        id: 1,
        text: '列表项 1',
        x: 0
      },
      {
        id: 2,
        text: '列表项 2',
        x: 0
      },
      {
        id: 3,
        text: '列表项 3',
        x: 0
      },
      {
        id: 4,
        text: '列表项 4',
        x: 0
      },
      {
        id: 5,
        text: '列表项 5',
        x: 0
      }
    ],
    listData: []
  },

  // movable-view位置改变时触发
  onChange(e) {
    const index = e.currentTarget.dataset.index;
    const x = e.detail.x;

    this.data.list[index].x = x;
  },
  setstatus: function (e) {
    shopUpdatestatus({
      changeIds: [e.currentTarget.dataset.id],
      status:e.currentTarget.dataset.status
    }, resd => {
      if (resd.code == 10000) {
        wx.showToast({
          title: resd.msg,
          icon: 'success'
        })
        this.getproductSelectList()
      }
    })
  },
  getSearchvalue: function (e) {
    this.setData({
      searchText: e.detail.value
    })
  },
  selectType: function (e) {
    console.log(e)
    this.setData({
      keyId: e.currentTarget.dataset.id,
      typename:e.currentTarget.dataset.name || ''
    })
    this.getproductSelectList()
  },
  noclose: function () {

  },
  jumpAddshop:function(){
  wx.navigateTo({
    url:`/pages/addShopForm/addShopForm?id=${this.data.id}&name=${this.data.name}`,
  })
  },
  godetail:function(e){
    wx.navigateTo({
      url:`/pages/addShopForm/addShopForm?id=${this.data.id}&name=${this.data.name}&shopId=${e.currentTarget.dataset.id}`,
    })
  },
  closem: function () {
    this.setData({
      clsModelvisible: false
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
  getproductSelectList: function () {
    productSelectList({
      shopId: this.data.id,
      name: this.data.searchText,
      searchIds:this.data.keyId==0?undefined:[this.data.keyId]
    }, resd => {
      this.setData({
        listData: resd.data,
        clsModelvisible: false,
      })
    })
  },
  getproductClass: function () {
    productClass({
      searchField4: this.data.id
    }, resd => {
      this.setData({
        productClassList: resd.data
      })
    })
  },
  showtypemodal: function () {
    this.setData({
      clsModelvisible: true
    })
  },
  showComponent: function (e) {
    wx.showModal({
      title: '',
      content: '确定要删除该商品吗？',
      cancelColor: "#333333",
      confirmColor: "#FB6C00",
      complete: (res) => {
        if (res.confirm) {
          productDelete({
            deleteIds: [e.currentTarget.dataset.id]
          }, resd => {
            if (resd.code == 10000) {
              wx.showToast({
                title: resd.msg,
                icon: 'success'
              })
              this.getproductSelectList()
            }
          })
        }
      }
    })
  },
  // 触摸开始事件
  onTouchStart(e) {
    const index = e.currentTarget.dataset.index;

    // 记录触摸开始时的位置
    this.touchStartX = e.touches.clientX;
    this.currentIndex = index;
  },

  // 触摸结束事件
  onTouchEnd(e) {
    const index = e.currentTarget.dataset.index;
    const item = this.data.list[index];
    const moveThreshold = 50; // 滑动阈值

    if (!this.touchStartX) return;

    const touchEndX = e.changedTouches.clientX;
    const deltaX = this.touchStartX - touchEndX;

    // 根据滑动距离判断是否展开删除按钮
    if (deltaX > moveThreshold) {
      // 向左滑动超过阈值，完全展开删除按钮
      item.x = -150;
    } else if (deltaX < -moveThreshold) {
      // 向右滑动超过阈值，收起删除按钮
      item.x = 0;
    } else {
      // 滑动距离不足，恢复原来状态
      item.x = item.x < -75 ? -150 : 0;
    }

    this.setData({
      list: this.data.list
    });

    // 清除临时变量
    this.touchStartX = null;
    this.currentIndex = null;
  },

  // 删除事件
  onDelete(e) {
    const index = e.currentTarget.dataset.index;

    // 显示确认弹窗
    wx.showModal({
      title: '提示',
      content: '确定要删除此项吗？',
      success: (res) => {
        if (res.confirm) {
          // 执行删除操作
          this.data.list.splice(index, 1);
          this.setData({
            list: this.data.list
          });

          wx.showToast({
            title: '删除成功',
            icon: 'success'
          });
        } else {
          // 取消删除，恢复位置
          this.data.list[index].x = 0;
          this.setData({
            list: this.data.list
          });
        }
      }
    });
  },
  searchsubmit: function () {
    this.getproductSelectList()
  },
  onLoad(options) {
    this.setData({
      id: options.shopId,
      name:options.name,
    })
    this.getproductClass()
  },
  onShow(){
    this.getproductSelectList()
    this.getproductClass()
  }
});