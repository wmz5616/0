/* components/circle/circle.js */
Component({
  options: {
    multipleSlots: true // 在组件定义时的选项中启用多slot支持
  },
  properties: {
    targetSteps:{ //今日步数
      type: Number,
      value: 0,
    },
    stepNum: { //今日步数
      type: Number,
      value: 0,
    },
    draw: { //画板元素名称id
      type: String,
      value: 'draw'
    },
    per: { //百分比 通过此值转换成step
      type: String,
      value: '0'
    },
    r: { //半径
      type: String,
      value: '50'
    },
    surplusTime: { //百分比 通过此值转换成step
      type: String,
      value: '0'
    },
    surplusTimes: { //百分比 通过此值转换成step
      type: String,
      value: '0'
    },
    startTime: { //百分比 通过此值转换成step
      type: String,
      value: '0'
    },
  },

  data: {
    /*  私有数据，可用于模版渲染 */
    step: 1, //用来算圆的弧度0-2
    size: 0, //画板大小
    screenWidth: 750, //实际设备的宽度
    txt: 0,
    src: '',
  },
  methods: {
    clearCanvas: function(el, r, w) {
      var context = wx.createCanvasContext(el, this);
      // 清空整个画布区域[5,8](@ref)
      context.clearRect(0, 0, r * 2, r * 2);
      context.draw();
    },
    /**
     * el:画圆的元素
     * r:圆的半径
     * w:圆的宽度
     * step:圆的弧度 (0-2)
     * 功能:彩色圆环
     */
    drawCircle: function (el, r, w, step) {
      var context = wx.createCanvasContext(el, this);

      // 先绘制灰色背景圆环（完整的360度）
      context.setLineWidth(w);
      context.setStrokeStyle('#eff0f1'); // 灰色背景
      context.setLineCap('round');
      context.beginPath();
      context.arc(r, r, r - w, -Math.PI / 2, 3 * Math.PI / 2, false); // 完整的圆
      context.stroke();

      // 再绘制彩色进度圆环
      // 设置渐变
      var gradient = context.createLinearGradient(2 * r, r, 0);
      gradient.addColorStop("0", "#5DD5DE");
      gradient.addColorStop("0.4","#4EACD3");
      gradient.addColorStop("0.65","#5DD5DE");
      gradient.addColorStop("0.8","#a4f7fe");
      gradient.addColorStop("1","#a4f7fe");
      context.setLineWidth(w);
      context.setStrokeStyle(gradient);
      context.setLineCap('round');
      context.beginPath();
      // step 从0到2为一周，只绘制进度部分
      context.arc(r, r, r - w, -Math.PI / 2, step * Math.PI - Math.PI / 2, false);
      context.stroke();

      context.draw(false, () => {
        setTimeout(() => {
          wx.canvasToTempFilePath({
            canvasId: el,
            success: (res) => {
              console.log('图片临时路径：', res.tempFilePath);
              this.setData({
                src: res.tempFilePath
              })
              // 可以在这里触发事件将路径传递给父组件
              this.triggerEvent('canvasReady', {
                tempFilePath: res.tempFilePath
              });
              this.clearCanvas()
            },
            fail: (err) => {
              console.log('转换失败：', err.errMsg);
            }
          }, this); // 在自定义组件中需要传入this[6](@ref)
        }, 500); // 延迟500ms，可根据设备性能调整[6](@ref)
      });
    }

  },

  lifetimes: {
    // 生命周期函数，可以为函数，或一个在methods段中定义的方法名
    // attached: function () {
    //   const _this = this;
    //   //获取屏幕宽度
    //   wx.getSystemInfo({
    //     success: function (res) {
    //       _this.setData({
    //         screenWidth: res.windowWidth
    //       });
    //     },
    //   });

    //   //初始化
    //   const el = _this.data.draw; //画板元素
    //   const per = _this.data.per; //圆形进度
    //   const r = Number(_this.data.r); //圆形半径

    //   _this.setData({
    //     step: (2 * Number(_this.data.per)) / 100,
    //     txt: _this.data.per
    //   });


    //   //获取屏幕宽度(并把真正的半径px转成rpx)
    //   let rpx = (_this.data.screenWidth / 750) * r;
    //   //计算出画板大小
    //   this.setData({
    //     size: rpx * 2
    //   });
    //   const w = 30; //圆形的宽度

    //   //组件入口,调用下面即可绘制 背景圆环和彩色圆环。
    //   _this.drawCircle(el, rpx, w, _this.data.step); //绘制 彩色圆环
    // },
  },
  observers: {
    'per, surplusTime': function (per, n2) {
      const _this = this
      wx.getSystemInfo({
        success: function (res) {
          _this.setData({
            screenWidth: res.windowWidth,
          });
        },
      });
      
      const el = _this.data.draw; //画板元素
      const r = Number(_this.data.r); //圆形半径

      _this.setData({
        // step: (2 * Number((new Date().getTime() - this.data.startTime) / per * 100)) / 100,
        step: (2 * Number(per)) / 100,
        txt: _this.data.per
      });
      //获取屏幕宽度(并把真正的半径px转成rpx)
      let rpx = (_this.data.screenWidth / 620) * r;
      //计算出画板大小
      this.setData({
        size: rpx * 2
      });
      const w = 30; //圆形的宽度

      //组件入口,调用下面即可绘制 背景圆环和彩色圆环。
      _this.drawCircle(el, rpx, w, _this.data.step); //绘制 彩色圆环
    }
  },

})

// <!-- function calculateRemainingTime(targetTime) {
//   function updateRemainingTime() {
//     var currentTime = new Date().getTime();
//     var targetTime = new Date(targetTime).getTime();
//     var remainingTime = targetTime - currentTime;

//     var hours = Math.floor(remainingTime / (1000 * 3600));
//     var minutes = Math.floor((remainingTime % (1000 * 3600)) / (1000 * 60));
//     var seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);

//     // 更新剩余时间显示
//     document.getElementById('remainingTime').textContent = String(hours).padStart(2, '0') + ':' + String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
//   }

//   // 每秒更新剩余时间
//   setInterval(updateRemainingTime, 1000); -->
// }