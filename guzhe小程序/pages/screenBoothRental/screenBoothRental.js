// pages/screenBoothRental/screenBoothRental.js
import {
  nextAreaInfo,
  commonSupermarketList,
  availableLists,
  screenRent,
  contractStatus
} from '../../utils/request'
import {
  debounce
} from '../../utils/util'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showPop: false,
    circleList: [{
      id: '',
      name: "全部",
    }],
    activeId: '',
    activeName: '全部',
    showArea: false,
    targetAddress: [],
    screenAddress: '',
    columns: [],
    showSwitch: 0,
    showAgree: false,
    equipmentList: [],
    isCharge: false,
    isShowArgee: false,
    chargePrice: 0,
  },
  selectAgree() {
    this.setData({
      showAgree: !this.data.showAgree,
    })
  },
  selectSwitch(e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      showSwitch: type,
    })
  },
  selectTab(e) {
    const {
      id
    } = e.currentTarget.dataset
    this.setData({
      activeId: id,
      activeName: this.data.circleList.find(i => i.id == id).name || '',
      showPop: false,
    })
    this.getAvailableLists()
  },
  showPop() {
    this.setData({
      showPop: true,
    })
  },
  goBack: function () {
    const pages = getCurrentPages(); // 获取当前页面栈
    if (pages.length > 1) {
      // 存在上一页，正常返回
      wx.navigateBack({
        delta: 1
      });
    } else {
      // 不存在上一页，重定向到首页（或其他指定页面）
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },
  jump(e) {
    const {
      url
    } = e.currentTarget.dataset

    wx.navigateTo({
      url,
    })
  },

  onClose(e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: false,
    })
  },
  clear() {
    this.setData({
      targetAddress: {},
      screenAddress: '',
      showArea: false,
      columns: [],
    })
    this.getCommonSupermarketList()
  },
  onFinish(e) {
    const {
      selectedOptions,
      value
    } = e.detail;
    if (selectedOptions.length < 3) return
    this.setData({
      targetAddress: selectedOptions.reduce((pre, cur) => {
        pre[cur.level == 1 ? 'province' : cur.level == 2 ? 'city' : 'district'] = cur.name
        return pre
      }, {}),
      screenAddress: selectedOptions.map(i => i.name).join(''),
      showArea: false,
    })
    this.getCommonSupermarketList()
  },
  onChange(e) {
    console.log(e.detail)
    const {
      value,
      selectedOptions
    } = e.detail
    this.getNextAreaInfo(value, selectedOptions)
  },
  showArea() {
    this.setData({
      showArea: true,
    })
    this.getNextAreaInfo(100000)
  },
  getNextAreaInfo(id, selectedOptions = []) {
    nextAreaInfo({
      searchId: id,
    }, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data.map(i => ({
          ...i,
          text: i.name,
          value: i.id,
        }));

        if (!data.length) return

        // 如果是第一级（省份），直接设置 columns
        if (selectedOptions.length === 0) {
          this.setData({
            columns: this.data.columns.length ? this.data.columns : data
          });
          return;
        }

        const columns = this.data.columns;
        const lastOption = selectedOptions[selectedOptions.length - 1];

        // 更新对应节点的 children
        this.updateChildren(columns, lastOption.value, data);

        this.setData({
          columns
        });
      }
    });
  },

  updateChildren(nodes, targetId, children) {
    nodes.some(node =>
      node.value === targetId ?
      (node.children = children, true) :
      node.children?.length && this.updateChildren(node.children, targetId, children)
    )
  },
  getCommonSupermarketList() {
    const {
      targetAddress
    } = this.data
    commonSupermarketList({
      province: targetAddress?.province || undefined,
      city: targetAddress?.city || undefined,
      district: targetAddress?.district || undefined,
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          circleList: [{
              id: '',
              name: "全部",
            },
            ...resd.data,
          ],
          activeId: '',
          activeName: '全部'
        })
        this.getAvailableLists()
      }
    })
  },
  getAvailableLists() {
    const {
      activeId,
      screenAddress
    } = this.data
    wx.showLoading()
    availableLists({
      businessCircleId: activeId || '',
      screenAddress: !activeId ? screenAddress : undefined,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.hideLoading()
        this.setData({
          equipmentList: resd.data.map(i => ({
            ...i,
            selectMonths: [],
          })),
          chargePrice: 0,
        })
      }
    })
  },
  selecPlace(e) {
    const {
      id,
      month,
      status
    } = e.currentTarget.dataset

    if (status) {
      wx.showToast({
        title: '该月份已租满！',
        icon: 'none'
      })
      return
    }
    const {
      equipmentList
    } = this.data

    const targetIndex = equipmentList.findIndex(i => i.equipmentId == id)
    const targetItem = equipmentList[targetIndex]

    let selectMonths = targetItem.selectMonths
    const flag = selectMonths.includes(month)
    selectMonths = flag ? selectMonths.filter(i => i != month) : [
      ...selectMonths,
      month
    ]
    equipmentList[targetIndex] = {
      ...targetItem,
      hasSelect: selectMonths.length ? true : false,
      selectMonths,
    }
    this.setData({
      equipmentList,
      chargePrice: this.data.chargePrice + (flag ? -targetItem.money : targetItem.money) / 100
    })
  },
  getScreenRent: debounce(function () {
    const {
      equipmentList,
      shopId,
      activeId,
      showSwitch,
      isCharge,
      isShowArgee,
      showAgree
    } = this.data;

    // 防止重复提交
    if (isCharge) return;
    this.setData({
      isCharge: true
    });

    // 过滤已选设备
    const selectedEquipment = equipmentList.filter(item => item.hasSelect);
    if (!selectedEquipment.length) {
      return this.showError('请选择要租用的店位');
    }

    // 校验必填项
    if (!showSwitch) {
      return this.showError('请选择要展示的内容');
    }

    if (isShowArgee && !showAgree) {
      return this.showError('请阅读并同意相关协议');
    }

    // 构造请求参数
    const selections = selectedEquipment.map(item => ({
      equipmentId: item.equipmentId,
      rentalMonths: item.selectMonths
    }));

    wx.showLoading({
      title: '加载中'
    });

    screenRent({
      businessCircleId: activeId || undefined,
      shopId,
      selections,
      displayType: showSwitch,
    }, (res) => {
      this.setData({
        isCharge: false
      });
      if (res?.code === 10000) {
        wx.showToast({
          title: '操作成功！',
          icon: 'success'
        });
        this.getAvailableLists();
        wx.navigateTo({
          url: `/pages/storeLocationOrder/storeLocationOrder?shopId=${this.data.shopId}`,
        })
      }
    }, (err) => {
      wx.hideLoading();
      this.setData({
        isCharge: false
      });
    });
  }),

  showError(msg) {
    this.setData({
      isCharge: false
    });
    wx.showToast({
      title: msg,
      icon: 'none'
    });
  },
  getContractStatus() {
    contractStatus({}, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          isShowArgee: +resd.data ? true : false,
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      shopId: +options.shopId,
    })
    this.getContractStatus()
    this.getCommonSupermarketList()
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