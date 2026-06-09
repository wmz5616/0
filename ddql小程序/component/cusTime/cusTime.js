const moment = require('../../miniprogram_npm/moment/index.js')

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    // 开始时间戳（毫秒）
    startTime: {
      type: Number,
      value: null
    },
    // 结束时间戳（毫秒）
    endTime: {
      type: Number,
      value: null
    },
    // 最小日期
    minDate: {
      type: Number,
      value: null
    },
    // 最大日期
    maxDate: {
      type: Number,
      value: null
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    showTime: false,
    activeTime: 1, // 1表示选择开始时间，2表示选择结束时间
    // 展示的时间（只有确认后才更新）
    displayStartTime: 0,
    displayEndTime: 0,
    // 弹窗内的临时时间
    tempStartTime: 0,
    tempEndTime: 0,
    time: 0,// 日期格式化器
    formatter(type, value) {
      if (type === 'year') {
        return `${value}年`
      }
      if (type === 'month') {
        return `${value}月`
      }
      if (type === 'day') {
        return `${value}日`
      }
      return value
    }
  },

  /**
   * 组件生命周期
   */
  lifetimes: {
    attached() {
      this.initTime()
    }
  },

  /**
   * 属性监听器
   */
  observers: {
    'startTime, endTime': function(startTime, endTime) {
      if (startTime && endTime) {
        this.setData({
          displayStartTime: startTime,
          displayEndTime: endTime,
          tempStartTime: startTime,
          tempEndTime: endTime,
          time: startTime
        })
      }
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    // 初始化时间
    initTime() {
      const now = new Date().getTime()
      const startOfYear = new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime()

      // 计算默认值
      const defaultMinDate = this.data.minDate || startOfYear
      const defaultMaxDate = this.data.maxDate || new Date(new Date().getFullYear() + 1, 11, 31).getTime()
      const defaultStartTime = this.data.startTime || startOfYear
      const defaultEndTime = this.data.endTime || now

      this.setData({
        minDate: defaultMinDate,
        maxDate: defaultMaxDate,
        displayStartTime: defaultStartTime,
        displayEndTime: defaultEndTime,
        tempStartTime: defaultStartTime,
        tempEndTime: defaultEndTime,
        time: defaultStartTime,
        activeTime: 1
      })
    },

    // 显示时间选择弹窗
    showTimePopup() {
      const { displayStartTime, displayEndTime } = this.data
      this.setData({
        showTime: true,
        activeTime: 1,
        // 打开弹窗时，将展示的时间复制到临时时间
        tempStartTime: displayStartTime,
        tempEndTime: displayEndTime,
        time: displayStartTime
      })
    },

    // 关闭弹窗
    close() {
      this.setData({
        showTime: false
      })
    },

    // 切换选择开始/结束时间
    changeTime(e) {
      const index = Number(e.currentTarget.dataset.index)
      this.setData({
        activeTime: index,
        time: index === 1 ? this.data.tempStartTime : this.data.tempEndTime
      })
    },

    // 日期选择器输入事件（只更新临时时间）
    onInput(e) {
      const value = e.detail
      const { activeTime } = this.data

      this.setData({
        [activeTime === 1 ? 'tempStartTime' : 'tempEndTime']: value,
        time: value
      })
    },

    // 确认选择
    confirm() {
      const { tempStartTime, tempEndTime } = this.data

      // 验证开始时间不能大于结束时间
      if (tempStartTime > tempEndTime) {
        wx.showToast({
          title: '开始时间不能大于结束时间',
          icon: 'none'
        })
        return
      }

      // 确认后才更新展示的时间
      this.setData({
        displayStartTime: tempStartTime,
        displayEndTime: tempEndTime,
        showTime: false
      })

      // 触发确认事件，传递选择的时间
      this.triggerEvent('onChange', {
        startTime: tempStartTime,
        endTime: tempEndTime
      })
    },

    // 重置时间（只重置临时时间）
    reset() {
      const startOfYear = new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime()
      const now = new Date().getTime()
      const { activeTime } = this.data

      this.setData({
        tempStartTime: startOfYear,
        tempEndTime: now,
        time: activeTime === 1 ? startOfYear : now
      })
    },
  }
})
