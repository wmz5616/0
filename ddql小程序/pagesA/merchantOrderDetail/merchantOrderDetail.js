Page({
  data: {
    orderInfo: {},
    statusList: [{
      value: 0,
      color: '#11af21',
      text: '收款',
    },
    {
      value: 1,
      color: '#d9006b',
      text: '退款',
    },
    ]
  },
  onLoad(options) {
    if (options.data) {
      try {
        const dataStr = decodeURIComponent(options.data);
        const orderInfo = JSON.parse(dataStr);
        this.setData({
          orderInfo
        });
      } catch (e) {
        try {
          // Fallback if not encoded
          const orderInfo = JSON.parse(options.data);
          this.setData({
            orderInfo
          });
        } catch (err) {
          console.error('解析订单数据失败', err);
        }
      }
    }
  },
  goBack() {
    wx.navigateBack({
      delta: 1
    });
  },
  copy(e) {
    const { text } = e.currentTarget.dataset;
    if (!text) return;
    wx.setClipboardData({
      data: text,
      success() {
        wx.showToast({
          title: '复制成功',
          icon: 'success'
        });
      }
    });
  }
});
