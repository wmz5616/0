// components/customNavBar/index.js
import {
  locationLists
} from '../../utils/request'
Component({

  /**
   * 组件的属性列表
   */
  properties: {
    title: {
      type: String,
      value: '',
    },
    showSolt: {
      type: String,
      value: '',
    },
    height: {
      type: String,
      value: '50%',
    },
    border: {
      type: Boolean,
      value: false,
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    showPop: false,
    locationId: 0,
    locationName: '',
    list: [],
  },

  pageLifetimes: {
    show() {
      // 请求辖区接口
      this.getLocationLists()
    },
  },
  /**
   * 组件的方法列表
   */
  methods: {
    getLocationLists() {
      locationLists({}, resd => {
        if (resd && resd.code == 10000) {
          const data = resd.data
          // 有全部版本: 
          // const list = data.isAll === 'yes' ? [{
          //       locationName: '全部',
          //       locationId: 0
          //     },
          //     ...data.locationInfo
          //   ] :
          //   data.locationInfo

          // 没有全部版本
          const list = data.locationInfo

          const app = getApp()
          const sceneData = app.globalData.sceneData
          const areaData = app.globalData.areaData

          // 先拿扫码进来的辖区id 或者点击短链进来的辖区id
          let changeData = sceneData ? {
            locationId: sceneData,
            locationName: list.find(i => i.locationId == sceneData)?.locationName || '',
          } : undefined

          console.log(changeData)

          app.globalData.sceneData = undefined

          if (!changeData) {
            // 如果没有扫码辖区id，先拿缓存的辖区id，再默认赋值第一个id
            changeData = areaData ? areaData : {
              locationId: list[0]?.locationId,
              locationName: list[0]?.locationName,
            }
          }

          app.globalData.areaData = changeData

          //请求接口
          this.setData({
            list,
            ...changeData
          })
          this.triggerEvent('init', {
            value: changeData,
          })
        }
      })
    },
    showPop() {
      this.setData({
        showPop: true,
      })
    },
    onClose() {
      this.setData({
        showPop: false,
      })
    },
    selectItem(e) {
      const {
        data: {
          locationId,
          locationName
        }
      } = e.currentTarget.dataset

      this.setData({
        locationId,
        locationName,
        showPop: false,
      }, () => {
        const app = getApp()
        app.globalData.areaData = {
          locationId,
          locationName,
        }
      })
      this.changeSelect({
        locationId,
        locationName
      })
    },
    changeSelect(data) {
      this.triggerEvent('itemChange', {
        value: data,
      })
    },
  }
})