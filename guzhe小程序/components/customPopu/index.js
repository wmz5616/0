// components/customPopu/index.js
Component({

  /**
   * 组件的属性列表
   */
  properties: {
    title: {
      type: String,
      value: ''
    },
    columns: {
      type: Array,
      value: [],
    },
    text: {
      type: String,
      value: '',
      observer: function (newVal) {
        this.setData({
          text: newVal
        })
      },
    },
    flag: {
      type: String,
      value: '',
    },
  },

  /**
   * 组件的初始数据
   */
  data: {
    showPop: false,
    text: '',
  },
  /**
   * 组件的方法列表
   */
  methods: {
    selectItem(e) {
      const {
        index,
        value
      } = e.detail
      this.setData({
        showPop: false,
        text: this.properties.columns[index].text
      })
      this.changeSelect({
        ...value
      })
    },
    changeSelect(data) {
      console.log(data)
      this.triggerEvent('itemChange', {
        value: data.value,
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
  }
})