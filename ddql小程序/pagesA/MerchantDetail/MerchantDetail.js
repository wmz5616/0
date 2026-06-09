// pages/MerchantDetail/MerchantDetail.js
const WxParse = require('../../wxParse/wxParse')
import {
  allowedTypes
} from '../../utils/util'
import {
  uploadImages,
  UPLOAD_IMG_BASE_URI,
  shopDetail,
  generateQrcode,
  downloadQrcode,
  getIndustry,
  getSuper,
  addcoinshop,
  editcoinshop,
  getcoinshop,
  shopBusinessData,
  shoporderSelect,
  orderrefund,
  shopSettlementRecordLists,
  managerSearch,
  managerUpdate,
  shopContract,
  BASE_URI
} from '../../utils/request'
import moment from 'moment'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    transaStat: {},
    transaLitst: [],
    businessData: {},
    superData: [],
    Industry: [],
    active: 0,
    fileList: [], // logo
    bannerList: [], // 轮播图
    contractList: [], // 合同照片
    typeList: [],
    circleList: [],
    // 0表示类别，1表示商圈
    pickertype: 'typeList',
    showPop: false,
    typeColumns: [{
        value: 0,
        text: '食品',
      },
      {
        value: 1,
        text: '超时百货',
      },
    ],
    circleColumns: [{
        value: 0,
        text: '国贸',
      },
      {
        value: 1,
        text: '万达广场',
      },
    ],
    columns: [],
    showPop1: false,
    showStartTime: '',
    showEndTime: '',
    startTime: '00:00',
    endTime: '23:59',
    minHour: 0,
    maxHour: 23,
    forma: function (type, value) {
      // if (type === 'hour') {
      //   return `${value}时`;
      // }
      // if (type === 'minute') {
      //   return `${value}分`;
      // }
      return value;
    },
    // 收款码背景图： 展示背景图，下载背景图
    ImgList: ['https://api.dongqilai.cc/showImg.png', 'https://api.dongqilai.cc/DownLoadImg.png'],
    bgImgList: [],
    startTime1: new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
    endTime1: new Date().getTime(),
    minDate: new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime(),
    maxDate: new Date(new Date().getFullYear() + 1, 11, 31).getTime(),
    formatter(type, value) {
      if (type === 'year') {
        return `${value}年`;
      }
      if (type === 'month') {
        return `${value}月`;
      }
      if (type === 'day') {
        return `${value}日`;
      }
      return value;
    },
    showDOverlay: false,
    orderStat: {},
    isIOS: false,
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
    ],
    orderList: [{}, {}, {}],
    typeList1: [{
        value: 0,
        text: '收款'
      },
      {
        value: 1,
        text: '退款'
      },
      {
        value: 2,
        text: '待发货'
      },
    ],
    showDOverlay1: false,
    showRefund: false,
    refundPrice: 100,
    step: 100,
    refundData: {},
    refundReason: '',
    totalCount: 0,
    totalAmount: '0.00',
    list: [],
    pageNum: 1,
    pageSize: 10,
    pages: 1,
    showSearch: false,
    consumptionInfo: {},
    gQrcode: '',
    dQrcode: '',
  },
  timeConfirm(e) {
    const {
      startTime,
      endTime
    } = e.detail
    this.setData({
      startTime1: startTime,
      endTime1: endTime,
    })
    if (this.data.activeTitle == '数据统计') {
      this.getShopBusinessData()
    } else if (this.data.activeTitle == '交易记录') {
      this.shoporderSelect()
    } else if (this.data.activeTitle == '结算记录') {
      this.setData({ pageNum: 1 })
      this.getSettlementList()
    }
  },
  getShopBusinessData() {
    const {
      id,
      startTime1,
      endTime1,
    } = this.data
    wx.showLoading({
      title: '加载中'
    })
    shopBusinessData({
      searchField4: id,
      startTime: moment(startTime1).format("YYYY-MM-DD HH:mm:ss"),
      endTime: moment(endTime1).format("YYYY-MM-DD HH:mm:ss"),
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.hideLoading()
        this.setData({
          businessData: resd.data
        })
      }
    })
  },
  async getQrCode() {
    const {
      id
    } = this.data

    wx.showLoading({
      title: '加载中',
    })
    await Promise.allSettled([this.getGenerateQrcode(id), this.getDownloadQrcode(id)])

    wx.hideLoading()
  },
  getGenerateQrcode(searchId) {
    return new Promise((resolve, reject) => {
      generateQrcode({
        searchId,
      }, resd => {
        if (resd && resd.code == 10000) {
          const data = resd.data
          this.setData({
            gQrcode: data.qrCode
          })
          resolve()
        }
      }, err => {
        reject()
      })
    })
  },
  getDownloadQrcode(searchId) {
    return new Promise((resolve, reject) => {
      downloadQrcode({
        searchId,
      }, resd => {
        if (resd && resd.code == 10000) {
          const data = resd.data
          this.setData({
            dQrcode: data.qrCode
          })
          resolve()
        }
      }, err => {
        reject()
      })
    })
  },
  changeSelect() {
    this.setData({
      showSearch: true,
    })
  },
  changePrice: function (e) {
    let inputValue = e.detail.value.trim()
    // 验证是否为有效数字（允许小数和负数）
    if (!inputValue || isNaN(inputValue) || !this.isValidNumber(inputValue)) {
      wx.showToast({
        title: '请输入有效数字',
        icon: 'none'
      });
      // 清空无效输入
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return;
    }
    // 转换为数字类型
    let numericValue = Number(inputValue);

    // 验证金额不能小于等于0
    if (numericValue <= 0) {
      wx.showToast({
        title: '退款金额要大于0',
        icon: 'none'
      });
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return;
    }

    // 验证金额不能大于退款金额
    if (numericValue * 100 > this.data.refundData.amount) {
      wx.showToast({
        title: '超出支付金额',
        icon: 'none'
      });
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return;
    }

    this.setData({
      refundPrice: numericValue * 100
    })
  },
  // 辅助函数：验证数字格式
  isValidNumber: function (value) {
    // 允许整数、小数、正数
    return /^-?\d*\.?\d+$/.test(value);
  },
  calc: function (e) {
    const {
      add
    } = e.currentTarget.dataset
    const flag = add > 0 ? true : false

    if (!flag && this.data.refundPrice <= this.data.step) {
      wx.showToast({
        title: '退款金额要大于0',
        icon: 'none'
      })
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return
    }
    if (flag && this.data.refundPrice + this.data.step > this.data.refundData.amount) {
      wx.showToast({
        title: '超出支付金额',
        icon: 'none'
      })
      this.setData({
        refundPrice: this.data.refundPrice,
      });
      return
    }
    this.setData({
      flag,
      refundPrice: Number(this.data.refundPrice) + add
    })
  },
  changeReason: function (e) {
    let inputValue = e.detail.value.trim()
    this.setData({
      refundReason: inputValue
    })
  },
  async showRefund(e) {
    const {
      orderitem
    } = e.currentTarget.dataset

    // wx.showLoading({
    //   title: '加载中',
    // })
    // const amount = await this.getProductOrderInfo(orderitem.id)
    // if (amount == -1) {
    //   return
    // }

    // wx.hideLoading()

    this.setData({
      showRefund: true,
      refundData: orderitem,
      // refundPrice: amount,
      refundReason: '',
    })
  },
  changeInput: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: e.detail.value
    })
  },
  reset1: function (e) {
    this.setData({
      applyUser: '',
      orderNo: '',
      typeName: '',
      typeStatus: '',
      showDOverlay: false,
      pageNum: 1,
    })
  },
  onConfirm1() {
    const {
      name
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
    })
  },
  onDConfirm: function (e) {
    const pickerData = e.detail.value
    this.setData({
      showAreaPicker: false,
      typeName: pickerData.text,
      typeStatus: pickerData.value,
    })
  },
  onClickIcon() {
    this.setData({
      showAreaPicker: true,
    })
  },
  showSearch() {
    this.setData({
      showDOverlay: true,
    })
  },
  jump: function (e) {
    const {
      url
    } = e.currentTarget.dataset
    wx.navigateTo({
      url: url,
    })
  },
  onInput: function (e) {
    this.setData({
      [e.currentTarget.dataset.field]: e.detail
    })
  },
  getconinvalue: function (e) {
    this.setData({
      [e.currentTarget.dataset.type]: e.detail.value
    })
  },
  onInput1: function (e) {
    this.setData({
      keyword: e.detail,
    })
  },
  choseMember: function (e) {
    const {
      index,
      id,
      name,
    } = e.currentTarget.dataset
    const selectRadio = index === this.data.selectRadio ? '' : index
    this.setData({
      selectRadio,
      tearnuserName: name,
      showDialog: selectRadio !== '' ? true : false,
      selectId: id,
    })
  },
  dialogConfirm() {
    if (this.data.selectRadio === '') return;
    const selectedUser = this.data.list[this.data.selectRadio];
    managerUpdate({
      searchId: this.data.id,
      searchStrField1: selectedUser.phone,
    }, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '操作成功！',
          icon: 'success',
          duration: 1000
        })
        this.setData({
          showSearch: false,
          keyword: '',
          list: [],
          pageNum: 1,
        })
      }
    })
  },
  onSearch() {
    const { keyword, id } = this.data
    if (keyword.length != 11) {
      wx.showToast({
        title: '请输入完整的手机号搜索',
        icon: 'none'
      })
      return
    }
    managerSearch({
      searchId: id,
      keyword: keyword
    }, resd => {
      if (resd && resd.code == 10000) {
        this.setData({
          list: resd.data || [],
          pageNum: 1,
        })
        if (!resd.data || resd.data.length === 0) {
           wx.showToast({ title: '未找到相关店员', icon: 'none' });
        }
      }
    })
  },
  onCancel: function () {
    this.setData({
      keyword: '',
      list: [],
      pageNum: 1,
    })
  },
  close: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    this.setData({
      [type]: false,
    })
  },
  shoporderSelect: function () { //查询商家交易记录
    shoporderSelect({
      id: this.data.id
    }, resd => {
      this.setData({
        transaLitst: resd.data.list || [],
        transaStat: resd.data?.reserveData
      })
    })
  },
  getSettlementList: function () { //查询商家结算记录
    wx.showLoading({ title: '加载中' });
    const { id, pageNum, pageSize, startTime1, endTime1 } = this.data;
    shopSettlementRecordLists({
      shopId: id,
      pageNum,
      pageSize,
      startDate: startTime1 ? moment(startTime1).format('YYYY-MM-DD') : undefined,
      endDate: endTime1 ? moment(endTime1).format('YYYY-MM-DD') : undefined
    }, res => {
      wx.hideLoading();
      if (res.code == 10000 && res.data) {
        this.setData({
          list: res.data.pageInfo?.list || [],
          totalCount: res.data.totalCount || 0,
          totalAmount: res.data.totalAmount || '0.00',
          pages: res.data.pageInfo?.pages || 1,
          pageNum: res.data.pageInfo?.pageNum || 1
        });
      }
    });
  },
  prevPage: function () {
    if (this.data.pageNum > 1) {
      this.setData({ pageNum: this.data.pageNum - 1 });
      this.getSettlementList();
    }
  },
  nextPage: function () {
    if (this.data.pageNum < this.data.pages) {
      this.setData({ pageNum: this.data.pageNum + 1 });
      this.getSettlementList();
    }
  },
  handleExport: function () {
    const { id, startTime1, endTime1 } = this.data;
    const startDate = startTime1 ? moment(startTime1).format('YYYY-MM-DD') : undefined;
    const endDate = endTime1 ? moment(endTime1).format('YYYY-MM-DD') : undefined;
    const token = wx.getStorageSync("token");
    
    wx.showLoading({ title: '正在导出报表' });
    wx.request({
      url: BASE_URI + '/ddql/wechat/shop/settlement_record/export',
      method: 'POST',
      header: {
        'token': token,
        'Content-Type': 'application/json'
      },
      data: {
        shopId: id,
        startDate,
        endDate
      },
      responseType: 'arraybuffer',
      success: (res) => {
        wx.hideLoading();
        if (res.statusCode === 200) {
          const fs = wx.getFileSystemManager();
          const filePath = `${wx.env.USER_DATA_PATH}/商家结算记录_${moment().format('YYYYMMDDHHmmss')}.xlsx`;
          fs.writeFile({
            filePath: filePath,
            data: res.data,
            encoding: 'binary',
            success: () => {
              wx.openDocument({
                filePath: filePath,
                showMenu: true,
                success: function () {
                  console.log('打开报表成功');
                },
                fail: (err) => {
                  wx.showToast({ title: '预览文档失败', icon: 'none' });
                }
              });
            },
            fail: (err) => {
              wx.showToast({ title: '保存报表失败', icon: 'none' });
            }
          });
        } else {
          wx.showToast({ title: '导出失败', icon: 'none' });
        }
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({ title: '导出请求失败', icon: 'none' });
      }
    });
  },
  orderrefund: function () { //订单退款
    shoporderSelect({
      id: this.data.id,
      refundAmount: this.data.refundtype == '2' ? 0 :this.data.refundAmount,
      refundCoin: this.data.refundCoin,
      refundReason: this.data.refundReason
    }, resd => {
      if(resd.code==10000){
        wx.showToast({
          title: resd.msg,
          icon:'success'
        })
      }
    })
  },
  reset() {
    const {
      activeTime
    } = this.data
    const app = getApp()
    const tempStartTime = new Date(moment().startOf('year').format('YYYY-MM-DD 00:00:00')).getTime()
    const tempEndTime = new Date().getTime()
    this.setData({
      tempStartTime,
      tempEndTime,
      time: activeTime == 1 ? tempStartTime : tempEndTime,
    })
  },
  comfrimDate: function (e) {
    if (this.data.endTime < this.data.startTime) {
      wx.showToast({
        title: '结束时间不能小于开始时间',
        icon: 'none'
      })
      return
    }
    this.setData({
      showStartTime: this.data.startTime,
      showEndTime: this.data.endTime
    })
    this.onClose(e)
  },
  clearItem(e) {
    const {
      value,
      pickertype
    } = e.currentTarget.dataset
    this.setData({
      [pickertype]: this.data[pickertype]?.filter(i => i.value != value)
    })
  },
  onConfirm(e) {
    const pickerData = e.detail.value
    const {
      pickertype
    } = this.data
    const targetList = this.data[pickertype]
    // 判断是否已经选择
    if (targetList.findIndex(i => i.value == pickerData.value) != -1) {
      wx.showToast({
        title: `已选择过该${pickertype == 'typeList'?'类别':'商圈'}`,
        icon: 'none',
      })
      return
    }

    this.setData({
      [pickertype]: [...targetList, pickerData],
      showPop: false,
    })
  },
  onChange: function (e) {
    const {
      name,
      title
    } = e.detail
    if (title == '收款二维码') {
      this.getQrCode()
    } else if (title == '数据统计') {
      this.getShopBusinessData()
    } else if (title == '交易记录') {
      this.shoporderSelect()
    } else if (title == '结算记录') {
      this.getSettlementList()
    }
    this.setData({
      active: name,
      activeTitle: title,
    })
  },
  onClose(e) {
    const {
      name,
    } = e.currentTarget.dataset
    this.setData({
      [name]: false,
      selectRadio: '',
    })
  },
  openTime() {
    this.setData({
      showPop1: true,
      startTime: this.data.showStartTime ? this.data.showStartTime : '00:00',
      endTime: this.data.showEndTime ? this.data.showEndTime : '23:59'
    })
  },
  openPop(e) {
    const {
      pickertype
    } = e.currentTarget.dataset
    const {
      circleColumns,
      typeColumns
    } = this.data
    let columns = []
    // 行业类别
    if (pickertype == 'typeList') {
      columns = typeColumns
    } else if (pickertype == 'circleList') {
      // 商圈
      columns = circleColumns
    }
    this.setData({
      columns,
      showPop: true,
      pickertype,
    })
  },
  // 上传校验
  beforeRead(event) {
    const {
      file,
      callback
    } = event.detail;
    const fileExtension = file.url ? file.url.substring(file.url.lastIndexOf('.')).toLowerCase() : '';

    const isValidExtension = file.url ? allowedTypes.includes(fileExtension) : false;
    // 回调结果
    callback(file.fileType == 'image' && isValidExtension);
    // 如果不合法，可以给出提示
    if (!isValidExtension) {
      wx.showToast({
        title: '只支持jpg、jpeg、png格式',
        icon: 'none',
        duration: 2000
      });
    }
  },
  afterRead(event) {
    const {
      file
    } = event.detail;
    const {
      type
    } = event.currentTarget.dataset
    // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
    const list = this.data[type]
    uploadImages(file.url, resd => {
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '上传成功!',
          icon: 'success'
        })
        list.push({
          ...file,
          url: UPLOAD_IMG_BASE_URI + resd.data.url
        });
        this.setData({
          [type]: list,
        });
      }
    })
  },
  deleteRead: function (e) {
    const {
      type
    } = e.currentTarget.dataset
    const index = e.detail.index
    this.setData({
      [type]: this.data[type].filter((item, i) => i != index)
    })
  },
  goBack: function () {
    if (this.data.showSearch) {
      this.setData({
        showSearch: false,
        keyword: '',
        list: [],
        pageNum: 1,
      })
      return
    }
    wx.navigateBack({
      delta: 1,
    });
  },

  downLoadImg(url) {
    return new Promise((resolve, reject) => {
      wx.downloadFile({
        url,
        success: (res) => {
          resolve(res.tempFilePath)
        },
        fail: () => {
          reject(new Error('下载失败'))
        }
      })
    })
  },
  async getBgImgList() {
    const result = await Promise.all(this.data.ImgList.map(url => this.downLoadImg(url)))

    this.setData({
      bgImgList: result,
    })

    // 获取初始预览图片位置信息
    this.getInitGroupInfo()
  },
  getInitGroupInfo() {
    const that = this
    const query = wx.createSelectorQuery().in(that)

    query.select('#bgimg').boundingClientRect().select('#qrcode').boundingClientRect().select('#bgText1').boundingClientRect().select('#my-canvas .WxEmojiView ').boundingClientRect().exec((res) => {
      console.log(res)
      if (res.every(i => i)) {
        const {
          height,
          width,
          left,
          top,
          dataset: {
            url
          },
        } = res[0]

        const qrRect = res[1]
        const titleRect = res[2]
        const infoRect = res[3]

        this.setData({
          height,
          width,
          // 背景图片
          imgUrl: url,
          // 二维码
          qrRect: {
            ...qrRect,
            top: qrRect.top - top,
            left: qrRect.left - left,
          },
          // 文字位置
          titleRect: {
            ...titleRect,
            top: titleRect.top - top,
            left: width / 2,
          },
          // 介绍位置
          infoRect: {
            ...infoRect,
            top: infoRect.top - top,
            left: infoRect.left - left,
          },
        })
      } else {
        console.log('存在未加载完的元素！')
      }
    })
  },
  // 画图片
  async startCustomDraw() {
    try {
      wx.showLoading({
        title: '加载中',
      })

      // 使用 Canvas 2D API
      const query = wx.createSelectorQuery().in(this)
      const canvasRes = await new Promise((resolve) => {
        query.select('#myCanvas')
          .fields({
            node: true,
            size: true
          })
          .exec((res) => {
            resolve(res[0])
          })
      })

      const canvas = canvasRes.node
      const ctx = canvas.getContext('2d')
      const dpr = wx.getSystemInfoSync().pixelRatio

      // 设置 canvas 尺寸
      canvas.width = this.data.width * dpr
      canvas.height = this.data.height * dpr
      ctx.scale(dpr, dpr)

      // 绘制背景图
      const bgImg = await this.loadImage(canvas, this.data.imgUrl)
      ctx.drawImage(bgImg, 0, 0, this.data.width, this.data.height)

      // 绘制qrCode
      if (this.data.dQrcode) {
        const qrImg = await this.loadImage(canvas, this.data.dQrcode)
        const {
          left,
          top,
          width,
          height
        } = this.data.qrRect
        ctx.drawImage(qrImg, left, top, width, height)
      }

      // 绘制标题
      this.strockText(ctx, {
        x: this.data.titleRect.left,
        y: this.data.titleRect.top,
        fontSize: 23,
        fontWeight: 'bold',
        color: '#04C0D9',
        text: '壹里咖啡店',
        textAlign: 'center',
        textBaseline: 'top'
      })

      // Canvas 2D 不需要调用 draw()，自动绘制
      setTimeout(() => {
        this.generateImageFromCanvas(canvas)
      }, 500)

    } catch (e) {
      console.error('绘制失败:', e)
      wx.hideLoading()
      wx.showToast({
        title: '绘制失败',
        icon: 'none'
      })
    }
  },

  // 加载图片（Canvas 2D 方式）
  loadImage(canvas, src) {
    return new Promise((resolve, reject) => {
      const img = canvas.createImage()
      img.src = src
      img.onload = () => resolve(img)
      img.onerror = (err) => reject(err)
    })
  },
  strockText(ctx, options) {
    const {
      x,
      y,
      fontSize,
      fontWeight,
      color,
      text,
      textAlign,
      textBaseline
    } = options

    // Canvas 2D API：直接赋值而不是使用 setXXX 方法
    ctx.font = `${fontWeight} ${fontSize}px sans-serif`
    ctx.fillStyle = color
    ctx.textAlign = textAlign
    ctx.textBaseline = textBaseline
    ctx.fillText(text, x, y)
  },

  generateImageFromCanvas(canvas) {
    console.log(111111)
    const dpr = wx.getSystemInfoSync().pixelRatio
    const {
      width,
      height,
    } = this.data

    // Canvas 2D 使用 canvas.toTempFilePath 而不是 wx.canvasToTempFilePath
    wx.canvasToTempFilePath({
      canvas: canvas,
      fileType: 'png',
      quality: 1,
      width: width,
      height: height,
      destWidth: width * dpr,
      destHeight: height * dpr,
      success: async (res) => {
        console.log('图片生成成功:', res.tempFilePath)
        wx.previewImage({
          urls: [res.tempFilePath],
        })
      },
      fail: (err) => {
        console.error('生成图片失败:', err)
        wx.showToast({
          title: '生成图片失败',
          icon: 'none'
        })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },
  getshopDetail: function () {
    shopDetail({
      searchId: this.data.id
    }, resd => {
      const formData = resd.data.shop || {}
      const industriesData = resd.data.industryList.map(q => q.industryCategoryId)
      const circlesData = resd.data.circleList.map(q => q.circleId)
      const location = formData.location ? formData.location.split(',') : []
      let plainText = formData.description ?
        formData.description.replace(/<.+?>/g, '') // 1. 去除所有 HTML 标签
        .replace(/&nbsp;/g, ' ') // 2. 将 &nbsp; 替换为正常空格
        .replace(/\s+/g, ' ') // 3. 将连续的空白字符（换行、制表符等）合并为一个空格
        .trim() : '';
      console.log(this.data.superData)
      const contractImages = formData.contractImage ? formData.contractImage.split(',').filter(i => i).map(item => ({ url: item })) : []
      this.setData({
        authorityId: formData.shopId,
        typeList: this.data.Industry.filter(a => industriesData.findIndex(cc => cc == a.id) != -1),
        circleList: this.data.superData.filter(a => circlesData.findIndex(c => c == a.id) != -1),
        shopAuditInfo: {
          ...resd.data.settlementApplication,
          ...resd.data.shopAudit
        },
        fileList: formData.coverImageUrl ? [{
          url: formData.coverImageUrl,
        }] : [],
        qrcCodeList: formData.customerCodeImg?.split(',').map(xzc => {
          return {
            url: xzc
          }
        }) || [],
        bannerList: formData.galleryImages ? formData.galleryImages.map(xzc => {
          return {
            url: xzc
          }
        }) : [],
        name: formData.name,
        location: formData.location,
        selectAddress: {
          title: formData.address,
          longitude: location.length ? location[1] : undefined,
          latitude: location ? location[0] : undefined,
        },
        userName: formData.userName,
        phone: formData.phone,
        businessTime: formData.businessTime,
        // showStartTime: formData.startTime,
        // showEndTime: formData.endTime,
        circleIds: this.data.circleList.map(z => z.value),
        industryCategoryIds: this.data.typeList.map(a => a.value),
        description: plainText,
        customerPhone: formData.customerPhone,
        customerCodeImg: formData.customerCodeImg?.split(',').map(xzc => {
          return {
            url: xzc
          }
        }),
      })
    })
  },
  getcoinshop: function () { //获取用币规则详情数据
    getcoinshop({
      searchId: this.data.id
    }, resd => {
      if (resd.data) {
        this.setData({
          coinId: resd.data.id,
          beginAmount: resd.data.beginAmount,
          threshold: resd.data.threshold,
          deduct: resd.data.deduct,
          maxDeduct: resd.data.maxDeduct,
          remark: resd.data.remark,
        })
      }
    })
  },
  submitconin: function () { //提交用币规则数据
    const params = {
      beginAmount: +this.data.beginAmount,
      threshold: +this.data.threshold,
      deduct: +this.data.deduct,
      maxDeduct: +this.data.maxDeduct,
      remark: this.data.remark,
      shopId: this.data.id
    }
    if (this.data.coinId) {
      params.id = this.data.coinId
    }
    if (this.data.coinId) {
      editcoinshop({
        ...params
      }, resd => {
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
        }
      })
    } else {
      addcoinshop({
        ...params
      }, resd => {
        if (resd.code == 10000) {
          wx.showToast({
            title: resd.msg,
            icon: 'success'
          })
        }
      })
    }
  },
  // scrollTolower() {
  //   const totalPages = Math.ceil(this.data.total / this.data.pageSize)
  //   const currentTotalPages = Math.ceil(this.data.orderList.length / this.data.pageSize)
  //   if (currentTotalPages < totalPages) {
  //     this.setData({
  //       pageNum: this.data.pageNum + 1,
  //     })
  //   }
  // },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      id: +options.shopId
    })
    this.getcoinshop()
    getIndustry({}, resd => {
      resd.data.map(rex => {
        rex.value = rex.id
        rex.text = rex.name
      })
      this.setData({
        Industry: resd.data
      })
      this.getshopDetail()
    })
    getSuper({
      status: 1
    }, resd => {
      resd.data.map(rex => {
        rex.value = rex.id
        rex.text = rex.name
      })
      this.setData({
        superData: resd.data
      })
    })
    // 加载s
    this.getBgImgList()
    WxParse.wxParse('newsDetail.contentHtml', 'html', '抵扣规则说明：抵扣规则的解释权归商家所有，如有异议请联系商家解释。抵扣规则说明：抵扣规则的解释权归商家所有，如有异议请联系商家解释', this, 5)
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

  },
  submitContract() {
    const { contractList, id } = this.data;
    if (!contractList || contractList.length === 0) {
      wx.showToast({ title: '请先上传合同照片', icon: 'none' });
      return;
    }
    const contractUrls = contractList.map(item => item.url).join(',');
    wx.showLoading({ title: '保存中' });
    shopContract({
      searchId: id,
      searchStrField1: contractUrls
    }, resd => {
      wx.hideLoading();
      if (resd && resd.code == 10000) {
        wx.showToast({
          title: '保存成功！',
          icon: 'success'
        });
      }
    }, err => {
      wx.hideLoading();
    });
  }
})