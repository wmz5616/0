// pages/address/address.js
import {
  nextAreaInfo,
  selectAddress,
  addAddress,
  deleteAddress,
  updateAddress
} from '../../utils/request'
import {
  validateField
} from '../../utils/util'
var QQMapWX = require('../../utils/qqmap-wx-jssdk.min');
var qqmapsdk = new QQMapWX({
  key: '7EZBZ-DNNWZ-L7HXL-TPRJ5-S5PJF-LBFEH'
});
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showDialog: false,
    showOverlay: false,
    overlayType: 'add',
    columns: [],
    showAreaPicker: false,
    changeFlag: false,
    addressList: [],
    name: '',
    phone: '',
    regionId: undefined,
    address: '',
  },
  getDeleteAddress: function () {
    deleteAddress({
      id: this.data.deleteid
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '删除成功!',
          icon: 'success',
        })
        this.setData({
          selectAddressId: this.data.deleteid == this.data.selectAddressId ? undefined : this.data.selectAddressId
        })
        this.getSelectAddress()
      }
    })
  },
  getAddAddress: function (flag) {
    const {
      overlayType
    } = this.data
    const params = {
      isDefault: 0,
      name: this.data.name,
      phone: this.data.phone,
      regionId: this.data.regionId || undefined,
      address: this.data.address || undefined,
      location: this.data.location
    }
    const title = validateField(params)
    if (title) {
      wx.showToast({
        title,
        icon: 'none'
      })
      return
    }
    if (overlayType == 'add') {
      addAddress(params, resd => {
        if (resd && resd.code == 10000) {
          wx.showToast({
            title: '新增成功!',
            icon: 'success'
          })
          this.setData({
            showOverlay: false,
            regionId: undefined,
            address: '',
            phone: '',
            name: '',
            location: '',
            region: '',
          })
          this.getSelectAddress()
        }
      })
    } else {
      updateAddress({
        ...params,
        id: this.data.editId,
        isDefault: flag ?? this.data.isDefault,
        userId: this.data.userInfo.id,
      }, resd => {
        if (resd && resd.code == 10000) {
          wx.showToast({
            title: '编辑成功!',
            icon: 'success'
          })
          this.setData({
            showOverlay: false,
            regionId: undefined,
            address: '',
            phone: '',
            name: '',
            location: '',
            region: '',
            editId: '',
          })
          this.getSelectAddress()
        }
      })
    }
  },
  getSelectAddress: function () {
    selectAddress({}, resd => {
      if (resd && resd.code == 10000) {
        const data = resd.data
        resd.data.map(x => {
          x.regionName = x.regionList.map(x => x.name).join("")
        })
        this.setData({
          addressList: data,
        })
      }
    })
  },
  initPickerColumns: function (regionName) {
    nextAreaInfo({ id: 100000 }, resd => {
      if (resd && resd.code == 10000) {
        const provinces = resd.data.map(i => ({ ...i, text: i.name, value: i.id }));
        let pIndex = 0;
        if (regionName) {
           pIndex = provinces.findIndex(p => regionName.includes(p.name));
           if (pIndex < 0) pIndex = 0;
        } else {
           pIndex = provinces.findIndex(p => p.id == 440000);
           if (pIndex < 0) pIndex = 0;
        }
        nextAreaInfo({ id: provinces[pIndex].id }, res2 => {
          if (res2 && res2.code == 10000) {
            const cities = res2.data.map(i => ({ ...i, text: i.name, value: i.id }));
            let cIndex = 0;
            if (regionName) {
               cIndex = cities.findIndex(c => regionName.includes(c.name));
               if (cIndex < 0) cIndex = 0;
            } else if (provinces[pIndex].id == 440000) {
               cIndex = cities.findIndex(c => c.id == 441900);
               if (cIndex < 0) cIndex = 0;
            }
            nextAreaInfo({ id: cities[cIndex].id }, res3 => {
              if (res3 && res3.code == 10000) {
                const districts = res3.data.map(i => ({ ...i, text: i.name, value: i.id }));
                let dIndex = 0;
                if (regionName) {
                   dIndex = districts.findIndex(d => regionName.includes(d.name));
                   if (dIndex < 0) dIndex = 0;
                }
                this.setData({
                  columns: [
                    { values: provinces, defaultIndex: pIndex },
                    { values: cities, defaultIndex: cIndex },
                    { values: districts, defaultIndex: dIndex }
                  ]
                });
              }
            });
          }
        });
      }
    });
  },
  onClickIcon: function (e) {
    const { region } = this.data
    const { type } = e.currentTarget.dataset
    let data = {}
    if (type == 'area') {
      this.initPickerColumns(region)
      data.showAreaPicker = true
    } else {
      data.changeFlag = true
    }
    this.setData({
      ...data
    })
  },
  onAreaChange: function (e) {
    const { picker, value, index } = e.detail;
    if (index === 0) {
      nextAreaInfo({ id: value[0].id }, res => {
        if (res && res.code == 10000) {
          const cities = res.data.map(i => ({ ...i, text: i.name, value: i.id }));
          picker.setColumnValues(1, cities);
          nextAreaInfo({ id: cities[0].id }, res2 => {
            if (res2 && res2.code == 10000) {
              const districts = res2.data.map(i => ({ ...i, text: i.name, value: i.id }));
              picker.setColumnValues(2, districts);
            }
          });
        }
      });
    } else if (index === 1) {
      nextAreaInfo({ id: value[1].id }, res => {
        if (res && res.code == 10000) {
          const districts = res.data.map(i => ({ ...i, text: i.name, value: i.id }));
          picker.setColumnValues(2, districts);
        }
      });
    }
  },
  onConfirm: function (e) {
    const pickerData = e.detail
    console.log(pickerData)
    this.setData({
      region: pickerData.value.map(i => i.name).join(''),
      showAreaPicker: false,
      regionId: pickerData.value.at(-1).id,
      address: '',
    })
    this.getLocation('', 'show')
  },
  openOverlay: function (e) {
    const {
      type,
      item
    } = e.currentTarget.dataset
    const data = {}
    if (type == 'edit') {
      console.log(item)
      data.name = item.name
      data.address = item.address
      data.phone = item.phone
      data.region = item.regionList.map(xx => xx.name).join('')
      data.regionId = item.regionId
      data.editId = item.id,
        data.isDefault = item.isDefault
      data.location = item.location
    } else {
      data.name = ''
      data.address = ''
      data.phone = '',
        data.region = ''
      data.regionId = undefined
      data.location = ''
      data.placeList = []
    }
    this.setData({
      overlayType: type,
      showOverlay: true,
      ...data,
    }, () => {
      if (type == 'edit') {
        this.getLocation('', 'show', 'edit')
      }
    })
  },
  deleteAddress: function (e) {
    const {
      deleteid
    } = e.currentTarget.dataset
    this.setData({
      deleteid,
      showDialog: true,
      // selectAddressId: undefined,
    })
  },
  onClose: function (e) {
    const {
      name,
      reset
    } = e.currentTarget.dataset
    let data = {}
    if (reset) {
      data.region = ''
    }
    this.setData({
      [name]: false,
      ...data
    })
  },
  overlayConfirm: function (e) {
    this.getAddAddress()
  },
  changeRadio: function (e) {
    const {
      item
    } = e.currentTarget.dataset
    console.log(item)
    this.setData({
      overlayType: 'edit',
      name: item.name,
      address: item.address,
      phone: item.phone,
      region: item.regionList.map(x => x.name).join(''),
      regionId: item.regionId,
      editId: item.id,
    }, () => {
      this.getAddAddress(item.isDefault ? 0 : 1)
    })
  },
  goBack() {
    const app = getApp()
    app.globalData.selectAddressId = this.data.selectAddressId
    wx.navigateBack({
      delta: 1,
    });
  },
  changeAddress: function (e) {
    const {
      item
    } = e.currentTarget.dataset
    console.log(item)
    this.setData({
      location: `${item.location.lng},${item.location.lat}`,
      address: item.title,
      flag: false,
      changeFlag: false,
    })
  },
  getAddressLocation: function (e) {
    const value = e.detail.value
    if (!value) return
    const region = this.data.region ? this.data.region : ''
    const address = region + value
    qqmapsdk.geocoder({
      address,
      success: (res) => {
        const result = res.result
        this.setData({
          location: `${result.location.lng},${result.location.lat}`,
          // address,
        })
      }
    })
  },
  getLocation: function (e, show, flag) {
    const address = e?.detail ? e?.detail : flag ? this.data.address : ''
    const region = this.data.region ? this.data.region : ''
    qqmapsdk.getSuggestion({
      keyword: region + address,
      policy: 1,
      success: (res1) => {
        this.setData({
          address,
          placeList: res1.data,
          flag: show ? false : true,
        })
      },
      fail: function (res) {
        console.log(res);
      }
    })
  },
  choseAddress: function (e) {
    const {
      selectid
    } = e.currentTarget.dataset
    this.setData({
      selectAddressId: selectid,
    })
    this.goBack()
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      userInfo: wx.getStorageSync('userinfo'),
      selectAddressId: +options.selectAddressId,
      Dflag: options.Dflag,
    })
    this.getSelectAddress()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {},

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