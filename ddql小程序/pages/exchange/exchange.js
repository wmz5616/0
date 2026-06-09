// pages/exchange/exchange.js
import {
  categoryLists,
  productLists,
  consumptionList,
  getSuper,
  getIndustry
} from '../../utils/request'
import {
  formatNumber
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    productList: [],
    categoryList: [],
    active: '',
    pageNum: 1,
    pageSize: 10,
    total: 0,
    name: '',
    userInfo: {},
    typeActive: 0,
    typeActiveList: [{
      id: 0,
      title: '消费门店'
    },
    {
      id: 1,
      title: '线上商城'
    }
    ],
    swiperCurrent: 0,
    bannerList: [
    ],
    storeList: [{
      pic: '/assets/images/backgroundImage.png',
      title: '海底捞.下发火锅菜（大岭山店）撒大苏打大阿松大萨达萨达萨达',
      info: '每满100元可抵扣100金币',
      distance: '787m',
    },
    {
      pic: '/assets/images/backgroundImage.png',
      title: '海底捞.下发火锅菜（大岭山店）',
      info: '每满100元可抵扣100金币',
      distance: '787m',
    },
    {
      pic: '/assets/images/backgroundImage.png',
      title: '海底捞.下发火锅菜（大岭山店）',
      info: '每满100元可抵扣100金币',
      distance: '787m',
    },
    ],
  },
  scan() {
    wx.scanCode({
      success: (res) => {

      }
    })
  },
  swiperChange(e) {
    this.setData({
      swiperCurrent: e.detail.current
    })
  },
  selectTypeActive(e) {
    const {
      index
    } = e.currentTarget.dataset
    if (index == this.data.typeActive) return
    this.setData({
      typeActive: index,
      pageNum: 1,
      total: 0,
      active: index == 0 ? 'all' : '',
    })
    this.getCategoryLists()
  },
  onSearch: function (e) {
    this.setData({
      name: e.detail
    })
    this.comfrimsearch()
  },
  comfrimsearch: function () {
    if (this.data.typeActive) {
      this.getProductLists()
    } else {

    }
  },
  search: function (e) {
    this.setData({
      name: e.detail
    })
  },
  onCancel: function () {
    console.log(11)
    this.setData({
      name: '',
    })
    this.getProductLists()
  },
  getLocation: function () {
    wx.getLocation({
      type: "gcj02",
      success: (res) => {
        this.setData({
          latitude: res.latitude,
          longitude: res.longitude
        })
        this.getProductLists()
      },
      fail: res => {
        wx.showToast({
          title: '获取位置信息失败',
          icon: 'none'
        })
      }
    })
  },
  getCategoryLists: function () {
    const fetchFunc = this.data.typeActive == 1 ? categoryLists : getSuper;
    fetchFunc({}, resd => {
      if (resd && resd.code == 10000) {
        let data = resd.data || []
        if (this.data.typeActive == 0) {
           data = [{id: 'all', name: '全部'}, ...data.filter(item => item.name !== '全部' && item.id != null)]
        }
        if (data.length != 0) {
          this.setData({
            categoryList: data,
            active: (this.data.active !== '' && this.data.active !== undefined) ? this.data.active : data[0].id,
          })
        }
      }
      if (!this.data.latitude) {
        this.getLocation()
      } else {
        this.getProductLists()
      }
    }, err => {
      // API failed, but we still need to load the store list
      if (!this.data.latitude) {
        this.getLocation()
      } else {
        this.getProductLists()
      }
    })
  },
  getProductLists: function (e) {
    const {
      active,
      name,
      pageNum,
      pageSize,
      typeActive
    } = this.data

    if (typeActive == 1) {
      // 线上商城：请求商品列表
      productLists({
        categoryId: +active,
        name: name,
        pageNum,
        pageSize,
      }, resd => {
        if (resd && resd.code == 10000) {
          const data = resd.data || {}
          const rawList = data.list || (Array.isArray(data) ? data : [])
          const list = rawList.map(i => ({
            ...i,
            exchangeNum: formatNumber(i.exchangeNum)
          }))
          this.setData({
            productList: e ? [...this.data.productList, ...list] : list,
            total: data.total || 0,
          })
        }
      })
      return;
    }

    // 消费门店：请求门店列表（API一次返回topShops和shopList）
    const params = {
      searchId: active === 'all' ? 0 : +active,
      searchStrField1: String(this.data.longitude || ''),
      searchStrField2: String(this.data.latitude || ''),
      pageNum,
      pageSize,
    }
    if (name) {
      params.name = name
    }

    consumptionList(params, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data || {}

        const processShop = i => {
          let info = i.info || '';
          if (!info && i.coinRule) {
             const rule = i.coinRule;
             const fullAmount = rule.fullAmount || rule.conditionAmount || 0;
             const deductCoin = rule.deductionCoin || rule.deductCoin || 0;
             if (fullAmount || deductCoin) {
               info = `每满${fullAmount}元可抵扣${deductCoin}金币`;
             }
          }
          let distanceStr = i.distance;
          if (distanceStr !== undefined && distanceStr !== null && distanceStr !== '') {
            let d = parseFloat(distanceStr);
            if (!isNaN(d)) {
               if (d < 1) {
                  distanceStr = (d * 1000).toFixed(0) + 'm';
               } else {
                  distanceStr = d.toFixed(1) + 'Km';
               }
            }
          }
          return {
            ...i,
            info: info,
            distance: distanceStr,
            exchangeNum: i.exchangeNum ? formatNumber(i.exchangeNum) : 0
          }
        }

        // 处理置顶门店（topShops）
        let flatBanners = (data.topShops || []).map(processShop);
        let chunkedBanners = []
        for (let i = 0; i < flatBanners.length; i += 2) {
          chunkedBanners.push(flatBanners.slice(i, i + 2))
        }
        this.setData({
          bannerList: chunkedBanners,
        })

        // 处理普通门店列表（shopList）
        const shopData = data.shopList || {}
        const rawList = shopData.list || []
        const list = rawList.map(processShop)
        this.setData({
          storeList: e ? [...this.data.storeList, ...list] : list,
          total: shopData.total || 0,
        })
      }
    })
  },
  changePageNum() {
    const totalPages = Math.ceil(this.data.total / this.data.pageSize)
    const listLength = this.data.typeActive == 1 ? this.data.productList.length : this.data.storeList.length
    const currentTotalPages = Math.ceil(listLength / this.data.pageSize)
    if (currentTotalPages < totalPages) {
      this.setData({
        pageNum: this.data.pageNum + 1,
      })
      this.getProductLists('e')
    }
  },
  onChange: function (e) {
    const id = e.detail.name
    this.setData({
      active: id,
      pageNum: 1,
      total: 0,
    })
    this.getProductLists()
  },
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
    })
  },
  goBack: function () {
    const pages = getCurrentPages()
    // 如果是返回其他页面用navigateBack
    if (pages.length > 2) {
      wx.navigateBack({
        delta: 1,
      });
      return
    }
    // 否则返回首页
    wx.switchTab({
      url: '/pages/index/index',
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      userInfo: wx.getStorageSync('userinfo')
    })
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
    const app = getApp()
    const isLoginBack = app.globalData.isLoginBack
    console.log(isLoginBack)
    if (isLoginBack) {
      app.globalData.isLoginBack = undefined
      return
    }
    // 获取商品类别列表
    this.getCategoryLists()
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